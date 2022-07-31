package swag.sparql_builder;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.core.VarExprList;
import org.apache.jena.sparql.expr.E_Coalesce;
import org.apache.jena.sparql.expr.E_Lang;
import org.apache.jena.sparql.expr.E_LangMatches;
import org.apache.jena.sparql.expr.E_LogicalAnd;
import org.apache.jena.sparql.expr.E_LogicalNot;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.ExprAggregator;
import org.apache.jena.sparql.expr.ExprList;
import org.apache.jena.sparql.expr.ExprVar;
import org.apache.jena.sparql.expr.aggregate.AggAvg;
import org.apache.jena.sparql.expr.aggregate.AggCountVar;
import org.apache.jena.sparql.expr.aggregate.AggCountVarDistinct;
import org.apache.jena.sparql.expr.aggregate.AggMax;
import org.apache.jena.sparql.expr.aggregate.AggMin;
import org.apache.jena.sparql.expr.aggregate.AggSum;
import org.apache.jena.sparql.expr.nodevalue.NodeValueString;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementFilter;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementOptional;
import org.apache.jena.sparql.syntax.ElementSubQuery;
import org.apache.jena.sparql.syntax.ElementTriplesBlock;
import org.apache.log4j.Logger;

import swag.md_elements.MDElement;
import swag.md_elements.MDSchema;

/**
 * 
 * Contains functionalities to manipulate SPARQL queries.
 * 
 * @author swag
 *
 */
public class SPARQLUtilities {

	private static final Logger logger = Logger.getLogger(SPARQLUtilities.class);

	/**
	 * 
	 * If the body of the passed query is a subquery, this function extracts the
	 * body of the subquery and returns it, otherwise throws an exception.
	 * 
	 * @param q
	 * @return
	 * @throws Exception
	 */
	public static Element extractSubQuery(CustomSPARQLQuery q) throws Exception {

		if (q.getSparqlQuery().getQueryPattern() instanceof ElementSubQuery) {
			ElementSubQuery subQuery = (ElementSubQuery) q.getSparqlQuery().getQueryPattern();
			return subQuery.getQuery().getQueryPattern();
		}

		logger.error("The pattern of the passed Query is not a subQuery.");
		throw new Exception("The pattern of the passed Query is not a subQuery.");
	}

	/**
	 * 
	 * Replaces all occurrences in the query {@code originalQ} of variable
	 * {@code oldVarName} with the variable {@code newVarName} (adds question
	 * marks to both inside the funtion, no need to consider the question mark
	 * when passing).
	 * 
	 * @param originalQ
	 * @param oldVarName
	 * @param newVarName
	 * 
	 * @return a NEW query resulting from the replacement.
	 * 
	 */
	public static CustomSPARQLQuery renameVariableInQuery(CustomSPARQLQuery originalQ, String oldVarName,
			String newVarName) {

		CustomSPARQLQuery newQ;
		String originalQString = originalQ.toString();
		String newQString = originalQString.replaceAll("\\?" + oldVarName + "\\b", "?" + newVarName);
		newQ = new CustomSPARQLQuery(newQString);
		return newQ;
	}

	public static String renameMsrVariableInQuery(CustomSPARQLQuery originalQ, String oldVarName, String newVarName) {

		CustomSPARQLQuery newQ;
		String originalQString = originalQ.toString();
		if (!StringUtils.isEmpty(newVarName)) {
			String newQString = originalQString.replaceFirst("\\?" + oldVarName + "\\b",
					"<" + newVarName + ">(" + "?" + oldVarName + ")");
			return newQString;
		} else {
			return originalQString;
		}
	}

	/**
	 * 
	 * Joins query patterns only. Headers are not joined; only headers from
	 * {@code q1} are considered. Generates a NEW query as a result, rather than
	 * putting the result in one of the passed queries. The join operation is
	 * safe to the nullQuery. The join is done by reference
	 * 
	 * @param q1
	 *            the first query to join, both its header and pattern will be
	 *            considered.
	 * @param q2
	 *            the second query to join, only pattern will be considered.
	 * 
	 * @return a NEW query, containing header of {@code q1} and joined patterns
	 *         of both parameters.
	 * 
	 */
	public static CustomSPARQLQuery joinWithAsOptionalOnlyPatternAndGroupBy(CustomSPARQLQuery q1,
			CustomSPARQLQuery q2) {
		if (q1.isEmptyQuery() && q2.isEmptyQuery()) {
			return new CustomSPARQLQuery();
		}
		if (q2.isEmptyQuery()) {
			return new CustomSPARQLQuery(q1.getSparqlQuery().cloneQuery());
		} else {
			CustomSPARQLQuery q = new CustomSPARQLQuery();
			q.getSparqlQuery().setQuerySelectType();
			q.setQueryHeader(CustomSPARQLQuery.joinQueryHeaders(q1.getSparqlQuery().getProject(), null));

			q.getSparqlQuery().setQueryPattern(q1.getSparqlQuery().getQueryPattern());

			q.makeEmptyGroupIfPatternIsNull();
			CustomSPARQLQuery.addPatternToQueryGroup(q.getSparqlQuery().getQueryPattern(),
					new ElementOptional(q2.getSparqlQuery().getQueryPattern()));

			/* Add grouping by variables */
			if (!q1.getSparqlQuery().getGroupBy().isEmpty()) {
				q.getSparqlQuery().getGroupBy().addAll(q1.getSparqlQuery().getGroupBy());
			}
			if (!q2.getSparqlQuery().getGroupBy().isEmpty()) {
				q.getSparqlQuery().getGroupBy().addAll(q2.getSparqlQuery().getGroupBy());
			}

			return q;
		}
	}

	/**
	 * 
	 * Joins query patterns only. Headers are not joined; only headers from
	 * {@code q1} are considered. Generates a NEW query as a result, rather than
	 * putting the result in one of the passed queries. The join operation is
	 * safe to the nullQuery. The join is done by reference
	 * 
	 * @param q1
	 *            the first query to join, both its header and pattern will be
	 *            considered.
	 * @param q2
	 *            the second query to join, only pattern will be considered.
	 * 
	 * @return a NEW query, containing header of {@code q1} and joined patterns
	 *         of both parameters.
	 * 
	 */
	public static CustomSPARQLQuery joinWithAsOptionalOnlyPattern(CustomSPARQLQuery q1, CustomSPARQLQuery q2) {
		if (q1.isEmptyQuery() && q2.isEmptyQuery()) {
			return new CustomSPARQLQuery();
		}
		if (q2.isEmptyQuery()) {
			return new CustomSPARQLQuery(q1.getSparqlQuery().cloneQuery());
		} else {
			CustomSPARQLQuery q = new CustomSPARQLQuery();
			q.getSparqlQuery().setQuerySelectType();
			q.setQueryHeader(CustomSPARQLQuery.joinQueryHeaders(q1.getSparqlQuery().getProject(), null));

			q.getSparqlQuery().setQueryPattern(q1.getSparqlQuery().getQueryPattern());

			q.makeEmptyGroupIfPatternIsNull();
			CustomSPARQLQuery.addPatternToQueryGroup(q.getSparqlQuery().getQueryPattern(),
					new ElementOptional(q2.getSparqlQuery().getQueryPattern()));

			return q;
		}
	}

	/**
	 * 
	 * Joins queries. Both headers and patterns are joined. Generates a NEW
	 * query as a result, rather than putting the result in one of the passed
	 * queries. The join operation is safe to the nullQuery. The join is done by
	 * reference
	 * 
	 * @param q1
	 *            the first query to join, both its header and pattern will be
	 *            considered.
	 * @param q2
	 *            the second query to join, both its header and pattern will be
	 *            considered.
	 * 
	 * @return a NEW query, resulting from the join.
	 * 
	 */
	public static CustomSPARQLQuery joinWithAsOptional(CustomSPARQLQuery q1, CustomSPARQLQuery q2) {
		if (q1.isEmptyQuery() && q2.isEmptyQuery()) {
			return new CustomSPARQLQuery();
		}
		if (q2.isEmptyQuery()) {
			return new CustomSPARQLQuery(q1.getSparqlQuery().cloneQuery());
		} else {
			CustomSPARQLQuery q = new CustomSPARQLQuery();
			q.getSparqlQuery().setQuerySelectType();
			q.setQueryHeader(CustomSPARQLQuery.joinQueryHeaders(q1.getSparqlQuery().getProject(),
					q2.getSparqlQuery().getProject()));

			q.getSparqlQuery().setQueryPattern(q1.getSparqlQuery().getQueryPattern());

			q.makeEmptyGroupIfPatternIsNull();
			CustomSPARQLQuery.addPatternToQueryGroup(q.getSparqlQuery().getQueryPattern(),
					new ElementOptional(q2.getSparqlQuery().getQueryPattern()));

			return q;
		}
	}

	/**
	 * Non used keeps the part of the query after the top outer where keyword
	 * 
	 * @param q
	 *            the query string
	 * @return the resulting string as described
	 */
	public static String removeSelectFromQueryString(String q) {
		String res = "";
		String[] strs = q.split("WHERE", 2);
		res = strs[1].replaceAll("\n", "").replaceAll(" ", "");
		return res;
	}

	/**
	 * TODO creates a rooted query from a set of subsequent queries
	 * 
	 * @param queries
	 *            to join
	 * @param queryType
	 * @return the resulting RootedQuery
	 */
	public static CustomSPARQLQuery joinSubsequentQueries(List<Query> queries, QueryType queryType) {

		CustomSPARQLQuery rq = new CustomSPARQLQuery();
		for (Query query : queries) {
			// rq.unifyVariablesNaming(query); // ensure that there are no
			// naming contradictions
			rq.getSprqlQuery().setQueryPattern(
					CustomSPARQLQuery.joinQueryPatterns(rq.getSprqlQuery().getQueryPattern(), query.getQueryPattern()));
		}
		rq.setQueryHeader((queries.get(queries.size() - 1)).getProject()); // last
		// query's
		// header
		// is
		// the
		// result's
		// header
		rq.getSparqlQuery().setQuerySelectType();
		return rq;
	}

	/**
	 * Creates a new query, and adds the patterns (bodies) of the joined query
	 * to it one by one. The header of the resulting query is the header of the
	 * last query in the list, as by the definition of the query of an MD path.
	 * 
	 * @param queries
	 *            a list of queries to join
	 * 
	 * @return the resulting {@code CustomSPARQLQuery}, or a null in case of a
	 *         failure
	 * 
	 */
	public static CustomSPARQLQuery createMDPathQuery(List<Query> queries) {

		CustomSPARQLQuery rq = CustomSPARQLQuery.createCustomSPARQLQueryWithEmptyGroup();
		Element queryPattern = rq.getSparqlQuery().getQueryPattern();

		for (Query query : queries) {

			if (!CustomSPARQLQuery.addPatternToQueryGroup(queryPattern, query.getQueryPattern())) {
				return null;
			}
		}
		// last query's header is the result's header
		rq.setQueryHeader((queries.get(queries.size() - 1)).getProject());
		return rq;
	}

	/**
	 * TODO this function should be extended, to have a list of RootedQuery
	 * lists as a parameter --> List <List<RootedQuery>> queries given a list of
	 * sequential joinable RootedQueries, this function generates a RootedQuery
	 * that is the sequential join of the queries in the list.
	 * 
	 * @param queries
	 *            list of queries to join
	 * @param queryType
	 * @return
	 */
	public static CustomSPARQLQuery joinSubsequentRootedQueries(List<CustomSPARQLQuery> queries, QueryType queryType) {

		CustomSPARQLQuery rq = new CustomSPARQLQuery();
		for (CustomSPARQLQuery query : queries) {
			if (rq.isEmptyQuery()) {
				rq.getSparqlQuery().setQueryPattern(query.getSparqlQuery().getQueryPattern());
			} else {
				unifyVariablesNaming(query); // ensure that there are no naming
				// contradictions
				rq.getSprqlQuery().setQueryPattern(CustomSPARQLQuery.joinQueryPatterns(
						rq.getSprqlQuery().getQueryPattern(), query.getSprqlQuery().getQueryPattern()));
			}
		}
		rq.setQueryHeader((queries.get(queries.size() - 1)).getSparqlQuery().getProject()); // last
		// query's
		// header
		// is
		// the
		// result's
		// header
		rq.getSparqlQuery().setQuerySelectType();
		return rq;
	}

	/**
	 * TODO implement this function When there is naming differences between
	 * sequential queries to join, this funciton removes these differences
	 * 
	 * @param unifiedQuery
	 */
	public static void unifyVariablesNaming(CustomSPARQLQuery unifiedQuery) {

	}

	/**
	 * 
	 * Creates aggregation query.
	 * 
	 * @param sourceQuery
	 *            query to create aggregation
	 * @param aggOperator
	 *            the aggregation operator
	 * @param measureQueryHeadVar
	 *            the variable to aggregate
	 * @param nameOfCreatedVariable
	 *            name of the new aggregated variable (out variable)
	 * 
	 * @return the aggregation query
	 */
	public static CustomSPARQLQuery createAggregationQuery(CustomSPARQLQuery sourceQuery, Object aggOperator,
			Var measureQueryHeadVar,
			List<String> nameOfCreatedVariable /* out variable */) {

		CustomSPARQLQuery q = new CustomSPARQLQuery();
		q.getSprqlQuery().setQuerySelectType();
		q.setQueryHeader(insertAggOpIntoQueryHeader(sourceQuery, aggOperator.toString(), measureQueryHeadVar,
				nameOfCreatedVariable));
		q.getSprqlQuery().setQueryPattern(sourceQuery.getSprqlQuery().getQueryPattern());
		q.getSparqlQuery().getGroupBy().addAll(sourceQuery.getSparqlQuery().getGroupBy());
		return q;
	}

	/**
	 * generates an aggregation query that aggregates the measure variable using
	 * the aggregation funciton TODO the new instantiation of aggregation
	 * variables here needs to take DISTINCT into consideration
	 * 
	 * @param aggOperator
	 *            the aggregation operator to insert. Currently it is passed as
	 *            a String
	 * @param measureQueryHeadVar
	 *            where to apply the aggregation
	 * @return VarExprList including the aggregated measure
	 */
	public static VarExprList insertAggOpIntoQueryHeader(CustomSPARQLQuery query, Object aggOperator,
			Var measureQueryHeadVar,
			List<String> nameOfCreatedVariable /* out variable */) {
		VarExprList qh = query.getSprqlQuery().getProject();
		qh.remove(measureQueryHeadVar);
		String varName = "";
		if ("count".equalsIgnoreCase((String) aggOperator)) {
			ExprVar instance = new ExprVar(measureQueryHeadVar);
			varName = nameVariable("count", measureQueryHeadVar.getName());
			qh.add(Var.alloc(varName), new ExprAggregator(instance.asVar(), new AggCountVar(instance)));
		} else {
			if ("sum".equalsIgnoreCase((String) aggOperator)) {
				ExprVar instance = new ExprVar(measureQueryHeadVar);
				varName = nameVariable("sum", measureQueryHeadVar.getName());
				qh.add(Var.alloc(varName), new ExprAggregator(instance.asVar(), new AggSum(instance)));
			} else {
				if ("avg".equalsIgnoreCase((String) aggOperator)) {
					ExprVar instance = new ExprVar(measureQueryHeadVar);
					varName = nameVariable("avg", measureQueryHeadVar.getName());
					qh.add(Var.alloc(varName), new ExprAggregator(instance.asVar(), new AggAvg(instance)));
				} else {
					if ("min".equalsIgnoreCase((String) aggOperator)) {
						ExprVar instance = new ExprVar(measureQueryHeadVar);
						varName = nameVariable("min", measureQueryHeadVar.getName());
						qh.add(Var.alloc(varName), new ExprAggregator(instance.asVar(), new AggMin(instance)));
					} else {
						if ("max".equalsIgnoreCase((String) aggOperator)) {
							ExprVar instance = new ExprVar(measureQueryHeadVar);
							varName = nameVariable("max", measureQueryHeadVar.getName());
							qh.add(Var.alloc(varName), new ExprAggregator(instance.asVar(), new AggMax(instance)));
						} else {
							if ("count_distinct".equalsIgnoreCase((String) aggOperator)) {
								ExprVar instance = new ExprVar(measureQueryHeadVar);
								varName = nameVariable("countDistinct", measureQueryHeadVar.getName());
								qh.add(Var.alloc(varName),
										new ExprAggregator(instance.asVar(), new AggCountVarDistinct(instance)));
							}
						}
					}
				}
			}
		}
		nameOfCreatedVariable.add(varName);
		return qh;
	}

	/**
	 * generates an aggregation query that aggregates the measure variable using
	 * the aggregation funciton TODO the new instantiation of aggregation
	 * variables here needs to take DISTINCT into consideration
	 * 
	 * @param aggOperator
	 *            the aggregation operator to insert. Currently it is passed as
	 *            a String
	 * @param measureQueryHeadVar
	 *            where to apply the aggregation
	 * @return VarExprList including the aggregated measure
	 */
	public static VarExprList insertAggOpIntoQueryHeaderWithPredefinedName(CustomSPARQLQuery query, Object aggOperator,
			Var measureQueryHeadVar,
			List<String> nameOfCreatedVariable /* out variable */, String nameOfCreatedVar) {
		VarExprList qh = query.getSprqlQuery().getProject();
		qh.remove(measureQueryHeadVar);
		String varName = "";
		if ("count".equalsIgnoreCase((String) aggOperator)) {
			ExprVar instance = new ExprVar(measureQueryHeadVar);
			varName = nameOfCreatedVar;
			qh.add(Var.alloc(varName), new ExprAggregator(instance.asVar(), new AggCountVar(instance)));
		} else {
			if ("sum".equalsIgnoreCase((String) aggOperator)) {
				ExprVar instance = new ExprVar(measureQueryHeadVar);
				varName = nameOfCreatedVar;
				qh.add(Var.alloc(varName), new ExprAggregator(instance.asVar(), new AggSum(instance)));
			} else {
				if ("avg".equalsIgnoreCase((String) aggOperator)) {
					ExprVar instance = new ExprVar(measureQueryHeadVar);
					varName = nameOfCreatedVar;
					qh.add(Var.alloc(varName), new ExprAggregator(instance.asVar(), new AggAvg(instance)));
				} else {
					if ("min".equalsIgnoreCase((String) aggOperator)) {
						ExprVar instance = new ExprVar(measureQueryHeadVar);
						varName = nameOfCreatedVar;
						qh.add(Var.alloc(varName), new ExprAggregator(instance.asVar(), new AggMin(instance)));
					} else {
						if ("max".equalsIgnoreCase((String) aggOperator)) {
							ExprVar instance = new ExprVar(measureQueryHeadVar);
							varName = nameOfCreatedVar;
							qh.add(Var.alloc(varName), new ExprAggregator(instance.asVar(), new AggMax(instance)));
						} else {
							if ("count_distinct".equalsIgnoreCase((String) aggOperator)) {
								ExprVar instance = new ExprVar(measureQueryHeadVar);
								varName = nameOfCreatedVar;
								qh.add(Var.alloc(varName),
										new ExprAggregator(instance.asVar(), new AggCountVarDistinct(instance)));
							}
						}
					}
				}
			}
		}
		nameOfCreatedVariable.add(varName);
		return qh;
	}

	/**
	 * 
	 * Fails when number is bigger than 9 (two decimal places)
	 * 
	 * @param aggOp
	 * @param originalVarName
	 * @return
	 * 
	 */
	private static String nameVariable(String aggOp, String originalVarName) {

		if (originalVarName.toUpperCase().contains(aggOp.toUpperCase())) {
			int num = 2;
			try {
				num = Integer.parseInt("" + originalVarName.charAt(originalVarName.length() - 1)) + 1;
			} catch (Exception ex) {

			}
			return originalVarName + num;
		} else {
			return aggOp + originalVarName.substring(0, 1).toUpperCase()
					+ originalVarName.substring(1, originalVarName.length());
		}
	}

	/**
	 * applies Inserts
	 * 
	 * @param aggOperator
	 * @return
	 */
	public static VarExprList insertAggOpIntoQueryHeader(CustomSPARQLQuery query, Object aggOperator) {

		VarExprList qh = query.getSprqlQuery().getProject();
		Var measureQueryHeadVar = query.getUnaryProjectionVar();
		// single query header variable --> this header is the query root
		if (measureQueryHeadVar == null)
			if (query.getSprqlQuery().getProject().getVars().size() == 1)
				measureQueryHeadVar = query.getSprqlQuery().getProject().getVars().get(0);

		qh.remove(measureQueryHeadVar);

		if ("count".equalsIgnoreCase((String) aggOperator)) {
			ExprVar instance = new ExprVar(measureQueryHeadVar);
			qh.add(Var.alloc("count"), new ExprAggregator(instance.asVar(), new AggCountVar(instance)));
		}
		return qh;
	}

	/**
	 * adds the passed queryString parameter to the end of the query before the
	 * last '}'
	 * 
	 * @param queryString
	 *            the string of the query to be manipulated
	 * @return
	 */
	static public String appendToQueryString(String queryString, String stringToAppend) {

		int index = queryString.lastIndexOf('}');
		String newQueryString = queryString.substring(0, index) + " \n " + stringToAppend + " \n "
				+ queryString.substring(index, queryString.length());
		return newQueryString;
	}

	/**
	 * append LIMIT 1 to the end of the query
	 * 
	 */
	public static void insertLIMIT1AtQueryEnd(CustomSPARQLQuery query) {
		ParameterizedSparqlString pStr = new ParameterizedSparqlString(query.getSprqlQuery().toString());
		String str = pStr.toString();
		str += " LIMIT 1";
		Query q = QueryFactory.create(str);
		query.setSparqlQuery(q);
	}

	/**
	 * append ORDER BY to the end of the query
	 * 
	 */
	public static void insertORDERBYAtQueryEnd(CustomSPARQLQuery query, String orderByVarName) {
		ParameterizedSparqlString pStr = new ParameterizedSparqlString(query.getSprqlQuery().toString());
		String str = pStr.toString();
		str += " ORDER BY ?" + orderByVarName;
		Query q = QueryFactory.create(str);
		query.setSparqlQuery(q);
	}

	/**
	 * append ORDER BY DESC to the end of the query
	 * 
	 */
	public static void insertORDERBYDescAtQueryEnd(CustomSPARQLQuery query, String orderByVarName) {
		ParameterizedSparqlString pStr = new ParameterizedSparqlString(query.getSprqlQuery().toString());
		String str = pStr.toString();
		str += " ORDER BY DESC (?" + orderByVarName + ")";
		Query q = QueryFactory.create(str);
		query.setSparqlQuery(q);
	}

	/**
	 * Append distinct to a query with a single head variable
	 * 
	 * @return true if the insertion is done successfully, false otherwise
	 */
	public static boolean insertDistinctToSingleHeadedQuery(CustomSPARQLQuery query) {

		if (query.getSprqlQuery().getProjectVars().size() != 1)
			return false;
		else {
			ParameterizedSparqlString pStr = new ParameterizedSparqlString(query.getSprqlQuery().toString());
			String str = pStr.toString();
			str = str.replaceFirst("(?i)" + Pattern.quote("select"), "select distinct");
			Query q = QueryFactory.create(str);
			query.setSparqlQuery(q);
			return true;
		}
	}

	/**
	 * 
	 * Append IF clause as string to the select. If the granularity variable is
	 * not bound, then a default value that uses "Other" and the name of the
	 * variable is used.
	 * 
	 * @param query
	 * @param ifVariableName
	 * 
	 * @return true if the insertion is done successfully, false otherwise
	 * 
	 */
	public static boolean appendIfToQueryHeader(CustomSPARQLQuery query, String ifVariableName) {

		if (query == null) {
			return false;

		} else {

			String newVarName = ifVariableName;
			if (!ifVariableName.startsWith("?")) {
				newVarName = "\\?" + newVarName;
			}
			String queryString = query.getSparqlQuery().toString().replaceFirst(newVarName,
					"(IF (BOUND (" + newVarName + ")," + newVarName + ", \"Other" + ifVariableName + "\" ) AS "
							+ renameVariableByNumber(newVarName) + ") ");
			Query q = QueryFactory.create(queryString);
			query.setSparqlQuery(q);
			return true;
		}
	}

	/**
	 * Append IF clause as string to the select. if the granularity variable is
	 * not found, it's sub elements are used instead in the bind clause
	 * recursively until the fact is reached.
	 * 
	 * @param query
	 * @param ifVariableName
	 * @param mdSchema
	 * @param elem
	 * 
	 * @return true if the insertion is done successfully, false otherwise
	 * 
	 */
	public static boolean appendIfToQueryHeaderRecursively(CustomSPARQLQuery query, String ifVariableName,
			MDSchema mdSchema, MDElement elem) {

		if (query == null) {
			return false;

		} else {

			String newVarName = ifVariableName;
			if (!ifVariableName.startsWith("?")) {
				newVarName = "\\?" + newVarName;
			}
			String queryString = query.getSparqlQuery().toString().replaceFirst(newVarName,
					"(IF (BOUND (" + newVarName + ")," + newVarName + ","
							+ appendIfToQueryHeaderRecursivelyPart(query, ifVariableName, mdSchema, elem, true)
							+ " ) AS " + renameVariableByNumber(newVarName) + ") ");
			Query q = QueryFactory.create(queryString);
			query.setSparqlQuery(q);
			return true;
		}
	}

	/**
	 * @param query
	 * @param ifVariableName
	 * @param mdSchema
	 * @param elem
	 * @param firstPass
	 * @return
	 */
	private static String appendIfToQueryHeaderRecursivelyPart(CustomSPARQLQuery query, String ifVariableName,
			MDSchema mdSchema, MDElement elem, boolean firstPass) {

		if (firstPass) {
			return appendIfToQueryHeaderRecursivelyPart(query,
					mdSchema.getDescendantMDElement(elem.getIdentifyingName()).getMapping().getQuery()
							.getNameOfUnaryProjectionVar(),
					mdSchema, mdSchema.getDescendantMDElement(elem.getURI()), false);
		}

		if (query == null) {
			return "";

		} else {

			String newVarName = ifVariableName;
			if (!ifVariableName.startsWith("?")) {
				newVarName = "\\?" + newVarName;
			}

			if (elem.equals(mdSchema.getFactOfSchema())) {
				return "(IF (BOUND (" + newVarName + ")," + newVarName + ", \"Other" + ifVariableName + "\" )) ";
			} else {
				return "(IF (BOUND (" + newVarName + ")," + newVarName + ","
						+ appendIfToQueryHeaderRecursivelyPart(query,
								mdSchema.getDescendantMDElement(elem.getURI()).getMapping().getQuery()
										.getNameOfUnaryProjectionVar(),
								mdSchema, mdSchema.getDescendantMDElement(elem.getURI()), false)
						+ "  )) ";
			}
		}
	}

	/**
	 * 
	 * Replaces all occurrences of a variable in a query with a new name for the
	 * variable.
	 * 
	 * @param query
	 * @param oldVarName
	 *            the old variable name (without a question mark)
	 * @param newVarName
	 *            the new variable name (without a question mark)
	 * 
	 * @return a query with replaced variable as specified.
	 */
	public static Query renameAllOccurrencesOfVariable(Query query, String oldVarName, String newVarName) {

		String queryString = query.toString();

		queryString = queryString.replaceAll("\\?" + oldVarName + "[\\W]?", "?" + newVarName + " ");

		return QueryFactory.create(queryString);
	}

	public static String renameVariableByNumber(String originalVarName) {

		int num = 2;
		try {
			num = Integer.parseInt("" + originalVarName.charAt(originalVarName.length() - 1)) + 1;
		} catch (NumberFormatException ex) {

		}
		return originalVarName + num;

	}

	/**
	 * Append filter clause with FILTER() part as string to the end of the where
	 * clause of the passed query
	 * 
	 * @return true if the insertion is done successfully, false otherwise
	 */
	public static boolean appendFilterClauseToQuery(CustomSPARQLQuery query, String filter) {

		if (query == null) {
			return false;

		} else {
			String queryString = query.getSparqlQuery().toString();
			int index = queryString.lastIndexOf("}");
			String newString = queryString.substring(0, index);
			newString += " FILTER (" + filter + ")" + "}";
			Query q = QueryFactory.create(newString);
			query.setSparqlQuery(q);
			return true;
		}
	}

	/**
	 * Append filter clause without FILTER() part as string to the end of the
	 * where clause of the passed query
	 * 
	 * @return true if the insertion is done successfully, false otherwise
	 */
	public static boolean appendFiltersToQuery(CustomSPARQLQuery query, String filter) {

		if (query == null) {
			return false;

		} else {
			String queryString = query.getSparqlQuery().toString();
			int index = queryString.lastIndexOf("}");
			String newString = queryString.substring(0, index);
			newString += filter + "}";
			Query q = QueryFactory.create(newString);
			query.setSparqlQuery(q);
			return true;
		}
	}

	/**
	 * Given a FILTER statement containing a value of a specific type, this
	 * function puts it in a SPARQL query ready form. For example
	 * 1^^http://www.example.com/int becomes "1"^^<http://www.example.com/int>
	 * 
	 * @return the new processed operand if success, same passed operand if
	 *         failure.
	 */
	public static String literalzeConditoinOperand(String operand) {

		// The dice value is a data typed value (literal)
		if (operand.contains("^^")) {
			String[] tmp = operand.split("\\^\\^");
			String part1 = tmp[0].substring(0, tmp[0].length());
			String part2 = tmp[1].substring(0, tmp[1].length());

			if (!part1.startsWith("\"") && !part1.endsWith("\"")) {
				part1 = "\"" + part1 + "\"";
			}
			if (!part2.startsWith("<") && !part2.endsWith(">")) {
				part2 = "<" + part2 + ">";
			}
			return part1 + "^^" + part2;
		}
		return operand;
	}

	/**
	 * Append distinct to a query with a single head variable
	 * 
	 * @return true if the insertion is done successfully, false otherwise
	 */
	public static boolean insertDistinctToDoubleHeadedQuery(CustomSPARQLQuery query) {

		if (query.getSprqlQuery().getProjectVars().size() != 2)
			return false;
		else {
			ParameterizedSparqlString pStr = new ParameterizedSparqlString(query.getSprqlQuery().toString());
			String str = pStr.toString();
			str = str.replaceFirst("(?i)" + Pattern.quote("select"), "select distinct");
			Query q = QueryFactory.create(str);
			query.setSparqlQuery(q);
			return true;
		}
	}

	/**
	 * Append distinct to a query with a single head variable
	 * 
	 * @return true if the insertion is done successfully, false otherwise
	 */
	public static boolean insertDistinctToHeadedQuery(CustomSPARQLQuery query) {

		if (query.getSprqlQuery().getProjectVars().size() == 0)
			return false;
		else {
			ParameterizedSparqlString pStr = new ParameterizedSparqlString(query.getSprqlQuery().toString());
			String str = pStr.toString();
			str = str.replaceFirst("(?i)" + Pattern.quote("select"), "select distinct");
			Query q = QueryFactory.create(str);
			query.setSparqlQuery(q);
			return true;
		}
	}

	/**
	 * 
	 * Modifies the calling current object Adds a HAVING expression to the
	 * aggregation query to ensure correct execution.
	 * 
	 */
	public static void addHavingToQuery(CustomSPARQLQuery query, String havingConds) {
		if (!query.getSparqlQuery().getGroupBy().isEmpty()) {

			ParameterizedSparqlString pStr = new ParameterizedSparqlString(query.getSprqlQuery().toString());
			String str = pStr.toString();

			if (query.getSparqlQuery().getHavingExprs() == null || query.getSparqlQuery().getHavingExprs().isEmpty()) {
				str += "HAVING (" + havingConds + ")";
				Query q = QueryFactory.create(str);
				query.setSparqlQuery(q);
			} else {
				str = str.replace("\\bHAVING\\s*\\(", "HAVING (" + "cond1" + " && ");
				Query q = QueryFactory.create(str);
				query.setSparqlQuery(q);
			}
		}
	}

	/**
	 * 
	 * Modifies the calling current object Adds a HAVING condition WITHOUT
	 * HAVING clause to the aggregation query to ensure correct execution.
	 * 
	 */
	public static void addHavingConditionsToQuery(CustomSPARQLQuery query, String havingConds) {
		if (!query.getSparqlQuery().getGroupBy().isEmpty()) {

			ParameterizedSparqlString pStr = new ParameterizedSparqlString(query.getSprqlQuery().toString());
			String str = pStr.toString();

			if (query.getSparqlQuery().getHavingExprs() == null || query.getSparqlQuery().getHavingExprs().isEmpty()) {
				str += havingConds;
				Query q = QueryFactory.create(str);
				query.setSparqlQuery(q);
			} else {
				str = str.replace("\\bHAVING\\s*\\(", "HAVING (" + "cond1" + " && ");
				Query q = QueryFactory.create(str);
				query.setSparqlQuery(q);
			}
		}
	}

	/**
	 * 
	 * @return a new object of type Query, which is labeled
	 */
	public static Query addLabelsToQuery(CustomSPARQLQuery query, String labelURI) {

		Query newQuery = new Query();
		newQuery.setQuerySelectType();
		ElementSubQuery subQuery = new ElementSubQuery(query.getSparqlQuery());

		newQuery.setQueryPattern(subQuery);

		List<Var> vars = new ArrayList<Var>();
		List<Triple> triplesToAdd = new ArrayList<Triple>();

		VarExprList qh = new VarExprList();
		ListIterator<Var> itr = query.getSparqlQuery().getProjectVars().listIterator();

		List<Var> aggregateVars = new ArrayList<Var>();

		while (itr.hasNext()) {

			Var v = itr.next();

			if (!query.getSparqlQuery().getProject().getExprs().keySet().contains(v)) { // ensure
				// the
				// variable
				// is
				// not
				// used
				// in
				// an
				// expression

				// newQuery.addResultVar(v.getName() + "_label");

				ExprList exprList = new ExprList();
				exprList.add(new ExprVar(v.getName() + "_labelTemp"));
				exprList.add(new ExprVar(v.getName()));
				org.apache.jena.sparql.expr.E_Coalesce coal = new E_Coalesce(exprList);

				qh.add(Var.alloc(v.getName()));
				qh.add(Var.alloc(v.getName() + "_label"), coal);

				Triple triple = Triple.create(Var.alloc(v.getName()), NodeFactory.createURI(labelURI),
						Var.alloc(v.getName() + "_labelTemp"));

				triplesToAdd.add(triple);
			} else {
				aggregateVars.add(Var.alloc(v.getName()));
			}
		}

		// adding non aggregate variables first
		newQuery.getProject().addAll(qh);
		// adding aggregate variables then [order is relevant]
		for (Var v : aggregateVars) {
			newQuery.addResultVar(v);
		}

		ElementTriplesBlock elm = new ElementTriplesBlock();

		ElementGroup grp = new ElementGroup();
		List<ElementOptional> optionalTriples = new ArrayList<ElementOptional>();

		for (Triple trp : triplesToAdd) {
			ElementGroup group = new ElementGroup();
			ElementTriplesBlock elm1 = new ElementTriplesBlock();

			Expr e = new E_Lang(new ExprVar(Var.alloc(trp.getObject().getName())));

			Expr ee = new NodeValueString("EN");

			Expr e1 = new E_LangMatches(e, ee);

			org.apache.jena.sparql.syntax.ElementFilter filter = new org.apache.jena.sparql.syntax.ElementFilter(e1);

			elm1.addTriple(trp);
			group.addElement(elm1);
			group.addElementFilter(filter);
			ElementOptional elmOptional = new ElementOptional(group);

			optionalTriples.add(elmOptional);

			// grp.addElement(elmOptional);
		}

		ElementGroup elmGrp = new ElementGroup();
		elmGrp.addElement(newQuery.getQueryPattern());
		for (ElementOptional elmOptional : optionalTriples) {
			elmGrp.addElement(elmOptional);
			newQuery.setQueryPattern(elmGrp);
			// newQuery.setQueryPattern(joinQueryPatterns(newQuery.getQueryPattern(),
			// elmOptional));
		}

		// newQuery.setQueryPattern(joinQueryPatterns(newQuery.getQueryPattern(),
		// grp));

		/*
		 * triplesToAdd.stream().forEach(triple -> elm.addTriple(triple));
		 * newQuery.setQueryPattern(joinQueryPatterns(newQuery.getQueryPattern()
		 * , elm));
		 */
		return newQuery;

	}

	/**
	 * Creates a new query that applies the passed selectExpression in the
	 * project of the passed query which becomes the body of the new query.
	 * 
	 * @param query
	 *            the query to become the body of the returned query.
	 * @param selectExpression
	 *            the expression to be applied in the project of the returned
	 *            query.
	 * 
	 * @return a new query.
	 */
	public static String encapsulateQuyerInSelect(Query query, String selectExpression) {

		String queryString = query.toString();

		queryString = "SELECT " + selectExpression + "{" + queryString + "}";

		return queryString;
	}

	/**
	 * @return a new instance of Query
	 */
	public static Query encapsulateBasicQuery(CustomSPARQLQuery query, String uri) {

		Query newQuery = new Query();
		newQuery.setQuerySelectType();
		ElementSubQuery subQuery = new ElementSubQuery(query.getSparqlQuery());

		newQuery.setQueryPattern(subQuery);

		List<Var> vars = new ArrayList<Var>();
		List<Triple> triplesToAdd = new ArrayList<Triple>();

		VarExprList qh = new VarExprList();
		ListIterator<Var> itr = query.getSparqlQuery().getProjectVars().listIterator();

		List<Var> aggregateVars = new ArrayList<Var>();

		while (itr.hasNext()) {

			Var v = itr.next();

			if (!query.getSparqlQuery().getProject().getExprs().keySet().contains(v)) { // ensure
				// the
				// variable
				// is
				// not
				// used
				// in
				// an
				// expression

				// newQuery.addResultVar(v.getName() + "_label");

				ExprList exprList = new ExprList();
				exprList.add(new ExprVar(v.getName() + "_labelTemp"));
				exprList.add(new ExprVar(v.getName()));
				org.apache.jena.sparql.expr.E_Coalesce coal = new E_Coalesce(exprList);

				qh.add(Var.alloc(v.getName() + "_label"), coal);

				Triple triple = Triple.create(Var.alloc(v.getName()), NodeFactory.createURI(uri),
						Var.alloc(v.getName() + "_labelTemp"));

				triplesToAdd.add(triple);
			} else {
				aggregateVars.add(Var.alloc(v.getName()));
			}
		}

		// adding non aggregate variables first
		newQuery.getProject().addAll(qh);
		// adding aggregate variables then [order is relevant]
		for (Var v : aggregateVars) {
			newQuery.addResultVar(v);
		}

		generateLanguageTags(newQuery, triplesToAdd);

		// newQuery.setQueryPattern(joinQueryPatterns(newQuery.getQueryPattern(),
		// grp));

		/*
		 * triplesToAdd.stream().forEach(triple -> elm.addTriple(triple));
		 * newQuery.setQueryPattern(joinQueryPatterns(newQuery.getQueryPattern()
		 * , elm));
		 */
		return newQuery;
	}

	private static void generateLanguageTags(Query newQuery, List<Triple> triplesToAdd) {
		List<ElementOptional> optionalTriples = new ArrayList<ElementOptional>();

		for (Triple trp : triplesToAdd) {

			ElementTriplesBlock elm1 = new ElementTriplesBlock();
			ElementGroup group = new ElementGroup();

			Expr e = new E_Lang(new ExprVar(Var.alloc(trp.getObject().getName())));
			Expr ee = new NodeValueString("EN");
			Expr e1 = new E_LangMatches(e, ee);

			Expr eG = new NodeValueString("EN-GB");
			Expr e2 = new E_LogicalNot(new E_LangMatches(e, eG));

			Expr eU = new NodeValueString("EN-US");
			Expr e3 = new E_LogicalNot(new E_LangMatches(e, eU));

			Expr eC = new NodeValueString("EN-CA");
			Expr e4 = new E_LogicalNot(new E_LangMatches(e, eC));

			ElementFilter filter = new ElementFilter(
					new E_LogicalAnd(new E_LogicalAnd(e1, e2), new E_LogicalAnd(e3, e4)));

			elm1.addTriple(trp);

			group.addElement(elm1);

			if (Configuration.getInstance().is("handleLabels")) {
				group.addElementFilter(filter);
			}

			ElementOptional elmOptional = new ElementOptional(group);

			optionalTriples.add(elmOptional);
			// grp.addElement(elmOptional);
		}

		ElementGroup elmGrp = new ElementGroup();
		elmGrp.addElement(newQuery.getQueryPattern());
		for (ElementOptional elmOptional : optionalTriples) {
			elmGrp.addElement(elmOptional);
			newQuery.setQueryPattern(elmGrp);
			// newQuery.setQueryPattern(joinQueryPatterns(newQuery.getQueryPattern(),
			// elmOptional));
		}
	}

	public static String generateLabelsQuery(CustomSPARQLQuery query) {

		String queryBody = query.getSparqlQuery().getQueryPattern().toString();

		Set<Var> exprVars = new LinkedHashSet<Var>();

		for (Map.Entry<Var, Expr> entry : query.getSparqlQuery().getProject().getExprs().entrySet()) {
			exprVars.add(entry.getKey());
		}

		for (Var v : query.getSparqlQuery().getProjectVars()) {
			if (!exprVars.contains(v)) {
				// queryBody += addLabelTriplePattern(v.getName());
				// this.getSparqlQuery().addResultVar(v.getName()+ "_label .");
				// this.getSparqlQuery().getProject().remove(v);
			}
		}

		return queryBody;
	}

	public String addLabelTriplePattern(CustomSPARQLQuery query, String varName) {
		return varName + "<http://www.w3.org/2000/01/rdf-schema#label> " + "?" + varName + "_label .";
	}

}
