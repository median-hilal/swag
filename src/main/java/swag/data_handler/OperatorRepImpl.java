package swag.data_handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.ontology.Individual;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.vocabulary.RDF;

import swag.analysis_graphs.execution_engine.Signature;
import swag.analysis_graphs.execution_engine.analysis_situations.AggregationOperationInAnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.AnalysisSituationToBaseMeasureCondition;
import swag.analysis_graphs.execution_engine.analysis_situations.AnalysisSituationToResultFilters;
import swag.analysis_graphs.execution_engine.analysis_situations.DiceSpecificaitonImpl;
import swag.analysis_graphs.execution_engine.analysis_situations.DimensionToAnalysisSituationImpl;
import swag.analysis_graphs.execution_engine.analysis_situations.GranualritySpecificationImpl;
import swag.analysis_graphs.execution_engine.analysis_situations.IDiceSpecification;
import swag.analysis_graphs.execution_engine.analysis_situations.IDimensionQualification;
import swag.analysis_graphs.execution_engine.analysis_situations.IGranularitySpecification;
import swag.analysis_graphs.execution_engine.analysis_situations.IMeasureToAnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.ISliceSinglePosition;
import swag.analysis_graphs.execution_engine.analysis_situations.ItemInAnalysisSituationType;
import swag.analysis_graphs.execution_engine.analysis_situations.LevelInAnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.MeasureAndAggFuncSpecificationImpl;
import swag.analysis_graphs.execution_engine.analysis_situations.MeasureAndAggFuncSpecificationInterface;
import swag.analysis_graphs.execution_engine.analysis_situations.MeasureDerived;
import swag.analysis_graphs.execution_engine.analysis_situations.MeasureInAnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.Variable;
import swag.analysis_graphs.execution_engine.analysis_situations.VariableState;
import swag.analysis_graphs.execution_engine.navigations.NavigationStep;
import swag.analysis_graphs.execution_engine.operators.AddBaseMeasureSelectionOperator;
import swag.analysis_graphs.execution_engine.operators.AddDimTypedSliceConditoinOperator;
import swag.analysis_graphs.execution_engine.operators.AddDimensionSelectionOperator;
import swag.analysis_graphs.execution_engine.operators.AddMeasureOperator;
import swag.analysis_graphs.execution_engine.operators.AddResultSelectionOperator;
import swag.analysis_graphs.execution_engine.operators.DrillDownOperator;
import swag.analysis_graphs.execution_engine.operators.DrillDownToOperator;
import swag.analysis_graphs.execution_engine.operators.MoveDownToDiceNodeOperator;
import swag.analysis_graphs.execution_engine.operators.MoveToDiceNodeOperator;
import swag.analysis_graphs.execution_engine.operators.MoveToNextDiceNodeOperator;
import swag.analysis_graphs.execution_engine.operators.MoveToPreviousDiceNodeOperator;
import swag.analysis_graphs.execution_engine.operators.MoveUpToDiceNodeOperator;
import swag.analysis_graphs.execution_engine.operators.NullOperator;
import swag.analysis_graphs.execution_engine.operators.Operation;
import swag.analysis_graphs.execution_engine.operators.OperatorInsufficientDefinitionException;
import swag.analysis_graphs.execution_engine.operators.RollUpOperator;
import swag.analysis_graphs.execution_engine.operators.RollUpToOperator;
import swag.md_elements.Dimension;
import swag.md_elements.Level;
import swag.md_elements.MDSchema;
import swag.md_elements.Measure;
import swag.md_elements.QB4OHierarchy;
import swag.predicates.IPredicateGraph;

public class OperatorRepImpl implements OperatorsRepInterface {

  private MDSchema mdSchema;
  private OWlConnection owlConnection;
  private MDSchemaRepInterface mdInterface;
  private MappingRepInterface mappRepInterface;
  private IPredicateGraph predicateGraph;


  private static final org.apache.log4j.Logger logger =
      org.apache.log4j.Logger.getLogger(OperatorRepImpl.class);


  public OperatorRepImpl(OWlConnection owlConnection, MDSchemaRepInterface mdInterface,
      MappingRepInterface mappRepInterface, MDSchema graph, IPredicateGraph predicateGraph) {
    super();
    this.owlConnection = owlConnection;
    this.mdInterface = mdInterface;
    this.mappRepInterface = mappRepInterface;
    this.mdSchema = graph;
    this.predicateGraph = predicateGraph;
  }

  /**
   * Creates a new RollUpOperator, the passed URI should be for an existing RollUp operator
   * 
   * @param uri the URI of the operator
   * @return a new RollUpOpreator
   * @throws OperatorInsufficientDefinitionException when the operator doesn't have sufficient
   *         information attached to it
   */
  public RollUpOperator createRollUpOperatorFromURI(String uri)
      throws OperatorInsufficientDefinitionException {

    String dimURI = DataHandlerUtils.getOpDimensoinByURI(owlConnection, uri);
    String hierURI = DataHandlerUtils.getOpHierarchyByURI(owlConnection, uri);
    org.apache.jena.ontology.Individual ind = owlConnection.getModel().getIndividual(uri);

    if (!StringUtils.isEmpty(dimURI) && !StringUtils.isEmpty(hierURI)) {

      Dimension dim = (Dimension) mdSchema.getNode(dimURI);
      QB4OHierarchy hier = (QB4OHierarchy) mdSchema.getNode(hierURI);

      if (dim != null && hier != null) {
        return new RollUpOperator(uri, ind.getLocalName(), ind.getLabel("en"), ind.getComment("en"),
            (Dimension) mdSchema.getNode(dimURI), (QB4OHierarchy) mdSchema.getNode(hierURI));
      }
    }
    throwOperatorInsufficientDefinitionException("Rollup Operation", uri);
    return null;
  }

  /**
   * 
   * Encapsulates throwing the exception
   * 
   * @param op
   * @param uri
   * @throws OperatorInsufficientDefinitionException
   */
  protected void throwOperatorInsufficientDefinitionException(String op, String uri)
      throws OperatorInsufficientDefinitionException {

    OperatorInsufficientDefinitionException opEx =
        new OperatorInsufficientDefinitionException(op + ": " + uri);
    logger.error("exception:", opEx);
    opEx.printStackTrace();
    throw opEx;
  }

  /**
   * Creates a new RollUpToOperator, the passed URI should be for an existing RollUp operator
   * 
   * @param uri the URI of the operator
   * @return a new RollUpOpreator
   * @throws OperatorInsufficientDefinitionException when the operator doesn't have sufficient
   *         information attached to it
   */
  public RollUpToOperator createRollUpToOperatorFromURI(String uri, String factURI,
      List<Variable> nvVariables) throws OperatorInsufficientDefinitionException {

    String dimURI = DataHandlerUtils.getOpDimensoinByURI(owlConnection, uri);
    String hierURI = DataHandlerUtils.getOpHierarchyByURI(owlConnection, uri);
    String toGranularityURI = DataHandlerUtils.getOpToLevelByURI(owlConnection, uri);

    Individual opInd = owlConnection.getModel().getIndividual(uri);
    Individual granInd = owlConnection.getModel().getIndividual(toGranularityURI);

    if (!StringUtils.isEmpty(dimURI) && !StringUtils.isEmpty(hierURI)
        && !StringUtils.isEmpty(toGranularityURI)) {

      Dimension dim = (Dimension) mdSchema.getNode(dimURI);
      QB4OHierarchy hier = (QB4OHierarchy) mdSchema.getNode(hierURI);

      if (dim != null && hier != null) {

        IDimensionQualification dimToAS = new DimensionToAnalysisSituationImpl(dim, hier);
        IGranularitySpecification gr =  MDElementParameterCreator.readGranularity(granInd, owlConnection,
                dimToAS, mdSchema, dimURI, hierURI,
                nvVariables, toGranularityURI);

        if(gr == null){
          throwOperatorInsufficientDefinitionException("RollUpToOperator", uri);
          return null;
        }

        return new RollUpToOperator(uri, opInd.getLocalName(), opInd.getLabel("en"),
              opInd.getComment("en"),
                  gr,
                  dim, hier);
      }
    }
    throwOperatorInsufficientDefinitionException("RollUpToOperator", uri);
    return null;
  }

  /**
   * Creates a new DrillDownOperator, the passed URI should be for an existing DrillDown operator
   * 
   * @param uri the URI of the operator
   * @return a new DrillDownOperator
   * @throws OperatorInsufficientDefinitionException when the operator doesn't have sufficient
   *         information attached to it
   */
  public DrillDownOperator createDrillDownOperatorFromURI(String uri)
      throws OperatorInsufficientDefinitionException {

    String dimURI = DataHandlerUtils.getOpDimensoinByURI(owlConnection, uri);
    String hierURI = DataHandlerUtils.getOpHierarchyByURI(owlConnection, uri);
    org.apache.jena.ontology.Individual ind = owlConnection.getModel().getIndividual(uri);

    if (!StringUtils.isEmpty(dimURI) && !StringUtils.isEmpty(hierURI)) {

      Dimension dim = (Dimension) mdSchema.getNode(dimURI);
      QB4OHierarchy hier = (QB4OHierarchy) mdSchema.getNode(hierURI);

      if (dim != null && hier != null) {
        return new DrillDownOperator(uri, ind.getLocalName(), ind.getLabel("en"),
            ind.getComment("en"), dim, hier);
      }
    }
    throwOperatorInsufficientDefinitionException("Drill Down Operation", uri);
    return null;
  }

  /**
   * Creates a new DrillDownToOperator, the passed URI should be for an existing DrillDownTo
   * operator
   * 
   * @param uri the URI of the operator
   * @return a new DrillDownToOperator
   * @throws OperatorInsufficientDefinitionException when the operator doesn't have sufficient
   *         information attached to it
   */
  public DrillDownToOperator createDrillDownToOperatorFromURI(String uri, String factURI,
      List<Variable> nvVariables) throws OperatorInsufficientDefinitionException {

    String dimURI = DataHandlerUtils.getOpDimensoinByURI(owlConnection, uri);
    String hierURI = DataHandlerUtils.getOpHierarchyByURI(owlConnection, uri);
    String toGranularityURI = DataHandlerUtils.getOpToLevelByURI(owlConnection, uri);

    Individual opInd = owlConnection.getModel().getIndividual(uri);
    Individual granInd = owlConnection.getModel().getIndividual(toGranularityURI);

    if (!StringUtils.isEmpty(dimURI) && !StringUtils.isEmpty(hierURI)
        && !StringUtils.isEmpty(toGranularityURI)) {

      Dimension dim = (Dimension) mdSchema.getNode(dimURI);
      QB4OHierarchy hier = (QB4OHierarchy) mdSchema.getNode(hierURI);

      if (dim != null && hier != null) {

        IDimensionQualification dimToAS = new DimensionToAnalysisSituationImpl(dim, hier);

        IGranularitySpecification gr =  MDElementParameterCreator.readGranularity(granInd, owlConnection,
                dimToAS, mdSchema, dimURI, hierURI,
                nvVariables, toGranularityURI);

        if(gr == null){
          throwOperatorInsufficientDefinitionException("RollUpToOperator", uri);
          return null;
        }

          return new DrillDownToOperator(uri, opInd.getLocalName(), opInd.getLabel("en"),
              opInd.getComment("en"), gr, dim, hier);
      }
    }
    throwOperatorInsufficientDefinitionException("RollUpToOperator", uri);
    return null;
  }

  /**
   * Creates a new MoveToDiceNodeOperator, the passed URI should be for an existing
   * MoveToDiceNodeOperator operator
   * 
   * @param uri the URI of the operator
   * @return a new MoveToDiceNodeOperator
   * @throws OperatorInsufficientDefinitionException when the operator doesn't have sufficient
   *         information attached to it
   */
  public MoveToDiceNodeOperator createMoveToDiceNodeOperatorFromURI(String uri, String factURI,
      List<Variable> nvVariables) throws OperatorInsufficientDefinitionException {

    String dimURI = DataHandlerUtils.getOpDimensoinByURI(owlConnection, uri);
    String hierURI = DataHandlerUtils.getOpHierarchyByURI(owlConnection, uri);

    Individual opInd = owlConnection.getModel().getIndividual(uri);

    if (!StringUtils.isEmpty(dimURI) && !StringUtils.isEmpty(hierURI)) {

      Dimension dim = (Dimension) mdSchema.getNode(dimURI);
      QB4OHierarchy hier = (QB4OHierarchy) mdSchema.getNode(hierURI);

      if (dim != null && hier != null) {

        IDimensionQualification dimToAS = new DimensionToAnalysisSituationImpl(dim, hier);

        RDFNode diceLevelNode = owlConnection.getPropertyValueEnc(opInd.as(Individual.class),
            owlConnection.getModel().getObjectProperty(
                OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.OP_TO_DICE_LEVEL_E));
        RDFNode diceNodeNode = owlConnection.getPropertyValueEnc(opInd.as(Individual.class),
            owlConnection.getModel().getObjectProperty(
                OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.OP_TO_DICE_NOCE_E));

        IDiceSpecification diceSpec = new DiceSpecificaitonImpl();

        DataHandlerUtils.createDiceSpecification(owlConnection, dimURI, hierURI, mdSchema, opInd,
            dimToAS, diceSpec, diceLevelNode, diceNodeNode);
        dimToAS.setSingleDice(diceSpec);

        if (diceSpec.getPosition() != null && diceSpec.getPosition().getSignature().isVariable())
          nvVariables.add(diceSpec.getPosition());

        if (diceSpec.getDiceNodeInAnalysisSituation() != null
            && diceSpec.getDiceNodeInAnalysisSituation().getSignature().isVariable())
          nvVariables.add(diceSpec.getDiceNodeInAnalysisSituation());

        return new MoveToDiceNodeOperator(uri, opInd.getLocalName(), opInd.getLabel("en"),
            opInd.getComment("en"), dim, diceSpec, hier);
      }
    }
    throwOperatorInsufficientDefinitionException("MoveToDiceNodeOperator", uri);
    return null;
  }

  /**
   * Creates a new createMoveToNextDiceNodeOperator, the passed URI should be for an existing
   * MoveToDiceNodeOperator operator
   * 
   * @param uri the URI of the operator
   * @return a new MoveToDiceNodeOperator
   * @throws OperatorInsufficientDefinitionException when the operator doesn't have sufficient
   *         information attached to it
   */
  public MoveToNextDiceNodeOperator createMoveToNextDiceNodeOperatorFromURI(String uri,
      String factURI, List<Variable> nvVariables) throws OperatorInsufficientDefinitionException {

    String dimURI = DataHandlerUtils.getOpDimensoinByURI(owlConnection, uri);
    String hierURI = DataHandlerUtils.getOpHierarchyByURI(owlConnection, uri);
    Individual opInd = owlConnection.getModel().getIndividual(uri);

    if (!StringUtils.isEmpty(dimURI) && !StringUtils.isEmpty(hierURI)) {

      Dimension dim = (Dimension) mdSchema.getNode(dimURI);
      QB4OHierarchy hier = (QB4OHierarchy) mdSchema.getNode(hierURI);

      if (dim != null && hier != null) {

        return new MoveToNextDiceNodeOperator(uri, opInd.getLocalName(), opInd.getLabel("en"),
            opInd.getComment("en"), dim, new DiceSpecificaitonImpl(), hier);
      }
    }

    throwOperatorInsufficientDefinitionException("MoveToNextDiceNodeOperator", uri);
    return null;
  }

  /**
   * Creates a new MoveToPreviousDiceNodeOperator, the passed URI should be for an existing
   * MoveToDiceNodeOperator operator
   * 
   * @param uri the URI of the operator
   * @return a new MoveToDiceNodeOperator
   * @throws OperatorInsufficientDefinitionException when the operator doesn't have sufficient
   *         information attached to it
   */
  public MoveToPreviousDiceNodeOperator createMoveToPreviousDiceNodeOperatorFromURI(String uri,
      String factURI, List<Variable> nvVariables) throws OperatorInsufficientDefinitionException {

    String dimURI = DataHandlerUtils.getOpDimensoinByURI(owlConnection, uri);
    String hierURI = DataHandlerUtils.getOpHierarchyByURI(owlConnection, uri);
    Individual opInd = owlConnection.getModel().getIndividual(uri);

    if (!StringUtils.isEmpty(dimURI) && !StringUtils.isEmpty(hierURI)) {

      Dimension dim = (Dimension) mdSchema.getNode(dimURI);
      QB4OHierarchy hier = (QB4OHierarchy) mdSchema.getNode(hierURI);

      if (dim != null && hier != null) {

        return new MoveToPreviousDiceNodeOperator(uri, opInd.getLocalName(), opInd.getLabel("en"),
            opInd.getComment("en"), dim, new DiceSpecificaitonImpl(), hier);
      }
    }

    throwOperatorInsufficientDefinitionException("MoveToPreviousDiceNodeOperator", uri);
    return null;
  }

  // TODO-new not all the possible scenarios are implemented here; check the
  // previous function
  /**
   * Creates a new MoveDownToDiceNodeOperator, the passed URI should be for an existing
   * MoveDownToDiceNodeOperator operator
   * 
   * @param uri the URI of the operator
   * @return a new MoveDownToDiceNodeOperator
   * @throws OperatorInsufficientDefinitionException when the operator doesn't have sufficient
   *         information attached to it
   */
  public MoveDownToDiceNodeOperator createMoveDownToDiceNodeOperatorFromURI(String uri,
      String factURI, List<Variable> nvVariables) throws OperatorInsufficientDefinitionException {

    String dimURI = DataHandlerUtils.getOpDimensoinByURI(owlConnection, uri);
    String hierURI = DataHandlerUtils.getOpHierarchyByURI(owlConnection, uri);

    Individual opInd = owlConnection.getModel().getIndividual(uri);

    if (!StringUtils.isEmpty(dimURI) && !StringUtils.isEmpty(hierURI)) {

      Dimension dim = (Dimension) mdSchema.getNode(dimURI);
      QB4OHierarchy hier = (QB4OHierarchy) mdSchema.getNode(hierURI);

      if (dim != null && hier != null) {

        IDimensionQualification dimToAS = new DimensionToAnalysisSituationImpl(dim, hier);

        RDFNode diceLevelNode = owlConnection.getPropertyValueEnc(opInd.as(Individual.class),
            owlConnection.getModel().getObjectProperty(
                OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.OP_TO_DICE_LEVEL_E));
        RDFNode diceNodeNode = owlConnection.getPropertyValueEnc(opInd.as(Individual.class),
            owlConnection.getModel().getObjectProperty(
                OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.OP_TO_DICE_NOCE_E));

        IDiceSpecification diceSpec = new DiceSpecificaitonImpl();

        DataHandlerUtils.createDiceSpecification(owlConnection, dimURI, hierURI, mdSchema, opInd,
            dimToAS, diceSpec, diceLevelNode, diceNodeNode);
        dimToAS.setSingleDice(diceSpec);

        if (diceSpec.getPosition() != null && diceSpec.getPosition().getSignature().isVariable())
          nvVariables.add(diceSpec.getPosition());

        if (diceSpec.getDiceNodeInAnalysisSituation() != null
            && diceSpec.getDiceNodeInAnalysisSituation().getSignature().isVariable())
          nvVariables.add(diceSpec.getDiceNodeInAnalysisSituation());

        return new MoveDownToDiceNodeOperator(uri, opInd.getLocalName(), opInd.getLabel("en"),
            opInd.getComment("en"), dim, diceSpec, hier);
      }
    }
    throwOperatorInsufficientDefinitionException("MoveDownToDiceNodeOperator", uri);
    return null;

  }

  /**
   * Creates a new MoveUpToDiceNodeOperator, the passed URI should be for an existing
   * MoveUpToDiceNodeOperator operator
   * 
   * @param uri the URI of the operator
   * @return a new MoveUpToDiceNodeOperator
   * @throws OperatorInsufficientDefinitionException when the operator doesn't have sufficient
   *         information attached to it
   */
  public MoveUpToDiceNodeOperator createMoveUpToDiceNodeOperatorFromURI(String uri, String factURI,
      List<Variable> nvVariables) throws OperatorInsufficientDefinitionException {

    String dimURI = DataHandlerUtils.getOpDimensoinByURI(owlConnection, uri);
    String hierURI = DataHandlerUtils.getOpHierarchyByURI(owlConnection, uri);

    Individual opInd = owlConnection.getModel().getIndividual(uri);

    if (!StringUtils.isEmpty(dimURI) && !StringUtils.isEmpty(hierURI)) {

      Dimension dim = (Dimension) mdSchema.getNode(dimURI);
      QB4OHierarchy hier = (QB4OHierarchy) mdSchema.getNode(hierURI);

      if (dim != null && hier != null) {

        IDimensionQualification dimToAS = new DimensionToAnalysisSituationImpl(dim, hier);

        RDFNode diceLevelNode = owlConnection.getPropertyValueEnc(opInd.as(Individual.class),
            owlConnection.getModel().getObjectProperty(
                OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.OP_TO_DICE_LEVEL_E));
        RDFNode diceNodeNode = owlConnection.getPropertyValueEnc(opInd.as(Individual.class),
            owlConnection.getModel().getObjectProperty(
                OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.OP_TO_DICE_NOCE_E));

        IDiceSpecification diceSpec = new DiceSpecificaitonImpl();

        DataHandlerUtils.createDiceSpecification(owlConnection, dimURI, hierURI, mdSchema, opInd,
            dimToAS, diceSpec, diceLevelNode, diceNodeNode);
        dimToAS.setSingleDice(diceSpec);

        if (diceSpec.getPosition() != null && diceSpec.getPosition().getSignature().isVariable())
          nvVariables.add(diceSpec.getPosition());

        if (diceSpec.getDiceNodeInAnalysisSituation() != null
            && diceSpec.getDiceNodeInAnalysisSituation().getSignature().isVariable())
          nvVariables.add(diceSpec.getDiceNodeInAnalysisSituation());

        return new MoveUpToDiceNodeOperator(uri, opInd.getLocalName(), opInd.getLabel("en"),
            opInd.getComment("en"), dim, diceSpec, hier);
      }
    }
    throwOperatorInsufficientDefinitionException("MoveUpToDiceNodeOperator", uri);
    return null;
  }

  /**
   * Creates a new AddMeasureOperator, the passed URI should be for an existing AddMeasureOperator
   * operator
   * 
   * @param uri the URI of the operator
   * @return a new AddMeasureOperator
   * @throws OperatorInsufficientDefinitionException when the operator doesn't have sufficient
   *         information attached to it
   */
  public AddMeasureOperator createAddMeasureOperatorFromURI(String uri)
      throws OperatorInsufficientDefinitionException {

    boolean exceptionCase = false;
    Individual ind0 = this.owlConnection.getModel().getIndividual(uri);
    RDFNode navMeasureASNode =
        ind0.getPropertyValue(this.owlConnection.getModel().getObjectProperty(
            OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.navMeasureToAS));
    RDFNode navOnMeasureNode = null;
    RDFNode aggregationOperationNode = null;

    // AddMeasure operator
    if (navMeasureASNode != null) {
      navOnMeasureNode = navMeasureASNode.as(Individual.class)
          .getPropertyValue(this.owlConnection.getModel().getObjectProperty(
              OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.onMeasure_E));
      aggregationOperationNode = navMeasureASNode.as(Individual.class)
          .getPropertyValue(this.owlConnection.getModel()
              .getObjectProperty(OWLConnectionFactory.getAGNamespace(owlConnection)
                  + Constants.aggregationOperation_E));
      Individual ind = navMeasureASNode.as(Individual.class);

      if (navOnMeasureNode != null && aggregationOperationNode != null) {
        try {
          MeasureAndAggFuncSpecificationInterface measToAS =
              new MeasureAndAggFuncSpecificationImpl();
          measToAS = new MeasureAndAggFuncSpecificationImpl(
              new MeasureInAnalysisSituation(
                  (Measure) mdSchema.getNode(navOnMeasureNode.toString()),
                  new Signature<IMeasureToAnalysisSituation>(null,
                      ItemInAnalysisSituationType.Measure, VariableState.NON_VARIABLE, "", null)),
              new AggregationOperationInAnalysisSituation(
                  aggregationOperationNode.as(Individual.class).getLocalName(),
                  aggregationOperationNode.as(Individual.class).getLocalName(), new Object(),
                  new Signature<IMeasureToAnalysisSituation>(null,
                      ItemInAnalysisSituationType.AggregationOperation, VariableState.NON_VARIABLE,
                      "", null)));

          return new AddMeasureOperator(uri, ind0.getLocalName(), ind0.getLabel("en"),
              ind0.getComment("en"), measToAS);
        } catch (Exception ex) {
          OperatorInsufficientDefinitionException opEx =
              new OperatorInsufficientDefinitionException("AddMeasureOperator: " + uri);
          logger.error("exception:", opEx);
          opEx.printStackTrace();
          throw opEx;
        }
      } else {
        exceptionCase = true;
      }
    } else {
      exceptionCase = true;
    }

    if (exceptionCase) {
      OperatorInsufficientDefinitionException opEx =
          new OperatorInsufficientDefinitionException("AddMeasureOperator: " + uri);
      logger.error("exception:", opEx);
      opEx.printStackTrace();
      throw opEx;
    }

    // never reached
    return null;
  }



  /**
   * creates an instance of the suitable subclass of Operator depending on the URI provided
   * 
   * @param uri the URI to the operator at hand
   * @return a subclass of Operator, or a NullOperator when an exception occurs
   */
  private Operation getOperatorByURI(String uri, String factURI, List<Variable> nvVariables) {
    Individual ind0 = this.owlConnection.getModel().getIndividual(uri);
    if (ind0 != null) {
      try {
        if (ind0.hasProperty(RDF.type, this.owlConnection.getModel().getOntClass(
            OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.RollUpOpClass)))
          return createRollUpOperatorFromURI(uri);
        if (ind0.hasProperty(RDF.type, this.owlConnection.getModel().getOntClass(
            OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.RollUpToOpClass)))
          return createRollUpToOperatorFromURI(uri, factURI, nvVariables);
        if (ind0.hasProperty(RDF.type, this.owlConnection.getModel().getOntClass(
            OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.DrillDownOpClass)))
          return createDrillDownOperatorFromURI(uri);
        if (ind0.hasProperty(RDF.type, this.owlConnection.getModel().getOntClass(
            OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.DrillDownToOpClass)))
          return createDrillDownToOperatorFromURI(uri, factURI, nvVariables);
        if (ind0.hasProperty(RDF.type, this.owlConnection.getModel().getOntClass(
            OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.MoveToDiceNodeOpClass)))
          return createMoveToDiceNodeOperatorFromURI(uri, factURI, nvVariables);
        if (ind0.hasProperty(RDF.type,
            this.owlConnection.getModel()
                .getOntClass(OWLConnectionFactory.getAGNamespace(owlConnection)
                    + Constants.MoveUpToDiceNodeOpClass)))
          return createMoveUpToDiceNodeOperatorFromURI(uri, factURI, nvVariables);
        if (ind0.hasProperty(RDF.type,
            this.owlConnection.getModel()
                .getOntClass(OWLConnectionFactory.getAGNamespace(owlConnection)
                    + Constants.MoveDownToDiceNodeOpClass)))
          return createMoveDownToDiceNodeOperatorFromURI(uri, factURI, nvVariables);
        if (ind0.hasProperty(RDF.type,
            this.owlConnection.getModel()
                .getOntClass(OWLConnectionFactory.getAGNamespace(owlConnection)
                    + Constants.MoveToNextDiceNodeOpClass)))
          return createMoveToNextDiceNodeOperatorFromURI(uri, factURI, nvVariables);
        if (ind0.hasProperty(RDF.type,
            this.owlConnection.getModel()
                .getOntClass(OWLConnectionFactory.getAGNamespace(owlConnection)
                    + Constants.MoveToPrevDiceNodeOpClass)))
          return createMoveToPreviousDiceNodeOperatorFromURI(uri, factURI, nvVariables);
        if (ind0.hasProperty(RDF.type, this.owlConnection.getModel().getOntClass(
            OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.AddMeasureOpClass)))
          return createAddMeasureOperatorFromURI(uri);
        if (ind0.hasProperty(RDF.type,
            this.owlConnection.getModel()
                .getOntClass(OWLConnectionFactory.getAGNamespace(owlConnection)
                    + Constants.ModifySliceConditionOpClass))) {
          throw new UnsupportedOperationException();
          // return createModifySliceOperatorFromURI(uri, factURI, nvVariables);
        }
        if (ind0.hasProperty(RDF.type, this.owlConnection.getModel().getOntClass(
            OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.ChangeSliceOpClass))) {
          throw new UnsupportedOperationException();
          // return createModifySliceOperatorFromURI(uri, factURI, nvVariables);
        }
        if (ind0.hasProperty(RDF.type,
            this.owlConnection.getModel()
                .getOntClass(OWLConnectionFactory.getAGNamespace(owlConnection)
                    + Constants.AddDimensionSelectionOperator)))
          return createAddDimSliceConditionOperatorFromURI(uri, factURI, nvVariables);
        if (ind0.hasProperty(RDF.type,
            this.owlConnection.getModel()
                .getOntClass(OWLConnectionFactory.getAGNamespace(owlConnection)
                    + Constants.AddDimTypedConditoinOperator)))
          return createAddDimSliceConditionTypedOperatorFromURI(uri, factURI, nvVariables);
        if (ind0.hasProperty(RDF.type,
            this.owlConnection.getModel()
                .getOntClass(OWLConnectionFactory.getAGNamespace(owlConnection)
                    + Constants.AddResultSelectionOperator)))
          return createAddResultFilterOperatorFromURI(uri, factURI, nvVariables);
        if (ind0.hasProperty(RDF.type,
            this.owlConnection.getModel()
                .getOntClass(OWLConnectionFactory.getAGNamespace(owlConnection)
                    + Constants.AddBaseMeasureSeletionOperator)))
          return createAddBaseMsrConditionOperatorFromURI(uri, factURI, nvVariables);

        logger.error("Could not determine a suitable operation type. Skipping reading operator "
            + ind0.getURI());
        return new NullOperator();

      } catch (Exception ex) {
        logger.error("An exception happened " + ex.getMessage() + ex
            + ". Skipping reading operator " + ind0.getURI());
        return new NullOperator();
      }
    } else {
      return new NullOperator();
    }
  }

  @Override
  public List<Operation> getNavigationStepOperators(NavigationStep nv, String factURI) {

    List<Variable> nvVariables = new ArrayList<>();
    List<Operation> opList = new ArrayList<Operation>();

    NodeIterator nodeItr11 = this.owlConnection.getModel().getIndividual(nv.getName())
        .listPropertyValues(owlConnection.getModel().getObjectProperty(
            OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.HAS_OPERATION_E));
    while (nodeItr11.hasNext()) {
      Individual tempOp = nodeItr11.next().as(Individual.class);
      opList.add(getOperatorByURI(tempOp.getURI(), factURI,
          nvVariables/* , nv.getSource(), nv.getTarget() */));
    }

    for (Variable var : nvVariables) {
      var.addToAnalysisSituationOrNavigationStepVariables(nv);
    }

    for (Map.Entry<Integer, Variable> ent : nv.getVariables().entrySet()) {
      nv.addToInitialVariables(ent.getValue().shallowCopy(), null);
    }

    return opList;
  }

  @Override
  public AddBaseMeasureSelectionOperator createAddBaseMsrConditionOperatorFromURI(String uri,
      String factURI, List<Variable> nvVariables) throws OperatorInsufficientDefinitionException {


    org.apache.jena.ontology.Individual ind = owlConnection.getModel().getIndividual(uri);

    if (!StringUtils.isEmpty(uri)) {

      Measure msr = (MeasureDerived) mdSchema.getNode(uri);

      Individual condNode = owlConnection.getPropertyValueEncAsIndividual(ind,
          owlConnection.getModel()
              .getObjectProperty(OWLConnectionFactory.getAGNamespace(owlConnection)
                  + Constants.OP_MEASURE_SELECTION_PROP_E));

      if (msr != null) {

        ISliceSinglePosition<AnalysisSituationToBaseMeasureCondition> sc;
        try {
          sc = DataHandlerUtils.readMeasureCondsAndFiltersSliceConditoin(owlConnection, mdSchema,
              condNode, new AnalysisSituationToBaseMeasureCondition(null, new ArrayList<>()), uri,
              predicateGraph);
          AddBaseMeasureSelectionOperator op = new AddBaseMeasureSelectionOperator(uri,
              ind.getLocalName(), ind.getLabel("en"), ind.getComment("en"), sc);
          if (op.getCondition().getSignature().isVariable()) {
            nvVariables.add(op.getCondition());
          }
          return op;
        } catch (Exception e) {
          throwOperatorInsufficientDefinitionException("createAddBaseMsrConditionOperatorFromURI",
              uri);
        }
      }
    }
    throwOperatorInsufficientDefinitionException("createAddBaseMsrConditionOperatorFromURI", uri);
    return null;
  }

  @Override
  public AddResultSelectionOperator createAddResultFilterOperatorFromURI(String uri, String factURI,
      List<Variable> nvVariables) throws OperatorInsufficientDefinitionException {

    org.apache.jena.ontology.Individual ind = owlConnection.getModel().getIndividual(uri);

    if (!StringUtils.isEmpty(uri)) {
      Measure msr = (MeasureDerived) mdSchema.getNode(uri);
      Individual condNode = owlConnection.getPropertyValueEncAsIndividual(ind,
          owlConnection.getModel()
              .getObjectProperty(OWLConnectionFactory.getAGNamespace(owlConnection)
                  + Constants.OP_RESULT_FILTER_PROP_E));

      ISliceSinglePosition<AnalysisSituationToResultFilters> sc;
      try {
        sc = DataHandlerUtils.readMeasureCondsAndFiltersSliceConditoin(owlConnection, mdSchema,
            condNode, new AnalysisSituationToResultFilters(null, new ArrayList<>()), uri,
            predicateGraph);
        AddResultSelectionOperator op = new AddResultSelectionOperator(uri, ind.getLocalName(),
            ind.getLabel("en"), ind.getComment("en"), sc);
        if (op.getCondition().getSignature().isVariable()) {
          nvVariables.add(op.getCondition());
        }
        return op;
      } catch (Exception e) {
        throwOperatorInsufficientDefinitionException("createResultFilterOperatorFromURI", uri);
      }
    }
    throwOperatorInsufficientDefinitionException("createResultFilterOperatorFromURI", uri);
    return null;
  }

  @Override
  public AddDimensionSelectionOperator createAddDimSliceConditionOperatorFromURI(String uri,
      String factURI, List<Variable> nvVariables) throws OperatorInsufficientDefinitionException {


    String dimURI = DataHandlerUtils.getOpDimensoinByURI(owlConnection, uri);
    String hierURI = DataHandlerUtils.getOpHierarchyByURI(owlConnection, uri);
    org.apache.jena.ontology.Individual ind = owlConnection.getModel().getIndividual(uri);

    if (!StringUtils.isEmpty(dimURI) && !StringUtils.isEmpty(hierURI)) {

      Dimension dim = (Dimension) mdSchema.getNode(dimURI);
      QB4OHierarchy hier = (QB4OHierarchy) mdSchema.getNode(hierURI);

      Individual condNode = owlConnection.getPropertyValueEncAsIndividual(ind,
          owlConnection.getModel().getObjectProperty(
              OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.OP_SLICE_COND_PROP_E));

      if (dim != null && hier != null) {
        IDimensionQualification dimToAS = new DimensionToAnalysisSituationImpl(dim, hier);

        ISliceSinglePosition<IDimensionQualification> sc;
        try {
          sc = DataHandlerUtils.readSliceConditoin(owlConnection, mdSchema, condNode, dimToAS,
              dimURI, hierURI, predicateGraph);
          AddDimensionSelectionOperator op = new AddDimensionSelectionOperator(uri,
              ind.getLocalName(), ind.getLabel("en"), ind.getComment("en"), dim, hier, sc);
          if (op.getCondition().getSignature().isVariable()) {
            nvVariables.add(op.getCondition());
          }
          return op;
        } catch (Exception e) {
          throwOperatorInsufficientDefinitionException("createAddDimSliceConditionOperatorFromURI",
              uri);
        }
      }
    }
    throwOperatorInsufficientDefinitionException("createAddDimSliceConditionOperatorFromURI", uri);
    return null;
  }

  @Override
  public AddDimTypedSliceConditoinOperator createAddDimSliceConditionTypedOperatorFromURI(
      String uri, String factURI, List<Variable> nvVariables)
      throws OperatorInsufficientDefinitionException {

    String dimURI = DataHandlerUtils.getOpDimensoinByURI(owlConnection, uri);
    String hierURI = DataHandlerUtils.getOpHierarchyByURI(owlConnection, uri);
    org.apache.jena.ontology.Individual ind = owlConnection.getModel().getIndividual(uri);

    if (!StringUtils.isEmpty(dimURI) && !StringUtils.isEmpty(hierURI)) {
      Dimension dim = (Dimension) mdSchema.getNode(dimURI);
      QB4OHierarchy hier = (QB4OHierarchy) mdSchema.getNode(hierURI);
      Individual condNode = owlConnection.getPropertyValueEncAsIndividual(ind,
          owlConnection.getModel().getObjectProperty(
              OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.OP_SLICE_COND_PROP_E));

      if (dim != null && hier != null) {
        IDimensionQualification dimToAS = new DimensionToAnalysisSituationImpl(dim, hier);
        ISliceSinglePosition<IDimensionQualification> sc;

        try {
          sc = DataHandlerUtils.readSliceConditoin(owlConnection, mdSchema, condNode, dimToAS,
              dimURI, hierURI, predicateGraph);
          AddDimTypedSliceConditoinOperator op = new AddDimTypedSliceConditoinOperator(uri,
              ind.getLocalName(), ind.getLabel("en"), ind.getComment("en"), dim, hier, sc);
          if (op.getCondition().getSignature().isVariable()) {
            nvVariables.add(op.getCondition());
          }
          return op;
        } catch (Exception e) {
          throwOperatorInsufficientDefinitionException("createAddDimSliceConditionOperatorFromURI",
              uri);
        }
      }
    }
    throwOperatorInsufficientDefinitionException("createAddDimSliceConditionOperatorFromURI", uri);
    return null;
  }


  public IPredicateGraph getPredicateGraph() {
    return predicateGraph;
  }

  public void setPredicateGraph(IPredicateGraph predicateGraph) {
    this.predicateGraph = predicateGraph;
  }

  public MappingRepInterface getMappRepInterface() {
    return mappRepInterface;
  }

  public void setMappRepInterface(MappingRepInterface mappRepInterface) {
    this.mappRepInterface = mappRepInterface;
  }

}
