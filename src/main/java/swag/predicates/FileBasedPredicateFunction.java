package swag.predicates;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.log4j.Logger;

import swag.analysis_graphs.execution_engine.analysis_situations.*;
import swag.data_handler.Constants;
import swag.data_handler.connection_to_rdf.SPARQLEndpointConnection;
import swag.data_handler.connection_to_rdf.exceptions.RemoteSPARQLQueryExecutionException;
import swag.md_elements.*;

public class FileBasedPredicateFunction implements IPredicateFunctions {

    IPredicateGraph graph;
    Model model;
    SPARQLEndpointConnection conn;

    private static final Logger logger = Logger.getLogger(FileBasedPredicateFunction.class);

    protected FileBasedPredicateFunction(IPredicateGraph graph, Model model, SPARQLEndpointConnection conn) {
	super();
	if (graph != null && model != null && conn != null) {
	    this.graph = graph;
	    this.model = model;
	    this.conn = conn;
	} else {
	    throw new IllegalArgumentException("Cannot create FileBasedPredicateFunction instance; invalid parameters");
	}
    }

    @Override
    public List<LiteralConditionType> getAllLiteralConditionTypes(MDSchema schema) {

	ResultSet res = queryLiteralConditionTypes(model);
	List<LiteralConditionType> predicates = buildLiteralConditionTypes(res, schema);

	graph.addNode(LiteralConditionRootClass.createInstance());

	graph.createEdgeAndAddSourceEdgeTargetTriple(LiteralConditionRootClass.createInstance(),
		EPredicateEdgeType.INSTANCE_OF, LiteralConditionNoTypeClass.createInstance());

	for (LiteralConditionType pred : predicates) {
	    graph.createEdgeAndAddSourceEdgeTargetTriple(LiteralConditionRootClass.createInstance(),
		    EPredicateEdgeType.INSTANCE_OF, pred);
	}

	graph.addExpansion(PredicateClass.createInstance(),
		new Expansion(EExpansionType.LITERAL_CONDITION_TYPES, null));

	return predicates;
    }

    @Override
    public List<LiteralCondition> getAllConditions(MDSchema schema) {



	ResultSet res = queryLiteralConditions(model);
	List<LiteralConditionType> condTypes = new ArrayList<>();
	Expansion ex = new Expansion(EExpansionType.LITERAL_CONDITION_TYPES, null);

	if (!graph.isExpanded(graph.getNode(LiteralConditionRootClass.createInstance().getIdentifyingName()), ex)) {
	    condTypes = getAllLiteralConditionTypes(schema);
	}

	List<LiteralCondition> conditoins = buildConditions(res, schema, condTypes);

	Set<String> allTypes = conditoins.stream().flatMap(x -> x.getTypes().stream()).collect(Collectors.toSet());

	for (LiteralCondition c : conditoins) {

	    for (String type : c.getTypes()) {
		if (type != null) {
			if(graph.getNode(type) == null){
				graph.addNode(conditoins.stream().filter(cond -> cond.getURI().equals(type)).findAny().orElse(null));
			}
		    graph.createEdgeAndAddSourceEdgeTargetTriple(graph.getNode(type), EPredicateEdgeType.INSTANCE_OF,
			    c);
		} else {
		    graph.createEdgeAndAddSourceEdgeTargetTriple(LiteralConditionNoTypeClass.createInstance(),
			    EPredicateEdgeType.INSTANCE_OF, c);
		}
	    }

	}

	for (String type : allTypes) {
	    graph.addExpansion(graph.getNode(type), new Expansion(EExpansionType.LITERAL_CONDITOINS, null));
	}

	graph.addExpansion(LiteralConditionNoTypeClass.createInstance(),
		new Expansion(EExpansionType.LITERAL_CONDITOINS, null));

	return conditoins;
    }

    @Override
    public List<Predicate> getAllPredicates() {

	ResultSet res = queryPredicates(model);
	List<Predicate> predicates = buildPredicates(res);

	graph.addNode(PredicateClass.createInstance());

	for (Predicate pred : predicates) {
	    graph.createEdgeAndAddSourceEdgeTargetTriple(PredicateClass.createInstance(),
		    EPredicateEdgeType.INSTANCE_OF, pred);
	}

	graph.addExpansion(PredicateClass.createInstance(), new Expansion(EExpansionType.PREDICATES, null));

	return predicates;
    }

    /**
     * 
     * Get all the predicate instances of a specific predicate in the model.
     * 
     * @param m
     *            a Jena model on the file/other storage where the
     *            swag.predicates are stored
     * @param pred
     *            the predicate to get instances of
     * 
     * @return a {@code ResultSet} of executing the predicate instances query
     * 
     */
    public static ResultSet queryPredicatesInstances(Model m, String predicateURI) {

	String queryString = QUERY_STRINGS.PREDICATE_INSTANCES;
	Query query = QueryFactory.create(queryString);
	queryString.replaceAll("\\?xXxPredicatexXx", predicateURI);
	QueryExecution exec = QueryExecutionFactory.create(query, m);
	ResultSet res = exec.execSelect();
	return res;
    }

    /**
     * 
     * Get all the literal condition in the model.
     * 
     * @param m
     *            a Jena model on the file/other storage where the
     *            swag.predicates are stored
     * 
     * @return a {@code ResultSet} of executing the
     *         swag.predicates query
     * 
     */
    public static ResultSet queryLiteralConditions(Model m) {

	String queryString = QUERY_STRINGS.LITERAL_CONDITIONS;
	Query query = QueryFactory.create(queryString);
	QueryExecution exec = QueryExecutionFactory.create(query, m);
	ResultSet res = exec.execSelect();
	return res;

    }

    /**
     * 
     * Get all the literal condition in the model.
     * 
     * @param m
     *            a Jena model on the file/other storage where the
     *            swag.predicates are stored
     * 
     * @return a {@code ResultSet} of executing the
     *         swag.predicates query
     * 
     */
    public static ResultSet queryLiteralConditionTypes(Model m) {

	String queryString = QUERY_STRINGS.LITERAL_CONDITION_TYPES;
	Query query = QueryFactory.create(queryString);
	QueryExecution exec = QueryExecutionFactory.create(query, m);
	ResultSet res = exec.execSelect();
	return res;

    }

    /**
     * 
     * Get all the swag.predicates in the model.
     * 
     * @param m
     *            a Jena model on the file/other storage where the
     *            swag.predicates are stored
     * 
     * @return a {@code ResultSet} of executing the
     *         swag.predicates query
     * 
     */
    public static ResultSet queryPredicates(Model m) {

	String queryString = QUERY_STRINGS.PREDICATES;
	Query query = QueryFactory.create(queryString);
	QueryExecution exec = QueryExecutionFactory.create(query, m);
	ResultSet res = exec.execSelect();
	return res;
    }

    /**
     * 
     * Generates {@code Predicate} objects from an input result set. Skips
     * conditions displaying errors in reading.
     * 
     * @param res
     *            the result set to retrieve data from to build the objects.
     * 
     * @return a {@code List} of swag.predicates
     * 
     */
    public static List<LiteralConditionType> buildLiteralConditionTypes(ResultSet res, MDSchema schema) {

	List<LiteralConditionType> predicates = new ArrayList<>();
	LiteralConditionType pred = new LiteralConditionType();
	boolean firstTime = true;
	boolean shouldSkip = false;
	// swag.predicates.add(pred);

	String predicateURI = "";

	while (res.hasNext()) {

	    QuerySolution sol = res.next();

	    if (!predicateURI.equals(sol.get("literalConditionType").toString())) {

		pred = new LiteralConditionType();
		predicates.add(pred);

		predicateURI = Utils.getStringValueIfNotNull(sol.get("literalConditionType"));
		pred.setUri(predicateURI);

		String label = Utils.getStringValueIfNotNull(sol.get("label"));
		pred.setName(label == null ? "" : label);
		pred.setLabel(label == null ? "" : label);

		String comment = Utils.getStringValueIfNotNull(sol.get("comment"));
		pred.setComment(comment == null ? "" : comment);

		String expression = Utils.getStringValueIfNotNull(sol.get("expression"));
		pred.setExpression(expression == null ? "" : expression);

		String sType = Utils.getStringValueIfNotNull(sol.get("sType"));
		pred.setSyntacticType(sType == null ? PredicateSyntacticTypes.DEFAULT
			: PredicateSyntacticTypes.findSyntacticType(sType));

		shouldSkip = false;

	    }

	    if (!shouldSkip) {

		firstTime = false;

		String inputvar = sol.get("inputVar") != null ? sol.get("inputVar").toString() : null;
		String inputvarType = sol.get("inputVarType") != null ? sol.get("inputVarType").toString() : null;
		String inputvarName = sol.get("inputVarName") != null ? sol.get("inputVarName").toString() : null;

		if (inputvar != null && inputvarName != null) {
		    PredicateInputVar var = new PredicateInputVar(inputvarType, inputvar, inputvarName);
		    pred.addToInputVars(var);
		} else {
		    logger.warn("Cannot read a variable for condition type " + predicateURI);
		    shouldSkip = true;
		    continue;
		}

		String positionVar = sol.get("positionVar") != null ? sol.get("positionVar").toString() : null;
		String positionVarType = sol.get("positionVarType") != null ? sol.get("positionVarType").toString()
			: null;
		String positionVarName = sol.get("positionVarName") != null ? sol.get("positionVarName").toString()
			: null;

		PredicateInputVar var;
		if (positionVar != null && positionVarName != null) {
		    var = new PredicateInputVar(positionVarType, positionVar, positionVarName);
		    pred.addToPositionVars(var);
		} else {
		    logger.warn("Cannot read a position for condition type " + predicateURI);
		    shouldSkip = true;
		    continue;
		}

		try {
		    for (MDElement elem : getMDElementFormSolution(sol, schema)) {
			pred.addToMDElems(elem);
			pred.addToMappings(new PredicateVariableToMDElementMapping(var, elem, null));
		    }
		} catch (Exception ex) {
		    logger.warn("Cannot read position for condition type" + predicateURI);
		    shouldSkip = true;
		    continue;
		}
	    }

	}
	return predicates;
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


	/**
     * 
     * Generates {@code Predicate} objects from an input result set. Skips
     * conditions displaying errors in reading.
     * 
     * @param res
     *            the result set to retrieve data from to build the objects.
     * 
     * @return a {@code List} of swag.predicates
     * 
     */
    public static List<LiteralCondition> buildConditions(ResultSet res, MDSchema schema,
	    List<LiteralConditionType> directTypes) {

	List<LiteralCondition> predicates = new ArrayList<>();
	LiteralCondition pred = new LiteralCondition();
	boolean firstTime = true;
	boolean shouldSkip = false;
	Set<String> types = new HashSet<>();
	// swag.predicates.add(pred);

	String predicateURI = "";

	while (res.hasNext()) {

	    QuerySolution sol = res.next();

	    if (!predicateURI.equals(sol.get("literalConditionType").toString())) {

		types = new HashSet<>();
		pred = new LiteralCondition();
		predicates.add(pred);

		predicateURI = Utils.getStringValueIfNotNull(sol.get("literalConditionType"));
		pred.setUri(predicateURI);

		String label = Utils.getStringValueIfNotNull(sol.get("label"));
		pred.setName(label == null ? "" : label);
		pred.setLabel(label == null ? "" : label);

		String comment = Utils.getStringValueIfNotNull(sol.get("comment"));
		pred.setComment(comment == null ? "" : comment);

		String expression = Utils.getStringValueIfNotNull(sol.get("expression"));
		pred.setExpression(expression == null ? "" : expression);

		String sType = Utils.getStringValueIfNotNull(sol.get("sType"));
		pred.setSyntacticType(sType == null ? PredicateSyntacticTypes.DEFAULT
			: PredicateSyntacticTypes.findSyntacticType(sType));

		shouldSkip = false;

	    }

	    if (!shouldSkip) {

		firstTime = false;

		String conditoinType = sol.get("conditionType") != null ? sol.get("conditionType").toString() : null;

		if (conditoinType != null) {
		    pred.addToTypes(conditoinType);
		}

		String implying = sol.get("implying") != null ? sol.get("implying").toString() : null;

		if (implying != null) {
			pred.addToTypes(implying);
		}

		String positionVar = sol.get("positionVar") != null ? sol.get("positionVar").toString() : null;
		String positionVarType = sol.get("positionVarType") != null ? sol.get("positionVarType").toString()
			: null;
		String positionVarName = sol.get("positionVarName") != null ? sol.get("positionVarName").toString()
			: null;

		PredicateInputVar var;
		if (positionVar != null && positionVarName != null) {
		    var = new PredicateInputVar(positionVarType, positionVar, positionVarName);
		    pred.addToPositionVars(var);
		} else {
		    logger.warn("Cannot read a position for condition type " + predicateURI);
		    shouldSkip = true;
		    continue;
		}

		try {
		    for (MDElement elem : getMDElementFormSolution(sol, schema)) {
			pred.addToMDElems(elem);
			pred.addToMappings(new PredicateVariableToMDElementMapping(var, elem, null));
		    }
		} catch (Exception ex) {
		    logger.warn("Cannot read position for condition type" + predicateURI);
		    shouldSkip = true;
		    continue;
		}
	    }

	}
	return predicates;
    }

    /**
     * 
     * Genrates {@code LiteralCondition} objects from an input result set.
     * 
     * @param res
     *            the result set to retrieve data from to build the objects.
     * 
     * @return a {@code List} of literal conditions
     * 
     */
    @Deprecated
    public static List<LiteralCondition> buildConditionsOld(ResultSet res, MDSchema schema,
	    List<LiteralConditionType> directTypes) {

	Set<LiteralCondition> conds = new HashSet<>();
	Map<String, String> bindings = new HashMap<String, String>();

	String conditoinURI = "";
	String exConditionString = "";
	Set<String> types = new HashSet<>();

	while (res.hasNext()) {

	    try {

		QuerySolution sol = res.next();
		String newURI = Utils.getStringValueIfNotNull(sol.get("conditoinURI"));

		// Still iterating within the same condition
		if (asUtilities.equalsWithNull(newURI, conditoinURI)) {
		    doCommonOperations(sol, bindings, types);

		    // New condition encountered
		} else {

		    String conditoinString = Utils.getStringValueIfNotNull(sol.get("conditoinString"));
		    conditoinURI = newURI;
		    String directType = Utils.getStringValueIfNotNull(sol.get(Constants.INSTANCE_OF));

		    LiteralConditionType directConditionType = directTypes.stream()
			    .filter(x -> x.getURI().equals(directType)).findFirst().orElse(null);

		    if (directConditionType == null) {
			logger.warn("Cannot find direct type of condition " + conditoinURI);
			continue;
		    }

		    String label = Utils.getStringValueIfNotNull(sol.get("label"));
		    String comment = Utils.getStringValueIfNotNull(sol.get("comment"));

		    LiteralCondition cond = new LiteralCondition(directConditionType, types, conditoinURI, comment,
			    label, label, conditoinString, null, null, null, null);
		    conds.add(cond);

		    doCommonOperations(sol, bindings, types);

		    types = new HashSet<>();
		    bindings = new HashMap<>();

		}
	    } catch (Exception ex) {
		logger.error("problem retrieving condition", ex);
		continue;
	    }
	}

	return new ArrayList<>(conds);
    }

    public static void doCommonOperations(QuerySolution sol, Map<String, String> bindings, Set<String> types) {

	String conditoinType = sol.get("conditionType") != null ? sol.get("conditionType").toString() : null;

	if (conditoinType != null) {
	    types.add(conditoinType);
	}

	// allElms.add(getMDElementFormSolution(sol, schema));

	String bindingVar = sol.get("bindingVar") != null ? sol.get("bindingVar").toString() : null;

	String bindingVal = sol.get("bindingVal") != null ? sol.get("bindingVal").toString() : null;

	if (!StringUtils.isEmpty(bindingVar) && !StringUtils.isEmpty(bindingVal)) {
	    bindings.put(bindingVar, bindingVal);
	}
    }

    /**
     * 
     * Genrates {@code Predicate} objects from an input result set.
     * 
     * @param res
     *            the result set to retrieve data from to build the objects.
     * 
     * @return a {@code List} of swag.predicates
     * 
     */
    public static List<Predicate> buildPredicates(ResultSet res) {

	List<Predicate> predicates = new ArrayList<>();
	Predicate pred = new Predicate();
	boolean firstTime = true;
	// swag.predicates.add(pred);

	String predicateURI = "";
	while (res.hasNext()) {
	    QuerySolution sol = res.next();

	    if (!predicateURI.equals(sol.get("predicate").toString())) {
		pred = new Predicate();
		predicates.add(pred);
		predicateURI = sol.get("predicate").toString();
		pred.setURI(predicateURI);
	    }

	    firstTime = false;

	    String outputvar = sol.get("outputVar") != null ? sol.get("outputVar").toString() : null;
	    String outputvarType = sol.get("outputVarType") != null ? sol.get("outputVarType").toString() : null;
	    String outputvarName = sol.get("outputVarName") != null ? sol.get("outputVarName").toString() : null;

	    if (outputvar != null) {
		PredicateOutputVar var = new PredicateOutputVar(outputvarType, outputvar, outputvarName);
		pred.addTooUTputVars(var);
	    }

	    String inputvar = sol.get("inputVar") != null ? sol.get("inputVar").toString() : null;
	    String inputvarType = sol.get("inputVarType") != null ? sol.get("inputVarType").toString() : null;
	    String inputvarName = sol.get("inputvarName") != null ? sol.get("inputvarName").toString() : null;

	    if (inputvar != null) {
		PredicateInputVar var = new PredicateInputVar(inputvarType, inputvar, inputvarName);
		pred.addToInputVars(var);
	    }

	    String subjectvar = sol.get("subjectVar") != null ? sol.get("subjectVar").toString() : null;
	    String subjectVarType = sol.get("subjectVarType") != null ? sol.get("subjectVarType").toString() : null;
	    String subjectVarName = sol.get("subjectVarName") != null ? sol.get("subjectVarName").toString() : null;

	    if (subjectvar != null && pred.getSubjectVar() == null) {
		PredicateOutputVar var = new PredicateOutputVar(subjectVarType, subjectvar, subjectVarName);
		pred.setSubjectVar(var);
	    }

	    String descriptionvar = sol.get("descriptionVar") != null ? sol.get("descriptionVar").toString() : null;
	    String descriptionvarType = sol.get("descriptionVarType") != null ? sol.get("descriptionVarType").toString()
		    : null;
	    String descriptionvarName = sol.get("descriptionvarName") != null ? sol.get("descriptionvarName").toString()
		    : null;

	    if (descriptionvar != null && pred.getDescriptionVar() == null) {
		PredicateOutputVar var = new PredicateOutputVar(descriptionvarType, descriptionvar, descriptionvarName);
		pred.setDescriptionVar(var);
	    }

	    String query = sol.get("query") != null ? sol.get("query").toString() : null;
	    if (query != null && pred.getQuery() == null) {
		pred.setQuery(new swag.predicates.Query(query));
	    }

	    String topic = sol.get("topic") != null ? sol.get("topic").toString() : null;
	    if (topic != null) {
		pred.addToTopics(topic);
	    }
	}

	return predicates;
    }

    @Override
    public List<PredicateInstance> getAllPredicateInstances(String predicateURI) {

	List<PredicateInstance> preds = new ArrayList<>();
	Predicate pred = graph.getNodeG(predicateURI);

	if (pred == null) {
	    getAllPredicates();
	}

	pred = graph.getNodeG(predicateURI);
	if (pred == null) {
	    return preds;
	}

	String queryString = QUERY_STRINGS.PREDICATE_INSTANCES;
	queryString = queryString.replaceAll("\\?xXxPredicatexXx", Utils.stringToUriString(predicateURI));
	Query query = QueryFactory.create(queryString);
	QueryExecution exec = QueryExecutionFactory.create(query, model);
	ResultSet res = exec.execSelect();
	preds = buildPredicateInstances(res, pred);
	for (PredicateInstance instance : preds) {
	    graph.createEdgeAndAddSourceEdgeTargetTriple(pred, EPredicateEdgeType.INSTANCE_OF, instance);
	}

	graph.addExpansion(pred, new Expansion(EExpansionType.STORED_PREDICATE_INSTANCES, null));

	return preds;
    }

    /**
     * 
     * Genrates {@code PredicateInstance} objects from an input result set.
     * 
     * @param res
     *            the result set to retrieve data from to build the objects
     * 
     * @return a {@code List} of predicate instances
     * 
     */
    public static List<PredicateInstance> buildPredicateInstances(ResultSet res, Predicate parentPredicate) {

	List<PredicateInstance> predicateInstancess = new ArrayList<>();
	PredicateInstance inst = new PredicateInstance();
	inst.setInstanceOf(parentPredicate);

	boolean firstTime = true;

	String predicateInstanceURI = "";
	while (res.hasNext()) {
	    QuerySolution sol = res.next();

	    if (!predicateInstanceURI.equals(sol.get("predicateInstance").toString())) {

		inst = new PredicateInstance();
		inst.setInstanceOf(parentPredicate);
		predicateInstanceURI = sol.get("predicateInstance").toString();
		inst.setUri(predicateInstanceURI);
		String nameFromURI = predicateInstanceURI.substring(predicateInstanceURI.lastIndexOf("#") != -1
			? predicateInstanceURI.lastIndexOf("#") + 1 : predicateInstanceURI.lastIndexOf("//"),
			predicateInstanceURI.length());
		inst.setName(nameFromURI);
		inst.setDescription(sol.get("description") != null ? sol.get("description").toString() : "");
		predicateInstancess.add(inst);
	    }

	    firstTime = false;

	    String bindingvar = sol.get("bindingVar") != null ? sol.get("bindingVar").toString() : null;
	    String bindingVal = sol.get("bindingVal") != null ? sol.get("bindingVal").toString() : null;

	    if (bindingvar != null) {
		PredicateInputVar var = parentPredicate.getInputVariableByURI(bindingvar);
		VariableBinding binding = new VariableBinding(var, bindingVal);
		inst.addVarBinding(binding);
	    }

	    String projectionVar = sol.get("projectionVar") != null ? sol.get("projectionVar").toString() : null;

	    if (projectionVar != null) {
		PredicateOutputVar var = parentPredicate.getOutputVariableByURI(projectionVar);
		inst.addProjectionVar(var);
	    }
	}

	return predicateInstancess;
    }

    @Override
    public List<PredicateInstance> generateAllSubjectPredicateInstances(String predicateURI) {

	List<PredicateInstance> preds = new ArrayList<>();
	Predicate pred = graph.getNodeG(predicateURI);

	if (pred == null) {
	    getAllPredicates();
	}

	pred = graph.getNodeG(predicateURI);

	Query query = instantiatePredicateInstanceQuery(new ArrayList<VariableSelection>(),
		QueryFactory.create(Utils.removeEscapeCharacter(pred.getQuery().getQuery())));

	ResultSet res = null;

	try {
	    res = conn.sendQueryToEndpointAndGetResults(query);
	} catch (RemoteSPARQLQueryExecutionException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	Set<String> subjects = new HashSet<>();

	while (res.hasNext()) {
	    QuerySolution sol = res.next();
	    String sub = Utils.getStringValueIfNotNull(sol.get("subjectVar"));
	    if (sub != null) {
		subjects.add(sub);
	    }
	}

	for (String sub : subjects) {
	    List<VariableBinding> bindings = new ArrayList<>();
	    bindings.add(new VariableBinding(pred.getSubjectVar(), sub));
	    PredicateInstance inst = new PredicateInstance(pred, bindings);
	    preds.add(inst);

	}

	for (PredicateInstance instance : preds) {
	    graph.createEdgeAndAddSourceEdgeTargetTriple(pred, EPredicateEdgeType.INSTANCE_OF, instance);
	}

	graph.addExpansion(pred, new Expansion(EExpansionType.STORED_PREDICATE_INSTANCES, null));

	return preds;
    }

    public static Query instantiatePredicateInstanceQuery(List<VariableSelection> inputVars, Query query) {

	String predicatInstanceQuery = query.toString();

	for (VariableSelection sel : inputVars) {
	    predicatInstanceQuery = predicatInstanceQuery.replaceFirst("\\{",
		    "{ FILTER (" + sel.getValue() + " = " + "?" + sel.getVar().getVariable() + ")");
	}

	return QueryFactory.create(predicatInstanceQuery);
    }

    @Override
    public PredicateInstance createPredicateInstance(String predicateURI, List<VariableBinding> bindings) {

	Predicate pred = graph.getNodeG(predicateURI);

	if (pred == null) {
	    getAllPredicates();
	}

	pred = graph.getNodeG(predicateURI);
	if (pred == null) {
	    try {
		throw new Exception("Cannot find predicate: " + predicateURI);
	    } catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}

	PredicateInstance instance = new PredicateInstance(pred, bindings);

	return instance;
    }

    @Override
    public Query generatePredicateInstanceQuery(String predicateURIInstance) {

	PredicateInstance instance = graph.getNodeG(predicateURIInstance);

	if (instance == null) {

	}

	String predicatInstanceQuery = "";

	for (VariableBinding binding : instance.getBindings()) {
	    predicatInstanceQuery = predicatInstanceQuery.replaceFirst("\\{",
		    "{ FILTER (" + binding.getValue() + " = " + "?" + binding.getVar().getVariable() + ")");
	}

	return QueryFactory.create(predicatInstanceQuery);
    }

    /**
     * @param sol
     * @param schema
     * @return
     * @throws Exception
     */
    public static Set<MDElement> getMDElementFormSolution(QuerySolution sol, MDSchema schema) throws Exception {

	Set<MDElement> elems = new HashSet<>();

	String conditoinPosition = sol.get("onMDElement") != null ? sol.get("onMDElement").toString() : null;

	String conditinPositoinLevel = sol.get("conditinPositoinLevel") != null
		? sol.get("conditinPositoinLevel").toString() : null;

	String hierLevel = sol.get("hierLevel") != null ? sol.get("hierLevel").toString() : null;

	String dimLevel = sol.get("dimLevel") != null ? sol.get("dimLevel").toString() : null;

	String conditinPositoinAttribute = sol.get("conditinPositoinAttribute") != null
		? sol.get("conditinPositoinAttribute").toString() : null;

	String levelOnHierAndDim = sol.get("levelOnHierAndDim") != null ? sol.get("levelOnHierAndDim").toString()
		: null;

	String hierAttribute = sol.get("hierAttribute") != null ? sol.get("hierAttribute").toString() : null;

	String dimAttribute = sol.get("dimAttribute") != null ? sol.get("dimAttribute").toString() : null;
	
	String hierOfCond = sol.get("hierOfCond") != null ? sol.get("hierOfCond").toString() : null;

	String dimOfCond = sol.get("dimOfCond") != null ? sol.get("dimOfCond").toString() : null;

	String conditinPositoinMeasure = sol.get("conditinPositoinMeasure") != null
		? sol.get("conditinPositoinMeasure").toString() : null;

	if (conditinPositoinLevel != null) {
	    Level elem = (Level) schema.getNode(
		    schema.getIdentifyingNameFromUriAndDimensionAndHier(conditinPositoinLevel, dimLevel, hierLevel));
	    if (elem == null) {
		throw new Exception("Insufficient specification of MD element for condition.");
	    }
	    elems.add(elem);
	    return elems;
	}

	if (conditinPositoinAttribute != null) {
	    Descriptor elem = (Descriptor) schema
		    .getNode(schema.getLevelAttributeIdentifyingNameFromUriAndDimensionAndHier(conditinPositoinLevel,
			    (schema.getIdentifyingNameFromUriAndDimensionAndHier(levelOnHierAndDim, dimAttribute,
				    hierAttribute))));
	    if (elem == null) {
		throw new Exception("Insufficient specification of MD element for condition.");
	    }
	    elems.add(elem);
	    return elems;
	}
	
	if (dimOfCond!=null && hierOfCond!=null){
		 Level elem = (Level) schema.getNode(
				    schema.getIdentifyingNameFromUriAndDimensionAndHier(conditoinPosition, dimOfCond, hierOfCond));
			    if (elem == null) {
				throw new Exception("Insufficient specification of MD element for condition.");
			    }
			    elems.add(elem);
			    return elems;
	}

	if (conditinPositoinMeasure != null) {
	    Measure elem = (Measure) schema.getNode(conditinPositoinMeasure);
	    if (elem == null) {
		throw new Exception("Insufficient specification of MD element for condition.");
	    }
	    elems.add(elem);
	    return elems;
	}

	elems = schema.getNodesByURI(conditoinPosition);
	if (elems.size() == 0) {
	    throw new Exception("Insufficient specification of MD element for condition.");
	}
	return elems;
    }

    @Override
    public List<LiteralCondition> getAllConditionTypeInstances(MDSchema schema, String conditionType) {

	if (graph.getNode(conditionType) != null) {

	    Expansion ex = new Expansion(EExpansionType.LITERAL_CONDITOINS, null);
	    if (graph.isExpanded(graph.getNode(conditionType), ex)) {
		return graph.getAllConditionTypeInstances(conditionType);
	    } else {
		getAllConditions(schema);
	    }
	}

	return new ArrayList<LiteralCondition>();
    }
}
