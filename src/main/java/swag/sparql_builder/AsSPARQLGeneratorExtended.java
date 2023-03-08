package swag.sparql_builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.core.VarExprList;
import org.apache.jena.sparql.expr.E_Equals;
import org.apache.jena.sparql.expr.E_GreaterThan;
import org.apache.jena.sparql.expr.E_LessThan;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.ExprVar;
import org.apache.jena.sparql.syntax.ElementFilter;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.log4j.Logger;

import swag.analysis_graphs.execution_engine.AnalysisGraph;
import swag.analysis_graphs.execution_engine.NoOrderFunctionApplicableException;
import swag.analysis_graphs.execution_engine.analysis_situations.AnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.AnalysisSituationToBaseMeasureCondition;
import swag.analysis_graphs.execution_engine.analysis_situations.AnalysisSituationToResultFilters;
import swag.analysis_graphs.execution_engine.analysis_situations.IDimensionQualification;
import swag.analysis_graphs.execution_engine.analysis_situations.IMeasureInAS;
import swag.analysis_graphs.execution_engine.analysis_situations.ISliceSinglePosition;
import swag.analysis_graphs.execution_engine.analysis_situations.LevelInAnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.MeasureAggregated;
import swag.analysis_graphs.execution_engine.analysis_situations.MeasureAggregatedInAS;
import swag.analysis_graphs.execution_engine.analysis_situations.MeasureDerived;
import swag.analysis_graphs.execution_engine.analysis_situations.MeasureDerivedInAS;
import swag.data_handler.MeasureFactory;
import swag.md_elements.MDElement;
import swag.md_elements.MDSchema;
import swag.sparql_builder.ASElements.ASElementSPARQLGenerator;
import swag.sparql_builder.ASElements.DimensionParserCreator;
import swag.sparql_builder.ASElements.DimensionToSPARQLQueryGenerator;
import swag.sparql_builder.ASElements.DimensionsSubQueryGenerator;
import swag.sparql_builder.ASElements.IASElementGenerateSPARQLVisitor;
import swag.sparql_builder.ASElements.IAnalysisSituationToSPARQL;
import swag.sparql_builder.ASElements.IDimensionQueryGroupVarBodyBuilder;
import swag.sparql_builder.ASElements.IDimensionToSPARQLQueryGenerator;
import swag.sparql_builder.ASElements.IMeasureToSPARQLQueryGenerator;
import swag.sparql_builder.ASElements.MeasureToSPARQLQueryGenerator;
import swag.sparql_builder.reporting.IDimensionReoprter;
import swag.sparql_builder.reporting.IMeasureReoprter;

/**
 * 
 * 
 * 
 * @author swag
 *
 */
public class AsSPARQLGeneratorExtended implements IAnalysisSituationToSPARQL {

	QueryUtils utils;
	MDSchema mdSchema;
	IDimensionQueryGroupVarBodyBuilder dimGrpVarBuilder;
	AnalysisGraph ag;
	Map<Var, Var> varmappings;
	Map<MDElement, String> mdElemToVarMap = new HashMap<>();
	ASElementSPARQLGenerator visitor;

	public AsSPARQLGeneratorExtended(MDSchema mdSchema, AnalysisGraph ag) {
		super();
		this.mdSchema = mdSchema;
		this.ag = ag;
		visitor = new ASElementSPARQLGenerator(ag, mdElemToVarMap);
		utils = new QueryUtils(mdSchema);
	}

	private static final Logger logger = Logger.getLogger(AsSPARQLGeneratorExtended.class);

	/**
	 * Given an analysis situation, this function generates a corresponding SPARQL
	 * query The whole query body is treated as one single pattern, i.e., any
	 * duplicated triple pattern anywhere in the whole query is removed
	 * 
	 * @param as the bound analysis situation to generate its SPARQL
	 * @return a rooted SPARQL query
	 * @throws SPARQLQueryGenerationException
	 */
	public CustomSPARQLQuery doQueryGenerationMainProcessing(AnalysisSituation as)
			throws SPARQLQueryGenerationException {

		logger.info("generating SPARQL query for Analysis Situation " + as.getName());
		try {
			CustomSPARQLQuery rq = new CustomSPARQLQuery();
			Map<Var, Var> varsMappings = new HashMap<>();
			List<CustomSPARQLQuery> dimsList = new ArrayList<CustomSPARQLQuery>();
			List<Var> groupByVars = new ArrayList<Var>();
			List<IDimensionReoprter> allDimsReporters = new ArrayList<>();

			logger.info("Starting generating SPARQL of dimensions/hierarchies.");
			/* generating a query for each dimension */
			for (IDimensionQualification dimToAS : as.getDimensionsToAnalysisSituation()) {

				IDimensionToSPARQLQueryGenerator dimGenerator = new DimensionToSPARQLQueryGenerator(as, ag, rq,
						mdElemToVarMap, dimToAS);

				/* Preparing all reports for reporting summarizability */
				dimGenerator.getReporters().forEach(reporter -> allDimsReporters.add(reporter));

				/* Preparing list of dimensional queries */
				dimsList.add(dimGenerator.generateSPARQLFromDimToAS(mdSchema, dimToAS, varsMappings));

				/* Adding a group by variable for the granularity if exists */
				if (dimToAS.getGranularities().size() > 0) {
					LevelInAnalysisSituation granLvl = dimToAS.getGranularities().get(0).getPosition();
					if (granLvl != null && !granLvl.getIdentifyingName().equals("")
							&& !granLvl.getSignature().isVariable())
						groupByVars.add(granLvl.getMapping().getQuery().getUnaryProjectionVar());
				}
			}

			List<CustomSPARQLQuery> clonedList = new ArrayList<CustomSPARQLQuery>();
			boolean hasGroupByVars = false;
			boolean hasOtherVars = false;

			// edited on 04.05.2018
			// shifting dimensional queries without headers to the end of the
			// query to avoid exceptions
			for (CustomSPARQLQuery rootedQuery : dimsList) {
				if (rootedQuery.getSparqlQuery().getQueryPattern() != null) {
					if (rootedQuery.getSparqlQuery().getProjectVars().isEmpty()) {
						clonedList.add(clonedList.size(), rootedQuery);
						hasOtherVars = true;
					} else {
						clonedList.add(0, rootedQuery);
						hasGroupByVars = true;
					}
				}
			}

			List<Var> granVars = new ArrayList<>();

			logger.info("Generating SPARQL of dimensions.");
			if (hasGroupByVars || hasOtherVars) {
				DimensionsSubQueryGenerator dimsSubGenerator = DimensionParserCreator
						.createDimensionsSubQueryGenerator(as, ag, rq, mdElemToVarMap);
				granVars = dimsSubGenerator.mainGenerateSubQuery(clonedList, as.getFact().getHeadVar(), varsMappings);
			}

			/*
			 * adding measure queries first to have a header (project) for the query,
			 * otherwise if there are no granularities and exception might occur. adding
			 * measures queries
			 */
			List<Var> renamedGranVarsUsedForCount = new ArrayList<>();
			// Names of variables used in count for the split query
			for (Var v : granVars) {
				if (varsMappings.get(v) != null) {
					renamedGranVarsUsedForCount.add(varsMappings.get(v));
				}
			}

			logger.info("Generating SPARQL of measures subqueries.");
			/* Creating the measures subquery */
			rq = addMeasureSubQueries(rq, mdSchema, as, renamedGranVarsUsedForCount);

			logger.info("Generating SPARQL of base measures filters.");
			/* Adding filters of base measures to the main query */
			addBaseMeasureFilters(rq, mdSchema, as);

			// adding the fact query
			// rq.joinWith(as.getFact().getMapping().getQuery());

			logger.info("Appending granularities to query.");
			/* adding group by variables */
			for (Var v : granVars) {
				rq.getSparqlQuery().addGroupBy(v.getName());
				rq.getSparqlQuery().getProjectVars().add(v);
			}

			logger.info("Generating SPARQL of result filters.");
			// here removing any duplicated triple pattern
			// rq.removeQueryPatternDuplications();
			addResultFilters(rq, mdSchema, as);

			/*
			 * Adding aggregations and BIND statements related to summarizability to the
			 * query
			 */
			if (Configuration.getInstance().isReportingActive()) {
				for (IDimensionReoprter reporter : allDimsReporters) {
					rq = reporter.doReport(rq);
				}
			}

			/* Necessary for some buggy, weird SPARQL evaluation behaviours */
			SPARQLUtilities.addHavingToQuery(rq, " COUNT (*) > 0 ");

			logger.info("Dealing with labels.");
			/* Adding human readable labels */
			rq.setSparqlQuery(SPARQLUtilities.encapsulateBasicQuery(rq, mdSchema.getPreferredLabelProperty()));

			return rq;

		} catch (Exception ex) {
			SPARQLQueryGenerationException exSPARQL = new SPARQLQueryGenerationException(
					"Error generating SPARQL for analysis situation " + as.getName() + "\n Root cause: "
							+ ex.getMessage(),
					ex);
			logger.error("Error generating SPARQL for analysis situation " + as.getName(), ex);
			throw exSPARQL;
		}
	}

	/**
	 * Collects the base (derived) measures included in the base measure conditions.
	 * 
	 * @param as
	 * @return
	 */
	public final List<MeasureDerivedInAS> collectReferencedDerivedMeasures(AnalysisSituation as) {

		List<MeasureDerivedInAS> measures = new ArrayList<>();

		for (ISliceSinglePosition<AnalysisSituationToBaseMeasureCondition> msr : as.getResultBaseFilters()) {
			MeasureDerived elm = (MeasureDerived) msr.getPositionOfCondition();
			boolean covered = false;
			List<MeasureAggregatedInAS> resultMeasures = as.getResultMeasures();
			for (MeasureAggregatedInAS msrAgg : resultMeasures) {
				// Measure is already include in the results
				if (msrAgg.isBasedOnMeasure(elm)) {
					covered = true;
					break;
				}
			}
			if (!covered) {
				MeasureDerivedInAS derivedMeasure = (MeasureDerivedInAS) MeasureFactory.createMeasureFromURI(mdSchema,
						as, elm.getURI());
				measures.add(derivedMeasure);
			}
		}
		return measures;
	}

	/**
	 * Collects the aggregated measures included in the base measure conditions.
	 * 
	 * @param as
	 * @return
	 */
	public final List<MeasureAggregatedInAS> collectReferencedAggreagatedMeasures(AnalysisSituation as) {

		List<MeasureAggregatedInAS> measures = new ArrayList<>();

		for (ISliceSinglePosition<AnalysisSituationToResultFilters> msr : as.getResultFilters()) {
			MeasureAggregated elm = (MeasureAggregated) msr.getPositionOfCondition();
			boolean covered = false;
			List<MeasureAggregatedInAS> resultMeasures = as.getResultMeasures();
			for (MeasureAggregatedInAS msrAgg : resultMeasures) {
				// Measure is already include in the results
				if (msrAgg.isbasedOnAggregatedMeasure(elm)) {
					covered = true;
					break;
				}
			}
			if (!covered) {
				MeasureAggregatedInAS aggMeasure = (MeasureAggregatedInAS) MeasureFactory.createMeasureFromURI(mdSchema,
						as, elm.getURI());
				measures.add(aggMeasure);
			}
		}
		return measures;
	}

	/**
	 * 
	 * Appends the result measures queries to the main query. This is the default
	 * implementation. As the process of this function flows, there function that
	 * can be overridden in sub classes to chieve a different functionality.
	 * 
	 * @param rq                          the main query to append subqueries to
	 * @param mdSchema                    the MD schema
	 * @param as                          the current analysis situation
	 * @param renamedGranVarsUsedForCount
	 * @throws Exception
	 */
	protected CustomSPARQLQuery addMeasureSubQueries(CustomSPARQLQuery rq, MDSchema mdSchema, AnalysisSituation as,
			List<Var> renamedGranVarsUsedForCount) throws Exception {

		CustomSPARQLQuery copyQuery = new CustomSPARQLQuery(rq.getSparqlQuery());
		List<IMeasureInAS> measuresList = new ArrayList<>();
		measuresList.addAll(as.getResultMeasures());
		measuresList.addAll(collectReferencedDerivedMeasures(as));
		measuresList.addAll(collectReferencedAggreagatedMeasures(as));

		Var factVariable = as.getFact().getHeadVar();
		List<IDimensionQualification> dims = as.getDimensionsToAnalysisSituation();

		for (IMeasureInAS measToAS : measuresList) {

			logger.info("generating SPARQL for measure " + measToAS.getName());

			IMeasureToSPARQLQueryGenerator generator = new MeasureToSPARQLQueryGenerator(ag, mdElemToVarMap, as,
					measToAS);
			CustomSPARQLQuery subQuery = generator.generateSPARQLFromMeasToAS();

			if (Configuration.getInstance().isReportingActive()) {
				for (IMeasureReoprter reporter : generator.getReporter()) {
					reporter.fillBindStatementVariablesAndExpressions();
				}
			}

			// The measure at hand is a result measure and should be included in
			// the result
			if (measToAS instanceof MeasureAggregatedInAS) {
				appendOuterAggregation(generator, copyQuery, subQuery, (MeasureAggregatedInAS) measToAS,
						renamedGranVarsUsedForCount, dims, as);
				copyQuery = new CustomSPARQLQuery(generateMeasureRangeStr(
						((MeasureAggregatedInAS) measToAS).getSourceDerivedMeasure().getHeadVar(), copyQuery,
						measToAS));
			}
			addFactVariable(measToAS, factVariable, subQuery);
			subQuery.removeQueryPatternDuplications();
			SPARQLUtilities.insertDistinctToHeadedQuery(subQuery);
			appendSubQueryToMainQuery(copyQuery, subQuery);
			for (IMeasureReoprter reporter : generator.getReporter()) {
				copyQuery = reporter.appendBindStatementVariablesAndExpressions(copyQuery);
			}
		}

		return copyQuery;
	}

	/**
	 * Generates the most outer aggregation variables of the main analytical query.
	 * Can Be overridden. Implicitly assumes that all required measures for the
	 * filter are result measures and hence appear in the header of the most outer
	 * main query.
	 * 
	 * @param the                         measure query generator
	 * @param rq                          the main analytical query
	 * @param subQuery                    the subquery of the measure of analysis
	 *                                    situation at hand
	 * @param measToAS                    the measure of analysis situation at hand
	 * @param renamedGranVarsUsedForCount
	 * @param dims                        the dimensions qualification of the
	 *                                    analysis situation
	 */
	protected void appendOuterAggregation(IMeasureToSPARQLQueryGenerator generator, CustomSPARQLQuery rq,
			CustomSPARQLQuery subQuery, MeasureAggregatedInAS measToAS, List<Var> renamedGranVarsUsedForCount,
			List<IDimensionQualification> dims, AnalysisSituation as) {

		CustomSPARQLQuery headerQuery = generator.generateOuterMostAggVars(subQuery, measToAS,
				renamedGranVarsUsedForCount, dims, as);
		VarExprList list = headerQuery.getSparqlQuery().getProject();
		rq.getSparqlQuery().getProject().addAll(list);
	}

	/**
	 * Appending the generated measure subquery to the main containing query. This
	 * implementation can be overridden.
	 * 
	 * @param rq       the main analytical query
	 * @param subQuery the subquery of the measure of analysis situation at hand
	 */
	protected void appendSubQueryToMainQuery(CustomSPARQLQuery rq, CustomSPARQLQuery subQuery) {
		rq.insertSubQuery(subQuery);
	}

	/**
	 * @param rq
	 * @param mdSchema
	 * @param as
	 * @throws Exception
	 */
	protected void addBaseMeasureFilters(CustomSPARQLQuery rq, MDSchema mdSchema, AnalysisSituation as)
			throws Exception {

		List<String> filters = new ArrayList<>();
		for (ISliceSinglePosition<?> cond : as.getResultBaseFilters()) {
			logger.info("Generating SPARQL for filter " + cond.getConditoin());
			cond.acceptVisitor(getVisitor());
			filters.addAll(getVisitor().getReturn());
		}
		if (!filters.isEmpty()) {
			String allFiltersString = StringUtils.EMPTY;
			for (String str : filters) {
				allFiltersString = allFiltersString + str;
			}
			SPARQLUtilities.appendFiltersToQuery(rq, allFiltersString);
		}
	}

	/**
	 * @param rq
	 * @param mdSchema
	 * @param as
	 * @throws Exception
	 */
	protected void addResultFilters(CustomSPARQLQuery rq, MDSchema mdSchema, AnalysisSituation as) throws Exception {

		List<String> filters = new ArrayList<>();
		for (ISliceSinglePosition<?> cond : as.getResultFilters()) {
			logger.info("Generating SPARQL for filter " + cond.getConditoin());
			cond.acceptVisitor(getVisitor());
			filters.addAll(getVisitor().getReturn());
		}
		if (!filters.isEmpty()) {
			String allFiltersString = StringUtils.EMPTY;
			for (String str : filters) {
				allFiltersString = allFiltersString + str;
			}
			SPARQLUtilities.addHavingToQuery(rq, allFiltersString);
		}
	}

	/**
	 * @param measToAS
	 * @param factVariable
	 * @param subQuery
	 */
	protected static void addFactVariable(IMeasureInAS measToAS, Var factVariable, CustomSPARQLQuery subQuery) {
		if (measToAS.getConfiguration().get("internalagg") != null
				&& !measToAS.getConfiguration().get("internalagg").equalsIgnoreCase("TREAT_AS_FACT")) {
			subQuery.getSparqlQuery().addGroupBy(factVariable);
		}

		subQuery.getSparqlQuery().getProjectVars().add(factVariable);
	}

	/**
	 * @param query
	 */
	protected static void addSubQueryForDimensionsToQuery(CustomSPARQLQuery query) {
		ParameterizedSparqlString pStr = new ParameterizedSparqlString(query.getSprqlQuery().toString());
		String str = pStr.toString();
		str += " HAVING (COUNT (*) > 0) ";
		Query q = QueryFactory.create(str);
		query.setSparqlQuery(q);
	}

	@Override
	public final Query generateQueryForNextUpDiceValue(Query q, String val1, String varName) {

		Expr e = new E_Equals(new ExprVar(varName), new ExprVar("temporaryExpression"));
		ElementFilter filter = new ElementFilter(e); // Make a filter matching
														// the expression
		ElementGroup body = new ElementGroup(); // Group our pattern match and
												// filter
		body.addElement(filter);
		Query newQ = org.apache.jena.query.QueryFactory.create(q.toString());
		newQ.setQueryPattern(CustomSPARQLQuery.joinQueryPatterns(q.getQueryPattern(), body));
		// here the replacement takes place
		ParameterizedSparqlString queryString = new ParameterizedSparqlString(newQ.toString());
		Model m = ModelFactory.createDefaultModel();

		if (val1.contains("^^")) { // the dice value is a data typed value
									// (literal)
			String[] tmp = val1.split("\\^\\^");
			String part1 = tmp[0].substring(1, tmp[0].length() - 1);
			String part2 = tmp[1].substring(1, tmp[1].length() - 1);
			Literal lit = m.createTypedLiteral(part1, part2);
			queryString.setLiteral("temporaryExpression", lit);
		} else { // the dice value is a URI
			queryString.setIri("temporaryExpression", val1);
			// queryString.setLiteral("temporaryExpression", str);
		}
		CustomSPARQLQuery cusQ = new CustomSPARQLQuery(
				org.apache.jena.query.QueryFactory.create(queryString.toString()));
		cusQ.removeQueryPatternDuplications();
		return cusQ.getSparqlQuery();
	}

	@Override
	public final Query generateQueryForNextOrPreviousDiceValue(Query q, String val1, String varName, boolean isNext)
			throws NoOrderFunctionApplicableException {

		// try{

		ParameterizedSparqlString queryString;
		if (val1.contains("^^")) { // the dice value is a data typed value
									// (literal)
			Expr e;
			if (isNext) {
				e = new E_GreaterThan(new ExprVar(varName), new ExprVar("temporaryExpression"));
			} else {
				e = new E_LessThan(new ExprVar(varName), new ExprVar("temporaryExpression"));
			}
			ElementFilter filter = new ElementFilter(e); // Make a filter
															// matching the
															// expression
			ElementGroup body = new ElementGroup(); // Group our pattern match
													// and filter
			body.addElement(filter);
			Query newQ = org.apache.jena.query.QueryFactory.create(q.toString());
			newQ.setQueryPattern(CustomSPARQLQuery.joinQueryPatterns(q.getQueryPattern(), body));
			// here the replacement takes place
			queryString = new ParameterizedSparqlString(newQ.toString());
			Model m = ModelFactory.createDefaultModel();
			String[] tmp = val1.split("\\^\\^");
			String part1 = tmp[0].substring(1, tmp[0].length() - 1);
			String part2 = tmp[1].substring(1, tmp[1].length() - 1);
			Literal lit = m.createTypedLiteral(part1, part2);
			queryString.setLiteral("temporaryExpression", lit);
		} else { // convert URI to strings by adding STR() function
			Expr e;
			if (isNext) {
				e = new E_GreaterThan(new ExprVar("tempVar"), new ExprVar("temporaryExpression"));
			} else {
				e = new E_LessThan(new ExprVar("tempVar"), new ExprVar("temporaryExpression"));
			}

			ElementFilter filter = new ElementFilter(e); // Make a filter
															// matching the
															// expression
			ElementGroup body = new ElementGroup(); // Group our pattern match
													// and filter
			body.addElement(filter);
			Query newQ = org.apache.jena.query.QueryFactory.create(q.toString());
			newQ.setQueryPattern(CustomSPARQLQuery.joinQueryPatterns(q.getQueryPattern(), body));
			// here the replacement takes place
			String tempStr = newQ.toString();
			tempStr = tempStr.replace("?tempVar", "STR(?" + varName + ")");
			queryString = new ParameterizedSparqlString(tempStr);
			// Model m = ModelFactory.createDefaultModel();
			// queryString.setLiteral("tempVar", "STR(?" + varName + ")");
			// throw new NoOrderFunctionApplicableException();
			queryString.setIri("temporaryExpression", val1);
			tempStr = queryString.toString();
			tempStr = tempStr.replace("<" + val1 + ">", "STR(<" + val1 + ">)");
			queryString = new ParameterizedSparqlString(tempStr);
		}
		CustomSPARQLQuery cusQ = new CustomSPARQLQuery(
				org.apache.jena.query.QueryFactory.create(queryString.toString()));
		if (isNext)
			SPARQLUtilities.insertORDERBYAtQueryEnd(cusQ, varName);
		else
			SPARQLUtilities.insertORDERBYDescAtQueryEnd(cusQ, varName);
		SPARQLUtilities.insertLIMIT1AtQueryEnd(cusQ);
		cusQ.removeQueryPatternDuplications();
		return cusQ.getSparqlQuery();
		// }catch (NoOrderFunctionApplicableException ex){
		// throw ex;
		// }
	}

	public IASElementGenerateSPARQLVisitor getVisitor() {
		return this.visitor;
	}

	public static void main(String[] args) {

		String queryString = "select (sum(?x) as ?sum) ?y " + "where {" + "?x ?y ?z." + "} " + "group by ?y";
		Query sparqlQuery = QueryFactory.create(queryString);

		CustomSPARQLQuery custSparqlQuery = new CustomSPARQLQuery(sparqlQuery);
		custSparqlQuery.setSparqlQuery(SPARQLUtilities.encapsulateBasicQuery(custSparqlQuery, ""));
		SPARQLUtilities.generateLabelsQuery(custSparqlQuery);

	}

	@Override
	public void fillInRenamedVariablesMap() {
		// TODO Auto-generated method stub

	}

	public Map<MDElement, String> getMdElemToVarMap() {
		return mdElemToVarMap;
	}

	public void setMdElemToVarMap(Map<MDElement, String> mdElemToVarMap) {
		this.mdElemToVarMap = mdElemToVarMap;
	}

	public IDimensionQueryGroupVarBodyBuilder getDimGrpVarBuilder() {
		return dimGrpVarBuilder;
	}

	public void setDimGrpVarBuilder(IDimensionQueryGroupVarBodyBuilder dimGrpVarBuilder) {
		this.dimGrpVarBuilder = dimGrpVarBuilder;
	}

	public AnalysisGraph getAg() {
		return ag;
	}

	public void setAg(AnalysisGraph ag) {
		this.ag = ag;
	}

	public Map<Var, Var> getVarmappings() {
		return varmappings;
	}

	public void setVarmappings(Map<Var, Var> varmappings) {
		this.varmappings = varmappings;
	}

	public MDSchema getMdSchema() {
		return mdSchema;
	}

	public void setMdSchema(MDSchema mdSchema) {
		this.mdSchema = mdSchema;
	}

	private String generateMeasureRangeStr(Var msrVar, CustomSPARQLQuery query, IMeasureInAS msr) {

		String varName = msrVar.getVarName();
		String queryStr = query.toString();

		return SPARQLUtilities.renameMsrVariableInQuery(query, varName, msr.getMeasure().getMeasureRange());
	}

}
