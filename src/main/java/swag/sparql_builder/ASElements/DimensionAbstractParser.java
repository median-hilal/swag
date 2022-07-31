package swag.sparql_builder.ASElements;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import swag.analysis_graphs.execution_engine.AnalysisGraph;
import swag.analysis_graphs.execution_engine.analysis_situations.IDiceSpecification;
import swag.analysis_graphs.execution_engine.analysis_situations.IDimensionQualification;
import swag.analysis_graphs.execution_engine.analysis_situations.IGranularitySpecification;
import swag.analysis_graphs.execution_engine.analysis_situations.ISliceSinglePosition;
import swag.graph.Path;
import swag.md_elements.Fact;
import swag.md_elements.Level;
import swag.md_elements.MDElement;
import swag.md_elements.MDRelation;
import swag.md_elements.MDSchema;
import swag.sparql_builder.CustomSPARQLQuery;

/**
 * 
 * Abstract class to generating a query from a dimension. implements the main
 * method to generate the query and provides a set of helping methods.
 * Additionally it provides abstract methods to be implemented by implementing
 * classes.
 * 
 * @author swag
 *
 */
public abstract class DimensionAbstractParser implements IDimensionParser {

    private AnalysisGraph ag;
    private CustomSPARQLQuery asQuery;
    private ASElementSPARQLGenerator visitor;
    private Map<MDElement, String> varMappings;

    /**
     * 
     * Create a new {@code DimensionAbstractParser} instance.
     * 
     * @param ag
     * @param asQuery
     * @param visitor
     * @param varMappings
     */
    public DimensionAbstractParser(AnalysisGraph ag, CustomSPARQLQuery asQuery, Map<MDElement, String> varMappings) {
	super();
	this.ag = ag;
	this.asQuery = asQuery;
	this.varMappings = varMappings;
	this.visitor = new ASElementSPARQLGenerator(ag, asQuery.getSparqlQuery(), varMappings);
    }

    /**
     * 
     * This method contains additional parsing operations that are to be done by
     * extending classes.
     * 
     * @param dimToAS
     *            dimension
     * @param mdSchema
     *            MD schema
     * @param frgments
     *            the list of fragments
     */
    public abstract void parseAdditionals(IDimensionQualification dimToAS, MDSchema mdSchema,
	    List<MDPathFragment> frgments);

    @Override
    public void parseDimensionToAS(MDSchema mdSchema, IDimensionQualification dimToAS, List<MDElement> granularities,
	    List<MDPathFragment> frgments, List<String> filters) throws Exception {

	Set<MDPathFragment> fragmentSet = new HashSet<>(getAllFragmentsFromFact(dimToAS, mdSchema));
	fragmentSet = mergeFragments(fragmentSet, mdSchema);
	manageGranularities(dimToAS, granularities);
	manageDiceQuery(dimToAS, filters);
	manageSliceQuery(dimToAS, filters);
	frgments.clear();
	frgments.addAll(fragmentSet);

    }

    public void manageGranularities(IDimensionQualification dimToAS, List<MDElement> granularities) {
	if (dimToAS.getGranularities().size() > 0) {
	    if (dimToAS.getGranularities().get(0) != null) {
		granularities.add(dimToAS.getGranularities().get(0).getPosition());
	    }
	}
    }

    public List<MDPathFragment> getAllFragmentsFromFact(IDimensionQualification dimToAS, MDSchema mdSchema) {

	List<MDPathFragment> frgments = new ArrayList<>();
	Fact fact = mdSchema.getFactOfSchema();

	for (IGranularitySpecification gran : dimToAS.getGranularities()) {
	    if (gran.isDue()) {
		MDPathFragment frag = new MDPathFragment(fact, gran.getPosition(), MDPathFragmentType.Granularity);
		frgments.add(frag);
	    }
	}

	for (IDiceSpecification dice : dimToAS.getDices()) {
	    if (dice.isDue()) {
		MDPathFragment frag = new MDPathFragment(fact, dice.getPosition(), MDPathFragmentType.Dice);
		frgments.add(frag);
	    }
	}

	for (ISliceSinglePosition<IDimensionQualification> slice : dimToAS.getSliceConditions()) {
	    if (!slice.getSignature().isVariable()) {
		MDPathFragment frag = new MDPathFragment(fact, slice.getPositionOfCondition(),
			MDPathFragmentType.Slice);
		frgments.add(frag);
	    }
	}

	parseAdditionals(dimToAS, mdSchema, frgments);
	return frgments;

    }

    /**
     * 
     * Checks whether merging fragments has finished. That means that there is
     * nothing shared between any couple of fragments on the dimension.
     * 
     * @param frgments
     * @return
     * 
     */
    public boolean areFragmentsIsolated(Set<MDPathFragment> frgments, MDSchema mdSchema) {

	if (frgments.size() <= 1) {
	    return true;
	}
	for (MDPathFragment frag : frgments) {
	    for (MDPathFragment otherFrag : frgments) {

		if (!frag.equals(otherFrag)) {

		    List<MDRelation> intersection = new ArrayList<>();

		    Path<MDElement, MDRelation> path1 = frag.getPathOfFragment(mdSchema);
		    Path<MDElement, MDRelation> path2 = otherFrag.getPathOfFragment(mdSchema);

		    int comparison;

		    if (path1 != null && path2 != null) {
			comparison = path1.compareWith(path2, intersection);
		    } else {
			comparison = -3;
		    }

		    if (comparison != -3) {
			return false;
		    }
		}
	    }
	}
	return true;
    }

    public Set<MDPathFragment> mergeFragments(Set<MDPathFragment> fragments, MDSchema mdSchema) {

	if (fragments.size() <= 1) {
	    return fragments;
	}

	// TODO performance here can be reduced by two
	Set<MDPathFragment> newFragments = new HashSet<>();

	Set<Set<MDPathFragment>> comparedFragments = new HashSet<>();
	List<List<MDPathFragment>> comparedFragmentsList = new ArrayList<>();

	boolean innerDone = false;

	for (MDPathFragment frag : fragments) {

	    boolean isDoomed = false;
	    for (MDPathFragment otherFrag : fragments) {

		boolean done = false;

		Path<MDElement, MDRelation> path1 = frag.getPathOfFragment(mdSchema);
		Path<MDElement, MDRelation> path2 = otherFrag.getPathOfFragment(mdSchema);

		Set<MDPathFragment> currentAddedSet = new HashSet<>();
		currentAddedSet.add(frag);
		currentAddedSet.add(otherFrag);

		List<MDPathFragment> currentAddedList = new ArrayList<>();
		currentAddedList.add(frag);
		currentAddedList.add(otherFrag);

		if (
		/* !comparedFragments.contains(currentAddedSet) && */ !frag.equals(otherFrag) && path1 != null
			&& path2 != null) {

		    comparedFragments.add(currentAddedSet);
		    comparedFragmentsList.add(currentAddedList);

		    List<MDRelation> intersection = new ArrayList<>();

		    int comparison;
		    int compareMDFragTypes;

		    comparison = path1.compareWith(path2, intersection);

		    compareMDFragTypes = MDPathFragmentType.isStrongerThan(frag.getTyp(), otherFrag.getTyp());

		    switch (comparison) {
		    // current Contains other
		    case 1: {
			// Current stronger or equal than other
			if (compareMDFragTypes >= 0) {

			    // newFragments.add(frag);
			    innerDone = true;

			    // Other stronger than current
			} else {

			    done = true;
			    isDoomed = true;
			    // newFragments.add(otherFrag);

			    if (!otherFrag.getTarget().compareASMDElement(frag.getTarget())) {
				newFragments.add(
					new MDPathFragment(otherFrag.getTarget(), frag.getTarget(), frag.getTyp()));

				System.out.println(
					"Adding fragment case 1 - else" + otherFrag.getTarget().getIdentifyingName()
						+ " -- " + frag.getTarget().getIdentifyingName());
			    } else {
				newFragments.add(
					new MDPathFragment(frag.getSource(), otherFrag.getSource(), frag.getTyp()));

				System.out
					.println("Adding fragment case 1 - else" + frag.getSource().getIdentifyingName()
						+ " -- " + otherFrag.getSource().getIdentifyingName());
			    }
			}
			break;
		    }
		    // Equal
		    case 0: {
			// Current stronger or equal than other
			if (compareMDFragTypes >= 0) {
			    innerDone = true;
			    // newFragments.add(frag);

			    if (compareMDFragTypes == 0) {
				List<MDPathFragment> toAdd = new ArrayList<>();
				toAdd.add(otherFrag);
				toAdd.add(frag);

				if (!comparedFragmentsList.contains(toAdd)) {

				    System.out.println(
					    "Adding fragment case 0 - if" + frag.getSource().getIdentifyingName()
						    + " -- " + frag.getTarget().getIdentifyingName());

				    newFragments.add(frag);
				}
			    }

			    // Other stronger than current
			} else {

			    isDoomed = true;
			    done = true;

			    System.out.println(
				    "Adding fragment case 0 - else" + otherFrag.getSource().getIdentifyingName()
					    + " -- " + otherFrag.getTarget().getIdentifyingName());

			    newFragments.add(otherFrag);
			}
			break;
		    }
		    // Other contains current
		    case -1: {
			// Current stronger or equal than other
			if (compareMDFragTypes >= 0) {

			    if (compareMDFragTypes == 0) {
				isDoomed = true;
			    } else {

				innerDone = true;
				// newFragments.add(frag);

				if (!otherFrag.getTarget().compareASMDElement(frag.getTarget())) {
				    newFragments.add(new MDPathFragment(frag.getTarget(), otherFrag.getTarget(),
					    otherFrag.getTyp()));

				    System.out.println("Adding fragment case -1 - 0 else if"
					    + frag.getTarget().getIdentifyingName() + " -- "
					    + otherFrag.getTarget().getIdentifyingName());
				} else {

				    System.out.println("Adding fragment case -1 - 0 else else"
					    + otherFrag.getSource().getIdentifyingName() + " -- "
					    + frag.getSource().getIdentifyingName());

				    newFragments.add(new MDPathFragment(otherFrag.getSource(), frag.getSource(),
					    otherFrag.getTyp()));
				}
			    }

			    // Other stronger than current
			} else {
			    isDoomed = true;
			    done = true;

			    System.out.println(
				    "Adding fragment case -1 big else " + otherFrag.getSource().getIdentifyingName()
					    + " -- " + otherFrag.getTarget().getIdentifyingName());

			    newFragments.add(otherFrag);
			}
			break;
		    }
		    // Intersection
		    case -2: {

			// Intersection is shared from the beginning element
			if (intersection.get(0).getSource().compareASMDElement(frag.getSource())
				&& intersection.get(0).getSource().compareASMDElement(otherFrag.getSource())) {

			    done = true;

			    innerDone = true;
			    isDoomed = true;

			    newFragments.add(new MDPathFragment(intersection.get(0).getSource(),
				    intersection.get(intersection.size() - 1).getTarget(), MDPathFragmentType.Shared));

			    System.out.println("Adding fragment case intersection shared"
				    + intersection.get(0).getSource().getIdentifyingName() + " -- "
				    + intersection.get(intersection.size() - 1).getTarget().getIdentifyingName());

			    if (intersection.get(0).getSource().equals(frag.getSource())) {
				newFragments
					.add(new MDPathFragment(intersection.get(intersection.size() - 1).getTarget(),
						frag.getTarget(), frag.getTyp()));

				System.out.println("Adding fragment case intersection 1.1 "
					+ intersection.get(intersection.size() - 1).getTarget().getIdentifyingName()
					+ " -- " + frag.getTarget().getIdentifyingName());

			    } else {
				if (intersection.get(0).getTarget().compareASMDElement(frag.getTarget())) {
				    newFragments.add(new MDPathFragment(frag.getSource(),
					    intersection.get(0).getSource(), frag.getTyp()));

				    System.out.println("Adding fragment case intersection 1.2 "
					    + frag.getSource().getIdentifyingName() + " -- "
					    + intersection.get(0).getSource().getIdentifyingName());

				} else {
				    newFragments.add(
					    new MDPathFragment(intersection.get(intersection.size() - 1).getTarget(),
						    frag.getTarget(), frag.getTyp()));
				    newFragments.add(new MDPathFragment(frag.getSource(),
					    intersection.get(0).getSource(), frag.getTyp()));

				    System.out.println("Adding fragment case intersection 1.3 "
					    + frag.getSource().getIdentifyingName() + " -- "
					    + intersection.get(0).getSource().getIdentifyingName());

				}
			    }

			    if (intersection.get(0).getSource().compareASMDElement(frag.getSource())) {
				newFragments
					.add(new MDPathFragment(intersection.get(intersection.size() - 1).getTarget(),
						otherFrag.getTarget(), otherFrag.getTyp()));

				System.out.println("Adding fragment case intersection 1.1 "
					+ intersection.get(intersection.size() - 1).getTarget().getIdentifyingName()
					+ " -- " + otherFrag.getTarget().getIdentifyingName());

			    } else {
				if (intersection.get(0).getTarget().compareASMDElement(otherFrag.getTarget())) {
				    newFragments.add(new MDPathFragment(otherFrag.getSource(),
					    intersection.get(0).getSource(), otherFrag.getTyp()));

				    System.out.println("Adding fragment case intersection 1.2 "
					    + otherFrag.getSource().getIdentifyingName() + " -- "
					    + intersection.get(0).getSource().getIdentifyingName());

				} else {
				    newFragments.add(
					    new MDPathFragment(intersection.get(intersection.size() - 1).getTarget(),
						    otherFrag.getTarget(), otherFrag.getTyp()));
				    newFragments.add(new MDPathFragment(otherFrag.getSource(),
					    intersection.get(0).getSource(), otherFrag.getTyp()));

				    System.out.println("Adding fragment case intersection 1.3 "
					    + otherFrag.getSource().getIdentifyingName() + " -- "
					    + intersection.get(0).getSource().getIdentifyingName());
				}
			    }

			} else {

			}
			break;
		    }
		    // Unknown
		    case -3: {
			break;
		    }
		    }
		}
		if (done) {
		    break;
		}
	    }
	    if (!isDoomed) {
		newFragments.add(frag);
	    }
	}

	/*
	 * System.out.println("------------Curr frags----------");
	 * 
	 * for (MDPathFragment fragggg : newFragments) {
	 * System.out.println("frag source:" +
	 * fragggg.getSource().getIdentifyingName());
	 * System.out.println("frag target:" +
	 * fragggg.getTarget().getIdentifyingName());
	 * System.out.println("frag target:" + fragggg.getTyp());
	 * System.out.println("#####################"); }
	 * 
	 * System.out.println("-------------------------------");
	 */

	if (!areFragmentsIsolated(newFragments, mdSchema)) {
	    return mergeFragments(newFragments, mdSchema);
	} else {
	    return newFragments;
	}
    }

    public static void compareSingleFragmentToGranularity(int compare, Level terminalLevel, Level granularity,
	    MDElement element, MDPathFragmentType fragmentType, MDSchema mdSchema, List<MDPathFragment> frgments) {

	// Granularity is before or equal
	if (compare > 0) {

	    if (!element.equals(terminalLevel)) {
		MDPathFragment frag = new MDPathFragment(terminalLevel, element, fragmentType);
		frgments.add(frag);
	    }
	} else {
	    // Dice/slice rolls up to granularity
	    if (compare == 0) {

		if (!element.equals(terminalLevel)) {
		    MDPathFragment frag1 = new MDPathFragment(terminalLevel, element, fragmentType);
		    frgments.add(frag1);
		}

		MDPathFragment frag2 = new MDPathFragment(element, granularity, MDPathFragmentType.Granularity);
		frgments.add(frag2);

	    }
	    // No direct comparison possible between dice/slice and granularity
	    // levels
	    else {

		MDElement elem = mdSchema.getLeastCommonAncestor(granularity.getIdentifyingName(),
			element.getIdentifyingName());

		if (!terminalLevel.equals(elem)) {
		    MDPathFragment frag = new MDPathFragment(terminalLevel, elem, MDPathFragmentType.Shared);
		    frgments.add(frag);
		}

		MDPathFragment frag1 = new MDPathFragment(elem, element, fragmentType);
		frgments.add(frag1);

		MDPathFragment frag2 = new MDPathFragment(elem, granularity, MDPathFragmentType.Granularity);
		frgments.add(frag2);

	    }
	}
    }

    /**
     * 
     * Generates a filter statement that corresponds to dice specification on a
     * dimension.
     * 
     * @param dimToAS
     *            the dimension qualification at hand
     * @param diceFilters
     *            output set of filters as strings
     * 
     */
    protected void manageDiceQuery(IDimensionQualification dimToAS, List<String> diceFilters) {

	for (IDiceSpecification dc : dimToAS.getDices()) {
	    dc.acceptVisitor(getVisitor());
	    diceFilters.addAll(getVisitor().getReturn());
	}
    }

    /**
     * 
     * Generates a filter statement that corresponds to slice specification on a
     * dimension.
     * 
     * @param dimToAS
     *            the dimension qualification at hand
     * @param sliceFilters
     *            output set of filters as strings
     * @throws Exception
     * 
     */
    protected void manageSliceQuery(IDimensionQualification dimToAS, List<String> sliceFilters) throws Exception {

	for (ISliceSinglePosition<IDimensionQualification> sc : dimToAS.getSliceConditions()) {

	    sc.acceptVisitor(getVisitor());
	    List<String> filters = getVisitor().getReturn();
	    sliceFilters.addAll(filters);
	}
    }

    public CustomSPARQLQuery getAsQuery() {
	return asQuery;
    }

    public void setAsQuery(CustomSPARQLQuery asQuery) {
	this.asQuery = asQuery;
    }

    public void setAg(AnalysisGraph ag) {
	this.ag = ag;
    }

    public void setVisitor(ASElementSPARQLGenerator visitor) {
	this.visitor = visitor;
    }

    public Map<MDElement, String> getVarMappings() {
	return varMappings;
    }

    public void setVarMappings(Map<MDElement, String> varMappings) {
	this.varMappings = varMappings;
    }

    public AnalysisGraph getAg() {
	return ag;
    }

    public ASElementSPARQLGenerator getVisitor() {
	return visitor;
    }
}
