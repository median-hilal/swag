package swag.sparql_builder;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.expr.E_Equals;
import org.apache.jena.sparql.expr.E_GreaterThan;
import org.apache.jena.sparql.expr.E_LessThan;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.ExprVar;
import org.apache.jena.sparql.syntax.ElementFilter;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.log4j.Logger;

import swag.analysis_graphs.execution_engine.NoOrderFunctionApplicableException;
import swag.analysis_graphs.execution_engine.analysis_situations.AnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.DiceNodeInAnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.IDiceSpecification;
import swag.analysis_graphs.execution_engine.analysis_situations.IDimensionQualification;
import swag.analysis_graphs.execution_engine.analysis_situations.IMeasureToAnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.ISliceSetDim;
import swag.analysis_graphs.execution_engine.analysis_situations.ISliceSinglePosition;
import swag.analysis_graphs.execution_engine.analysis_situations.LevelInAnalysisSituation;
import swag.md_elements.MDElement;
import swag.sparql_builder.ASElements.IAnalysisSituationToSPARQL;

public class BasicAnalysisSituationSPARQLQueryGenerator implements IAnalysisSituationToSPARQL {


	private static final Logger logger = Logger.getLogger(BasicAnalysisSituationSPARQLQueryGenerator.class);

	/**
	 * Generates a query from a dimensionToAS This function is unaware of the
	 * variables, no special treatments for variables
	 * 
	 * @param dimToAS
	 *            dimension to analysis situation at hand
	 * @return a rooted query for the dimensionToAS at hand
	 */
	public static CustomSPARQLQuery generateQueryFromDimToAS(IDimensionQualification dimToAS) {

		// when there is no granularity --> there is no head for the query -->
		// artificial variable is
		// used and removed before returning
		boolean queryContainsHeadVar = true;
		CustomSPARQLQuery rq = new CustomSPARQLQuery();
		CustomSPARQLQuery granularityLevelQuery = new CustomSPARQLQuery();
		CustomSPARQLQuery diceLevelQuery = new CustomSPARQLQuery();

		if (dimToAS.getGranularities().size() != 0) {



			// the granularity query is added as-is in case it is neither null
			// nor a variable
			if (dimToAS.getGranularities().get(0).getPosition() != null
					&& !dimToAS.getGranularities().get(0).getPosition().getIdentifyingName().equals("")
					&& !dimToAS.getGranularities().get(0).getPosition().getSignature().isVariable()) {
				granularityLevelQuery = new CustomSPARQLQuery(
						dimToAS.getGranularities().get(0).getPosition().getMapping().getQuery().getSparqlQuery());
				rq = rq.joinWith(granularityLevelQuery);

				// the case there is no granularity, i.e. it is either null or a
				// non-bound variable
			} else {
				if (dimToAS.getGranularities().get(0).getPosition().getIdentifyingName().equals("")) {
					queryContainsHeadVar = false;
					rq.getSparqlQuery().addResultVar("x"); // Artificial
															// temporal head
															// variable is added
															// and
															// removed
					// at the end of this funciton
				}
			}
		} else {
			queryContainsHeadVar = false;
			rq.getSparqlQuery().addResultVar("x");
		}

		for (IDiceSpecification dc : dimToAS.getDices()) {

			LevelInAnalysisSituation lvl = dc.getPosition();
			// the diceLevel query is added as-is in case it is neither null nor
			// a variable. It needs to
			// be joined in body, as it adds no header variables
			if (lvl != null && !lvl.getIdentifyingName().equals("") && !lvl.getSignature().isVariable()) {
				// when building a dimensionToAS, the function
				// getDimensionToASByName guarantees that
				// diceLevel and diceNode are both null or non-null
				diceLevelQuery = new CustomSPARQLQuery(lvl.getMapping().getQuery().getSparqlQuery());
				rq.getSparqlQuery().setQueryPattern(CustomSPARQLQuery.joinQueryPatterns(
						rq.getSparqlQuery().getQueryPattern(), diceLevelQuery.getSparqlQuery().getQueryPattern()));
			}

			DiceNodeInAnalysisSituation node = dc.getDiceNodeInAnalysisSituation();
			// treating dice value
			if (node != null && // null dice values do not cause anything
					!node.getNodeValue().equals("") && // empty dice values do
														// not cause anything
					!node.getSignature().isVariable()) {
				// equality expression. Left is the diceLevel, right is a
				// temporal variable to be replaced
				// by the value read from the file
				Expr e = new E_Equals(new ExprVar(lvl.getMapping().getQuery().getUnaryProjectionVar()),
						new ExprVar("temporaryExpression"));
				ElementFilter filter = new ElementFilter(e); // Make a filter
																// matching the
																// expression
				ElementGroup body = new ElementGroup(); // Group our pattern
														// match and filter
				body.addElement(filter);
				rq.getSparqlQuery().setQueryPattern(
						CustomSPARQLQuery.joinQueryPatterns(rq.getSparqlQuery().getQueryPattern(), body));
				// here the replacement takes place
				ParameterizedSparqlString queryString = new ParameterizedSparqlString(rq.getSparqlQuery().toString());
				Model m = ModelFactory.createDefaultModel();
				org.apache.jena.datatypes.TypeMapper tm = new org.apache.jena.datatypes.TypeMapper();
				String str = node.getNodeValue();
				if (str.contains("^^")) { // the dice value is a data typed
											// value (literal)
					String[] tmp = str.split("\\^\\^");
					String part1 = tmp[0].substring(0, tmp[0].length());
					String part2 = tmp[1].substring(0, tmp[1].length());
					Literal lit = m.createTypedLiteral(part1, part2);
					queryString.setLiteral("temporaryExpression", lit);
				} else { // the dice value is a URI
					if (str.startsWith("http://")) {
						queryString.setIri("temporaryExpression", str);
						// queryString.setLiteral("temporaryExpression", str);
					} else { // dice node has no type, considered string by
								// default
						Literal lit = m.createTypedLiteral(str, "http://www.w3.org/2001/XMLSchema#string");
						queryString.setLiteral("temporaryExpression", lit);
					}
				}
				rq.setSparqlQuery(org.apache.jena.query.QueryFactory.create(queryString.toString()));
			}
		}

		for (ISliceSetDim set : dimToAS.getSliceSets()) {
			for (ISliceSinglePosition<IDimensionQualification> sc : set.getConditions()) {

				MDElement pos = sc.getPositionOfCondition();
				// the slicePosition query is added as-is in case it is neither
				// null nor a variable. It
				// needs
				// to be joined in body, as it adds no header variables
				CustomSPARQLQuery slicePositionQuery = new CustomSPARQLQuery();
				if (pos != null && !pos.getIdentifyingName().equals("")) {
					// when building a dimensionToAS, the function
					// getDimensionToASByName guarantees that
					// slicePosition and sliceCondition are both null or
					// non-null
					slicePositionQuery = new CustomSPARQLQuery(pos.getMapping().getQuery().getSparqlQuery());
					rq.getSparqlQuery()
							.setQueryPattern(CustomSPARQLQuery.joinQueryPatterns(rq.getSparqlQuery().getQueryPattern(),
									slicePositionQuery.getSparqlQuery().getQueryPattern()));
				}

				// treating slice condition
				if (sc != null && !sc.getConditoin().equals("") && !sc.getSignature().isVariable()) {
					ParameterizedSparqlString queryString = new ParameterizedSparqlString(
							rq.getSparqlQuery().toString());
					queryString = new ParameterizedSparqlString(
							SPARQLUtilities.appendToQueryString(queryString.toString(), "FILTER ("
									+ pos.getMapping().getQuery().getUnaryProjectionVar() + sc.getConditoin() + ")"));
					rq.setSparqlQuery(org.apache.jena.query.QueryFactory.create(queryString.toString()));
				}
			}
		}

		// back to the case of the artificial granularity head variable; here
		// the removal takes place
		if (!queryContainsHeadVar)
			rq.getSparqlQuery().getProject().clear();

		// rq.removeQueryPatternDuplications();
		return rq;
	}

	/**
	 * Given an analysis situation, this function generates a corresponding
	 * SPARQL query The whole query body is treated as one single pattern, i.e.,
	 * any duplicated triple pattern anywhere in the whole query is removed
	 * 
	 * @param as
	 *            the bound analysis situation to generate its SPARQL
	 * @return a rooted SPARQL query
	 * @throws SPARQLQueryGenerationException
	 */
	public CustomSPARQLQuery doQueryGenerationMainProcessing(AnalysisSituation as)
			throws SPARQLQueryGenerationException {

		try {
			CustomSPARQLQuery rq = new CustomSPARQLQuery();
			List<CustomSPARQLQuery> dimsList = new ArrayList<CustomSPARQLQuery>();
			List<CustomSPARQLQuery> measList = new ArrayList<CustomSPARQLQuery>();
			List<Var> groupByVars = new ArrayList<Var>();

			// generating a query for each dimension
			for (IDimensionQualification dimToAS : as.getDimensionsToAnalysisSituation()) {
				dimsList.add(generateQueryFromDimToAS(dimToAS));
				// adding a group by variable for the granularity if exists

				if (dimToAS.getGranularities().size() > 0) {
					LevelInAnalysisSituation granLvl = dimToAS.getGranularities().get(0).getPosition();
					if (granLvl != null && !granLvl.getIdentifyingName().equals("")
							&& !granLvl.getSignature().isVariable())
						groupByVars.add(granLvl.getMapping().getQuery().getUnaryProjectionVar());
				}
			}
			// generating a query for each measure
			for (IMeasureToAnalysisSituation measureToAS : as.getMeasuresToAnalysisSituation()) {
				measList.add((new CustomSPARQLQuery(measureToAS.getMeasureSpecificationInterface().getPosition()
						.getMapping().getQuery().getSparqlQuery())));
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

			// adding measure queries first to have a header (project) for the
			// query, otherwise if there
			// are no granularities and exception might occur.
			// adding measures queries
			for (CustomSPARQLQuery rootedQuery : measList) {
				rq = rq.joinWith(new CustomSPARQLQuery(rootedQuery));
			}
			List<String> nameOfCreatedVariable = new ArrayList<>();
			// creating the aggregation for the measure
			for (IMeasureToAnalysisSituation measToAS : as.getMeasuresToAnalysisSituation()) {
				SPARQLUtilities.createAggregationQuery(rq,
						measToAS.getMeasureSpecificationInterface().getAggregationOperationInAnalysisSituation()
								.getName(),
						measToAS.getMeasureSpecificationInterface().getPosition().getMapping().getQuery()
								.getUnaryProjectionVar(),
						nameOfCreatedVariable);

			}

			if (hasGroupByVars || hasOtherVars) {
				// adding dimensional queries to the final query
				for (CustomSPARQLQuery rootedQuery : clonedList) {
					if (rootedQuery.getSparqlQuery().getQueryPattern() != null)
						rq = rq.joinWith(rootedQuery);
				}
			}

			// adding the fact query
			// rq.joinWith(as.getFact().getMapping().getQuery());

			// adding group by variables
			for (Var v : groupByVars) {
				rq.getSparqlQuery().addGroupBy(v.getName());
			}
			// here removing any duplicated triple pattern
			rq.removeQueryPatternDuplications();

			SPARQLUtilities.addHavingToQuery(rq, " COUNT (*) > 0 ");
			rq.setSparqlQuery(SPARQLUtilities.encapsulateBasicQuery(rq, ""));

			return rq;

		} catch (Exception ex) {
			SPARQLQueryGenerationException exSPARQL = new SPARQLQueryGenerationException(
					"Error generating SPARQL for analysis situation " + as.getName() + "\n Root cause: "
							+ ex.getMessage(),
					ex);
			logger.error(ex);
			throw exSPARQL;
		}
	}

	/**
	 * Generates a SPARQL query that gets the dice value corresponding to the
	 * current one in the higher level
	 * 
	 * @param q
	 *            the query from the lower level to the higherLevel
	 * @param val1
	 *            the current dice value
	 * @param varName
	 *            the name of the variable on which to apply a FILTER expression
	 *            and suppress it to {@param val1}
	 * @return a query capable of retrieving the required new dice value
	 */
	public Query generateQueryForNextUpDiceValue(Query q, String val1, String varName) {

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

	/**
	 * @param q
	 * @param val1
	 * @param varName
	 * @param isNext
	 *            TODO
	 * @return
	 */
	public Query generateQueryForNextOrPreviousDiceValue(Query q, String val1, String varName, boolean isNext)
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
}
