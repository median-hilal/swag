package swag.sparql_builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.syntax.TripleCollectorBGP;
import org.apache.log4j.Logger;

import swag.analysis_graphs.execution_engine.AnalysisGraph;
import swag.analysis_graphs.execution_engine.analysis_situations.*;
import swag.md_elements.MDSchema;
import swag.sparql_builder.ASElements.DimensionParserCreator;
import swag.sparql_builder.ASElements.DimensionToSPARQLQueryGenerator;
import swag.sparql_builder.ASElements.DimensionsSubQueryGenerator;
import swag.sparql_builder.ASElements.IDimensionToSPARQLQueryGenerator;

/**
 * 
 * 
 * 
 * @author swag
 *
 */
public class AsSPARQLGeneratorSimple extends AsSPARQLGeneratorExtended {

	private QueryUtils utils = new QueryUtils(mdSchema);

	public AsSPARQLGeneratorSimple(MDSchema mdSchema, AnalysisGraph ag) {
		super(mdSchema, ag);
	}

	public void collect(AnalysisSituation as) throws Exception {

		TripleCollectorBGP bgp = new TripleCollectorBGP();
		List<String> conds = new ArrayList<>();
		List<String> filters = new ArrayList<>();
		List<String> aggs = new ArrayList<>();
		List<Var> grans = new ArrayList<Var>();

		SetBasedBGP.addBgpToBgp(bgp, utils.getTriplesOfFact());

		for (IDimensionQualification dimToAS : as.getDimensionsToAnalysisSituation()) {

			if (dimToAS.getGranularities().size() > 0) {
				 SetBasedBGP.addBgpToBgp(bgp, utils.getTriplesOfPath(dimToAS.getD().getURI(),
						dimToAS.getGranularities().get(0).getPosition().getURI(),
						mdSchema.getFinestLevelOnDimension1(dimToAS.getD().getURI()).getURI()));
				 grans.add(utils.getVarOfLevel(dimToAS.getD().getURI(),dimToAS.getGranularities().get(0).getPosition().getURI()));
			}

			if (dimToAS.getDices().size() > 0) {
				SetBasedBGP.addBgpToBgp(bgp, utils.getTriplesOfPath(dimToAS.getD().getURI(),
						dimToAS.getDices().get(0).getPosition().getURI(),
						mdSchema.getFinestLevelOnDimension1(dimToAS.getD().getURI()).getURI()));
				String str = " FILTER(?" + utils.getVarOfLevel(dimToAS.getD().getURI(), dimToAS.getDices().get(0).getPosition().getURI()).getVarName()
						+ " = <" +  dimToAS.getDices().get(0).getDiceNodeInAnalysisSituation().getNodeValue() + ">)";
				conds.add(str);
			}

			for (ISliceSinglePosition<IDimensionQualification> cond : dimToAS.getSliceConditions()) {
				SetBasedBGP.addBgpToBgp(bgp, utils.getTriplesOfPath(dimToAS.getD().getURI(),
						cond.getPositionOfCondition().getURI(),
						mdSchema.getFinestLevelOnDimension1(dimToAS.getD().getURI()).getURI()));
				conds.add(utils.generateConditionnASQuery(cond, dimToAS.getD().getURI(), ag));
			}
		}

		for (MeasureAggregatedInAS msr: as.getResultMeasures()){
			SetBasedBGP.addBgpToBgp(bgp, utils.getTriplesOfPathMeasuure (msr.getMeasure().getMeasure().getURI()));
			String str = "" + msr.getAgg() + "(" + utils.getVarOfMeasure(msr.getMeasure().getMeasure().getURI()) + ") as ?agg" + msr.getName();
			aggs.add(str);
		}

		for(ISliceSinglePosition<AnalysisSituationToResultFilters> filter: as.getResultFilters()){
			MeasureAggregated mAgg = (MeasureAggregated) filter.getPositionOfCondition();
			SetBasedBGP.addBgpToBgp(bgp, utils.getTriplesOfPathMeasuure (mAgg.getMeasure().getURI()));
			filters.add(utils.generateConditionnASQueryMsr(filter, ag));
		}


		String query = "";
		query += "SELECT ";

		for (String agg: aggs){
			query+= "(" + agg + ")";
		}

		for (Var gran : grans){
			query += " ?" + gran.getName() + " ";
		}

		query += " WHERE {";
		for (Triple t : bgp.getBGP().getList()){
			query += "?" + ((Var) t.getSubject()).getName() +
					" <" + t.getPredicate().getURI() + "> " +
					((t.getObject() instanceof  Var)?
							"?" + ((Var) t.getObject()).getName() :
							" <" + t.getObject().getURI() + "> ") +
					". ";
		}
		query += " \n ";
		for (String cond : conds){
			query += cond + " \n ";
		}
		query += " } GROUP BY ";

		for (Var gran : grans){
			query += " ?" + gran.getName() + " ";
		}

		query += " HAVING (";

		for (String filter : filters){
			query+= filter + " && ";
		}

		query += ")";

		System.out.println("query: " + query);

	}

	@Override
	public CustomSPARQLQuery doQueryGenerationMainProcessing(AnalysisSituation as)
			throws SPARQLQueryGenerationException {

		try {
			collect(as);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		logger.info("generating SPARQL query for Analysis Situation " + as.getName());
		try {

			overrideConfiguration(as);
			CustomSPARQLQuery rq = new CustomSPARQLQuery();

			Map<Var, Var> varsMappings = new HashMap<>();

			List<CustomSPARQLQuery> dimsList = new ArrayList<CustomSPARQLQuery>();
			List<Var> groupByVars = new ArrayList<Var>();

			logger.info("Starting generating SPARQL of dimensions/hierarchies.");
			// generating a query for each dimension
			for (IDimensionQualification dimToAS : as.getDimensionsToAnalysisSituation()) {

				if (dimToAS.getGranularities().size() > 0) {
					TripleCollectorBGP bgp = bgp = utils.getTriplesOfPath(dimToAS.getD().getURI(),
							dimToAS.getGranularities().get(0).getPosition().getURI(),
							mdSchema.getFinestLevelOnDimension1(dimToAS.getD().getURI()).getURI());
				}

				IDimensionToSPARQLQueryGenerator dimGenerator = new DimensionToSPARQLQueryGenerator(as, ag, rq,
						mdElemToVarMap, dimToAS);

				dimsList.add(dimGenerator.generateSPARQLFromDimToAS(mdSchema, dimToAS, varsMappings));
				// adding a group by variable for the granularity if exists

				if (dimToAS.getGranularities().size() > 0) {
					LevelInAnalysisSituation granLvl = dimToAS.getGranularities().get(0).getPosition();
					if (granLvl != null && !granLvl.getIdentifyingName().equals("")
							&& !granLvl.getSignature().isVariable())
						groupByVars.add(granLvl.getMapping().getQuery().getUnaryProjectionVar());
				}
			}

			mdSchema.stringifyGraph();
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

			CustomSPARQLQuery finalQuery = new CustomSPARQLQuery();
			finalQuery.getSparqlQuery().setQueryPattern(SPARQLUtilities.extractSubQuery(rq));

			// adding measure queries first to have a header (project) for the
			// query, otherwise if there
			// are no granularities and exception might occur.
			// adding measures queries

			List<Var> renamedGranVarsUsedForCount = new ArrayList<>();

			logger.info("Generating SPARQL of measures.");
			finalQuery = addMeasureSubQueries(finalQuery, mdSchema, as, renamedGranVarsUsedForCount);

			logger.info("Generating SPARQL of base measures filters.");
			addBaseMeasureFilters(rq, mdSchema, as);
			// adding the fact query
			// rq.joinWith(as.getFact().getMapping().getQuery());

			logger.info("Appending granularities to query.");
			// adding group by variables
			for (Var v : granVars) {
				finalQuery.getSparqlQuery().addGroupBy(v.getName());
				finalQuery.getSparqlQuery().getProjectVars().add(v);
			}
			// here removing any duplicated triple pattern
			// rq.removeQueryPatternDuplications();
			logger.info("Generating SPARQL of result filters.");
			addResultFilters(finalQuery, mdSchema, as);
			SPARQLUtilities.addHavingToQuery(finalQuery, " COUNT (*) > 0 ");

			logger.info("Dealing with labels.");
			finalQuery.setSparqlQuery(
					SPARQLUtilities.encapsulateBasicQuery(finalQuery, mdSchema.getPreferredLabelProperty()));

			return finalQuery;

		} catch (Exception ex) {
			SPARQLQueryGenerationException exSPARQL = new SPARQLQueryGenerationException(
					"Error generating SPARQL for analysis situation " + as.getName() + "\n Root cause: "
							+ ex.getMessage(),
					ex);
			logger.error("Error generating SPARQL for analysis situation " + as.getName(), ex);
			throw exSPARQL;
		}
	}

	private void overrideConfiguration(AnalysisSituation as) {

		for (IDimensionQualification d : as.getDimensionsToAnalysisSituation()) {
			d.getConfiguration().resetConfiguration();
		}
		for (IMeasureInAS m : as.getResultMeasures()) {
			m.getConfiguration().resetConfiguration();
		}
	}

	private static final Logger logger = Logger.getLogger(AsSPARQLGeneratorSimple.class);

	/**
	 * Given an analysis situation, this function generates a corresponding SPARQL
	 * query The whole query body is treated as one single pattern, i.e., any
	 * duplicated triple pattern anywhere in the whole query is removed
	 * 
	 * @param as the bound analysis situation to generate its SPARQL
	 * @return a rooted SPARQL query
	 * @throws SPARQLQueryGenerationException
	 */

	public CustomSPARQLQuery doQueryGenerationMainProcessing1(AnalysisSituation as)
			throws SPARQLQueryGenerationException {

		logger.info("generating SPARQL query for Analysis Situation " + as.getName());
		try {

			overrideConfiguration(as);
			CustomSPARQLQuery rq = new CustomSPARQLQuery();

			Map<Var, Var> varsMappings = new HashMap<>();

			List<CustomSPARQLQuery> dimsList = new ArrayList<CustomSPARQLQuery>();
			List<Var> groupByVars = new ArrayList<Var>();

			logger.info("Starting generating SPARQL of dimensions/hierarchies.");
			// generating a query for each dimension
			for (IDimensionQualification dimToAS : as.getDimensionsToAnalysisSituation()) {

				IDimensionToSPARQLQueryGenerator dimGenerator = new DimensionToSPARQLQueryGenerator(as, ag, rq,
						mdElemToVarMap, dimToAS);

				dimsList.add(dimGenerator.generateSPARQLFromDimToAS(mdSchema, dimToAS, varsMappings));
				// adding a group by variable for the granularity if exists

				if (dimToAS.getGranularities().size() > 0) {
					LevelInAnalysisSituation granLvl = dimToAS.getGranularities().get(0).getPosition();
					if (granLvl != null && !granLvl.getIdentifyingName().equals("")
							&& !granLvl.getSignature().isVariable())
						groupByVars.add(granLvl.getMapping().getQuery().getUnaryProjectionVar());
				}
			}

			mdSchema.stringifyGraph();
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

			CustomSPARQLQuery finalQuery = new CustomSPARQLQuery();
			finalQuery.getSparqlQuery().setQueryPattern(SPARQLUtilities.extractSubQuery(rq));

			// adding measure queries first to have a header (project) for the
			// query, otherwise if there
			// are no granularities and exception might occur.
			// adding measures queries

			List<Var> renamedGranVarsUsedForCount = new ArrayList<>();

			logger.info("Generating SPARQL of measures.");
			finalQuery = addMeasureSubQueries(finalQuery, mdSchema, as, renamedGranVarsUsedForCount);

			logger.info("Generating SPARQL of base measures filters.");
			addBaseMeasureFilters(rq, mdSchema, as);
			// adding the fact query
			// rq.joinWith(as.getFact().getMapping().getQuery());

			logger.info("Appending granularities to query.");
			// adding group by variables
			for (Var v : granVars) {
				finalQuery.getSparqlQuery().addGroupBy(v.getName());
				finalQuery.getSparqlQuery().getProjectVars().add(v);
			}
			// here removing any duplicated triple pattern
			// rq.removeQueryPatternDuplications();
			logger.info("Generating SPARQL of result filters.");
			addResultFilters(finalQuery, mdSchema, as);
			SPARQLUtilities.addHavingToQuery(finalQuery, " COUNT (*) > 0 ");

			logger.info("Dealing with labels.");
			finalQuery.setSparqlQuery(
					SPARQLUtilities.encapsulateBasicQuery(finalQuery, mdSchema.getPreferredLabelProperty()));

			return finalQuery;

		} catch (Exception ex) {
			SPARQLQueryGenerationException exSPARQL = new SPARQLQueryGenerationException(
					"Error generating SPARQL for analysis situation " + as.getName() + "\n Root cause: "
							+ ex.getMessage(),
					ex);
			logger.error("Error generating SPARQL for analysis situation " + as.getName(), ex);
			throw exSPARQL;
		}
	}

	@Override
	protected void appendSubQueryToMainQuery(CustomSPARQLQuery rq, CustomSPARQLQuery subQuery) {
		rq.joinWithOnlyPatternsAsGroupsByReference(subQuery);
	}

}
