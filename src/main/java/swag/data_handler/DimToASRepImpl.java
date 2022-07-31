package swag.data_handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.jena.ontology.Individual;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.apache.log4j.Logger;

import swag.analysis_graphs.execution_engine.ElementInsufficientSpecificationException;
import swag.analysis_graphs.execution_engine.Signature;
import swag.analysis_graphs.execution_engine.analysis_situations.AnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.DiceSpecificaitonImpl;
import swag.analysis_graphs.execution_engine.analysis_situations.DimensionToAnalysisSituationImpl;
import swag.analysis_graphs.execution_engine.analysis_situations.GranualritySpecificationImpl;
import swag.analysis_graphs.execution_engine.analysis_situations.IDiceSpecification;
import swag.analysis_graphs.execution_engine.analysis_situations.IDimensionQualification;
import swag.analysis_graphs.execution_engine.analysis_situations.IGranularitySpecification;
import swag.analysis_graphs.execution_engine.analysis_situations.ISetOfComparison;
import swag.analysis_graphs.execution_engine.analysis_situations.ISliceSetDim;
import swag.analysis_graphs.execution_engine.analysis_situations.ISliceSinglePosition;
import swag.analysis_graphs.execution_engine.analysis_situations.ItemInAnalysisSituationType;
import swag.analysis_graphs.execution_engine.analysis_situations.LevelInAnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.NoneSet;
import swag.analysis_graphs.execution_engine.analysis_situations.SliceCondition;
import swag.analysis_graphs.execution_engine.analysis_situations.SliceConditionTyped;
import swag.analysis_graphs.execution_engine.analysis_situations.SliceSet;
import swag.analysis_graphs.execution_engine.analysis_situations.VariableState;
import swag.md_elements.Dimension;
import swag.md_elements.HierarchyInDimension;
import swag.md_elements.Level;
import swag.md_elements.MDSchema;
import swag.md_elements.QB4OHierarchy;
import swag.predicates.IPredicateGraph;
import swag.sparql_builder.ASElements.configuration.Configuration;

public class DimToASRepImpl implements IQualificationDimToASRepInterface {

  private static final Logger logger = Logger.getLogger(DimToASRepImpl.class);

  private OWlConnection owlConnection;
  private MDSchemaRepInterface mdInterface;
  private MappingRepInterface mappRepInterface;
  private MDSchema mdSchema;
  private IPredicateGraph predicateGraph;

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

  public DimToASRepImpl(OWlConnection owlConnection, MDSchemaRepInterface mdInterface,
      MappingRepInterface mappRepInterface, MDSchema graph, IPredicateGraph predicateGraph) {
    super();
    this.owlConnection = owlConnection;
    this.mdInterface = mdInterface;
    this.mappRepInterface = mappRepInterface;
    this.mdSchema = graph;
    this.predicateGraph = predicateGraph;
  }



  @Override
  public IDimensionQualification getDimensionToASByURI(String uri, String factURI,
      AnalysisSituation as) throws Exception {

    try {
      IDimensionQualification dimToAS = new DimensionToAnalysisSituationImpl();
      Individual ind = this.owlConnection.getModel().getIndividual(uri);

      String dimensinoURI = DataHandlerUtils.getDimensoin(owlConnection, ind);
      String hierarchyURI = DataHandlerUtils.getHierarchy(owlConnection, ind);

      if (dimensinoURI != null && mdSchema.getNode(dimensinoURI) != null) {
        dimToAS.setD((Dimension) mdSchema.getNode(dimensinoURI));
        if (hierarchyURI == null || mdSchema.getNode(hierarchyURI) == null) {
          hierarchyURI = QB4OHierarchy.getDefaultHierarchy().getURI();
        }
        dimToAS.setHierarchy((QB4OHierarchy) mdSchema.getNode(hierarchyURI));
      } else {
        throw new ElementInsufficientSpecificationException(
            "Cannot find dimension " + dimensinoURI);
      }

      // dimToAS.setAs(as);
      // TODO check when granularity or diceLevel or diceNode are empty
      // and then assign null

      List<IGranularitySpecification> grans =
          handleGranularities(ind, dimToAS, dimensinoURI, hierarchyURI, as);
      // first treating granularity specification
      dimToAS.setGranularity(grans);
      List<IDiceSpecification> dices = handleDices(ind, dimToAS, dimensinoURI, hierarchyURI, as);
      dimToAS.setDices(dices);

      // slice specification
      // List<SliceCondition<IDimensionQualification>> slices =
      // new ArrayList<SliceCondition<IDimensionQualification>>();
      // slices.addAll(handleNoPositionSlices(ind, dimToAS, hierarchyURI, dimensinoURI, as));
      // slices.addAll(handlePredicateSlicesNoPosition(ind, dimToAS, hierarchyURI, as));
      // slices.addAll(handlePredicateSlices(ind, dimToAS, hierarchyURI, as));
      // dimToAS.setSliceSpecificaiotn(slices);

      List<ISliceSetDim> sliceSetDims =
          handleSliceSet(ind, dimToAS, dimensinoURI, hierarchyURI, as);
      if (sliceSetDims.size() > 0) {
        dimToAS.setSliceSet(sliceSetDims.get(0));
      }

      dimToAS.setSliceSets(sliceSetDims);

      // Reading configuration
      Configuration con = new Configuration(handleConfigs(ind, as));
      dimToAS.setConfiguration(con);

      return dimToAS;
    } catch (Exception ex) {
      throw (ex);
    }
  }


  private List<IGranularitySpecification> handleGranularities(Individual ind,
      IDimensionQualification dimToAS, String dimensinoURI, String hierarchyURI,
      AnalysisSituation as) {

    org.apache.jena.rdf.model.StmtIterator it =
        ind.listProperties(this.owlConnection.getModel().getProperty(
            OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.granularity));
    List<IGranularitySpecification> grans = new ArrayList<IGranularitySpecification>();

    while (it.hasNext()) {
      Statement stmt = it.nextStatement();
      RDFNode node1 = stmt.getObject();
      if (node1 != null) {

        String granularityLevelURI = mdSchema.getIdentifyingNameFromUriAndDimensionAndHier(
            node1.as(Individual.class).getURI(), dimensinoURI, hierarchyURI);

        Individual granIndiv = node1.as(Individual.class);

        ISetOfComparison set = NoneSet.getNoneSet();

        String setUri = owlConnection.getPropertyValueEncAsString(granIndiv,
            this.owlConnection.getModel().getProperty(
                OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.ON_SET));

        if (setUri != null) {
          set = as.getSetByURI(setUri);
        }

        if (node1.as(Individual.class).hasProperty(RDF.type,
            this.owlConnection.getModel().getOntClass(
                OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.variable))) {
          IGranularitySpecification gr = new GranualritySpecificationImpl();
          LevelInAnalysisSituation level =
              new LevelInAnalysisSituation(new Signature<IDimensionQualification>(dimToAS,
                  ItemInAnalysisSituationType.GranularityLevel, VariableState.VARIABLE,
                  node1.as(Individual.class).getLocalName(), null));
          gr = new GranualritySpecificationImpl(level, set);
          grans.add(gr);
        } else {
          IGranularitySpecification gr = new GranualritySpecificationImpl();
          LevelInAnalysisSituation level =
              new LevelInAnalysisSituation((Level) mdSchema.getNode(granularityLevelURI),
                  new Signature<IDimensionQualification>(dimToAS,
                      ItemInAnalysisSituationType.GranularityLevel, VariableState.NON_VARIABLE, "",
                      null));
          gr = new GranualritySpecificationImpl(level, set);

          grans.add(gr);
          break;
        }
      }
    }
    return grans;
  }

  /**
   * @param ind
   * @param dimToAS
   * @return
   */
  private List<IDiceSpecification> handleDices(Individual ind, IDimensionQualification dimToAS,
      String dimensinoURI, String hierarchyURI, AnalysisSituation as) {

    org.apache.jena.rdf.model.StmtIterator it1 =
        ind.listProperties(this.owlConnection.getModel().getProperty(
            OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.diceSpecification));
    List<IDiceSpecification> dices = new ArrayList<IDiceSpecification>();
    while (it1.hasNext()) {
      Statement stmt = it1.nextStatement();
      RDFNode diceSpecificationIndivNode = stmt.getObject();
      if (diceSpecificationIndivNode != null) {

        RDFNode diceLevelNode =
            owlConnection.getPropertyValueEnc(diceSpecificationIndivNode.as(Individual.class),
                owlConnection.getModel().getObjectProperty(
                    OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.diceLevel));
        RDFNode diceNodeNode =
            owlConnection.getPropertyValueEnc(diceSpecificationIndivNode.as(Individual.class),
                owlConnection.getModel().getObjectProperty(
                    OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.diceNode));

        IDiceSpecification diceSpec = new DiceSpecificaitonImpl();
        DataHandlerUtils.createDiceSpecification(owlConnection, dimensinoURI, hierarchyURI,
            mdSchema, diceSpecificationIndivNode, dimToAS, diceSpec, diceLevelNode, diceNodeNode);

        if ((diceSpec.getPosition() == null && diceSpec.getDiceNodeInAnalysisSituation() != null)
            || (diceSpec.getPosition() != null
                && diceSpec.getDiceNodeInAnalysisSituation() == null)) {
          logger.error("problem with dice level and dice node, both set to null");
          diceSpec = null;
        }
        if (diceSpec != null) {
          dices.add(diceSpec);
        }
        break;
      }
    }
    return dices;

  }

  /**
   * @param ind
   * @param dimToAS
   * @return
   * @throws Exception
   */
  private List<ISliceSetDim> handleSliceSet(Individual ind, IDimensionQualification dimToAS,
      String dimensinoURI, String hierarchyURI, AnalysisSituation as) throws Exception {

    org.apache.jena.rdf.model.StmtIterator it1 =
        ind.listProperties(this.owlConnection.getModel().getProperty(
            OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.HAS_SLICE_SET));

    List<ISliceSetDim> slices = new ArrayList<>();

    ISliceSetDim sliceSetSpec = null;

    while (it1.hasNext()) {
      Statement stmt = it1.nextStatement();
      RDFNode sliceSetSpecNode = stmt.getObject();

      if (sliceSetSpecNode != null) {



        Individual sliceSetSpecInd = sliceSetSpecNode.as(Individual.class);


        ISetOfComparison set = NoneSet.getNoneSet();

        String setUri = owlConnection.getPropertyValueEncAsString(sliceSetSpecInd,
            this.owlConnection.getModel().getProperty(
                OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.ON_SET));

        if (setUri != null) {
          set = as.getSetByURI(setUri);
        }


        sliceSetSpec = null;

        HierarchyInDimension hm =
            mdSchema.getHierarchyInDimensionNode(dimToAS.getD(), dimToAS.getHierarchy());

        if (sliceSetSpecNode.as(Individual.class).hasProperty(RDF.type,
            this.owlConnection.getModel().getOntClass(
                OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.variable))) {

          sliceSetSpec = new SliceSet(new Signature<IDimensionQualification>(dimToAS,
              ItemInAnalysisSituationType.sliceSet, VariableState.VARIABLE,
              sliceSetSpecNode.as(Individual.class).getLocalName(), null));

        }

        else {
          sliceSetSpec = new SliceSet(sliceSetSpecNode.as(Individual.class).getURI(),
              sliceSetSpecNode.as(Individual.class).getLocalName(),
              new Signature<IDimensionQualification>(dimToAS, ItemInAnalysisSituationType.sliceSet,
                  VariableState.NON_VARIABLE, "", null));

        }


        Set<ISliceSinglePosition<IDimensionQualification>> conds = handleNoPositionSlices(
            sliceSetSpecNode.as(Individual.class), dimToAS, hierarchyURI, dimensinoURI, as);

        try {
          // conds.addAll(handlePredicateSlices(sliceSetSpecInd, dimToAS, hierarchyURI, as));
        } catch (Exception e) {
          // TODO Auto-generated catch block
          // e.printStackTrace();
        }
        sliceSetSpec.setConditions(conds.stream()
            .map(x -> (x instanceof SliceCondition) ? (SliceCondition<IDimensionQualification>) x
                : (SliceConditionTyped<IDimensionQualification>) x)
            .collect(Collectors.toList()));

      }
      slices.add(sliceSetSpec);
    }
    return slices;
  }

  /**
   * @param ind
   * @return
   */
  private Map<String, String> handleConfigs(Individual ind, AnalysisSituation as) {

    Map<String, String> configurationMap = new HashMap<String, String>();

    Individual configNode =

        ind.getPropertyValue(this.owlConnection.getModel()
            .getProperty(OWLConnectionFactory.getAGNamespace(owlConnection)
                + Constants.HAS_DIM_CONFIGURATION)) != null ?

                    ind.getPropertyValue(this.owlConnection.getModel()
                        .getProperty(OWLConnectionFactory.getAGNamespace(owlConnection)
                            + Constants.HAS_DIM_CONFIGURATION))
                        .as(Individual.class)
                    : null;

    if (configNode != null) {

      Individual nonStrictNode =

          configNode.getPropertyValue(this.owlConnection.getModel()
              .getProperty(OWLConnectionFactory.getAGNamespace(owlConnection)
                  + Constants.HAS_NON_STRICT_MODE)) != null ?

                      configNode
                          .getPropertyValue(
                              this.owlConnection.getModel()
                                  .getProperty(OWLConnectionFactory.getAGNamespace(owlConnection)
                                      + Constants.HAS_NON_STRICT_MODE))
                          .as(Individual.class)
                      : null;

      if (nonStrictNode != null) {
        configurationMap.put("nonstrict", nonStrictNode.getLocalName());
      }

      Individual inCompletetNode =

          configNode.getPropertyValue(this.owlConnection.getModel()
              .getProperty(OWLConnectionFactory.getAGNamespace(owlConnection)
                  + Constants.HAS_INCOMPLETE_MODE)) != null ?

                      configNode
                          .getPropertyValue(
                              this.owlConnection.getModel()
                                  .getProperty(OWLConnectionFactory.getAGNamespace(owlConnection)
                                      + Constants.HAS_INCOMPLETE_MODE))
                          .as(Individual.class)
                      : null;

      if (inCompletetNode != null) {
        configurationMap.put("incomplete", inCompletetNode.getLocalName());
      }

      Individual mandatoryNode =

          configNode.getPropertyValue(this.owlConnection.getModel()
              .getProperty(OWLConnectionFactory.getAGNamespace(owlConnection)
                  + Constants.HAS_MANDATORY_MODE_MODE)) != null ?

                      configNode
                          .getPropertyValue(this.owlConnection.getModel()
                              .getProperty(OWLConnectionFactory.getAGNamespace(owlConnection)
                                  + Constants.HAS_MANDATORY_MODE_MODE))
                          .as(Individual.class)
                      : null;

      if (mandatoryNode != null) {
        configurationMap.put("mandatory", mandatoryNode.getLocalName());
      }
    }
    return configurationMap;
  }


  /**
   * @param ind
   * @param dimToAS
   * @return
   * @throws Exception
   */
  private Set<ISliceSinglePosition<IDimensionQualification>> handleNoPositionSlices(Individual ind,
      IDimensionQualification dimToAS, String hierarchyURI, String dimensinoURI,
      AnalysisSituation as) throws Exception {

    Set<ISliceSinglePosition<IDimensionQualification>> slices = new HashSet<>();

    org.apache.jena.rdf.model.StmtIterator it2 = ind.listProperties(
        this.owlConnection.getModel().getProperty(OWLConnectionFactory.getAGNamespace(owlConnection)
            + Constants.SLICE_SPECIFICATION_PROP));

    while (it2.hasNext()) {

      Statement stmt = it2.nextStatement();
      RDFNode sliceSpecificationIndivNode = stmt.getObject();
      Individual nodeInd = sliceSpecificationIndivNode.as(Individual.class);

      ISliceSinglePosition<IDimensionQualification> sc = DataHandlerUtils.readSliceConditoin(
          owlConnection, mdSchema, nodeInd, dimToAS, dimensinoURI, hierarchyURI, predicateGraph);

      if (sc != null) {
        slices.add(sc);
      }
    }

    return slices;
  }

}
