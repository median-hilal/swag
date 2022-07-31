package swag.data_handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.RDF;
import org.apache.log4j.Logger;

import swag.analysis_graphs.execution_engine.DefinedAGConditions;
import swag.analysis_graphs.execution_engine.DefinedAGConditionsTypes;
import swag.analysis_graphs.execution_engine.DefinedAGPredicates;
import swag.analysis_graphs.execution_engine.MultiSliceSignature;
import swag.analysis_graphs.execution_engine.Signature;
import swag.analysis_graphs.execution_engine.analysis_situations.AnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.AnalysisSituationToBaseMeasureCondition;
import swag.analysis_graphs.execution_engine.analysis_situations.AnalysisSituationToResultFilters;
import swag.analysis_graphs.execution_engine.analysis_situations.AnalysisSituationType;
import swag.analysis_graphs.execution_engine.analysis_situations.IDimensionQualification;
import swag.analysis_graphs.execution_engine.analysis_situations.IMultipleSliceSpecification;
import swag.analysis_graphs.execution_engine.analysis_situations.ISetOfComparison;
import swag.analysis_graphs.execution_engine.analysis_situations.ISliceSinglePosition;
import swag.analysis_graphs.execution_engine.analysis_situations.ItemInAnalysisSituationType;
import swag.analysis_graphs.execution_engine.analysis_situations.MeasureAggregatedInAS;
import swag.analysis_graphs.execution_engine.analysis_situations.MultipleSliceSpecification;
import swag.analysis_graphs.execution_engine.analysis_situations.NoneSet;
import swag.analysis_graphs.execution_engine.analysis_situations.PredicateInAG;
import swag.analysis_graphs.execution_engine.analysis_situations.PredicateInASMultiple;
import swag.analysis_graphs.execution_engine.analysis_situations.PredicateVariableToMDElementMapping;
import swag.analysis_graphs.execution_engine.analysis_situations.SetOfComparison;
import swag.analysis_graphs.execution_engine.analysis_situations.VariableState;
import swag.analysis_graphs.execution_engine.navigations.NavigationStep;
import swag.md_elements.Fact;
import swag.md_elements.MDElement;
import swag.md_elements.MDSchema;
import swag.predicates.IPredicateGraph;
import swag.predicates.IPredicateNode;
import swag.predicates.LiteralCondition;
import swag.predicates.LiteralConditionType;
import swag.predicates.PredicateInstance;
import swag.predicates.PredicateVar;

/**
 * RDF specific implementation to retrieve main analysis graph objects from RDF
 * files.
 * 
 * @author swag
 */
public class AnalysisGraphRepRDF implements IAnalysisGraphRep {

    private static final Logger logger = Logger.getLogger(AnalysisGraphRepRDF.class);

    private MDSchema mdSchema;
    private OWlConnection owlConnection;
    private MDSchemaRepInterface mdInterface;
    private IPredicateRep predRep;
    private IPredicateGraph predicatesGraph;
    private MeasToASRepInterface mesToASRepInterface;
    private IDimsReader dimToASRepInterafce;

    /**
     * Creates a new {@code AnalysisGraphRepRDF} instance.
     * 
     * @param owlConnection
     *            the owl connection
     * @param mdInterface
     *            the access interface to the MD schema
     * @param mesToASRepInterface
     *            measures access interface
     * @param dimToASRepInterafce
     *            dims access interface
     * @param graph
     *            actual underlying MD schema
     * @param predRep
     *            swag.predicates access interface
     * @param predicateGraph
     *            swag.predicates graph
     */
    public AnalysisGraphRepRDF(OWlConnection owlConnection, MDSchemaRepInterface mdInterface,
	    MeasToASRepInterface mesToASRepInterface, IDimsReader dimToASRepInterafce, MDSchema graph,
	    IPredicateRep predRep, IPredicateGraph predicateGraph) {
	super();
	this.owlConnection = owlConnection;
	this.mdInterface = mdInterface;
	this.mesToASRepInterface = mesToASRepInterface;
	this.dimToASRepInterafce = dimToASRepInterafce;
	this.mdSchema = graph;
	this.predRep = predRep;
	this.predicatesGraph = predicateGraph;
    }

    @Override
    public DefinedAGPredicates getDefinedAGPredicates(IPredicateGraph predicateGraph) {
	DefinedAGPredicates defPreds = new DefinedAGPredicates();
	ExtendedIterator<Individual> predicates = owlConnection.getModel().listIndividuals(owlConnection.getModel()
		.getOntClass(OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.PredicateInstanceClass));

	while (predicates.hasNext()) {
	    Individual ind = predicates.next();
	    PredicateInAG pred = predRep.buildPredicate(ind);
	    if (pred != null) {
		defPreds.addPredicateInAG(pred);
	    }
	}

	return defPreds;
    }

    @Override
    public DefinedAGConditionsTypes getDefinedAGConditionTypes(IPredicateGraph predicateGraph) {
	DefinedAGConditionsTypes defConds = new DefinedAGConditionsTypes();

	for (IPredicateNode p : predicateGraph.getAllNodes()) {
	    if (p instanceof LiteralConditionType) {
		defConds.addConditioType((LiteralConditionType) p);
	    }
	}

	return defConds;
    }

    @Override
    public DefinedAGConditions getDefinedAGConditions(IPredicateGraph predicateGraph) {
	DefinedAGConditions defConds = new DefinedAGConditions();

	for (IPredicateNode p : predicateGraph.getAllNodes()) {
	    if (p instanceof LiteralCondition) {
		defConds.addCondition((LiteralCondition) p);
	    }
	}

	return defConds;
    }

    @Override
    public List<String> getAllAvailableAnalysisSituationsURIs(String agName) {
	List<String> allAS = new ArrayList<String>();
	OntClass agClass = owlConnection.getModel()
		.getOntClass(OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.ANALYSIS_GRAPH);
	ExtendedIterator<? extends OntResource> itr = agClass.listInstances();
	Individual agInd = null;

	while (itr.hasNext()) {
	    agInd = itr.next().asIndividual();
	    break;
	}

	if (agInd != null) {
	    NodeIterator asItr = agInd.listPropertyValues(owlConnection.getModel().getOntProperty(
		    OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.HAS_ANALYSIS_SITUATIO_E));

	    while (asItr.hasNext()) {
		try {
		    allAS.add(asItr.next().as(Individual.class).getURI());
		} catch (Exception ex) {
		    logger.error("Error reading analysis situation name " + ex);
		}
	    }
	}
	return allAS;
    }

    @Override
    public List<String> getAllAvailableNavigationStepsNames() {
	List<String> allNV = new ArrayList<String>();
	OntClass oc = owlConnection.getModel()
		.getOntClass(OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.navigationStep);

	for (ExtendedIterator<? extends OntResource> i = oc.listInstances(); i.hasNext();) {
	    OntResource or = i.next();
	    allNV.add(or.getURI());
	}

	return allNV;
    }

    /**
     * PRIVATE because a part of its functionality is built while getting the
     * analysis situation which is a source or a target for it given navigation
     * step name, this function scans the OWL file and constructs the navigation
     * step
     * 
     * @param uri
     *            the name of the navigation step
     * @return the navigation step
     */
    private NavigationStep getNavigationStepByURIWithoutOperators(String uri, AnalysisSituation src,
	    AnalysisSituation des) {

	logger.info("Reading navigaiton step " + uri);
	NavigationStep nv = new NavigationStep();
	Individual ind0 = this.owlConnection.getModel().getIndividual(uri);

	try {
	    String nvSummary = ind0.getComment("en");
	    nv.setSummary(nvSummary);
	} catch (Exception ex) {
	    nv.setSummary("");
	}

	nv.setName(uri);
	nv.setAbbName(ind0.getLocalName());
	nv.setLabel(ind0.getLabel("en"));

	return nv;
    }

    @Override
    public AnalysisSituation getAnalysisSituationByURI(String uri, int getType) throws Exception {

	AnalysisSituation as = new AnalysisSituation();
	as.setURI(uri);
	Individual ind0 = this.owlConnection.getModel().getIndividual(uri);
	as.setName(ind0.getLocalName());
	as.setLabel(ind0.getLabel("en"));

	if (ind0.hasProperty(RDF.type, this.owlConnection.getModel()
		.getProperty(OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.COMPARATIVE_AS))) {

	} else {
	    as.setAsType(AnalysisSituationType.NON_COMPARATIVE);
	    getNormalAnalysisSituation(uri, getType, as, ind0);
	}
	return as;
    }

    private AnalysisSituation getNormalAnalysisSituation(String uri, int getType, AnalysisSituation as, Individual ind0)
	    throws Exception {

	logger.info("Reading analysis situation " + as.getName());
	as.setSummary(ind0.getComment("en"));

	try {
	    Individual ind11 = this.owlConnection.getModel().getIndividual(uri);
	    NodeIterator setsItr = ind11.listPropertyValues(this.owlConnection.getModel()
		    .getProperty(OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.HAS_SET));

	    while (setsItr.hasNext()) {
		Individual indivSet = setsItr.next().as(Individual.class);
		ISetOfComparison set = new SetOfComparison(indivSet.getLocalName(), indivSet.getURI(),
			Objects.toString(indivSet.getComment("en"), ""));
		as.addToSets(set);
	    }
	    
	    RDFNode dataSetNode = ind11.getPropertyValue(this.owlConnection.getModel()
			    .getObjectProperty(OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.DATA_SET));
	    
	    if(dataSetNode!=null){
	    	Individual ind = dataSetNode.as(Individual.class);
	    	as.setDataSet(ind.getURI());
	    }

	    NodeIterator nodeItr11 = ind11.listPropertyValues(this.owlConnection.getModel()
		    .getObjectProperty(OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.FACT_CLASS_E));

	    // getting fact
	    Fact f = new Fact();
	    while (nodeItr11.hasNext()) {
		Individual tempIndMeas = nodeItr11.next().as(Individual.class);
		f = (Fact) mdSchema.getNode(tempIndMeas.getURI());
	    }
	    as.setFact(f);

	    // Getting dimensions
	    Individual ind = this.owlConnection.getModel().getIndividual(uri);
	    dimToASRepInterafce.readDims(uri, as.getFact().getURI(), as);

	    for (IDimensionQualification dimToAS : as.getDimensionsToAnalysisSituation()) {
		if (dimToAS.getGanularity() != null) {
		    dimToAS.getGanularity().addToAnalysisSituationVariables(as);
		}
		if (dimToAS.getDiceLevel() != null) {
		    dimToAS.getDiceLevel().addToAnalysisSituationOrNavigationStepVariables(as);
		}
		if (dimToAS.getDiceNode() != null) {
		    dimToAS.getDiceNode().addToAnalysisSituationOrNavigationStepVariables(as);
		}
		for (ISliceSinglePosition<IDimensionQualification> sc : dimToAS.getSliceConditions()) {
		    sc.addToAnalysisSituationOrNavigationStepVariables(as);
		}
	    }

	    Collections.sort(as.getDimensionsToAnalysisSituation(), new DimToASComparator());
	    List<PredicateInASMultiple> slices = handlePredicateSlices(ind, as);
	    as.setMultipleSlices(slices);

	    for (PredicateInASMultiple slice : slices) {
		slice.addToAnalysisSituationOrNavigationStepVariables(as);
	    }

	    logger.info("Reading dimension summarizability options...");
	    as.setDimConfigs(DataHandlerUtils.readDimConfigs(ind, owlConnection, mdSchema));

	    logger.info(" Reading measures of analysis situation " + as.getName());
	    // getting Measures and summarizability configurations
	    readResultMeasures(ind, as);
	    // Treating measure conditions/result filters
	    logger.info(" Reading base measures conditions of analysis situation " + as.getName());
	    getBaseMsrConditions(as, ind);
	    logger.info(" Reading result filters of analysis situation " + as.getName());
	    getResultFilters(as, ind);

	    // not called form nav step
	    if (getType == 0) {
		// Getting navigations
		getNavigationsOfAS(ind0, as);
	    }

	    // Processing variables
	    as.addVariablesToInitialVariables();

	    return as;
	} catch (Exception ex) {
	    logger.error("Exception getting analysis situation " + uri, ex);
	    throw ex;
	}
    }

    private void getNavigationsOfAS(Individual ind0, AnalysisSituation as) {

	// getting and setting navigations of the analysis situation
	Resource s;
	StmtIterator stmts = this.owlConnection.getModel().listStatements(null, this.owlConnection.getModel()
		.getObjectProperty(OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.source), ind0);

	while (stmts.hasNext()) {
	    s = stmts.next().getSubject();
	    if (s.as(Individual.class).hasProperty(RDF.type, this.owlConnection.getModel()
		    .getOntClass(OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.navigationStep))) {
		NavigationStep nv = this.getNavigationStepByURIWithoutOperators(s.getURI(), as, null);
		nv.setSource(as);
		as.getOutNavigations().add(nv);
	    }
	}

	stmts = this.owlConnection.getModel().listStatements(null, this.owlConnection.getModel()
		.getObjectProperty(OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.target), ind0);
	while (stmts.hasNext()) {
	    s = stmts.next().getSubject();
	    if (s.as(Individual.class).hasProperty(RDF.type, this.owlConnection.getModel()
		    .getOntClass(OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.navigationStep))) {
		NavigationStep nv = this.getNavigationStepByURIWithoutOperators(s.getURI(), null, as);
		nv.setTarget(as);
		as.getInNavigations().add(nv);
	    }
	}
    }

    /**
     * 
     * Reads all the measures that are part of the result of the analysis
     * situation. Furthermore, reads the summarizability configurations of these
     * measures.
     * 
     * @param ind
     * @param as
     */
    public void readResultMeasures(Individual ind, AnalysisSituation as) {

	// Reading measures
	NodeIterator nodeItrMsrsToShow = ind.listPropertyValues(this.owlConnection.getModel()
		.getObjectProperty(OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.MEASURE_E));

	List<String> msrsToShowUris = new ArrayList<>();

	while (nodeItrMsrsToShow.hasNext()) {
	    msrsToShowUris.add(nodeItrMsrsToShow.next().as(Individual.class).getURI());
	}

	for (String msr : msrsToShowUris) {
	    logger.info("Reading measure " + msr);
	    MeasureAggregatedInAS msrInAS = MeasureFactory.createResultMeasureFromURI(mdSchema, as, msr);
	    if (msrInAS != null) {
		as.AddResultMeasures(msrInAS);
	    }
	}

	// Reading configurations
	logger.info("Reading measure summarizability options...");
	as.setMsrConfigs(DataHandlerUtils.readMSrConfigs(ind, owlConnection, mdSchema));

    }

    /**
     * 
     * Reads base measure conditions of the analysi situation
     * 
     * @param as
     *            analysis situation
     * @param ind
     *            the analysis situation individual
     * @return a list of base measure slice conditions
     * 
     * @throws Exception
     */
    protected List<ISliceSinglePosition<AnalysisSituationToBaseMeasureCondition>> getBaseMsrConditions(
	    AnalysisSituation as, Individual ind) throws Exception {

	logger.info("Reading base measure conditoins.");

	List<ISliceSinglePosition<AnalysisSituationToBaseMeasureCondition>> conds = new ArrayList<>();
	NodeIterator nodeItr = ind.listPropertyValues(this.owlConnection.getModel()
		.getObjectProperty(OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.MEASURE_CONDITION_E));

	while (nodeItr.hasNext()) {
	    Individual ind1 = nodeItr.next().as(Individual.class);
	    ISliceSinglePosition<AnalysisSituationToBaseMeasureCondition> cond = DataHandlerUtils
		    .readMeasureCondsAndFiltersSliceConditoin(owlConnection, mdSchema, ind1,
			    as.getBaseMsrsCondsObject(), ind1.getURI(), predicatesGraph);

	    if (cond != null) {
		as.AddResultBaseFilter(cond);
		cond.addToAnalysisSituationOrNavigationStepVariables(as);
	    }
	}

	return conds;
    }

    /**
     * Reads result filters of the analysis situation.
     * 
     * @param as
     *            analysis situation
     * @param ind
     *            the analysis situation individual
     * @return a list of base measure slice conditions
     * 
     * @throws Exception
     */
    protected List<ISliceSinglePosition<AnalysisSituationToBaseMeasureCondition>> getResultFilters(AnalysisSituation as,
	    Individual ind) throws Exception {

	logger.info("Reading result filtres.");

	List<ISliceSinglePosition<AnalysisSituationToBaseMeasureCondition>> conds = new ArrayList<>();
	NodeIterator nodeItr = ind.listPropertyValues(this.owlConnection.getModel()
		.getObjectProperty(OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.RESULT_FILTER_E));

	while (nodeItr.hasNext()) {
	    Individual ind1 = nodeItr.next().as(Individual.class);
	    ISliceSinglePosition<AnalysisSituationToResultFilters> cond = DataHandlerUtils
		    .readMeasureCondsAndFiltersSliceConditoin(owlConnection, mdSchema, ind1,
			    as.getResultFiltersObject(), ind1.getURI(), predicatesGraph);

	    if (cond != null) {
		as.AddResultFilter(cond);
		cond.addToAnalysisSituationOrNavigationStepVariables(as);
	    }
	}

	return conds;
    }

    /**
     * Reads multiple swag.predicates and assigns them to the current analysis
     * situation.
     * 
     * @param ind
     *            the individual to read slices of
     * @param dimToAS
     *            the dimension to AS object being built
     * @return a list of multiple swag.predicates
     * 
     * @throws Exception
     */
    private List<PredicateInASMultiple> handlePredicateSlices(Individual ind, AnalysisSituation as) throws Exception {
	// slice specification
	org.apache.jena.rdf.model.StmtIterator it2;

	if (!as.isComparative()) {
	    it2 = ind.listProperties(this.owlConnection.getModel().getProperty(
		    OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.MULTIPLE_SLICE_SPECIFICATION_PROP));
	} else {
	    it2 = ind.listProperties(
		    this.owlConnection.getModel().getProperty(OWLConnectionFactory.getAGNamespace(owlConnection)
			    + Constants.COMP_MULTIPLE_SLICE_SPECIFICATION_PROP));
	}
	List<PredicateInASMultiple> slices = new ArrayList<>();

	while (it2.hasNext()) {
	    Statement stmt = it2.nextStatement();
	    RDFNode sliceSpecificationIndivNode = stmt.getObject();

	    if (sliceSpecificationIndivNode != null) {
		Individual granIndiv = sliceSpecificationIndivNode.as(Individual.class);
		ISetOfComparison set = NoneSet.getNoneSet();
		String setUri = owlConnection.getPropertyValueEncAsString(granIndiv, this.owlConnection.getModel()
			.getProperty(OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.ON_SET));

		if (setUri != null) {
		    set = as.getSetByURI(setUri);
		}
		PredicateInASMultiple pred = null;

		if (sliceSpecificationIndivNode.as(Individual.class).hasProperty(RDF.type, this.owlConnection.getModel()
			.getOntClass(OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.variable))) {

		    pred = new PredicateInASMultiple(new swag.analysis_graphs.execution_engine.Signature<AnalysisSituation>(as,
			    ItemInAnalysisSituationType.SlicePredicate, VariableState.VARIABLE,
			    sliceSpecificationIndivNode.as(Individual.class).getLocalName(), null));
		} else {
		    RDFNode basePredicateNode = this.owlConnection.getPropertyValueEnc(
			    sliceSpecificationIndivNode.as(Individual.class),
			    this.owlConnection.getModel().getObjectProperty(
				    OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.ON_PREDICATE));

		    if (basePredicateNode != null && basePredicateNode.as(Individual.class).hasProperty(RDF.type,
			    owlConnection.getClassByName("http://www.amcis2021.com/swag/pr#PredicateInstance"))) {
			Individual sliceCondIndiv = sliceSpecificationIndivNode.as(Individual.class);
			Individual predicateInd = owlConnection.getPropertyValueEncAsIndividual(sliceCondIndiv,
				this.owlConnection.getModel().getProperty(
					OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.ON_PREDICATE));

			if (predicateInd == null) {
			    throw new Exception("Cannot find predicate instance " + sliceCondIndiv);
			}

			PredicateInstance inst = (PredicateInstance) predicatesGraph.getNode(predicateInd.getURI());

			if (inst == null) {
			    throw new Exception("Cannot find predicate instance " + predicateInd);
			}
			org.apache.jena.rdf.model.StmtIterator mappingsItr = sliceCondIndiv
				.listProperties(this.owlConnection.getModel()
					.getProperty(OWLConnectionFactory.getAGNamespace(owlConnection)
						+ Constants.MULTIPLE_HAS_VAR_TO_ELEMENT_MAPPING));
			Set<PredicateVariableToMDElementMapping> mappings = new HashSet<>();

			while (mappingsItr.hasNext()) {
			    Statement mappingStmt = mappingsItr.nextStatement();
			    RDFNode varToElemNode = mappingStmt.getObject();

			    if (varToElemNode != null) {
				Individual varToElemInd = varToElemNode.as(Individual.class);
				Individual mdElemInd = owlConnection.getPropertyValueEncAsIndividual(varToElemInd,
					this.owlConnection.getModel()
						.getProperty(OWLConnectionFactory.getAGNamespace(owlConnection)
							+ Constants.MULTIPLE_HAS_MD_ELEM));

				if (true/*
					 * mdElemInd.getURI().equals(ind.getURI(
					 * ))
					 */) {
				    Individual varInd = owlConnection.getPropertyValueEncAsIndividual(varToElemInd,
					    this.owlConnection.getModel()
						    .getProperty(OWLConnectionFactory.getAGNamespace(owlConnection)
							    + Constants.HAS_VAR));

				    String connectOver = owlConnection.getPropertyValueEncAsString(varToElemInd,
					    this.owlConnection.getModel()
						    .getProperty(OWLConnectionFactory.getAGNamespace(owlConnection)
							    + Constants.HAS_CONNECT_OVER));

				    if (mdElemInd == null || varInd == null) {
					throw new Exception("Cannot find MD Element " + varToElemInd);
				    }

				    MDElement elem = readComplexMDElement(mdElemInd, mdSchema);
				    PredicateVar var = inst.getInstanceOf().getVariableByURI(varInd.getURI());

				    if (elem != null && var != null) {
					mappings.add(new PredicateVariableToMDElementMapping(var, elem,
						connectOver != null ? QueryFactory.create(connectOver) : null));
				    }
				}
			    }
			}

			pred = new PredicateInASMultiple(mappings, inst.getUri(), inst,
				new Signature<AnalysisSituation>(as, ItemInAnalysisSituationType.SlicePredicate,
					VariableState.NON_VARIABLE, "", null));
		    }
		}
		slices.add(pred);
	    }
	}
	return slices;
    }

    // TODO
    /**
     * @param ind
     * @param dimToAS
     * @return
     * @throws Exception
     */
    private List<IMultipleSliceSpecification> handlePredicateSlicesNoPosition(Individual ind, AnalysisSituation as)
	    throws Exception {
	// slice specification

	org.apache.jena.rdf.model.StmtIterator it2;

	if (!as.isComparative()) {
	    it2 = ind.listProperties(this.owlConnection.getModel().getProperty(
		    OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.MULTIPLE_SLICE_SPECIFICATION_PROP));
	} else {

	    it2 = ind.listProperties(
		    this.owlConnection.getModel().getProperty(OWLConnectionFactory.getAGNamespace(owlConnection)
			    + Constants.COMP_MULTIPLE_SLICE_SPECIFICATION_PROP));
	}
	List<IMultipleSliceSpecification> slices = new ArrayList<>();

	while (it2.hasNext()) {
	    Statement stmt = it2.nextStatement();
	    RDFNode sliceSpecificationIndivNode = stmt.getObject();

	    if (sliceSpecificationIndivNode != null) {

		Individual granIndiv = sliceSpecificationIndivNode.as(Individual.class);

		ISetOfComparison set = NoneSet.getNoneSet();

		String setUri = owlConnection.getPropertyValueEncAsString(granIndiv, this.owlConnection.getModel()
			.getProperty(OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.ON_SET));

		if (setUri != null) {
		    set = as.getSetByURI(setUri);
		}

		MultipleSliceSpecification sliceSpec = new MultipleSliceSpecification(set);

		if (sliceSpecificationIndivNode.as(Individual.class).hasProperty(RDF.type, this.owlConnection.getModel()
			.getOntClass(OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.variable))) {

		    PredicateInASMultiple pred = new PredicateInASMultiple(new MultiSliceSignature(as,
			    ItemInAnalysisSituationType.SlicePredicate, VariableState.VARIABLE,
			    sliceSpecificationIndivNode.as(Individual.class).getLocalName(), sliceSpec));

		    sliceSpec.setPredicate(pred);
		    sliceSpec.setPosition(as.getFact());

		} else {

		    RDFNode basePredicateNode = this.owlConnection.getPropertyValueEnc(
			    sliceSpecificationIndivNode.as(Individual.class),
			    this.owlConnection.getModel().getObjectProperty(
				    OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.ON_PREDICATE));

		    if (basePredicateNode != null && basePredicateNode.as(Individual.class).hasProperty(RDF.type,
			    owlConnection.getClassByName("http://www.amcis2021.com/swag/pr#PredicateInstance"))) {

			Individual sliceCondIndiv = sliceSpecificationIndivNode.as(Individual.class);

			Individual predicateInd = owlConnection.getPropertyValueEncAsIndividual(sliceCondIndiv,
				this.owlConnection.getModel().getProperty(
					OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.ON_PREDICATE));

			if (predicateInd == null) {
			    throw new Exception("Cannot find predicate instance " + sliceCondIndiv);
			}

			PredicateInstance inst = (PredicateInstance) predicatesGraph.getNode(predicateInd.getURI());

			if (inst == null) {
			    throw new Exception("Cannot find predicate instance " + predicateInd);
			}

			org.apache.jena.rdf.model.StmtIterator mappingsItr = sliceCondIndiv
				.listProperties(this.owlConnection.getModel()
					.getProperty(OWLConnectionFactory.getAGNamespace(owlConnection)
						+ Constants.MULTIPLE_HAS_VAR_TO_ELEMENT_MAPPING));

			Set<PredicateVariableToMDElementMapping> mappings = new HashSet<>();

			while (mappingsItr.hasNext()) {

			    Statement mappingStmt = mappingsItr.nextStatement();
			    RDFNode varToElemNode = mappingStmt.getObject();

			    if (varToElemNode != null) {

				Individual varToElemInd = varToElemNode.as(Individual.class);

				Individual mdElemInd = owlConnection.getPropertyValueEncAsIndividual(varToElemInd,
					this.owlConnection.getModel()
						.getProperty(OWLConnectionFactory.getAGNamespace(owlConnection)
							+ Constants.MULTIPLE_HAS_MD_ELEM));

				if (true/*
					 * mdElemInd.getURI().equals(ind.getURI(
					 * ))
					 */) {

				    Individual varInd = owlConnection.getPropertyValueEncAsIndividual(varToElemInd,
					    this.owlConnection.getModel()
						    .getProperty(OWLConnectionFactory.getAGNamespace(owlConnection)
							    + Constants.HAS_VAR));

				    String connectOver = owlConnection.getPropertyValueEncAsString(varToElemInd,
					    this.owlConnection.getModel()
						    .getProperty(OWLConnectionFactory.getAGNamespace(owlConnection)
							    + Constants.HAS_CONNECT_OVER));

				    if (mdElemInd == null || varInd == null) {
					throw new Exception("Cannot find MD Element " + varToElemInd);
				    }

				    MDElement elem = readComplexMDElement(mdElemInd, mdSchema);

				    PredicateVar var = inst.getInstanceOf().getVariableByURI(varInd.getURI());

				    if (elem != null && var != null) {

					mappings.add(new PredicateVariableToMDElementMapping(var, elem,
						connectOver != null ? QueryFactory.create(connectOver) : null));
				    }

				}
			    }
			}

			PredicateInASMultiple pred = new PredicateInASMultiple(mappings, inst.getUri(), inst,
				new MultiSliceSignature(as, ItemInAnalysisSituationType.SlicePredicate,
					VariableState.NON_VARIABLE, "", sliceSpec));

			sliceSpec.setSlicePositions(pred.getElems());
			sliceSpec.setPredicate(pred);

		    }
		}
		slices.add(sliceSpec);
	    }
	}
	return slices;
    }

    /**
     * Reads a complex MD element, i.e., it can be a level in a hierarchy in a
     * dimension, or an attribute in a level in a hierarchy in a dimension.
     * 
     * @param mdElemInd
     *            the individual of the MD element at hand to read.
     * @param schema
     *            the underlying MD schema
     * 
     * @return an MD element
     * @throws Exception
     *             if the element cannot be read or invalid or some error occurs
     */
    public MDElement readComplexMDElement(Individual mdElemInd, MDSchema schema) throws Exception {

	Individual onLevelInd = owlConnection.getPropertyValueEncAsIndividual(mdElemInd, this.owlConnection.getModel()
		.getProperty(OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.ELEM_ON_LEVEL));
	Individual onAttributeInd = owlConnection.getPropertyValueEncAsIndividual(mdElemInd,
		this.owlConnection.getModel()
			.getProperty(OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.ELEM_ON_ATTRIBUTE));
	Individual onHierInDimInd = owlConnection.getPropertyValueEncAsIndividual(mdElemInd,
		this.owlConnection.getModel().getProperty(
			OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.ELEM_ON_HIER_IN_DIM));

	if (onLevelInd == null && onAttributeInd == null || onHierInDimInd == null) {
	    throw new Exception("MD element is badly configured.");
	}

	Individual onHierInd = owlConnection.getPropertyValueEncAsIndividual(onHierInDimInd, this.owlConnection
		.getModel().getProperty(OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.ELEM_ON_DIM));
	Individual onDimInd = owlConnection.getPropertyValueEncAsIndividual(onHierInDimInd, this.owlConnection
		.getModel().getProperty(OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.ELEM_ON_HIER));

	if (onHierInd == null || onDimInd == null) {
	    throw new Exception("Dimensoin or hierarchy of element is missing.");
	}

	MDElement elem = mdSchema.getNode(mdSchema.getIdentifyingNameFromUriAndDimensionAndHier(mdElemInd.getURI(),
		onDimInd.getURI(), onHierInd.getURI()));

	return elem;
    }

    public MDSchemaRepInterface getMdInterface() {
	return mdInterface;
    }

    public void setMdInterface(MDSchemaRepInterface mdInterface) {
	this.mdInterface = mdInterface;
    }

    public MeasToASRepInterface getMesToASRepInterface() {
	return mesToASRepInterface;
    }

    public void setMesToASRepInterface(MeasToASRepInterface mesToASRepInterface) {
	this.mesToASRepInterface = mesToASRepInterface;
    }

    public IDimsReader getDimToASRepInterafce() {
	return dimToASRepInterafce;
    }

    public void setDimToASRepInterafce(IDimsReader dimToASRepInterafce) {
	this.dimToASRepInterafce = dimToASRepInterafce;
    }

    public OWlConnection getOwlConnection() {
	return owlConnection;
    }

    public void setOwlConnection(OWlConnection owlConnection) {
	this.owlConnection = owlConnection;
    }

    public MDSchema getGraph() {
	return mdSchema;
    }

    public void setGraph(MDSchema graph) {
	this.mdSchema = graph;
    }

    public IPredicateGraph getPredicatesGraph() {
	return predicatesGraph;
    }

    public void setPredicatesGraph(IPredicateGraph predicatesGraph) {
	this.predicatesGraph = predicatesGraph;
    }
}
