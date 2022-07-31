package swag.sparql_builder.ASElements;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementTriplesBlock;
import org.apache.log4j.Logger;

import swag.analysis_graphs.execution_engine.AnalysisGraph;
import swag.analysis_graphs.execution_engine.analysis_situations.AnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.IDimensionQualification;
import swag.analysis_graphs.execution_engine.analysis_situations.IMeasureInAS;
import swag.analysis_graphs.execution_engine.analysis_situations.MeasureAggregatedInAS;
import swag.data_handler.Constants;
import swag.md_elements.MDElement;
import swag.sparql_builder.Configuration;
import swag.sparql_builder.CustomSPARQLQuery;
import swag.sparql_builder.SPARQLUtilities;
import swag.sparql_builder.ASElements.configuration.DimensionConfigurationObject;
import swag.sparql_builder.ASElements.configuration.MeasureConfigurationObject;
import swag.sparql_builder.reporting.AbstractMeasureReporter;
import swag.sparql_builder.reporting.IMeasureReoprter;

/**
 * 
 * Implementation of SPAQRL query generation from measure of analysis situation
 * 
 * @author swag
 *
 */
public class MeasureToSPARQLQueryGenerator implements IMeasureToSPARQLQueryGenerator {

	private static final Logger logger = Logger.getLogger(MeasureToSPARQLQueryGenerator.class);
	private List<IMeasureReoprter> reporters = new ArrayList<>();
	private IMeasureInAS measToAS;
	private AnalysisGraph ag;
	private ASElementSPARQLGenerator visitor;
	private Map<MDElement, String> mdElemToVarMap;
	private AnalysisSituation as;

	/**
	 * Creates a new {@link MeasureToSPARQLQueryGenerator} instance
	 * 
	 * @param msrConfig
	 *            the configuration of the measure
	 * @param graph
	 *            the analysis graph
	 * @param mdElemToVarMap
	 *            the current mapping from MD elements to query variables
	 */
	public MeasureToSPARQLQueryGenerator(AnalysisGraph graph, Map<MDElement, String> mdElemToVarMap,
			AnalysisSituation as, IMeasureInAS measToAS) {
		super();
		this.ag = graph;
		this.mdElemToVarMap = mdElemToVarMap;
		this.as = as;
		this.measToAS = measToAS;

		if (measToAS instanceof MeasureAggregatedInAS) {
			this.reporters = AbstractMeasureReporter.createReporter(ag.getSchema(), (MeasureAggregatedInAS) measToAS,
					as);
		} else {
			logger.warn(measToAS.getMeasure().getName() + " is not aggregated measure; no reporting will be done");
		}

		this.visitor = new ASElementSPARQLGenerator(graph, mdElemToVarMap);
	}

	@Override
	public CustomSPARQLQuery generateSPARQLFromMeasToAS() throws Exception {

		String aggregationFunction = null;

		Optional<MeasureConfigurationObject> conf = MeasureConfigurationObject.getMatchingConf(as.getMsrConfigs(),
				measToAS.getMeasure());

		if (conf.isPresent() && conf.get().isNonStrictInternAgg()) {
			aggregationFunction = conf.get().getNonStrictVal().toString();
		}

		CustomSPARQLQuery subQuery = CustomSPARQLQuery.createCustomSPARQLQueryWithEmptyGroup();

		measToAS.getMeasure().acceptVisitor(getVisitor());
		List<String> queryStrs = getVisitor().getReturn();
		Query q = QueryFactory.create(queryStrs.get(0));

		if (aggregationFunction != null && !StringUtils.equalsIgnoreCase(aggregationFunction, "TREAT_AS_FACT")) {

			List<String> nameOfCreatedVariable = new ArrayList<>();
			subQuery = SPARQLUtilities.createAggregationQuery(subQuery, aggregationFunction, q.getProjectVars().get(0),
					nameOfCreatedVariable);

			// Grouping by the fact variable when needed
			subQuery.getSparqlQuery().getGroupBy().add(ag.getSchema().getFactOfSchema().getHeadVar());
		} else {
			subQuery.getSparqlQuery().getProjectVars().add(q.getProjectVars().get(0));
		}

		if (Configuration.getInstance().isReportingActive()) {
			for (IMeasureReoprter reporter : reporters) {
				subQuery = reporter.appendMeasureStatementVariablesAndExpressions(subQuery);
			}
		}

		subQuery = subQuery
				.joinWithOnlyPatternsAndGroupByAsGroups(ag.getSchema().getFactOfSchema().getMapping().getQuery());

		if ((conf.isPresent() && conf.get().isIncompleteOptional(reporters))) {
			subQuery = SPARQLUtilities.joinWithAsOptionalOnlyPatternAndGroupBy(subQuery, new CustomSPARQLQuery(q));
		} else {
			subQuery = subQuery.joinWithOnlyPatternsAndGroupByAsGroups(new CustomSPARQLQuery(q));
		}

		if (!StringUtils.isEmpty(as.getDataSet())) {

			Var vv = ag.getSchema().getFactOfSchema().getHeadVar();
			Model m = ModelFactory.createDefaultModel();
			Triple trp = Triple.create(vv, NodeFactory.createURI(Constants.QB_DATASET),
					NodeFactory.createURI((as.getDataSet())));

			ElementGroup elmGrp = new ElementGroup();
			elmGrp.addElement(subQuery.getSparqlQuery().getQueryPattern());
			ElementTriplesBlock trps = new ElementTriplesBlock();
			trps.addTriple(trp);
			elmGrp.addElement(trps);
			subQuery.getSparqlQuery().setQueryPattern(elmGrp);

		}

		return subQuery;
	}

	@Override
	public CustomSPARQLQuery generateOuterMostAggVars(CustomSPARQLQuery subQuery, MeasureAggregatedInAS measToAS,
			List<Var> granCountVars, List<IDimensionQualification> dims, AnalysisSituation as) {

		CustomSPARQLQuery headerQuery = CustomSPARQLQuery.createCustomSPARQLQueryWithEmptyGroup();

		Optional<MeasureConfigurationObject> conf = MeasureConfigurationObject.getMatchingConf(as.getMsrConfigs(),
				measToAS.getMeasure());

		if (conf.isPresent() && conf.get().isIncompleteOptional(reporters)) {
			if (DimensionConfigurationObject.isAnyNonStrictSplit(as.getDimConfigs())) {

				String divisionString = "(";

				List<String> newGranCountVarNames = new java.util.ArrayList<>();

				for (Var v : granCountVars) {

					String tempFirstLetter = v.getName().substring(0, 1);
					tempFirstLetter = tempFirstLetter.toUpperCase();
					String varCountString = tempFirstLetter + v.getName().substring(1, v.getName().length());
					varCountString = "count" + varCountString;
					newGranCountVarNames.add(varCountString);
				}

				boolean firstIn = true;

				for (String str : newGranCountVarNames) {
					if (!firstIn) {
						divisionString += " * ";
					}
					divisionString += "?" + str;
				}
				divisionString += ")";

				List<String> nameOfCreatedVariable = new ArrayList<>();
				headerQuery = SPARQLUtilities.createAggregationQuery(headerQuery, measToAS.getAgg().name(),
						subQuery.getUnaryProjectionVar(), nameOfCreatedVariable);

				String queryString = headerQuery.getSparqlQuery().toString();
				if (newGranCountVarNames.size() > 0) {
					queryString = queryString.replaceFirst(subQuery.getNameOfUnaryProjectionVar(),
							subQuery.getNameOfUnaryProjectionVar() + " / " + divisionString);
				}
				headerQuery = new CustomSPARQLQuery(QueryFactory.create(queryString));

				mdElemToVarMap.put(measToAS.getMeasure(), nameOfCreatedVariable.get(0));

			} else {

				List<String> nameOfCreatedVariable = new ArrayList<>();

				headerQuery = SPARQLUtilities.createAggregationQuery(headerQuery, measToAS.getAgg().name(),
						subQuery.getUnaryProjectionVar(), nameOfCreatedVariable);

				mdElemToVarMap.put(measToAS.getMeasure(), nameOfCreatedVariable.get(0));
			}

		} else {
			List<String> nameOfCreatedVariable = new ArrayList<>();

			headerQuery = SPARQLUtilities.createAggregationQuery(headerQuery, measToAS.getAgg().name(),
					subQuery.getUnaryProjectionVar(), nameOfCreatedVariable);

			String queryStr = subQuery.getSparqlQuery().toString();

			mdElemToVarMap.put(measToAS.getMeasure(), nameOfCreatedVariable.get(0));
		}

		if (Configuration.getInstance().isReportingActive()) {
			for (IMeasureReoprter reporter : reporters) {
				headerQuery = reporter.appendMostOuterVariablesAndExpressions(headerQuery);
			}
		}
		return headerQuery;
	}

	@Override
	public IASElementGenerateSPARQLVisitor getVisitor() {
		return this.visitor;
	}

	public AnalysisGraph getAg() {
		return ag;
	}

	public void setAg(AnalysisGraph ag) {
		this.ag = ag;
	}

	public List<IMeasureReoprter> getReporter() {
		return reporters;
	}

	public void setReporter(List<IMeasureReoprter> reporters) {
		this.reporters = reporters;
	}

	public IMeasureInAS getMeasToAS() {
		return measToAS;
	}

	public void setMeasToAS(IMeasureInAS measToAS) {
		this.measToAS = measToAS;
	}

}
