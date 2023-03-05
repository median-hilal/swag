package swag.data_handler;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.jena.ontology.Individual;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.NodeIterator;

import swag.analysis_graphs.dao.IMDSchemaDAO;
import swag.analysis_graphs.execution_engine.analysis_situations.AggregationFunction;
import swag.analysis_graphs.execution_engine.analysis_situations.MeasureAggregated;
import swag.analysis_graphs.execution_engine.analysis_situations.MeasureDerived;
import swag.analysis_graphs.execution_engine.analysis_situations.PredicateVariableToMDElementMapping;
import swag.md_elements.*;
import swag.predicates.FileBasedPredicateFunction;
import swag.predicates.PredicateInputVar;
import swag.predicates.QUERY_STRINGS;
import swag.predicates.Utils;

public abstract class MDSchemaBuilderAbstract implements IMDSchemaDAO {

	private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(MDSchemaBuilderAbstract.class);
    /**
     * Read derived measures defined for the schema
     * 
     * @param owlConnection
     *            the OWL connection
     * @param set
     *            the set of MD relations being built
     * @param sourceInd
     *            the fact individual
     * @param elem
     */
    public static final void handleDerivedMeasures(Set<Individual> msrIndivs, OWlConnection owlConnection,
	    Set<MDRelation> set, MDElement elem) {

	for (Individual indivNode : msrIndivs) {

	    if (DataHandlerUtils.isOfType(indivNode, owlConnection.getModel()
		    .getOntClass(OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.DERIVED_MEASURE))) {

		String uri = indivNode.getURI();
		String name = indivNode.getLocalName();
		String comment = indivNode.getComment("en");

		String expression = owlConnection.getPropertyValueEncAsString(indivNode,
			owlConnection.getModel().getProperty(OWLConnectionFactory.getAGNamespace(owlConnection)
				+ Constants.DERIVED_MEASURE_EXPRESSION));

		NodeIterator itrOfBaseMsrs = indivNode.listPropertyValues(owlConnection.getModel().getProperty(
			OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.DERIVED_MEASURE_MEASURE));

		List<Measure> msrs = new ArrayList<>();
		while (itrOfBaseMsrs.hasNext()) {
		    String nextMsrString = itrOfBaseMsrs.next().asNode().getURI();
		    msrs.add((Measure) gettargetOfMDRelation(set, nextMsrString));
		}

		MeasureDerived derived = new MeasureDerived(uri, name, comment, expression, msrs,
			indivNode.getLabel("en"));
		MDRelation rel;
		MDElement relElm;

		relElm = new MDElement(indivNode.getURI(), indivNode.getLocalName(), new Mapping(),
			indivNode.getLabel("en"));
		rel = MappableRelationFactory.createMappableRelation(Constants.HAS_MEASURE, relElm, elem, derived);
		set.add(rel);

	    }
	}
    }

    /**
     * Read aggregated measures defined for the schema
     * 
     * @param owlConnection
     *            the OWL connection
     * @param set
     *            the set of MD relations being built
     * @param sourceInd
     *            the fact individual
     * @param elem
     */
    public static final void handleAggregatedMeasures(Set<Individual> msrIndivs, OWlConnection owlConnection,
	    Set<MDRelation> set, MDElement elem) {

	for (Individual indivNode : msrIndivs) {

	    if (DataHandlerUtils.isOfType(indivNode, owlConnection.getModel()
		    .getOntClass(OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.AGG_MEASURE))) {

		String uri = indivNode.getURI();
		String name = indivNode.getLocalName();
		String comment = indivNode.getComment("en");

		String onMeasureStr = owlConnection.getPropertyValueEncAsString(indivNode,
			owlConnection.getModel().getProperty(
				OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.AGG_MEASURE_TO_DERIVED));

		MeasureDerived der = (MeasureDerived) gettargetOfMDRelation(set, onMeasureStr);

		String aggStr = owlConnection
			.getPropertyValueEncAsIndividual(indivNode,
				owlConnection.getModel().getProperty(Constants.QB4O_AGGREGATION_FUNCTION))
			.getLocalName();

		AggregationFunction agg = getAggregationFunctionFromLocalName(aggStr);

		MeasureAggregated aggregatedMsr = new MeasureAggregated(uri, name, comment, der, agg,
			indivNode.getLabel("en"));

		MDRelation rel;
		MDElement relElm;

		relElm = new MDElement(indivNode.getURI(), indivNode.getLocalName(), new Mapping(),
			indivNode.getLabel("en"));
		rel = MappableRelationFactory.createMappableRelation(Constants.HAS_MEASURE, relElm, elem,
			aggregatedMsr);
		set.add(rel);
	    }
	}
    }

	public static final void handleAggregatedMeasures1(Set<Individual> msrIndivs, OWlConnection owlConnection,
													  Set<MDRelation> set, MDElement elem) {

		for (Individual indivNode : msrIndivs) {

			if (DataHandlerUtils.isOfType(indivNode, owlConnection.getModel()
					.getOntClass(OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.AGG_MEASURE))) {

				String uri = indivNode.getURI();
				String name = indivNode.getLocalName();
				String comment = indivNode.getComment("en");

				String expr = owlConnection.getPropertyValueEncAsString(indivNode,
						owlConnection.getModel().getProperty(
								OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.PREDICATE_EXPRESSION));

				String onMeasureStr = owlConnection.getPropertyValueEncAsString(indivNode,
						owlConnection.getModel().getProperty(
								OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.AGG_MEASURE_TO_DERIVED));

				MeasureDerived der = (MeasureDerived) gettargetOfMDRelation(set, onMeasureStr);

				String aggStr = owlConnection
						.getPropertyValueEncAsIndividual(indivNode,
								owlConnection.getModel().getProperty(Constants.QB4O_AGGREGATION_FUNCTION))
						.getLocalName();

				AggregationFunction agg = getAggregationFunctionFromLocalName(aggStr);

				MeasureAggregated aggregatedMsr = new MeasureAggregated(uri, name, comment, der, agg,
						indivNode.getLabel("en"));

				MDRelation rel;
				MDElement relElm;

				relElm = new MDElement(indivNode.getURI(), indivNode.getLocalName(), new Mapping(),
						indivNode.getLabel("en"));
				rel = MappableRelationFactory.createMappableRelation(Constants.HAS_MEASURE, relElm, elem,
						aggregatedMsr);
				set.add(rel);
			}
		}
	}

    public static final MDElement gettargetOfMDRelation(Set<MDRelation> rels, String uri) {
	MDRelation relation = rels.stream().filter(rel -> rel.getTarget().getURI().equals(uri)).findFirst()
		.orElse(null);

	if (relation != null) {
	    return relation.getTarget();
	} else {
	    return null;
	}
    }

    public static final AggregationFunction getAggregationFunctionFromLocalName(String localName) {
	switch (localName) {
	case "avg":
	    return AggregationFunction.AVG;
	case "sum":
	    return AggregationFunction.SUM;
	case "count":
	    return AggregationFunction.COUNT;
	case "min":
	    return AggregationFunction.MIN;
	case "max":
	    return AggregationFunction.MAX;
	}
	return null;
    }

	/**
	 * Generates {@code Predicate} objects from an input result set. Skips
	 * conditions displaying errors in reading.
	 *
	 * @param res the result set to retrieve data from to build the objects.
	 * @return a {@code List} of swag.predicates
	 */
	public static void buildAggMeasures(MDSchema schema, Model model) {

		Set<MDRelation> set = new HashSet<>();

		String queryString = QUERY_STRINGS.AGG_MEASURES;
		Query query = QueryFactory.create(queryString);
		QueryExecution exec = QueryExecutionFactory.create(query, model);
		ResultSet res = exec.execSelect();


		List<MeasureAggregated> measuresAgg = new ArrayList<>();

		boolean firstTime = true;
		boolean shouldSkip = false;

		String expression = "";
		List<PredicateInputVar> vars = new ArrayList<>();
		List<MDElement> elements = new ArrayList<>();

		List<PredicateVariableToMDElementMapping> mappings = new ArrayList<>();

		String predicateURI = "";
		MeasureAggregated pred = null;

		while (res.hasNext()) {

			QuerySolution sol = res.next();

			if (!predicateURI.equals(sol.get("literalConditionType").toString())) {

				pred = new MeasureAggregated(predicateURI);
				measuresAgg.add(pred);

				predicateURI = Utils.getStringValueIfNotNull(sol.get("literalConditionType"));
				pred.setURI(predicateURI);

				String label = Utils.getStringValueIfNotNull(sol.get("label"));
				pred.setName(label == null ? "" : label);
				pred.setLabel(label == null ? "" : label);

				String comment = Utils.getStringValueIfNotNull(sol.get("comment"));
				pred.setComment(comment == null ? "" : comment);

				expression = Utils.getStringValueIfNotNull(sol.get("expression"));

				shouldSkip = false;

			}

			if (!shouldSkip) {

				firstTime = false;


				String positionVar = sol.get("positionVar") != null ? sol.get("positionVar").toString() : null;
				String positionVarType = sol.get("positionVarType") != null ? sol.get("positionVarType").toString()
						: null;
				String positionVarName = sol.get("positionVarName") != null ? sol.get("positionVarName").toString()
						: null;

				PredicateInputVar var;
				if (positionVar != null && positionVarName != null) {
					var = new PredicateInputVar(positionVarType, positionVar, positionVarName);
					vars.add(var);
				} else {
					logger.warn("Cannot read a position for condition type " + predicateURI);
					shouldSkip = true;
					continue;
				}

				try {
					for (MDElement elem : FileBasedPredicateFunction.getMDElementFormSolution(sol, schema)) {
						elements.add(elem);
						mappings.add(new PredicateVariableToMDElementMapping(var, elem, null));
					}
				} catch (Exception ex) {
					logger.warn("Cannot read position for condition type" + predicateURI);
					shouldSkip = true;
					continue;
				}
			}

			Map<String, String> funcs = getAggFunctionsFromExpression(expression, vars.stream().map(v -> v.getVariable()).collect(Collectors.toList()));

			String key = funcs.keySet().stream().findAny().orElse("");
			String str = funcs.get(key);
			AggregationFunction aggFunction = AggregationFunction.valueOf(str);

			pred.setAgg(aggFunction);
			MDElement baseMeasure = mappings.get(0).getElem();
			MeasureDerived derivedMeasure = new MeasureDerived(baseMeasure.getURI(), baseMeasure.getName(),
					baseMeasure.getLabel(), "?" + baseMeasure.getName(), baseMeasure.getLabel(), baseMeasure.getMapping());
			pred.setMeasure(derivedMeasure);

			MDRelation rel;
			MDElement relElm;

			relElm = new MDElement(pred.getURI(), pred.getName(), new Mapping(),
					pred.getLabel());
			rel = MappableRelationFactory.createMappableRelation(Constants.HAS_MEASURE, relElm, schema.getFactOfSchema(),
					pred);
			set.add(rel);

		}
		set.stream().forEach(e -> {
			schema.addEdge(e);
			schema.addNode(e.getTarget());
		});

	}

	private static Map<String, String> getAggFunctionsFromExpression(String expr, List<String> positions){

		Map<String, String> funcs = new HashMap<>();

		List<String> funcNames = Arrays.asList("SUM", "COUNT", "AVG", "MIN", "MAX" , "COUNT DISTINCT");

		for (String funcName : funcNames){
			for (String pos :  positions) {
				String strToCheck = funcName + " (" + pos + ")";
				if (expr.contains(strToCheck)) {
					funcs.put(pos, funcName);
					break;
				}
			}
		}

		return funcs;
	}


}
