package swag.data_handler;

import org.apache.jena.ontology.Individual;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.vocabulary.RDF;
import swag.analysis_graphs.execution_engine.Signature;
import swag.analysis_graphs.execution_engine.analysis_situations.*;
import swag.analysis_graphs.execution_engine.operators.RollUpToOperator;
import swag.md_elements.*;
import swag.predicates.IPredicateGraph;
import swag.predicates.LiteralCondition;

import java.util.List;

public class MDElementParameterCreator {


    public static boolean hasTypeAGOntology(RDFNode node, String typeUriInAGOntology, OWlConnection owlConnection){
        NodeIterator nodeItr = node.as(Individual.class).listPropertyValues(RDF.type);
        if (node.as(Individual.class).hasProperty(RDF.type, owlConnection.getModel()
                .getOntClass(OWLConnectionFactory.getAGNamespace(owlConnection) + typeUriInAGOntology))){
            return true;
        }
        return false;
    }



    public static LevelInAnalysisSituation readDiceLevel(RDFNode node, OWlConnection owlConnection, IDimensionQualification dimToAS,
                                                  MDSchema mdSchema, String dimensionURI, String hierarchyURI, String dicelevelURI){

        Individual binding = owlConnection.getPropertyValueEncAsIndividual(node.as(Individual.class),
                owlConnection.getModel().getProperty(
                        OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.BINDING));

        Individual parameter = owlConnection.getPropertyValueEncAsIndividual(node.as(Individual.class),
                owlConnection.getModel().getProperty(
                        OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.PARAMETER));

        LevelInAnalysisSituation level = null;

        if(hasTypeAGOntology(node, Constants.LEVEL_PAIR, owlConnection)) {

            if (binding != null){

            }else{
                level = new LevelInAnalysisSituation(
                        new Signature<IDimensionQualification>(dimToAS, ItemInAnalysisSituationType.DiceLevel,
                                VariableState.VARIABLE, parameter.getLocalName(), null));
            }


        }else{
            String idName = mdSchema.getIdentifyingNameFromUriAndDimensionAndHier(dicelevelURI,
                    dimensionURI, hierarchyURI);

            level = new LevelInAnalysisSituation(
                    (Level) mdSchema.getNode(idName), new Signature<IDimensionQualification>(dimToAS,
                    ItemInAnalysisSituationType.DiceLevel, VariableState.NON_VARIABLE, "", null));
        }
        return level;
    }

    public static IGranularitySpecification readGranularity(
            Individual granInd, OWlConnection owlConnection, IDimensionQualification dimToAS,
            MDSchema mdSchema,  String dimUri, String hierUri ,  List<Variable> nvVariables,
            String toGranularityURI){

        IGranularitySpecification gr = null;

        Individual binding = owlConnection.getPropertyValueEncAsIndividual(granInd.as(Individual.class),
                owlConnection.getModel().getProperty(
                        OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.BINDING));

        Individual parameter = owlConnection.getPropertyValueEncAsIndividual(granInd.as(Individual.class),
                owlConnection.getModel().getProperty(
                        OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.PARAMETER));

        if(hasTypeAGOntology(granInd, Constants.LEVEL_PAIR, owlConnection)){

            if(binding != null){

            }else{
                gr = new GranualritySpecificationImpl();
                LevelInAnalysisSituation level =
                        new LevelInAnalysisSituation(new Signature<IDimensionQualification>(dimToAS,
                                ItemInAnalysisSituationType.GranularityLevel, VariableState.VARIABLE,
                                granInd.getLocalName(), null));

                gr.setGranularityLevel(level);

                if (gr.getPosition() != null && gr.getPosition().getSignature().isVariable())
                    nvVariables.add(gr.getPosition());
            }
        }else{
            String idName = mdSchema.getIdentifyingNameFromUriAndDimensionAndHier(toGranularityURI,
                    dimUri, hierUri);

            Level grlevel = (Level) mdSchema.getNode(idName);
            if (grlevel != null) {
                LevelInAnalysisSituation level = new LevelInAnalysisSituation(grlevel,
                        new Signature<IDimensionQualification>(dimToAS,
                                ItemInAnalysisSituationType.GranularityLevel, VariableState.NON_VARIABLE, "",
                                null));
                gr = new GranualritySpecificationImpl(level);
            }
        }
        return gr;
    }

    /**
     *
     * @param node
     * @param owlConnection
     * @param dimToAS
     * @return
     */
    public static DiceNodeInAnalysisSituation readDiceNode(RDFNode node, OWlConnection owlConnection, IDimensionQualification dimToAS){

        Individual binding = owlConnection.getPropertyValueEncAsIndividual(node.as(Individual.class),
                owlConnection.getModel().getProperty(
                        OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.BINDING));

        Individual parameter = owlConnection.getPropertyValueEncAsIndividual(node.as(Individual.class),
                owlConnection.getModel().getProperty(
                        OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.PARAMETER));

        DiceNodeInAnalysisSituation diceNode = null;

        if(hasTypeAGOntology(node, Constants.DICE_NODE_PAIR, owlConnection)) {

            if (binding != null){

            }else{
                diceNode = new DiceNodeInAnalysisSituation(
                        new Signature<IDimensionQualification>(dimToAS, ItemInAnalysisSituationType.DiceNode,
                                VariableState.VARIABLE, parameter.as(Individual.class).getLocalName(), null));
            }


        }else{
            diceNode =
                    new DiceNodeInAnalysisSituation(node.as(Individual.class).getURI(),
                            new Signature<IDimensionQualification>(dimToAS,
                                    ItemInAnalysisSituationType.DiceNode, VariableState.NON_VARIABLE, node.as(Individual.class).getURI(),
                                    null));
        }
        return diceNode;

    }

    /**
     *
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

        ISliceSinglePosition<IDimensionQualification> sc = null;

        try {
            if (sliceSpecificationIndivNode == null) {
                return null;
            }
            Individual granIndiv = sliceSpecificationIndivNode.as(Individual.class);
            if (granIndiv == null) {
                return null;
            }

            Individual binding = owlConnection.getPropertyValueEncAsIndividual(sliceSpecificationIndivNode.as(Individual.class),
                    owlConnection.getModel().getProperty(
                            OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.BINDING));

            Individual parameter = owlConnection.getPropertyValueEncAsIndividual(sliceSpecificationIndivNode.as(Individual.class),
                    owlConnection.getModel().getProperty(
                            OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.PARAMETER));

            if(hasTypeAGOntology(sliceSpecificationIndivNode, Constants.DIM_PREDICATE_PAIR, owlConnection)) {

                if (binding != null){

                }else{

                    String type = readPredicateType(owlConnection, granIndiv);

                    // Typed Slice Condition
                    if (type != null) {
                        sc = new SliceConditionTyped<IDimensionQualification>(
                                new Signature<IDimensionQualification>(dimToAS,
                                        ItemInAnalysisSituationType.sliceCondition, VariableState.VARIABLE,
                                        parameter.getLocalName(), null),
                                type);
                    } else {
                        sc = new SliceCondition<IDimensionQualification>(new Signature<IDimensionQualification>(
                                dimToAS, ItemInAnalysisSituationType.sliceCondition, VariableState.VARIABLE,
                                parameter.getLocalName(), null));
                    }
                }
            }else{
                try {
                    sc = readDimConditionFromPredicatesGraph(sliceSpecificationIndivNode, predicateGraph,
                            dimToAS, dimensinoURI, hierarchyURI, mdSchema);
                } catch (Exception ex) {
                    throw (ex);
                }

            }
        } catch (Exception ex) {
            sc = null;
        }
        return sc;
    }

    /**
     *
     * @param owlConnection
     * @param mdSchema
     * @param sliceSpecificationIndivNode
     * @param dimToAS
     * @param uri
     * @param predicateGraph
     * @return
     * @param <T>
     * @throws Exception
     */
    public static <T extends ISignatureType> ISliceSinglePosition<T> readMeasureCondsAndFiltersSliceConditoin(
            OWlConnection owlConnection, MDSchema mdSchema, Individual sliceSpecificationIndivNode,
            T dimToAS, String uri, IPredicateGraph predicateGraph) throws Exception {

        ISliceSinglePosition<T> sc = null;

        if (sliceSpecificationIndivNode != null) {
            Individual granIndiv = sliceSpecificationIndivNode.as(Individual.class);

            if (granIndiv != null) {

                Individual binding = owlConnection.getPropertyValueEncAsIndividual(sliceSpecificationIndivNode.as(Individual.class),
                        owlConnection.getModel().getProperty(
                                OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.BINDING));

                Individual parameter = owlConnection.getPropertyValueEncAsIndividual(sliceSpecificationIndivNode.as(Individual.class),
                        owlConnection.getModel().getProperty(
                                OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.PARAMETER));

                if (hasTypeAGOntology(sliceSpecificationIndivNode, Constants.RES_PREDICATE_PAIR, owlConnection)) {

                    if (binding != null) {

                    } else {
                        String type = readPredicateType(owlConnection, granIndiv);

                        // Typed Slice Condition
                        if (type != null) {
                            sc = new SliceConditionTyped<T>(
                                    new Signature<T>(dimToAS, ItemInAnalysisSituationType.sliceCondition,
                                            VariableState.VARIABLE, granIndiv.getLocalName(), null),
                                    type);
                        } else {
                            sc = new SliceCondition<T>(
                                    new Signature<T>(dimToAS, ItemInAnalysisSituationType.sliceCondition,
                                            VariableState.VARIABLE, granIndiv.getLocalName(), null));
                        }
                    }
                } else {
                    try {
                        sc = readConditionFromPredicatesGraph(sliceSpecificationIndivNode, predicateGraph,
                                dimToAS);
                    } catch (Exception ex) {
                        throw (ex);
                    }
                }
            }
            if (sc != null) {
                return sc;
            }
        }

        return null;
    }

    /**
     * @param owlConnection
     * @param typeIndiv
     * @return
     */
    public static String readPredicateType(OWlConnection owlConnection, Individual typeIndiv) {

        Individual typeInd = owlConnection.getPropertyValueEncAsIndividual(typeIndiv,
                owlConnection.getModel().getProperty(
                        OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.VARIABLE_DOMAIN));

        Property instanceOf = owlConnection.getModel()
                .getProperty(OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.INSTANCE_OF);

        if (typeInd != null && typeInd.hasProperty(instanceOf)) {
            Individual pred = owlConnection.getPropertyValueEncAsIndividual(typeInd, instanceOf);
            return pred.getURI();
        }
        return null;
    }

    /**
     * @param sliceSpecificationIndivNode
     * @param predicateGraph
     * @param dimToAS
     * @return
     * @throws Exception
     */
    private static ISliceSinglePosition<IDimensionQualification> readDimConditionFromPredicatesGraph(
            Individual sliceSpecificationIndivNode, IPredicateGraph predicateGraph,
            IDimensionQualification dimToAS, String dimensionUri, String hierarchyUri, MDSchema schema)
            throws Exception {

        LiteralCondition cond = predicateGraph.getNodeG(sliceSpecificationIndivNode.getURI());
        if (cond != null) {
            MDElement elm = null;
            elm = cond.getMdElems().stream()
                    .filter(e -> (schema.getDimensoinOfLevelOrDescriptor(e.getIdentifyingName()).equals(dimensionUri)
                            && (schema.getHierarchyOfLevelOrDescriptor(e.getIdentifyingName()).equals(hierarchyUri)) || hierarchyUri.equals(DefaultHierarchy.getDefaultHierarchy().getURI())))
                    .findAny().orElse(null);

            if (elm != null) {
                return new SliceCondition<IDimensionQualification>(elm, cond.getExpression(), cond.getURI(),
                        new Signature<IDimensionQualification>(dimToAS,
                                ItemInAnalysisSituationType.sliceCondition, VariableState.NON_VARIABLE, "", null),
                        SliceConditionStatus.NON_WRITTEN);
            } else {
                throw new Exception("MD element is badly configured for condition " + cond.getURI());
            }
        } else {
            throw new Exception("Cannot find condition " + sliceSpecificationIndivNode.getURI());
        }
    }

    /**
     * @param sliceSpecificationIndivNode
     * @param predicateGraph
     * @param dimToAS
     * @return
     * @throws Exception
     */
    private static <T extends ISignatureType> ISliceSinglePosition<T> readConditionFromPredicatesGraph(
            Individual sliceSpecificationIndivNode, IPredicateGraph predicateGraph, T dimToAS)
            throws Exception {

        LiteralCondition cond = predicateGraph.getNodeG(sliceSpecificationIndivNode.getURI());
        if (cond != null) {
            MDElement elm = null;
            for (MDElement e : cond.getMdElems()) {
                elm = e;
                break;
            }
            if (elm != null) {
                return new SliceCondition<T>(elm,
                        cond.getExpression(), cond.getURI(), new Signature<T>(dimToAS,
                        ItemInAnalysisSituationType.sliceCondition, VariableState.NON_VARIABLE, "", null),
                        SliceConditionStatus.NON_WRITTEN);
            } else {
                throw new Exception("MD element is badly configured for condition " + cond.getURI());
            }
        } else {
            throw new Exception("Cannot find condition " + sliceSpecificationIndivNode.getURI());
        }
    }

}
