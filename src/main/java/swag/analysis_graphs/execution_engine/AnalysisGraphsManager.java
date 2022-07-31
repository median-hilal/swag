package swag.analysis_graphs.execution_engine;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.jena.query.Query;
import org.apache.jena.query.ResultSet;
import org.apache.log4j.Logger;

import swag.analysis_graphs.dao.IAnalysisGraphDAO;
import swag.analysis_graphs.dao.IDataDAO;
import swag.analysis_graphs.dao.IMDSchemaDAO;
import swag.analysis_graphs.execution_engine.analysis_situations.PredicateInAG;
import swag.analysis_graphs.execution_engine.navigations.NavigationStep;
import swag.helpers.AutoCompleteData;
import swag.md_elements.MDElement;
import swag.md_elements.MDSchema;
import swag.predicates.IPredicateGraph;
import swag.predicates.IPredicateGraphBuilder;
import swag.predicates.LiteralCondition;
import swag.web.WebConstants;

/**
 * 
 * The main class responsible for the execution logic. All requests from the
 * servlet (controller) should pass through the {@code AnalysisGraphsEngine}
 * 
 * @author swag
 *
 */
public class AnalysisGraphsManager {

	private static final Logger logger = Logger.getLogger(AnalysisGraphsManager.class);

	private ExecutionEngineState state = ExecutionEngineState.START;

	/**
	 * 
	 * Not really usefull.
	 * 
	 * @return
	 */
	public ExecutionEngineState getState() {
		return state;
	}

	/**
	 * 
	 * Not really useful.
	 * 
	 * @param state
	 */
	public void setState(ExecutionEngineState state) {
		this.state = state;
	}

	private IAnalysisGraphDAO agDao;
	private IMDSchemaDAO mdDao;
	private IDataDAO dataDao;
	private IPredicateGraphBuilder IPredicateDao;
	private MDSchema graph;
	private AnalysisGraph ag;
	private IPredicateGraph predicateGraph;

	/**
	 * @return
	 */
	public MDSchema buildMDSchema() {
		MDSchema schema = mdDao.buildMDSchema();
		// schema.stringifyGraph();
		return schema;
	}

	public AnalysisGraphsManager(IAnalysisGraphDAO agDao, IDataDAO dataDao, IMDSchemaDAO mdDao) {
		super();
		this.agDao = agDao;
		this.mdDao = mdDao;
		this.dataDao = dataDao;
	}
	

	public List<String> getPossibleLevelsOnDimension(String dimensionURI) {
		return graph.getPossibleLevelsOnDimension(dimensionURI);
	}

	public Set<String> getUniquePossibleLevelsOnDimension(String dimensionURI) {
		return graph.getUniquePossibleLevelsOnDimension(dimensionURI);
	}
	
	public Set<AutoCompleteData> getUniquePossibleLevelsOnDimensionWithLabels(String dimensionURI) {
		return graph.getUniquePossibleLevelsOnDimensionWithLabels(dimensionURI);		
	}

	public String getEndpointURI() {
		return graph.getEndpoint();
	}

	/**
	 * 
	 * Gets the possible values for a specific MD element.
	 * 
	 * @param mdSchema
	 * @param itemURI
	 * @return
	 * @throws NoValueFoundException
	 */
	public List<String> getMDItemPossibleValues(String itemURI) throws Exception {
		return dataDao.getMDItemPossibleValues(graph, itemURI);
	}

	/**
	 * @param mdSchema
	 * @param itemURI
	 * @return
	 * @throws NoValueFoundException
	 */
	public Map<String, String> getMDItemPossibleValuesPairs(String itemURI) throws Exception {
		return dataDao.getMDItemPossibleValuesPairs(graph, itemURI);
	}

	/**
	 * @param itemURI
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> getPredicatePossibleValuePairsOfASpecificMDElement(String itemURI) throws Exception {

		Set<PredicateInAG> preds = this.ag.getPredicatesOfMDPositoin(this.graph.getNode(itemURI));
		Map<String, String> res = new HashMap<>();

		for (PredicateInAG pred : preds) {
			res.put(pred.getPredicateInstance().getUri(), pred.getPredicateInstance().getName());
		}

		return res.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toMap(Map.Entry::getKey,
				Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
	}

	/**
	 * 
	 * Gets only the names of the swag.predicates.
	 * 
	 * @param itemURI
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> getPredicatePossibleValuePairs(String itemURI) throws Exception {

		Set<MDElement> elems = this.graph.getAllElements(itemURI);
		Map<String, String> res = new HashMap<>();

		for (MDElement elm : elems) {
			Set<PredicateInAG> conds = new HashSet<>();
			conds.addAll(this.ag.getPredicatesOfMDPositoin(elm));
			for (PredicateInAG cond : conds) {
				res.put(cond.getPredicateInstance().getUri() + WebConstants.PREDICATE_SPLITTER + elm.getURI(),
						cond.getPredicateInstance().getName());
			}
		}

		return res;
	}

	/**
	 * @param itemURI
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> getConditionPossibleValuePairsOnASpecificMDElement(String itemURI) throws Exception {
		Set<LiteralCondition> conds = this.ag.getLiteralConditionsOfMDPositoin(this.graph.getNode(itemURI));

		Map<String, String> res = new HashMap<>();

		for (LiteralCondition cond : conds) {
			res.put(cond.getName(), cond.getExpression());
		}

		return res;
	}

	/**
	 * 
	 * Gets only the names of the conditions.
	 * 
	 * @param itemURI
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> getConditionAndPredicatePossibleValuePairsOfType(String itemURI, String type)
			throws Exception {

		Map<String, String> res = getConditionPossibleValuePairsOfType(itemURI, type);
		res.putAll(getPredicatePossibleValuePairs(itemURI));
		return res.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toMap(Map.Entry::getKey,
				Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
	}

	/**
	 * 
	 * Gets only the names of the conditions.
	 * 
	 * @param itemURI
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> getConditionAndPredicatePossibleValuePairs(String itemURI) throws Exception {

		Map<String, String> res = getConditionPossibleValuePairs(itemURI);
		res.putAll(getPredicatePossibleValuePairs(itemURI));
		return res.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toMap(Map.Entry::getKey,
				Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
	}

	/**
	 * 
	 * Gets only the names of the conditions.
	 * 
	 * @param itemURI
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> getConditionPossibleValuePairsOfType(String itemURI, String type) throws Exception {
		Set<MDElement> elems = this.graph.getAllElements(itemURI);
		Set<LiteralCondition> conds = new HashSet<>();
		for (MDElement elm : elems) {
			conds.addAll(this.ag.getLiteralConditionsOfTypeOfMDPositoin(elm, type));
		}
		Map<String, String> res = new HashMap<>();
		for (LiteralCondition cond : conds) {
			res.put(cond.getIdentifyingName(), cond.getName());
		}
		return res;
	}

	/**
	 * 
	 * Gets only the names of the conditions.
	 * 
	 * @param itemURI
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> getConditionPossibleValuePairs(String itemURI) throws Exception {

		Set<MDElement> elems = this.graph.getAllElements(itemURI);
		Set<LiteralCondition> conds = new HashSet<>();

		for (MDElement elm : elems) {
			conds.addAll(this.ag.getLiteralConditionsOfMDPositoin(elm));
		}
		Map<String, String> res = new HashMap<>();
		for (LiteralCondition cond : conds) {
			res.put(cond.getIdentifyingName(), cond.getName());
		}
		return res;
	}

	/**
	 * @param query
	 * @return
	 * @throws Exception
	 */
	public ResultSet sendQueryToEndpointAndGetResults(Query query) throws Exception {
		return dataDao.sendQueryToEndpointAndGetResults(query.toString());
	}

	// source and target analysis situations are treated by reference, if the
	// original ones needed,
	// they have to be retrieved from the rdf files again
	public void doNavigate(NavigationStrategy strategy, NavigationStep nv) {
		strategy.doNavigate(nv, graph, mdDao, agDao, dataDao);
	}

	public IPredicateGraph getPredicateGraph() {
		return predicateGraph;
	}

	public void setPredicateGraph(IPredicateGraph predicateGraph) {
		this.predicateGraph = predicateGraph;
	}

	public IPredicateGraphBuilder getIPredicateDao() {
		return IPredicateDao;
	}

	public void setIPredicateDao(IPredicateGraphBuilder iPredicateDao) {
		IPredicateDao = iPredicateDao;
	}

	public AnalysisGraph getAg() {
		return ag;
	}

	public void setAg(AnalysisGraph ag) {
		this.ag = ag;
	}

	public MDSchema getGraph() {
		return graph;
	}

	public void setMDSchema(MDSchema graph) {
		this.graph = graph;
	}

}
