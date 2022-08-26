package swag.data_handler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;

import swag.analysis_graphs.execution_engine.Signature;
import swag.analysis_graphs.execution_engine.analysis_situations.DiceNodeInAnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.IDiceSpecification;
import swag.analysis_graphs.execution_engine.analysis_situations.IDimensionQualification;
import swag.analysis_graphs.execution_engine.analysis_situations.ISignatureType;
import swag.analysis_graphs.execution_engine.analysis_situations.ISliceSinglePosition;
import swag.analysis_graphs.execution_engine.analysis_situations.ItemInAnalysisSituationType;
import swag.analysis_graphs.execution_engine.analysis_situations.LevelInAnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.PredicateInASSimple;
import swag.analysis_graphs.execution_engine.analysis_situations.PredicateVariableToMDElementMapping;
import swag.analysis_graphs.execution_engine.analysis_situations.SliceCondition;
import swag.analysis_graphs.execution_engine.analysis_situations.SliceConditionStatus;
import swag.analysis_graphs.execution_engine.analysis_situations.SliceConditionTyped;
import swag.analysis_graphs.execution_engine.analysis_situations.VariableState;
import swag.md_elements.DefaultHierarchy;
import swag.md_elements.Dimension;
import swag.md_elements.HierarchyInDimension;
import swag.md_elements.Level;
import swag.md_elements.MDElement;
import swag.md_elements.MDSchema;
import swag.md_elements.Measure;
import swag.md_elements.QB4OHierarchy;
import swag.predicates.IPredicateGraph;
import swag.predicates.LiteralCondition;
import swag.predicates.PredicateInstance;
import swag.predicates.PredicateVar;
import swag.sparql_builder.Configuration;
import swag.sparql_builder.ASElements.configuration.DimensionConfigurationObject;
import swag.sparql_builder.ASElements.configuration.DimensionIncompleteConfigurationType;
import swag.sparql_builder.ASElements.configuration.DimensionNonStrictConfigurationType;
import swag.sparql_builder.ASElements.configuration.MeasureConfigurationObject;
import swag.sparql_builder.ASElements.configuration.MsrNonStrictConfigurationType;

public class DataHandlerUtils {

  private static final org.apache.log4j.Logger logger =
      org.apache.log4j.Logger.getLogger(DataHandlerUtils.class);

  /**
   * Gets a dimension of an individual. Uses property {@code Constants.ON_DIMENSION_E}
   * 
   * @param owlConnection the OWL connection at hand
   * @param ind the individual to get the dimension to
   * 
   * @return uri of the dimension
   */
  public static String getDimensoin(OWlConnection owlConnection,
      org.apache.jena.ontology.Individual ind) {
    return owlConnection.getPropertyValueEncAsString(ind,
        owlConnection.getModel().getObjectProperty(
            OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.ON_DIMENSION_E));
  }

  /**
   * Gets a hierarchy of an individual. Uses property {@code Constants.ON_HIERARCHY_E}
   * 
   * @param owlConnection the OWL connection at hand
   * @param ind the individual to get the hierarchy to
   * 
   * @return uri of the hierarchy
   */
  public static String getHierarchy(OWlConnection owlConnection,
      org.apache.jena.ontology.Individual ind) {

    if (Configuration.getInstance().is("singleHierarchy")) {
      return DefaultHierarchy.getDefaultHierarchy().getURI();
    }

    return owlConnection.getPropertyValueEncAsString(ind,
        owlConnection.getModel().getObjectProperty(
            OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.ON_HIERARCHY_E));

  }

  /**
   * Gets a level of an individual. Uses property {@code Constants.ON_LEVEL_E}
   * 
   * @param owlConnection the OWL connection at hand
   * @param ind the individual to get the hierarchy to
   * 
   * @return uri of the level
   */
  public static String getOnLevel(OWlConnection owlConnection,
      org.apache.jena.ontology.Individual ind) {

    return owlConnection.getPropertyValueEncAsString(ind,
        owlConnection.getModel().getObjectProperty(
            OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.ON_LEVEL_E));
  }

  /**
   * Gets a hierarchy of an individual. Uses property {@code Constants.MEASURE_E}
   * 
   * @param owlConnection the OWL connection at hand
   * @param ind the individual to get the measure to
   * 
   * @return uri of the measure
   */
  public static String getMeasure(OWlConnection owlConnection,
      org.apache.jena.ontology.Individual ind) {

    String msrURI =
        owlConnection.getPropertyValueEncAsString(ind, owlConnection.getModel().getObjectProperty(
            OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.MEASURE_E));

    return msrURI;
  }

  /**
   * Gets a level of an individual. Uses property {@code Constants.GRANULARITY_LEVEL_E}
   * 
   * @param owlConnection the OWL connection at hand
   * @param ind the individual to get the level to
   * 
   * @return uri of the level
   */
  public static String getLevel(OWlConnection owlConnection,
      org.apache.jena.ontology.Individual ind) {

    String levelURI =
        owlConnection.getPropertyValueEncAsString(ind, owlConnection.getModel().getObjectProperty(
            OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.GRANULARITY_LEVEL_E));

    return levelURI;
  }

  /**
   * Gets a level individual of an individual. Uses property {@code Constants.GRANULARITY_LEVEL_E}
   * 
   * @param owlConnection the OWL connection at hand
   * @param ind the individual to get the level individual to
   * 
   * @return individual of the level
   */
  public static Individual getLevelInd(OWlConnection owlConnection,
      org.apache.jena.ontology.Individual ind) {

    Individual level = owlConnection.getPropertyValueEncAsIndividual(ind,
        owlConnection.getModel().getObjectProperty(
            OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.GRANULARITY_LEVEL_E));

    return level;
  }

  /**
   * Gets a dimension individual of an individual. Uses property {@code Constants.ON_DIMENSION_E}
   * 
   * @param owlConnection the OWL connection at hand
   * @param ind the individual to get the dimension to
   * 
   * @return individual of the dimension
   */
  public static String getOpDimensoin(OWlConnection owlConnection,
      org.apache.jena.ontology.Individual ind) {
    String dimensinoURI =
        owlConnection.getPropertyValueEncAsString(ind, owlConnection.getModel().getObjectProperty(
            OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.OP_ON_DIMENSION));
    return dimensinoURI;

  }

  /**
   * Gets a operation hierarchy of an individual. Uses property {@code Constants.OP_ON_HIERARCHY}
   * 
   * @param owlConnection the OWL connection at hand
   * @param ind the individual to get the hierarchy to
   * 
   * @return uri of the hierarchy
   */
  public static String getOpHierarchy(OWlConnection owlConnection,
      org.apache.jena.ontology.Individual ind) {

    String hierarchyURI =
        owlConnection.getPropertyValueEncAsString(ind, owlConnection.getModel().getObjectProperty(
            OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.OP_ON_HIERARCHY));

    return hierarchyURI;
  }

  /**
   * Gets a dimension individual of an individual. Uses property {@code Constants.OP_ON_DIMENSION_E}
   * 
   * @param owlConnection the OWL connection at hand
   * 
   * @return individual of the dimension
   */
  public static String getOpDimensoinByURI(OWlConnection owlConnection, String uri) {
    String dimensinoURI = "";
    org.apache.jena.ontology.Individual ind = owlConnection.getModel().getIndividual(uri);

    if (ind != null) {
      dimensinoURI =
          owlConnection.getPropertyValueEncAsString(ind, owlConnection.getModel().getObjectProperty(
              OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.OP_ON_DIMENSION_E));
    }

    return dimensinoURI;
  }

  /**
   * Gets a operation hierarchy of an individual. Uses property {@code Constants.OP_ON_HIERARCHY_E}
   * 
   * @param owlConnection the OWL connection at hand
   * 
   * @return uri of the hierarchy
   */
  public static String getOpHierarchyByURI(OWlConnection owlConnection, String uri) {
	  

	    if (Configuration.getInstance().is("singleHierarchy")) {
	      return DefaultHierarchy.getDefaultHierarchy().getURI();
	    }

    String hierarchyURI = "";
    org.apache.jena.ontology.Individual ind = owlConnection.getModel().getIndividual(uri);

    if (ind != null) {
      hierarchyURI =
          owlConnection.getPropertyValueEncAsString(ind, owlConnection.getModel().getObjectProperty(
              OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.OP_ON_HIERARCHY_E));
    }
    return hierarchyURI;
  }

  /**
   * Gets a level of an individual. Uses property {@code Constants.OP_TO_LEVEL_E}
   * 
   * @param owlConnection the OWL connection at hand
   * 
   * @return uri of the level
   */
  public static String getOpToLevelByURI(OWlConnection owlConnection, String uri) {

    String navToLevelNode = "";
    org.apache.jena.ontology.Individual ind = owlConnection.getModel().getIndividual(uri);

    if (ind != null) {
      navToLevelNode =
          owlConnection.getPropertyValueEncAsString(ind, owlConnection.getModel().getObjectProperty(
              OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.OP_TO_LEVEL_E));
    }
    return navToLevelNode;
  }

  /**
   * Creates a dice specification given the individual of the specification.
   * 
   * @param owlConnection the OWL connection
   * @param dimensinoURI the URI of the dimension on which the specification is
   * @param hierarchyURI the URI of the hierarchy on which the specification is
   * @param mdSchema the MD schema
   * @param diceSpecificationIndivNode
   * @param dimToAS
   * 
   * @return a new dice specification
   */
  public static IDiceSpecification createDiceSpecification(OWlConnection owlConnection,
      String dimensinoURI, String hierarchyURI, MDSchema mdSchema,
      RDFNode diceSpecificationIndivNode, IDimensionQualification dimToAS,
      IDiceSpecification diceSpec, RDFNode diceLevelNode, RDFNode diceNodeNode) {

    // Dice level
    if (diceLevelNode != null) {
      String dicelevelURI = mdSchema.getIdentifyingNameFromUriAndDimensionAndHier(
              diceLevelNode.as(Individual.class).getURI(), dimensinoURI, hierarchyURI);

      LevelInAnalysisSituation level = MDElementParameterCreator.readDiceLevel(diceLevelNode, owlConnection, dimToAS,
              mdSchema, dimensinoURI, hierarchyURI, dicelevelURI);
      diceSpec.setDiceLevelInAnalysisSituation(level);
    }

    // Dice node
    if (diceNodeNode != null) {
      String diceNodeURI = diceNodeNode.as(Individual.class).getURI();


        DiceNodeInAnalysisSituation diceNode = MDElementParameterCreator.readDiceNode(diceNodeNode, owlConnection, dimToAS);
        diceSpec.setDiceNodeInAnalysisSituation(diceNode);


    }
    return diceSpec;
  }




  /**
   * @param owlConnection
   * @param mdSchema
   * @param sliceSpecificationIndivNode
   * @param dimToAS
   * @param dimensinoURI
   * @param hierarchyURI
   * @param predicateGraph
   * @return
   * @throws Exception
   */
  public static ISliceSinglePosition<IDimensionQualification> readSliceConditoin(
      OWlConnection owlConnection, MDSchema mdSchema, Individual sliceSpecificationIndivNode,
      IDimensionQualification dimToAS, String dimensinoURI, String hierarchyURI,
      IPredicateGraph predicateGraph) throws Exception {

    return MDElementParameterCreator.readSliceConditoin(owlConnection,  mdSchema,  sliceSpecificationIndivNode,
            dimToAS,  dimensinoURI, hierarchyURI, predicateGraph);
  }

  /**
   * @param owlConnection
   * @param mdSchema
   * @param sliceSpecificationIndivNode
   * @param dimToAS
   * @param predicateGraph
   * @return
   * @throws Exception
   */
  public static <T extends ISignatureType> ISliceSinglePosition<T> readMeasureCondsAndFiltersSliceConditoin(
      OWlConnection owlConnection, MDSchema mdSchema, Individual sliceSpecificationIndivNode,
      T dimToAS, String uri, IPredicateGraph predicateGraph) throws Exception {

    return MDElementParameterCreator.readMeasureCondsAndFiltersSliceConditoin(owlConnection, mdSchema,
            sliceSpecificationIndivNode,
            dimToAS,  uri,  predicateGraph);
  }




  /**
   * Use {@link readConditionFromPredicatesGraph} instead
   * 
   * @param sliceSpecificationIndivNode
   * @param predicateGraph
   * @param dimToAS
   * @param owlConnection
   * @param mdSchema
   * @param granIndiv
   * @return
   */
  @Deprecated
  private static <T extends ISignatureType> ISliceSinglePosition<T> readConditionAgainWitoutPredicateGraph(
      Individual sliceSpecificationIndivNode, IPredicateGraph predicateGraph, T dimToAS,
      OWlConnection owlConnection, MDSchema mdSchema, Individual granIndiv) {

    ISliceSinglePosition<T> sc;
    RDFNode sicePosNode = owlConnection.getPropertyValueEncAsIndividual(
        sliceSpecificationIndivNode.as(Individual.class),
        owlConnection.getModel().getObjectProperty(
            OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.PREDICATE_MD_ELEMENT));

    MDElement elm = null;

    if (sicePosNode != null) {
      Individual sicePosMDNode = owlConnection.getPropertyValueEncAsIndividual(
          sliceSpecificationIndivNode.as(Individual.class),
          owlConnection.getModel().getObjectProperty(
              OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.PREDICATE_MD_ELEMENT));
      elm = mdSchema.getNode(sicePosNode.as(Individual.class).getURI());

      if (elm == null) {
        try {
          elm = DataHandlerUtils.readFlatComplexMDElement(sicePosNode.as(Individual.class),
              mdSchema, owlConnection);
        } catch (Exception e) { // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }

    String conditoin = owlConnection.getPropertyValueEncAsString(granIndiv.as(Individual.class),
        owlConnection.getModel().getDatatypeProperty(
            OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.PREDICATE_EXPRESSION));

    if (conditoin != null) {

      return new SliceCondition<T>(elm,
          conditoin, granIndiv.getURI(), new Signature<T>(dimToAS,
              ItemInAnalysisSituationType.sliceCondition, VariableState.NON_VARIABLE, "", null),
          SliceConditionStatus.NON_WRITTEN);
    }
    return null;
  }

  @Deprecated
  private static ISliceSinglePosition<IDimensionQualification> readDimConditionAgainWitoutPredicateGraph(
      Individual sliceSpecificationIndivNode, IPredicateGraph predicateGraph,
      IDimensionQualification dimToAS, OWlConnection owlConnection, MDSchema mdSchema,
      Individual granIndiv, String dimensinoURI, String hierarchyURI) {

    RDFNode sicePosNode = owlConnection.getPropertyValueEncAsIndividual(
        sliceSpecificationIndivNode.as(Individual.class),
        owlConnection.getModel().getObjectProperty(
            OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.PREDICATE_MD_ELEMENT));

    MDElement elm = null;

    if (sicePosNode != null) {
      String elmStr = mdSchema.getIdentifyingNameFromUriAndDimensionAndHier(
          sicePosNode.as(Individual.class).getURI(), dimensinoURI, hierarchyURI);

      elm = mdSchema.getNode(elmStr);

      if (elm == null) {
        try {
          elm = DataHandlerUtils.readFlatComplexMDElement(sicePosNode.as(Individual.class),
              mdSchema, owlConnection);
        } catch (Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }
    String conditoin = owlConnection.getPropertyValueEncAsString(granIndiv.as(Individual.class),
        owlConnection.getModel().getDatatypeProperty(
            OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.PREDICATE_EXPRESSION));

    if (conditoin != null) {
      return new SliceCondition<IDimensionQualification>(elm, conditoin, granIndiv.getURI(),
          new Signature<IDimensionQualification>(dimToAS,
              ItemInAnalysisSituationType.sliceCondition, VariableState.NON_VARIABLE, "", null),
          SliceConditionStatus.NON_WRITTEN);
    }
    return null;
  }

  /**
   * @param ind
   * @param dimToAS
   * @return
   * @throws Exception
   */
  private static PredicateInASSimple handlePredicateSlicesNoPosition(
      Individual sliceSpecificationIndivNode, IDimensionQualification dimToAS, String hierarchyURI,
      OWlConnection owlConnection, MDSchema mdSchema, IPredicateGraph predicateGraph)
      throws Exception {

    PredicateInASSimple sliceSpec = null;

    if (sliceSpecificationIndivNode != null) {

      Individual sliceCondNode = sliceSpecificationIndivNode.as(Individual.class);

      if (sliceCondNode == null) {
        throw new Exception("Cannot find predicate instance ");
      }

      if (sliceCondNode.as(Individual.class).hasProperty(RDF.type, owlConnection.getClassByName(
          OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.PredicateInstanceClass))) {

        String setUri = owlConnection.getPropertyValueEncAsString(sliceCondNode,
            owlConnection.getModel().getProperty(
                OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.ON_SET));

        if (!sliceCondNode.hasProperty(RDF.type,
            owlConnection.getClassByName(OWLConnectionFactory.getAGNamespace(owlConnection)
                + Constants.PredicateInstanceClass))) {

        } else {

          /*
           * HierarchyInDimension hm = mdSchema.getHierarchyInDimensionNode(dimToAS.getD(),
           * dimToAS.getHierarchy());
           * 
           * SlicePositionInAnalysisSituation sp = new SlicePositionInAnalysisSituation(hm, new
           * DimensionToAnalysisSituationItemSignature(dimToAS,
           * ItemInAnalysisSituationType.slicePosition, VariableState.NON_VARIABLE, "", sliceSpec));
           * sliceSpec.setPosition(sp);
           */

          if (sliceCondNode.as(Individual.class).hasProperty(RDF.type,
              owlConnection.getModel().getOntClass(
                  OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.variable))) {

            sliceSpec = new PredicateInASSimple(new Signature<IDimensionQualification>(dimToAS,
                ItemInAnalysisSituationType.SlicePredicate, VariableState.VARIABLE,
                sliceCondNode.as(Individual.class).getLocalName(), sliceSpec));
          } else {

            RDFNode basePredicateNode = owlConnection.getPropertyValueEnc(
                sliceCondNode.as(Individual.class), owlConnection.getModel().getObjectProperty(
                    OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.ON_PREDICATE));

            if (basePredicateNode != null && basePredicateNode.as(Individual.class).hasProperty(
                RDF.type,
                owlConnection.getClassByName("http://www.dke.jku.ac.at/swag/pr#PredicateInstance"))) {

              Individual sliceCondIndiv = sliceCondNode.as(Individual.class);

              Individual predicateInd = owlConnection.getPropertyValueEncAsIndividual(
                  sliceCondIndiv, owlConnection.getModel().getProperty(
                      OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.ON_PREDICATE));

              if (predicateInd == null) {
                throw new Exception("Cannot find predicate instance " + sliceCondIndiv);
              }


              PredicateInstance inst =
                  (PredicateInstance) predicateGraph.getNode(predicateInd.getURI());

              if (inst == null) {
                throw new Exception("Cannot find predicate instance " + predicateInd);
              }



              org.apache.jena.rdf.model.StmtIterator mappingsItr =
                  sliceCondIndiv.listProperties(owlConnection.getModel()
                      .getProperty(OWLConnectionFactory.getAGNamespace(owlConnection)
                          + Constants.DIM_HAS_VAR_TO_ELEMENT_MAPPING));

              Set<PredicateVariableToMDElementMapping> mappings = new HashSet<>();

              MDElement elem = null;

              while (mappingsItr.hasNext()) {

                Statement mappingStmt = mappingsItr.nextStatement();
                RDFNode varToElemNode = mappingStmt.getObject();

                if (varToElemNode != null) {

                  Individual varToElemInd = varToElemNode.as(Individual.class);

                  Individual mdElemInd = owlConnection.getPropertyValueEncAsIndividual(varToElemInd,
                      owlConnection.getModel()
                          .getProperty(OWLConnectionFactory.getAGNamespace(owlConnection)
                              + Constants.DIM_HAS_MD_ELEM));



                  if (true/* mdElemInd.getURI().equals(ind.getURI()) */) {

                    Individual varInd = owlConnection.getPropertyValueEncAsIndividual(varToElemInd,
                        owlConnection.getModel()
                            .getProperty(OWLConnectionFactory.getAGNamespace(owlConnection)
                                + Constants.HAS_VAR));


                    String connectOver = owlConnection.getPropertyValueEncAsString(varToElemInd,
                        owlConnection.getModel()
                            .getProperty(OWLConnectionFactory.getAGNamespace(owlConnection)
                                + Constants.HAS_CONNECT_OVER));


                    if (mdElemInd == null || varInd == null) {
                      throw new Exception("Cannot find MD Element " + varToElemInd);
                    }


                    if (mdElemInd != null && varInd != null) {

                      elem = mdSchema.getNode(mdSchema.getIdentifyingNameFromUriAndDimensionAndHier(
                          mdElemInd.getURI(), dimToAS.getD().getIdentifyingName(), hierarchyURI));

                      if (elem == null) {
                        elem = DataHandlerUtils.readFlatComplexMDElement(mdElemInd, mdSchema,
                            owlConnection);
                      }

                      PredicateVar var = inst.getInstanceOf().getVariableByURI(varInd.getURI());

                      if (elem != null && var != null) {

                        mappings.add(new PredicateVariableToMDElementMapping(var, elem,
                            connectOver != null ? QueryFactory.create(connectOver) : null));
                      }
                    }
                  }
                }
                break;
              }

              if (elem == null) {
                throw new Exception("Cannot find an MD element for the predicate " + predicateInd);
              }

              sliceSpec = new PredicateInASSimple(mappings, inst.getUri(), inst,
                  new Signature<IDimensionQualification>(dimToAS,
                      ItemInAnalysisSituationType.SlicePredicate, VariableState.NON_VARIABLE, "",
                      sliceSpec),
                  elem);

            }
          }
        }

      }

    }
    return sliceSpec;

  }

  /**
   * Use {@code DataHandlerUtils.readFlatComplexMDElement} instead
   * 
   * @param mdElemInd
   * @param schema
   * @return
   * @throws Exception
   */
  @Deprecated
  public static MDElement readEncapsulatedComplexMDElement(Individual mdElemInd, MDSchema schema,
      OWlConnection owlConnection) throws Exception {

    boolean isAttribute = false;

    Individual onLevelInd = owlConnection.getPropertyValueEncAsIndividual(mdElemInd,
        owlConnection.getModel().getProperty(
            OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.ELEM_ON_LEVEL));

    Individual onAttributeInd = owlConnection.getPropertyValueEncAsIndividual(mdElemInd,
        owlConnection.getModel().getProperty(
            OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.ELEM_ON_ATTRIBUTE));

    Individual onLevelInHierarchyAndDimInd = null;
    if (onAttributeInd != null) {
      onLevelInHierarchyAndDimInd = owlConnection.getPropertyValueEncAsIndividual(mdElemInd,
          owlConnection.getModel().getProperty(OWLConnectionFactory.getAGNamespace(owlConnection)
              + Constants.ELEM_ON_LEVEL_IN_HIER_AND_DIM));
      isAttribute = true;
    }

    Individual onHierInDimInd = null;
    if (!isAttribute) {
      onHierInDimInd = owlConnection.getPropertyValueEncAsIndividual(mdElemInd,
          owlConnection.getModel().getProperty(
              OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.ELEM_ON_HIER_IN_DIM));
    } else {
      onHierInDimInd = owlConnection.getPropertyValueEncAsIndividual(onLevelInHierarchyAndDimInd,
          owlConnection.getModel().getProperty(
              OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.ELEM_ON_HIER_IN_DIM));
    }

    if (onLevelInd == null && (onAttributeInd == null || onLevelInHierarchyAndDimInd == null)
        || onHierInDimInd == null) {
      throw new Exception("MD element is badly configured. ");
    }


    Individual onHierInd = owlConnection.getPropertyValueEncAsIndividual(onHierInDimInd,
        owlConnection.getModel().getProperty(
            OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.ELEM_ON_DIM));

    Individual onDimInd = owlConnection.getPropertyValueEncAsIndividual(onHierInDimInd,
        owlConnection.getModel().getProperty(
            OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.ELEM_ON_HIER));

    if (onHierInd == null || onDimInd == null) {
      throw new Exception("Dimensoin or hierarchy of element is missing. ");
    }


    MDElement elem = null;

    if (!isAttribute) {
      elem = schema.getNode(schema.getIdentifyingNameFromUriAndDimensionAndHier(mdElemInd.getURI(),
          onDimInd.getURI(), onHierInd.getURI()));
    } else {
      elem = schema.getNode(schema.getLevelAttributeIdentifyingNameFromUriAndDimensionAndHier(
          mdElemInd.getURI(), schema.getIdentifyingNameFromUriAndDimensionAndHier(
              onLevelInHierarchyAndDimInd.getURI(), onDimInd.getURI(), onHierInd.getURI())));
    }
    return elem;
  }

  /**
   * @param mdElemInd
   * @param schema
   * @return
   * @throws Exception
   */
  public static MDElement readFlatComplexMDElement(Individual mdElemInd, MDSchema schema,
      OWlConnection owlConnection) throws Exception {

    Individual onLevelInd = null;
    Individual onAttributeInd = null;
    Individual onLevel = null;

    Individual onHierInd = owlConnection.getPropertyValueEncAsIndividual(mdElemInd,
        owlConnection.getModel().getProperty(
            OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.ELEM_IN_HIER_E));

    Individual onDimInd = owlConnection.getPropertyValueEncAsIndividual(mdElemInd,
        owlConnection.getModel().getProperty(
            OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.ELEM_IN_DIM_E));

    if (DataHandlerUtils.isOfType(mdElemInd.as(Individual.class),
        owlConnection.getModel().getOntClass(Constants.QUALIFIED_LEVEL_E))) {

      onLevelInd = owlConnection.getPropertyValueEncAsIndividual(mdElemInd,
          owlConnection.getModel().getProperty(
              OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.ELEM_ON_LEVEL_E));

      if (onHierInd == null || onDimInd == null || onLevelInd == null) {
        throw new Exception("MD element " + mdElemInd.getLocalName() + " is badly configured. ");
      }

      return schema.getNode(schema.getIdentifyingNameFromUriAndDimensionAndHier(onLevelInd.getURI(),
          onDimInd.getURI(), onHierInd.getURI()));

    } else {
      if (DataHandlerUtils.isOfType(mdElemInd.as(Individual.class),
          owlConnection.getModel().getOntClass(Constants.QUALIFIED_ATTRIBUTE_E))) {

        onAttributeInd = owlConnection.getPropertyValueEncAsIndividual(mdElemInd,
            owlConnection.getModel().getProperty(OWLConnectionFactory.getAGNamespace(owlConnection)
                + Constants.ELEM_ON_ATTRIBUTE_E));

        if (onAttributeInd != null) {
          onLevel = owlConnection.getPropertyValueEncAsIndividual(mdElemInd,
              owlConnection.getModel().getProperty(
                  OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.ELEM_IN_LEVEL_E));
        }

        if (onHierInd == null || onDimInd == null || onAttributeInd == null || onLevel == null) {
          throw new Exception("MD element " + mdElemInd.getLocalName() + " is badly configured. ");
        }

        return schema.getNode(
            schema.getLevelAttributeIdentifyingNameFromUriAndDimensionAndHier(mdElemInd.getURI(),
                schema.getIdentifyingNameFromUriAndDimensionAndHier(onLevel.getURI(),
                    onDimInd.getURI(), onHierInd.getURI())));
      } else {
        return schema.getNode(mdElemInd.getURI());
      }
    }
  }



  /**
   * @param mdElemInd
   * @param schema
   * @param owlConnection
   * @return
   * @throws Exception
   */
  public static MDElement readHierInDimension(Individual mdElemInd, MDSchema schema,
      OWlConnection owlConnection) throws Exception {

    Individual onLevelInd = owlConnection.getPropertyValueEncAsIndividual(mdElemInd,
        owlConnection.getModel().getProperty(
            OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.ELEM_ON_LEVEL));

    Individual onAttributeInd = owlConnection.getPropertyValueEncAsIndividual(mdElemInd,
        owlConnection.getModel().getProperty(
            OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.ELEM_ON_ATTRIBUTE));

    Individual onHierInDimInd = owlConnection.getPropertyValueEncAsIndividual(mdElemInd,
        owlConnection.getModel().getProperty(
            OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.ELEM_ON_HIER_IN_DIM));

    if (onLevelInd == null && onAttributeInd == null || onHierInDimInd == null) {
      throw new Exception("MD element is badly configured. ");
    }

    Individual onHierInd = owlConnection.getPropertyValueEncAsIndividual(onHierInDimInd,
        owlConnection.getModel().getProperty(
            OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.ELEM_ON_DIM));

    Individual onDimInd = owlConnection.getPropertyValueEncAsIndividual(onHierInDimInd,
        owlConnection.getModel().getProperty(
            OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.ELEM_ON_HIER));

    if (onHierInd == null || onDimInd == null) {
      throw new Exception("Dimensoin or hierarchy of element is missing. ");
    }

    MDElement elem =
        schema.getNode(schema.getIdentifyingNameFromUriAndDimensionAndHier(mdElemInd.getURI(),
            onDimInd.getURI(), onHierInd.getURI()));

    return elem;

  }

  /**
   * 
   * Reads measures' summarizability configurations
   * 
   * @param ind
   * @param owlConnection
   * @param schema
   * @return
   */
  public static List<MeasureConfigurationObject> readMSrConfigs(Individual ind,
      OWlConnection owlConnection, MDSchema schema) {

    org.apache.jena.rdf.model.StmtIterator it2 =
        ind.listProperties(owlConnection.getModel().getProperty(
            OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.MSR_SUMMARIZABILITY_E));

    List<MeasureConfigurationObject> confs = new ArrayList<>();
    while (it2.hasNext()) {
      Statement stmt = it2.nextStatement();
      RDFNode sliceSpecificationIndivNode = stmt.getObject();
      Individual configNode = sliceSpecificationIndivNode.as(Individual.class);

      try {
        confs.add(readMsrConfigurationMDElement(configNode, schema, owlConnection));
      } catch (Exception ex) {
        logger.warn(ex);
      }
    }
    return confs;
  }

  /**
   * @param mdElemInd
   * @param schema
   * @return
   * @throws Exception
   */
  public static MeasureConfigurationObject readMsrConfigurationMDElement(Individual mdElemInd,
      MDSchema schema, OWlConnection owlConnection) throws Exception {

    Individual onMsrInd =
        owlConnection.getPropertyValueEncAsIndividual(mdElemInd, owlConnection.getModel()
            .getProperty(OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.MEASURE_E));

    Measure msr = (Measure) schema.getNode(onMsrInd.getURI());

    if (msr == null) {
      throw new Exception("MD element for summarizability is badly configured.");
    }

    MeasureConfigurationObject dimConf = new MeasureConfigurationObject(msr);
    readMsrNonStrict(dimConf, mdElemInd, owlConnection);
    readMsrIncomplete(dimConf, mdElemInd, owlConnection);
    return dimConf;
  }

  /**
   * @param configurationMap
   * @param configNode
   */
  public static void readMsrNonStrict(MeasureConfigurationObject msrConf, Individual configNode,
      OWlConnection owlConnection) {

    Individual nonStrictNode = configNode.getPropertyValue(owlConnection.getModel().getProperty(
        OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.NON_STRICT)) != null ?

            configNode
                .getPropertyValue(owlConnection.getModel().getProperty(
                    OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.NON_STRICT))
                .as(Individual.class)
            : null;

    if (nonStrictNode != null) {

      String[] conf = getModeValpair(nonStrictNode, owlConnection);
      msrConf.setNonStrictConf(MsrNonStrictConfigurationType.getConfName(conf[0]));
      msrConf.setNonStrictVal(conf[1]);

    }
  }

  private static String[] getModeValpair(Individual nonStrictNode, OWlConnection owlConnection) {
    Individual nonStrictMode = nonStrictNode.getPropertyValue(owlConnection.getModel()
        .getProperty(OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.MODE)) != null
            ? nonStrictNode
                .getPropertyValue(owlConnection.getModel().getProperty(
                    OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.MODE))
                .as(Individual.class)
            : null;

    Individual nonStrictVal = nonStrictNode.getPropertyValue(owlConnection.getModel()
        .getProperty(OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.VAL)) != null
            ? nonStrictNode
                .getPropertyValue(owlConnection.getModel().getProperty(
                    OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.VAL))
                .as(Individual.class)
            : null;

    return new String[] {nonStrictMode != null ? nonStrictMode.getLocalName() : null,
        nonStrictVal != null ? nonStrictVal.getLocalName() : null};
  }

  /**
   * @param configurationMap
   * @param configNode
   */
  public static void readMsrIncomplete(MeasureConfigurationObject msrConf, Individual configNode,
      OWlConnection owlConnection) {

    Individual inCompletetNode = configNode.getPropertyValue(owlConnection.getModel().getProperty(
        OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.INCOMPLETE)) != null ?

            configNode
                .getPropertyValue(owlConnection.getModel().getProperty(
                    OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.INCOMPLETE))
                .as(Individual.class)
            : null;

    if (inCompletetNode != null) {
      String[] conf = getModeValpair(inCompletetNode, owlConnection);
      msrConf.setNonStrictConf(MsrNonStrictConfigurationType.getConfName(conf[0]));
      msrConf.setNonStrictVal(conf[1]);

    }
  }

  public static List<DimensionConfigurationObject> readDimConfigs(Individual ind,
      OWlConnection owlConnection, MDSchema schema) {

    org.apache.jena.rdf.model.StmtIterator it2 =
        ind.listProperties(owlConnection.getModel().getProperty(
            OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.DIM_SUMMARIZABILITY_E));

    List<DimensionConfigurationObject> confs = new ArrayList<DimensionConfigurationObject>();
    while (it2.hasNext()) {
      Statement stmt = it2.nextStatement();
      RDFNode sliceSpecificationIndivNode = stmt.getObject();
      Individual configNode = sliceSpecificationIndivNode.as(Individual.class);

      try {
        confs.add(readConfigurationMDElement(configNode, schema, owlConnection));
      } catch (Exception ex) {
        logger.warn(ex);
      }
    }
    return confs;
  }

  /**
   * @param mdElemInd
   * @param schema
   * @return
   * @throws Exception
   */
  public static DimensionConfigurationObject readConfigurationMDElement(Individual mdElemInd,
      MDSchema schema, OWlConnection owlConnection) throws Exception {

    String dimStr = getDimensoin(owlConnection, mdElemInd);
    String hierStr = getHierarchy(owlConnection, mdElemInd);
    String lvlStr = getLevel(owlConnection, mdElemInd);
    String msrStr = getMeasure(owlConnection, mdElemInd);

    Dimension dim = null;
    QB4OHierarchy hier = null;
    Level lvl = null;
    MDElement elm = null;

    if (dimStr == null) {
      throw new Exception(
          "MD element for summarizability is badly configured. no dimension specified...");
    }
    dim = (Dimension) schema.getNode(dimStr);

    if (hierStr != null) {
      hier = Optional.ofNullable(schema.getHierarchyInDimensionNode(dimStr, hierStr))
          .map(HierarchyInDimension::getHier).orElse(null);
    }

    if (lvlStr != null) {
      if (hier == null) {
        throw new Exception(
            "MD element for summarizability is badly configured. no hierarchy specified for level..."
                + lvlStr);
      }
      String lvlName = schema.getIdentifyingNameFromUriAndDimensionAndHier(lvlStr,
          hier.getIdentifyingName(), dim.getIdentifyingName());
      lvl = (Level) schema.getNode(lvlName);
    }

    elm = (lvl != null) ? lvl : (hier != null ? hier : (dim != null ? dim : null));

    if (elm == null) {
      throw new Exception("MD element for summarizability is badly configured.");
    }

    Measure msr = (Measure) schema.getNode(msrStr);

    DimensionConfigurationObject dimConf = new DimensionConfigurationObject(msr, elm, null, null);
    readDimNonStrict(dimConf, mdElemInd, owlConnection);
    readDimIncomplete(dimConf, mdElemInd, owlConnection);
    return dimConf;
  }

  /**
   * @param configurationMap
   * @param configNode
   */
  public static void readDimNonStrict(DimensionConfigurationObject dimConf, Individual configNode,
      OWlConnection owlConnection) {

    Individual nonStrictNode = configNode.getPropertyValue(owlConnection.getModel().getProperty(
        OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.NON_STRICT)) != null ?

            configNode
                .getPropertyValue(owlConnection.getModel().getProperty(
                    OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.NON_STRICT))
                .as(Individual.class)
            : null;

    if (nonStrictNode != null) {
      {
        String[] conf = getModeValpair(nonStrictNode, owlConnection);
        dimConf.setNonStrictConf(DimensionNonStrictConfigurationType.getConfName(conf[0]));
      }
    }
  }

  /**
   * @param configurationMap
   * @param configNode
   */
  public static void readDimIncomplete(DimensionConfigurationObject dimConf, Individual configNode,
      OWlConnection owlConnection) {

    Individual inCompletetNode = configNode.getPropertyValue(owlConnection.getModel().getProperty(
        OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.INCOMPLETE)) != null
            ? configNode
                .getPropertyValue(owlConnection.getModel().getProperty(
                    OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.INCOMPLETE))
                .as(Individual.class)
            : null;

    if (inCompletetNode != null) {
      {
        String[] conf = getModeValpair(inCompletetNode, owlConnection);
        dimConf.setIncompleteConf(DimensionIncompleteConfigurationType.getConfName(conf[0]));
      }
    }
  }

  /**
   * @param configurationMap
   * @param configNode
   */
  public static void readMandatory(Map<String, String> configurationMap, Individual configNode,
      OWlConnection owlConnection) {

    Individual mandatoryNode = configNode.getPropertyValue(
        owlConnection.getModel().getProperty(OWLConnectionFactory.getAGNamespace(owlConnection)
            + Constants.HAS_MANDATORY_MODE_MODE)) != null ?

                configNode.getPropertyValue(owlConnection.getModel()
                    .getProperty(OWLConnectionFactory.getAGNamespace(owlConnection)
                        + Constants.HAS_MANDATORY_MODE_MODE))
                    .as(Individual.class)
                : null;

    if (mandatoryNode != null) {
      configurationMap.put("mandatory", mandatoryNode.getLocalName());
    }
  }

  public static boolean isOfType(Individual indiv, OntClass cls) {

    if (indiv.hasProperty(RDF.type, cls)) {
      return true;
    }
    return false;
  }
  
  public static boolean isPair(RDFNode node1, OWlConnection owlConnection) {
	  node1.as(Individual.class).hasProperty(RDF.type,
	            owlConnection.getModel().getOntClass(
	                OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.PAIR_CLASS));	  
	  return false;
  }
  
  public static String getPairParameterName(RDFNode node1, OWlConnection owlConnection) {
	  return owlConnection.getPropertyValueEncAsString( node1.as(Individual.class),
	            owlConnection.getModel().getProperty(
	                OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.PARAMETER_PROP));
  }
}
