package swag.data_handler;

import java.util.HashMap;
import java.util.Map;
import org.apache.jena.ontology.Individual;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.vocabulary.RDF;
import org.apache.log4j.Logger;

import swag.analysis_graphs.execution_engine.ElementInsufficientSpecificationException;
import swag.analysis_graphs.execution_engine.NoMappingExistsForElementException;
import swag.analysis_graphs.execution_engine.Signature;
import swag.analysis_graphs.execution_engine.analysis_situations.AggregationOperationInAnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.AnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.IMeasureToAnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.ISetOfComparison;
import swag.analysis_graphs.execution_engine.analysis_situations.ItemInAnalysisSituationType;
import swag.analysis_graphs.execution_engine.analysis_situations.MeasureAndAggFuncSpecificationImpl;
import swag.analysis_graphs.execution_engine.analysis_situations.MeasureAndAggFuncSpecificationInterface;
import swag.analysis_graphs.execution_engine.analysis_situations.MeasureInAnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.NoneSet;
import swag.analysis_graphs.execution_engine.analysis_situations.VariableState;
import swag.md_elements.MDSchema;
import swag.md_elements.Measure;
import swag.sparql_builder.ASElements.configuration.Configuration;

public class MeasToASRepImpl implements MeasToASRepInterface {

  private static final Logger logger = Logger.getLogger(MeasToASRepImpl.class);

  private OWlConnection owlConnection;
  private MDSchemaRepInterface mdInterface;
  private MappingRepInterface mappRepInterface;
  private MDSchema mdSchema;;

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

  public MeasToASRepImpl(OWlConnection owlConnection, MDSchemaRepInterface mdInterface,
      MappingRepInterface mappRepInterface, MDSchema graph) {
    super();
    this.owlConnection = owlConnection;
    this.mdInterface = mdInterface;
    this.mappRepInterface = mappRepInterface;
    this.mdSchema = graph;
  }

  @Override
  public MeasureAndAggFuncSpecificationInterface getMeasureToASByURI(
      IMeasureToAnalysisSituation measToASInt, String uri, String factURI, AnalysisSituation as)
      throws ElementInsufficientSpecificationException, NoMappingExistsForElementException {


    MeasureAndAggFuncSpecificationInterface measureToAS = new MeasureAndAggFuncSpecificationImpl();
    Individual ind = this.owlConnection.getModel().getIndividual(uri);
    // TODO get dimension from dims (the parameter passed list)

    // first filling in the measure
    RDFNode node1 =
        this.owlConnection.getPropertyValueEnc(ind, this.owlConnection.getModel().getObjectProperty(
            OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.onMeasure));
    if (node1 != null) {
      String measureURI = node1.as(Individual.class).getURI();

      Individual granIndiv = node1.as(Individual.class);

      ISetOfComparison set = NoneSet.getNoneSet();

      String setUri =
          owlConnection.getPropertyValueEncAsString(granIndiv, this.owlConnection.getModel()
              .getProperty(OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.ON_SET));

      if (setUri != null) {
        set = as.getSetByURI(setUri);
      }

      if (node1.as(Individual.class).hasProperty(RDF.type, this.owlConnection.getModel()
          .getOntClass(OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.variable))) {
        MeasureInAnalysisSituation meas =
            new MeasureInAnalysisSituation(new Signature<IMeasureToAnalysisSituation>(measToASInt,
                ItemInAnalysisSituationType.Measure, VariableState.VARIABLE,
                node1.as(Individual.class).getLocalName(), null));
        measureToAS.setPosition(meas);
      } else {
        try {
          Signature<IMeasureToAnalysisSituation> sig =
              new Signature<IMeasureToAnalysisSituation>(measToASInt,
                  ItemInAnalysisSituationType.Measure, VariableState.NON_VARIABLE, "", null);
          MeasureInAnalysisSituation meas =
              new MeasureInAnalysisSituation((Measure) mdSchema.getNode(measureURI), sig);
          measureToAS.setPosition(meas);
        } catch (Exception ex) {
          throw ex;
        }
      }
    } else {
      ElementInsufficientSpecificationException ex = new ElementInsufficientSpecificationException(
          "measure to analysis situation: measure doesn't exist " + uri);
      logger.error("exception:", ex);
      ex.printStackTrace();
      throw ex;
    }

    // second filling in the aggregation operation
    node1 =
        this.owlConnection.getPropertyValueEnc(ind, this.owlConnection.getModel().getObjectProperty(
            OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.aggregationOperation));
    if (node1 != null) {
      String aggOpURI = node1.as(Individual.class).getURI();
      if (node1.as(Individual.class).hasProperty(RDF.type, this.owlConnection.getModel()
          .getOntClass(OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.variable))) {
        AggregationOperationInAnalysisSituation agg =
            new AggregationOperationInAnalysisSituation(new Signature<IMeasureToAnalysisSituation>(
                measToASInt, ItemInAnalysisSituationType.AggregationOperation,
                VariableState.VARIABLE, node1.as(Individual.class).getLocalName(), null));
        measureToAS.setAggregationOperationInAnalysisSituation(agg);
      } else {
        measureToAS.setAggregationOperationInAnalysisSituation(
            new AggregationOperationInAnalysisSituation(node1.as(Individual.class).getLocalName(),
                node1.as(Individual.class).getLocalName(),
                node1.as(Individual.class).getLocalName(),
                new Signature<IMeasureToAnalysisSituation>(measToASInt,
                    ItemInAnalysisSituationType.AggregationOperation, VariableState.NON_VARIABLE,
                    "", null)));
      }
    } else {
      ElementInsufficientSpecificationException ex = new ElementInsufficientSpecificationException(
          "measure to analysis situation: aggregation function for measure " + ind.getURI()
              + "doesn't exist.");
      logger.error("exception:", ex);
      ex.printStackTrace();
      throw ex;
    }

    // Read configuration
    measureToAS.setConfiguration(readMeasureConfiug(ind));

    return measureToAS;
  }

  /**
   * @param ind
   * @return
   */
  private Configuration readMeasureConfiug(Individual ind) {

    Map<String, String> configurationMap = new HashMap<String, String>();

    Individual configNode = ind.getPropertyValue(
        this.owlConnection.getModel().getProperty(OWLConnectionFactory.getAGNamespace(owlConnection)
            + Constants.HAS_MSR_CONFIGURATION)) != null ?

                ind.getPropertyValue(this.owlConnection.getModel()
                    .getProperty(OWLConnectionFactory.getAGNamespace(owlConnection)
                        + Constants.HAS_MSR_CONFIGURATION))
                    .as(Individual.class)
                : null;

    if (configNode != null) {

      Individual missingValNode =

          configNode.getPropertyValue(this.owlConnection.getModel()
              .getProperty(OWLConnectionFactory.getAGNamespace(owlConnection)
                  + Constants.HAS_MISSING_VAL_MODE)) != null ?

                      configNode
                          .getPropertyValue(
                              this.owlConnection.getModel()
                                  .getProperty(OWLConnectionFactory.getAGNamespace(owlConnection)
                                      + Constants.HAS_MISSING_VAL_MODE))
                          .as(Individual.class)
                      : null;

      if (missingValNode != null) {
        configurationMap.put("missingval", missingValNode.getLocalName());
      }

      Individual internalAggregationNode =

          configNode.getPropertyValue(this.owlConnection.getModel()
              .getProperty(OWLConnectionFactory.getAGNamespace(owlConnection)
                  + Constants.HAS_INTERNAL_AGG_MODE)) != null ?

                      configNode
                          .getPropertyValue(
                              this.owlConnection.getModel()
                                  .getProperty(OWLConnectionFactory.getAGNamespace(owlConnection)
                                      + Constants.HAS_INTERNAL_AGG_MODE))
                          .as(Individual.class)
                      : null;

      if (internalAggregationNode != null) {
        configurationMap.put("internalagg", internalAggregationNode.getLocalName());
      }
    }
    return new Configuration(configurationMap);
  }

  /**
   * @param measToASInt
   * @param uri
   * @param factURI
   * @param as
   * @return
   * @throws ElementInsufficientSpecificationException
   * @throws NoMappingExistsForElementException
   */
  public MeasureAndAggFuncSpecificationInterface getMeasureByURI(
      IMeasureToAnalysisSituation measToASInt, String uri, String factURI, AnalysisSituation as)
      throws ElementInsufficientSpecificationException, NoMappingExistsForElementException {


    MeasureAndAggFuncSpecificationInterface measureToAS = new MeasureAndAggFuncSpecificationImpl();
    Individual ind = this.owlConnection.getModel().getIndividual(uri);
    // TODO get dimension from dims (the parameter passed list)

    // first filling in the measure
    RDFNode node1 =
        this.owlConnection.getPropertyValueEnc(ind, this.owlConnection.getModel().getObjectProperty(
            OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.onMeasure));
    if (node1 != null) {
      String measureURI = node1.as(Individual.class).getURI();

      Individual granIndiv = node1.as(Individual.class);

      ISetOfComparison set = NoneSet.getNoneSet();

      String setUri =
          owlConnection.getPropertyValueEncAsString(granIndiv, this.owlConnection.getModel()
              .getProperty(OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.ON_SET));

      if (setUri != null) {
        set = as.getSetByURI(setUri);
      }

      if (node1.as(Individual.class).hasProperty(RDF.type, this.owlConnection.getModel()
          .getOntClass(OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.variable))) {
        MeasureInAnalysisSituation meas =
            new MeasureInAnalysisSituation(new Signature<IMeasureToAnalysisSituation>(measToASInt,
                ItemInAnalysisSituationType.Measure, VariableState.VARIABLE,
                node1.as(Individual.class).getLocalName(), null));
        measureToAS.setPosition(meas);
      } else {
        try {
          Signature<IMeasureToAnalysisSituation> sig =
              new Signature<IMeasureToAnalysisSituation>(measToASInt,
                  ItemInAnalysisSituationType.Measure, VariableState.NON_VARIABLE, "", null);
          MeasureInAnalysisSituation meas =
              new MeasureInAnalysisSituation((Measure) mdSchema.getNode(measureURI), sig);
          measureToAS.setPosition(meas);
        } catch (Exception ex) {
          throw ex;
        }
      }
    } else {
      ElementInsufficientSpecificationException ex = new ElementInsufficientSpecificationException(
          "measure to analysis situation: measure doesn't exist " + uri);
      logger.error("exception:", ex);
      ex.printStackTrace();
      throw ex;
    }

    // second filling in the aggregation operation
    node1 =
        this.owlConnection.getPropertyValueEnc(ind, this.owlConnection.getModel().getObjectProperty(
            OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.aggregationOperation));
    if (node1 != null) {
      String aggOpURI = node1.as(Individual.class).getURI();
      if (node1.as(Individual.class).hasProperty(RDF.type, this.owlConnection.getModel()
          .getOntClass(OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.variable))) {
        AggregationOperationInAnalysisSituation agg =
            new AggregationOperationInAnalysisSituation(new Signature<IMeasureToAnalysisSituation>(
                measToASInt, ItemInAnalysisSituationType.AggregationOperation,
                VariableState.VARIABLE, node1.as(Individual.class).getLocalName(), null));
        measureToAS.setAggregationOperationInAnalysisSituation(agg);
      } else {
        measureToAS.setAggregationOperationInAnalysisSituation(
            new AggregationOperationInAnalysisSituation(node1.as(Individual.class).getLocalName(),
                node1.as(Individual.class).getLocalName(),
                node1.as(Individual.class).getLocalName(),
                new Signature<IMeasureToAnalysisSituation>(measToASInt,
                    ItemInAnalysisSituationType.AggregationOperation, VariableState.NON_VARIABLE,
                    "", null)));
      }
    } else {
      ElementInsufficientSpecificationException ex = new ElementInsufficientSpecificationException(
          "measure to analysis situation: aggregation function for measure " + ind.getURI()
              + "doesn't exist.");
      logger.error("exception:", ex);
      ex.printStackTrace();
      throw ex;
    }

    // Reading configuration
    Map<String, String> configurationMap = new HashMap<String, String>();

    Individual configNode =

        ind.getPropertyValue(this.owlConnection.getModel()
            .getProperty(OWLConnectionFactory.getAGNamespace(owlConnection)
                + Constants.HAS_MSR_CONFIGURATION)) != null ?

                    ind.getPropertyValue(this.owlConnection.getModel()
                        .getProperty(OWLConnectionFactory.getAGNamespace(owlConnection)
                            + Constants.HAS_MSR_CONFIGURATION))
                        .as(Individual.class)
                    : null;

    if (configNode != null) {

      Individual missingValNode =

          configNode.getPropertyValue(this.owlConnection.getModel()
              .getProperty(OWLConnectionFactory.getAGNamespace(owlConnection)
                  + Constants.HAS_MISSING_VAL_MODE)) != null ?

                      configNode
                          .getPropertyValue(
                              this.owlConnection.getModel()
                                  .getProperty(OWLConnectionFactory.getAGNamespace(owlConnection)
                                      + Constants.HAS_MISSING_VAL_MODE))
                          .as(Individual.class)
                      : null;

      if (missingValNode != null) {
        configurationMap.put("missingval", missingValNode.getLocalName());
      }

      Individual internalAggregationNode =

          configNode.getPropertyValue(this.owlConnection.getModel()
              .getProperty(OWLConnectionFactory.getAGNamespace(owlConnection)
                  + Constants.HAS_INTERNAL_AGG_MODE)) != null ?

                      configNode
                          .getPropertyValue(
                              this.owlConnection.getModel()
                                  .getProperty(OWLConnectionFactory.getAGNamespace(owlConnection)
                                      + Constants.HAS_INTERNAL_AGG_MODE))
                          .as(Individual.class)
                      : null;

      if (internalAggregationNode != null) {
        configurationMap.put("internalagg", internalAggregationNode.getLocalName());
      }
    }

    Configuration con = new Configuration(configurationMap);
    measureToAS.setConfiguration(con);

    return measureToAS;
  }
}
