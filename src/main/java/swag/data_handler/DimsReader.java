package swag.data_handler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.jena.ontology.Individual;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.apache.log4j.Logger;

import swag.analysis_graphs.execution_engine.Signature;
import swag.analysis_graphs.execution_engine.analysis_situations.AnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.DiceSpecificaitonImpl;
import swag.analysis_graphs.execution_engine.analysis_situations.DimensionToAnalysisSituationImpl;
import swag.analysis_graphs.execution_engine.analysis_situations.GranualritySpecificationImpl;
import swag.analysis_graphs.execution_engine.analysis_situations.IDiceSpecification;
import swag.analysis_graphs.execution_engine.analysis_situations.IDimensionQualification;
import swag.analysis_graphs.execution_engine.analysis_situations.IGranularitySpecification;
import swag.analysis_graphs.execution_engine.analysis_situations.ISliceSinglePosition;
import swag.analysis_graphs.execution_engine.analysis_situations.ItemInAnalysisSituationType;
import swag.analysis_graphs.execution_engine.analysis_situations.LevelInAnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.SliceCondition;
import swag.analysis_graphs.execution_engine.analysis_situations.SliceSet;
import swag.analysis_graphs.execution_engine.analysis_situations.VariableState;
import swag.md_elements.DefaultHierarchy;
import swag.md_elements.Dimension;
import swag.md_elements.HierarchyInDimension;
import swag.md_elements.Level;
import swag.md_elements.MDSchema;
import swag.md_elements.QB4OHierarchy;
import swag.predicates.IPredicateGraph;
import swag.sparql_builder.Configuration;

/**
 * 
 * Contains methods to read dimensions of an analysis situation
 * 
 * @author swag
 *
 */
public class DimsReader implements IDimsReader {

    private static final Logger logger = Logger.getLogger(DimsReader.class);

    private OWlConnection owlConnection;
    private MDSchemaRepInterface mdInterface;
    private MappingRepInterface mappRepInterface;
    private MDSchema mdSchema;
    private IPredicateGraph predicateGraph;

    public DimsReader(OWlConnection owlConnection, MDSchemaRepInterface mdInterface,
	    MappingRepInterface mappRepInterface, MDSchema graph, IPredicateGraph predicateGraph) {
	super();
	this.owlConnection = owlConnection;
	this.mdInterface = mdInterface;
	this.mappRepInterface = mappRepInterface;
	this.mdSchema = graph;
	this.predicateGraph = predicateGraph;
    }

    @Override
    public void readDims(String uri, String factURI, AnalysisSituation as) throws Exception {

	logger.info("Reading dimensoins of analysis situation " + as.getName());

	Individual ind = owlConnection.getModel().getIndividual(uri);
	Map<HierarchyInDimension, IDimensionQualification> quals = new HashMap<>();

	Set<HierarchyInDimension> hierss = new HashSet<>();
	if (Configuration.getInstance().is("singleHierarchy")) {
	    hierss.addAll(mdSchema.getAllNodesOfType(HierarchyInDimension.class).stream()
		    .filter(hier -> hier.getHier().getIdentifyingName()
			    .equals(DefaultHierarchy.getDefaultHierarchy().getIdentifyingName()))
		    .collect(Collectors.toSet()));
	} else {
	    hierss.addAll(mdSchema.getAllNodesOfType(HierarchyInDimension.class));
	}

	for (HierarchyInDimension hd : hierss) {

	    IDimensionQualification dimToAS = new DimensionToAnalysisSituationImpl();
	    dimToAS.setD(hd.getDim());
	    dimToAS.setHierarchy(hd.getHier());
	    quals.put(hd, dimToAS);
	    as.getDimensionsToAnalysisSituation().add(dimToAS);
	}

	try {
	    handleGranularities(ind, as, quals);
	    handleDices(ind, as, quals);
	    handleNoPositionSlices(ind, as, quals);
	    // handleConfigs(ind, as, quals);
	} catch (Exception ex) {
	    throw (ex);
	}
    }

    /**
     * @param ind
     * @param as
     * @param quals
     */
    private void handleGranularities(Individual ind, AnalysisSituation as,
	    Map<HierarchyInDimension, IDimensionQualification> quals) {

	org.apache.jena.rdf.model.StmtIterator it = ind.listProperties(this.owlConnection.getModel()
		.getProperty(OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.granularityE));

	while (it.hasNext()) {
	    Statement stmt = it.nextStatement();
	    RDFNode node1 = stmt.getObject();

	    if (node1 != null) {
		Individual granIndiv = node1.as(Individual.class);
		String dimensinoURI = DataHandlerUtils.getDimensoin(owlConnection, granIndiv);
		String hierarchyURI = DataHandlerUtils.getHierarchy(owlConnection, granIndiv);

		if (dimensinoURI == null || hierarchyURI == null)  {
		    logger.warn("Cannot read granularity specification on dimensoin " + dimensinoURI + " / hierarchy"
			    + hierarchyURI + ". Skipping...");
		    continue;
		}
		
		HierarchyInDimension hd = new HierarchyInDimension((QB4OHierarchy) mdSchema.getNode(hierarchyURI),
			(Dimension) mdSchema.getNode(dimensinoURI));
		IDimensionQualification q = quals.get(hd);

		if (q == null) {
		    logger.warn("Cannot read granularity specification on dimensoin " + dimensinoURI + " / hierarchy"
			    + hierarchyURI + ". Skipping...");
		    continue;
		}

		String levelURI = DataHandlerUtils.getLevel(owlConnection, granIndiv);
		String granularityLevelURI = mdSchema.getIdentifyingNameFromUriAndDimensionAndHier(levelURI,
			dimensinoURI, hierarchyURI);

		if (granularityLevelURI == null) {
		    logger.warn("Cannot read granularity specification on dimensoin " + dimensinoURI + " / hierarchy"
			    + hierarchyURI + ". Skipping...");
		    continue;
		}

		Individual granLvlInd = DataHandlerUtils.getLevelInd(owlConnection, granIndiv);

		Individual binding = owlConnection.getPropertyValueEncAsIndividual(granLvlInd,
				owlConnection.getModel().getProperty(
						OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.BINDING));

		Individual parameter = owlConnection.getPropertyValueEncAsIndividual(granLvlInd,
				owlConnection.getModel().getProperty(
						OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.PARAMETER));

			if(MDElementParameterCreator.hasTypeAGOntology(granLvlInd, Constants.LEVEL_PAIR, owlConnection)) {

				if (binding != null) {

				} else {
					IGranularitySpecification gr = new GranualritySpecificationImpl();
					LevelInAnalysisSituation level = new LevelInAnalysisSituation(
							new Signature<IDimensionQualification>(q, ItemInAnalysisSituationType.GranularityLevel,
									VariableState.VARIABLE, granLvlInd.getLocalName(), null));
					gr = new GranualritySpecificationImpl(level);
					q.getGranularities().add(gr);
				}
			}else{
				IGranularitySpecification gr = new GranualritySpecificationImpl();
				LevelInAnalysisSituation level = new LevelInAnalysisSituation(
						(Level) mdSchema.getNode(granularityLevelURI),
						new Signature<IDimensionQualification>(q, ItemInAnalysisSituationType.GranularityLevel,
								VariableState.NON_VARIABLE, "", null));
				gr = new GranualritySpecificationImpl(level);
				q.getGranularities().add(gr);
			}
	    }
	}
    }
    
    boolean isValidSpec(String dimensinoURI, String hierarchyURI){
    	
    	if (dimensinoURI != null && Configuration.getInstance().is("singleHierarchy")) {
    		return true;
    	}
    	if (dimensinoURI != null && hierarchyURI != null) {
    		return true;
    	}
    	return false;
    }

    /**
     * @param ind
     * @param dimToAS
     * @return
     */
    private void handleDices(Individual ind, AnalysisSituation as,
	    Map<HierarchyInDimension, IDimensionQualification> quals) {

	org.apache.jena.rdf.model.StmtIterator it1 = ind.listProperties(this.owlConnection.getModel()
		.getProperty(OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.diceSpecificationE));

	while (it1.hasNext()) {
	    Statement stmt = it1.nextStatement();
	    RDFNode diceSpecificationIndivNode = stmt.getObject();
	    if (diceSpecificationIndivNode != null) {

		String dimensinoURI = DataHandlerUtils.getDimensoin(owlConnection,
			diceSpecificationIndivNode.as(Individual.class));
		String hierarchyURI = DataHandlerUtils.getHierarchy(owlConnection,
			diceSpecificationIndivNode.as(Individual.class));

		if (dimensinoURI == null || hierarchyURI == null) {
		    logger.warn("Cannot read dice specification on dimensoin " + dimensinoURI + " / hierarchy"
			    + hierarchyURI + ". Skipping...");
		    continue;
		}

		HierarchyInDimension hd = new HierarchyInDimension((QB4OHierarchy) mdSchema.getNode(hierarchyURI),
			(Dimension) mdSchema.getNode(dimensinoURI));
		IDimensionQualification q = quals.get(hd);

		if (q == null) {
		    logger.warn("Cannot read dice specification on dimensoin " + dimensinoURI + " / hierarchy"
			    + hierarchyURI + ". Skipping because dimension or hierarchy is not found.");
		    continue;
		}

		RDFNode diceLevelNode = owlConnection.getPropertyValueEnc(
			diceSpecificationIndivNode.as(Individual.class), owlConnection.getModel().getObjectProperty(
				OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.ON_DICE_LEVEL_E));
		RDFNode diceNodeNode = owlConnection.getPropertyValueEnc(
			diceSpecificationIndivNode.as(Individual.class), owlConnection.getModel().getObjectProperty(
				OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.ON_NODE_E));

		if (diceLevelNode == null || diceNodeNode == null) {
		    logger.warn("Cannot read dice specification on dimensoin " + dimensinoURI + " / hierarchy"
			    + hierarchyURI + ". Skipping because dice level or node is not found.");
		    continue;
		}

		IDiceSpecification diceSpec = new DiceSpecificaitonImpl();
		DataHandlerUtils.createDiceSpecification(owlConnection, dimensinoURI, hierarchyURI, mdSchema,
			diceSpecificationIndivNode, q, diceSpec, diceLevelNode, diceNodeNode);

		if (diceSpec.getPosition() == null || diceSpec.getDiceNodeInAnalysisSituation() == null) {
		    logger.warn("Cannot read dice specification on dimensoin " + dimensinoURI + " / hierarchy"
			    + hierarchyURI + ". Skipping because dice level or node is not found.");
		    diceSpec = null;
		    continue;
		}
		if (diceSpec != null) {
		    q.getDices().add(diceSpec);
		}
	    }
	}
    }

    /**
     * @param ind
     * @param dimToAS
     * @return
     * @throws Exception
     */
    private void handleNoPositionSlices(Individual ind, AnalysisSituation as,
	    Map<HierarchyInDimension, IDimensionQualification> quals) throws Exception {

	org.apache.jena.rdf.model.StmtIterator it2 = ind.listProperties(this.owlConnection.getModel()
		.getProperty(OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.HAS_SLICE_E));

	while (it2.hasNext()) {

	    Statement stmt = it2.nextStatement();
	    RDFNode sliceSpecificationIndivNode = stmt.getObject();
	    Individual nodeInd = sliceSpecificationIndivNode.as(Individual.class);

	    String dimensinoURI = DataHandlerUtils.getDimensoin(owlConnection, nodeInd);
	    String hierarchyURI = DataHandlerUtils.getHierarchy(owlConnection, nodeInd);

	    if (dimensinoURI == null || hierarchyURI == null) {
		logger.warn("Cannot read selection specification on dimensoin " + dimensinoURI + " / hierarchy"
			+ hierarchyURI + ". Skipping...");
		continue;
	    }

	    HierarchyInDimension hd = new HierarchyInDimension((QB4OHierarchy) mdSchema.getNode(hierarchyURI),
		    (Dimension) mdSchema.getNode(dimensinoURI));
	    IDimensionQualification q = quals.get(hd);

	    if (q == null) {
		logger.warn("Cannot read selection specification on dimensoin " + dimensinoURI + " / hierarchy"
			+ hierarchyURI + ". Skipping because dimension or hierarchy is not found.");
		continue;
	    }

	    ISliceSinglePosition<IDimensionQualification> sc;

	    Individual selectionCondNode = owlConnection.getPropertyValueEncAsIndividual(nodeInd,
		    owlConnection.getModel().getObjectProperty(
			    OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.HAS_DIM_PREDICATE_E));

	    if (selectionCondNode == null) {
		logger.warn(
			"Cannot read selection specification " + ind.getLocalName() + " on dimensoin " + dimensinoURI
				+ " / hierarchy" + hierarchyURI + ". Skipping because conditoin node is not found.");
		continue;
	    }

	    sc = DataHandlerUtils.readSliceConditoin(owlConnection, mdSchema, selectionCondNode, q, dimensinoURI,
		    hierarchyURI, predicateGraph);

	    if (sc == null) {
		logger.warn("Cannot read selection specification " + nodeInd + ". On dimensoin " + dimensinoURI
			+ " / hierarchy" + hierarchyURI + ". Skipping because dimension or hierarchy is not found.");
		continue;
	    }

	    if (q.getSliceSet() == null) {
		q.setSliceSet(new SliceSet());
	    }

	    if (sc instanceof SliceCondition) {
		q.getSliceSet().addCondition(sc);
	    } else {
		q.getSliceSet().addCondition(sc);
	    }
	}
    }

    public OWlConnection getOwlConnection() {
	return owlConnection;
    }

    public void setOwlConnection(OWlConnection owlConnection) {
	this.owlConnection = owlConnection;
    }

    public MDSchemaRepInterface getMdInterface() {
	return mdInterface;
    }

    public void setMdInterface(MDSchemaRepInterface mdInterface) {
	this.mdInterface = mdInterface;
    }

    public MappingRepInterface getMappRepInterface() {
	return mappRepInterface;
    }

    public void setMappRepInterface(MappingRepInterface mappRepInterface) {
	this.mappRepInterface = mappRepInterface;
    }
}
