package swag.data_handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.jena.ontology.Individual;
import org.apache.jena.rdf.model.NodeIterator;

import swag.analysis_graphs.dao.IMDSchemaDAO;
import swag.analysis_graphs.execution_engine.analysis_situations.AggregationFunction;
import swag.analysis_graphs.execution_engine.analysis_situations.MeasureAggregated;
import swag.analysis_graphs.execution_engine.analysis_situations.MeasureDerived;
import swag.md_elements.MDElement;
import swag.md_elements.MDRelation;
import swag.md_elements.MappableRelationFactory;
import swag.md_elements.Mapping;
import swag.md_elements.Measure;

public abstract class MDSchemaBuilderAbstract implements IMDSchemaDAO {

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

}
