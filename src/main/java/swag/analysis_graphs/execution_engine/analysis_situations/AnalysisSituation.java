package swag.analysis_graphs.execution_engine.analysis_situations;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.jena.sparql.core.Var;
import org.apache.log4j.Logger;

import swag.analysis_graphs.execution_engine.IVariablesList;
import swag.analysis_graphs.execution_engine.navigations.NavigationStep;
import swag.md_elements.Dimension;
import swag.md_elements.Fact;
import swag.md_elements.Level;
import swag.md_elements.MDElement;
import swag.md_elements.Measure;
import swag.sparql_builder.Configuration;
import swag.sparql_builder.ASElements.configuration.DimensionConfigurationObject;
import swag.sparql_builder.ASElements.configuration.MeasureConfigurationObject;

/**
 * 
 * The analysis situation class. An analysis situation is composed of a set of
 * specifications on MD elements.
 * 
 * @author swag
 *
 */
public class AnalysisSituation implements Serializable, IVariablesList, ISignatureType {

    private static final Logger logger = Logger.getLogger(AnalysisSituation.class);
    /**
     * 
     */
    private String label;
    private static final long serialVersionUID = -2566439234019091207L;
    private String uri; // URI
    private String name; // abbreviated (local) name, i.e. without prefix
    private Fact fact;
    private List<Measure> measures;
    private List<Dimension> dimensions;
    private List<IDimensionQualification> dimensionsToAnalysisSituation = new ArrayList<>();
    private List<IMeasureToAnalysisSituation> measuresToAnalysisSituation = new ArrayList<>();
    private List<MeasureAggregatedInAS> resultMeasures = new ArrayList<>();
    private AnalysisSituationToBaseMeasureCondition baseMsrsConds = new AnalysisSituationToBaseMeasureCondition(this,
	    new ArrayList<>());

    public AnalysisSituationToBaseMeasureCondition getBaseMsrsConds() {
	return baseMsrsConds;
    }

    private AnalysisSituationToResultFilters resultFilters = new AnalysisSituationToResultFilters(this,
	    new ArrayList<>());
    private AnalysisSituationToMDConditions mdConditions = new AnalysisSituationToMDConditions(this, new ArrayList<>());
    private List<ISliceSinglePosition<AnalysisSituation>> measureSliceSet;
    private ISliceSetMsr sliceSetMsr;
    private List<PredicateInASMultiple> multipleSlices = new ArrayList<>();
    private List<ISetOfComparison> sets;
    private ISliceSetMultiple sliceSet;
    
    private String dataSet;
    
	/**
     * How specifications of the analysis situation are aliased in the last
     * generated query.
     */
    private Map<ISpecification, Var> varMappings = new HashMap<>();
    private AnalysisSituationType asType = AnalysisSituationType.NON_COMPARATIVE;
    private Map<Integer, Variable> variables;
    private Map<Variable, Variable> initialVariables; // a copy of variables
						      // that is kept unmodified
    private int numOfVariables;
    private String summary;
    private List<NavigationStep> inNavigations;
    private List<NavigationStep> outNavigations;
    // TODO define the types here
    private Object mdPredicate;
    private Object measurePredicate;
    // url of the sparql service
    private String sparqlServiceString = "";

    private List<DimensionConfigurationObject> dimConfigs = new ArrayList<>();
    private List<MeasureConfigurationObject> msrConfigs = new ArrayList<>();

    private Map<Var, VarMetaData> varsMetaData = new HashMap<>();
    private String summarizabilityString;

    public AnalysisSituation cloneMe() {

	AnalysisSituation newAs = new AnalysisSituation();
	newAs.setURI(this.getURI() + System.currentTimeMillis());
	newAs.setName(this.getName() + System.currentTimeMillis());
	newAs.setFact(this.getFact());
	newAs.setAsType(this.getAsType());
	newAs.setDimConfigs(this.getDimConfigs());
	newAs.setDimensions(this.getDimensions());
	newAs.setDimensionsToAnalysisSituation(this.dimensionsToAnalysisSituation.stream()
		.map(x -> (IDimensionQualification) x.cloneMeTo(newAs)).collect(Collectors.toList()));

	newAs.setMeasures(this.getMeasures());
	newAs.setMsrConfigs(this.getMsrConfigs());

	newAs.setMeasuresToAnalysisSituation(this.getMeasuresToAnalysisSituation().stream()
		.map(x -> (IMeasureToAnalysisSituation) x.cloneMeTo(this)).collect(Collectors.toList()));

	AnalysisSituationToMDConditions asMD = new AnalysisSituationToMDConditions(newAs);
	asMD.setMDConditions(this.getMDConditions().stream()
		.map(x -> (ISliceSinglePosition<AnalysisSituationToMDConditions>) x.cloneMeTo(asMD))
		.collect(Collectors.toList()));
	newAs.setMdConditions(asMD);

	AnalysisSituationToResultFilters asFilter = new AnalysisSituationToResultFilters(newAs);
	asFilter.setResultFilters(this.getResultFilters().stream()
		.map(x -> (ISliceSinglePosition<AnalysisSituationToResultFilters>) x.cloneMeTo(asFilter))
		.collect(Collectors.toList()));
	newAs.setResultFilters(asFilter);

	AnalysisSituationToBaseMeasureCondition asBaseMsr = new AnalysisSituationToBaseMeasureCondition(newAs);
	asBaseMsr.setBaseMsrConditions(this.getBaseMsrsConds().getBaseMsrConditions().stream()
		.map(x -> (ISliceSinglePosition<AnalysisSituationToBaseMeasureCondition>) x.cloneMeTo(asBaseMsr))
		.collect(Collectors.toList()));
	newAs.setBaseMsrsConds(asBaseMsr);

	newAs.setResultMeasures(this.getResultMeasures().stream().map(x -> (MeasureAggregatedInAS) x.cloneMeTo(this))
		.collect(Collectors.toList()));

	CloneUtils.createVariables(newAs);

	return newAs;
    }

    /**
     * 
     * Returns all the MD elements that are involved on all the
     * dimensions/hierarchies.
     * 
     * @return a set of the involved MD elements
     * 
     */
    public Set<MDElement> getAllInvolvedDimensionalElements() {
	Set<MDElement> elms = new HashSet<>();

	getDimensionsToAnalysisSituation().stream().flatMap(x -> x.getAllInvolvedElms().stream()).map(x -> elms.add(x));

	return elms;
    }

    /**
     * 
     * Tries to get the MD element that corresponds to the query variable passed
     * 
     * @param varName
     *            name of the variable
     * 
     * @return name of the corresponding MD element
     * 
     */
    public String getMDElementNameByQueryVariableName(String varName) {

	try {
	    for (IDimensionQualification dimToAS : this.getDimensionsToAnalysisSituation()) {

		String grName = Optional.ofNullable(dimToAS.getGanularity()).map(IGranularitySpecification::getPosition)
			.map(MDElement::getNameOfHeadVar).orElse("xXx");

		if (varName.toLowerCase().startsWith(grName.toLowerCase())) {
		    return dimToAS.getGanularity().getPosition().getLabel();
		}
	    }

	    for (MeasureAggregatedInAS measToAS : this.getResultMeasures()) {

		String msrName = Optional.ofNullable(measToAS.getMeasure()).map(MeasureAggregated::getMeasure)
			.map(MDElement::getNameOfHeadVar).orElse("xXx");

		if (varName.toLowerCase().endsWith(msrName.toLowerCase())) {
		    return measToAS.getMeasure().getLabel();
		}
	    }

	    for (Map.Entry<Var, VarMetaData> entry : this.getVarsMetaData().entrySet()) {
		if (entry.getKey().getName().equals(varName)) {
		    return entry.getValue().getVarDisplayName();
		}
	    }
	} catch (Exception ex) {
	    logger.warn("Cannot find naming for variable " + varName + ". Using the same name for displaying results "
		    + varName);
	    return varName;
	}
	logger.warn("Cannot find naming for variable " + varName + ". Using the same name for displaying results "
		+ varName);
	return varName;
    }

    public String getMDElementURIByQueryVariableName(String varName) {

	for (IDimensionQualification dimToAS : this.getDimensionsToAnalysisSituation()) {
	    try {
		if (varName.toLowerCase().startsWith(dimToAS.getGanularity().getPosition().getMapping().getQuery()
			.getNameOfUnaryProjectionVar().toLowerCase())) {
		    return dimToAS.getGanularity().getPosition().getIdentifyingName();
		}
	    } catch (Exception ex) {

	    }
	}

	for (IMeasureToAnalysisSituation measToAS : this.getMeasuresToAnalysisSituation()) {
	    try {
		if (varName.toLowerCase().startsWith(measToAS.getMeasureSpecificationInterface().getPosition()
			.getMapping().getQuery().getNameOfUnaryProjectionVar().toLowerCase())) {
		    return measToAS.getMeasureSpecificationInterface().getPosition().getIdentifyingName();
		}
	    } catch (Exception ex) {

	    }

	    try {
		if (varName.toLowerCase().startsWith(measToAS.getMeasureSpecificationInterface()
			.getAggregationOperationInAnalysisSituation().getAggregationFunction().toLowerCase())) {
		    return measToAS.getMeasureSpecificationInterface().getAggregationOperationInAnalysisSituation()
			    .getAggregationFunction().toUpperCase();
		}
	    } catch (Exception ex) {

	    }
	}
	logger.warn("Cannot find variable " + varName);
	return varName;
    }

    /**
     * gets the key of a specific variable in variables, -1 if not found
     * 
     * @param var
     *            the variable to get its index
     * @return the index
     */
    @Override
    public Integer getKeyOfVariableInVariables(Variable var) {

	for (Map.Entry<Integer, Variable> entry : variables.entrySet()) {
	    if (entry.getValue().equals(var))
		return entry.getKey();
	}
	return -1;
    }

    /**
     * positional compare avoids comparing actual values of bound variables gets
     * the key of a specific variable in variables, -1 if not found
     * 
     * @param var
     *            the variable to get its index
     * @return the index
     */
    @Override
    public Integer getKeyOfVariableInVariablesByPositionalCompare(Variable var) {

	for (Map.Entry<Integer, Variable> entry : variables.entrySet()) {
	    if (entry.getValue().equalsPositional(var))
		return entry.getKey();
	}
	return -1;
    }

    /**
     * shallow copies each key variable in initial variables into a map
     * 
     * @return
     */
    @Override
    public Map<Integer, Variable> shallowCopyInitialVariablesIntoVariablesStyle() {

	Map<Integer, Variable> copied = new HashMap<Integer, Variable>();
	int i = 0;
	for (Map.Entry<Variable, Variable> entry : initialVariables.entrySet()) {
	    copied.put(i++, entry.getKey().shallowCopy());
	}
	return copied;
    }

    /**
     * @return
     */
    @Override
    public List<Variable> getInitialVariablesAllAsList() {
	List<Variable> allInit = new ArrayList<Variable>(initialVariables.keySet());
	return allInit;
    }

    /**
     * @return
     */
    @Override
    public List<Variable> getUnBoundVariables() {

	List<Variable> unbound = new ArrayList<Variable>();
	for (Map.Entry<Variable, Variable> entry : initialVariables.entrySet()) {
	    if (entry.getValue() == null) {
		unbound.add(entry.getKey());
	    }
	}
	return unbound;
    }

    /**
     * @return
     */
    @Override
    public List<Variable> getInitialVariablesThatAreBound() {

	List<Variable> bound = new ArrayList<Variable>();
	for (Map.Entry<Variable, Variable> entry : initialVariables.entrySet()) {
	    if (entry.getValue() != null) {
		bound.add(entry.getKey());
	    }
	}
	return bound;
    }

    /**
     * gets how the value of the variable became after being bound
     * 
     * @param var
     *            the variable to see its bound value
     * @return the bound variable, null if not found
     */
    @Override
    public Variable getBoundVarOfInitialVar(Variable var) {
	return initialVariables.get(var);
    }

    /**
     * gets how the initial value of a bound variable
     * 
     * @param var
     *            the bound variable to see its initial value
     * @return the initial variable, null if not found
     */
    @Override
    public Variable getInitialVarOfBoundVar(Variable var) {
	for (Map.Entry<Variable, Variable> entry : initialVariables.entrySet()) {
	    if (entry.getValue() != null)
		if (entry.getValue().equals(var))
		    return entry.getKey();
	}
	return null;
    }

    /**
     * @param dimName
     * @return
     */
    public IDimensionQualification getDimToASByDimAndHierName(String dimURI, String hierURI) {
	for (IDimensionQualification dimToAS : this.getDimensionsToAnalysisSituation()) {
	    if (dimToAS.isOnSameHierarchyAndDimensionByURIs(dimURI, hierURI)) {
		return dimToAS;
	    }
	}
	return null;
    }

    public AnalysisSituation() {
	super();
	this.inNavigations = new ArrayList<NavigationStep>();
	this.outNavigations = new ArrayList<NavigationStep>();
	this.variables = new HashMap<Integer, Variable>();
	this.numOfVariables = 0;
	this.initialVariables = new HashMap<Variable, Variable>();

	// TODO Auto-generated constructor stub
    }

    public AnalysisSituation(Fact fact, List<Measure> measures, List<Dimension> dimensions,
	    List<IDimensionQualification> dimensionsToAnalysisSituation,
	    List<IMeasureToAnalysisSituation> measuresToAnalysisSituation, Object mdPredicate, Object measurePredicate,
	    String sparqlServiceString, String summary, List<PredicateInASMultiple> multipleSlices) {
	this();
	this.fact = fact;
	this.measures = measures;
	this.dimensions = dimensions;
	this.dimensionsToAnalysisSituation = dimensionsToAnalysisSituation;
	this.measuresToAnalysisSituation = measuresToAnalysisSituation;
	this.mdPredicate = mdPredicate;
	this.measurePredicate = measurePredicate;
	this.sparqlServiceString = sparqlServiceString;
	this.summary = summary;
	this.multipleSlices = multipleSlices;
    }

    @Override
    public void addToVariables(Variable obj) {
	this.variables.put(numOfVariables++, obj);
    }

    @Override
    public void removeFromVariables(Variable obj) {
	this.variables.remove(obj);
    }

    @Override
    public void addToInitialVariables(Variable v1, Variable v2) {
	this.initialVariables.put(v1, v2);
    }

    public void addVariablesToInitialVariables() {
	for (Map.Entry<Integer, Variable> ent : this.getVariables().entrySet()) {
	    this.addToInitialVariables(ent.getValue().shallowCopy(), null);
	}
    }

    @Override
    public Map<Integer, Variable> shallowCopyVariables() {

	Map<Integer, Variable> copied = new HashMap<Integer, Variable>();

	for (Map.Entry<Integer, Variable> entry : variables.entrySet()) {
	    copied.put(entry.getKey(), entry.getValue().shallowCopy());
	}

	return copied;
    }

    @Override
    public void fillVariablesFrom(List<Object> variables) {

	for (Object o : variables) {
	    if (o instanceof Level) {

	    }
	}
    }

    // comparing analysis situations equality is done only by their uri (name)
    @Override
    public boolean equals(Object o) {

	boolean res = false;
	if (o instanceof AnalysisSituation) {
	    AnalysisSituation as = (AnalysisSituation) o;
	    if (this.uri != null && as.uri != null) {
		if (this.uri.equals(as.uri))
		    res = true;
	    }
	}
	return res;
    }

    @Override
    public boolean comparePositoinal(ISignatureType c) {

	if (this == c) {
	    return true;
	}

	if (c instanceof AnalysisSituation) {
	    AnalysisSituation as = (AnalysisSituation) c;
	    if (this.getFact().equals(as.getFact())) {
		return true;
	    }
	}
	return false;
    }

    @Override
    public int generatePositionalHashCode() {
	return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
		append(this.getFact()).toHashCode();
    }

    @Override
    public String getUri() {
	return uri;
    }

    public void setUri(String uri) {
	this.uri = uri;
    }

    public List<ISliceSinglePosition<AnalysisSituation>> getMeasureSliceSet() {
	return measureSliceSet;
    }

    public void setMeasureSliceSet(List<ISliceSinglePosition<AnalysisSituation>> measureSliceSet) {
	this.measureSliceSet = measureSliceSet;
    }

    public ISliceSetMsr getSliceSetMsr() {
	return sliceSetMsr;
    }

    public void setSliceSetMsr(ISliceSetMsr sliceSetMsr) {
	this.sliceSetMsr = sliceSetMsr;
    }

    public void AddResultMeasures(MeasureAggregatedInAS msr) {
	resultMeasures.add(msr);
    }

    public List<MeasureAggregatedInAS> getResultMeasures() {
	if (resultMeasures == null) {
	    resultMeasures = new ArrayList<>();
	}
	return resultMeasures;
    }

    public void setResultMeasures(List<MeasureAggregatedInAS> resultMeasures) {
	this.resultMeasures = resultMeasures;
    }

    public void AddResultFilter(ISliceSinglePosition<AnalysisSituationToResultFilters> cond) {
	this.resultFilters.getResultFilters().add(cond);
    }

    public List<ISliceSinglePosition<AnalysisSituationToResultFilters>> getResultFilters() {
	if (this.resultFilters.getResultFilters() == null) {
	    this.resultFilters.setResultFilters(new ArrayList<>());
	}
	return this.resultFilters.getResultFilters();
    }

    public void setResultFilters(List<ISliceSinglePosition<AnalysisSituationToResultFilters>> resultFilters) {
	this.resultFilters.setResultFilters(resultFilters);
    }

    public AnalysisSituationToResultFilters getResultFiltersObject() {
	return this.resultFilters;
    }

    public void AddResultBaseFilter(ISliceSinglePosition<AnalysisSituationToBaseMeasureCondition> cond) {
	this.baseMsrsConds.getBaseMsrConditions().add(cond);
    }

    public List<ISliceSinglePosition<AnalysisSituationToBaseMeasureCondition>> getResultBaseFilters() {
	if (this.baseMsrsConds.getBaseMsrConditions() == null) {
	    this.baseMsrsConds.setBaseMsrConditions(new ArrayList<>());
	}
	return this.baseMsrsConds.getBaseMsrConditions();
    }

    public void setResultBaseFilters(
	    List<ISliceSinglePosition<AnalysisSituationToBaseMeasureCondition>> resultBaseFilters) {
	this.baseMsrsConds.setBaseMsrConditions(resultBaseFilters);
    }

    public void AddMDCondition(ISliceSinglePosition<AnalysisSituationToMDConditions> cond) {
	this.mdConditions.getMDConditions().add(cond);
    }

    public List<ISliceSinglePosition<AnalysisSituationToMDConditions>> getMDConditions() {
	if (this.mdConditions.getMDConditions() == null) {
	    this.mdConditions.setMDConditions(new ArrayList<>());
	}
	return this.mdConditions.getMDConditions();
    }

    public void setMDConditions(List<ISliceSinglePosition<AnalysisSituationToMDConditions>> mdConditions) {
	this.mdConditions.setMDConditions(mdConditions);
    }

    public AnalysisSituationToBaseMeasureCondition getBaseMsrsCondsObject() {
	return baseMsrsConds;
    }

    public void setBaseMsrsConds(AnalysisSituationToBaseMeasureCondition baseMsrsConds) {
	this.baseMsrsConds = baseMsrsConds;
    }

    public AnalysisSituationToMDConditions getMdConditionsObject() {
	return mdConditions;
    }

    public void setMdConditions(AnalysisSituationToMDConditions mdConditions) {
	this.mdConditions = mdConditions;
    }

    public void setResultFilters(AnalysisSituationToResultFilters resultFilters) {
	this.resultFilters = resultFilters;
    }

    public ISliceSetMultiple getSliceSet() {
	return sliceSet;
    }

    public void setSliceSet(ISliceSetMultiple sliceSet) {
	this.sliceSet = sliceSet;
    }

    public boolean isComparative() {
	return this.getAsType().equals(AnalysisSituationType.COMPARATIVE);
    }

    public AnalysisSituationType getAsType() {
	return asType;
    }

    public void setAsType(AnalysisSituationType asType) {
	this.asType = asType;
    }

    public List<ISetOfComparison> getSets() {
	return sets;
    }

    public ISetOfComparison getSetByURI(String setURI) {
	for (ISetOfComparison set : getSets()) {
	    if (set.getURI().equals(setURI)) {
		return set;
	    }
	}
	return NoneSet.getNoneSet();
    }

    public void addToSets(ISetOfComparison set) {
	if (this.sets == null) {
	    this.sets = new ArrayList<>();
	}
	this.sets.add(set);
    }

    public void setSets(List<ISetOfComparison> sets) {
	this.sets = sets;
    }

    public List<PredicateInASMultiple> getMultipleSlices() {
	return multipleSlices;
    }

    public void setMultipleSlices(List<PredicateInASMultiple> multipleSlices) {
	this.multipleSlices = multipleSlices;
    }

    public String getURI() {
	return uri;
    }

    public void setURI(String uri) {
	this.uri = uri;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public Fact getFact() {
	return fact;
    }

    public void setFact(Fact fact) {
	this.fact = fact;
    }

    public List<Measure> getMeasures() {
	return measures;
    }

    public void setMeasures(List<Measure> measures) {
	this.measures = measures;
    }

    public List<Dimension> getDimensions() {
	return dimensions;
    }

    public void setDimensions(List<Dimension> dimensions) {
	this.dimensions = dimensions;
    }

    public List<IDimensionQualification> getDimensionsToAnalysisSituation() {
	return dimensionsToAnalysisSituation;
    }

    public void setDimensionsToAnalysisSituation(List<IDimensionQualification> dimensionsToAnalysisSituation) {
	this.dimensionsToAnalysisSituation = dimensionsToAnalysisSituation;
    }

    public List<IMeasureToAnalysisSituation> getMeasuresToAnalysisSituation() {
	return measuresToAnalysisSituation;
    }

    public void setMeasuresToAnalysisSituation(List<IMeasureToAnalysisSituation> measuresToAnalysisSituation) {
	this.measuresToAnalysisSituation = measuresToAnalysisSituation;
    }

    public Object getMdPredicate() {
	return mdPredicate;
    }

    public void setMdPredicate(Object mdPredicate) {
	this.mdPredicate = mdPredicate;
    }

    public Object getMeasurePredicate() {
	return measurePredicate;
    }

    public void setMeasurePredicate(Object measurePredicate) {
	this.measurePredicate = measurePredicate;
    }

    public String getSparqlServiceString() {
	return sparqlServiceString;
    }

    public void setSparqlServiceString(String sparqlServiceString) {
	this.sparqlServiceString = sparqlServiceString;
    }

    public String getSummary() {
	return summary;
    }

    public void setSummary(String summary) {
	this.summary = summary;
    }

    public List<NavigationStep> getInNavigations() {
	return inNavigations;
    }

    public void setInNavigations(List<NavigationStep> inNavigations) {
	this.inNavigations = inNavigations;
    }

    public List<NavigationStep> getOutNavigations() {
	return outNavigations;
    }

    public void setOutNavigations(List<NavigationStep> outNavigations) {
	this.outNavigations = outNavigations;
    }

    public Map<Integer, Variable> getVariables() {
	return variables;
    }

    public void setVariables(Map<Integer, Variable> variables) {
	this.variables = variables;
    }

    public Map<Variable, Variable> getInitialVariables() {
	return initialVariables;
    }

    public void setInitialVariables(Map<Variable, Variable> initialVariables) {
	this.initialVariables = initialVariables;
    }

    public Map<ISpecification, Var> getVarMappings() {
	return varMappings;
    }

    public void setVarMappings(Map<ISpecification, Var> varMappings) {
	this.varMappings = varMappings;
    }

    public List<DimensionConfigurationObject> getDimConfigs() {
	return dimConfigs;
    }

    public void setDimConfigs(List<DimensionConfigurationObject> dimConfigs) {
	this.dimConfigs = dimConfigs;
    }

    public List<MeasureConfigurationObject> getMsrConfigs() {
	return msrConfigs;
    }

    public void setMsrConfigs(List<MeasureConfigurationObject> msrConfigs) {
	this.msrConfigs = msrConfigs;
    }

    public Map<Var, VarMetaData> getVarsMetaData() {
	return varsMetaData;
    }

    public void setVarsMetaData(Map<Var, VarMetaData> varsMetaData) {
	this.varsMetaData = varsMetaData;
    }

    public void addToVarsMetaData(Var var, VarMetaData metaData) {
	this.varsMetaData.put(var, metaData);
    }

    public String generateSummarizabilityString() {

	StringBuilder builder = new StringBuilder();
	builder.append("Dimesnions Summarizability:\n");
	for (DimensionConfigurationObject conf : getDimConfigs()) {
	    builder.append(conf.toString());
	}
	builder.append("\nMeasures Summarizability:\n");
	for (MeasureConfigurationObject conf : getMsrConfigs()) {
	    builder.append(conf.toString());
	}
	return builder.toString();
    }

    public String getSummarizabilityString() {

	if (Configuration.getInstance().is("reporting", "active")) {

	    if (summarizabilityString == null) {
		summarizabilityString = generateSummarizabilityString();
	    }
	    return summarizabilityString;
	}
	return StringUtils.EMPTY;
    }

    public void setSummarizabilityString(String summarizabilityString) {
	this.summarizabilityString = summarizabilityString;
    }

    @Override
    public IClonableTo<ISignatureType> cloneMeTo(ISignatureType to) {
	return this.cloneMe();
    }

    public String getLabel() {
	return label;
    }

    public void setLabel(String label) {
	this.label = label;
    }
    
    public String getDataSet() {
		return dataSet;
	}

	public void setDataSet(String dataSet) {
		this.dataSet = dataSet;
	}


}
