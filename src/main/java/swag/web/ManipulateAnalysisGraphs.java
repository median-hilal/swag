package swag.web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.google.gson.Gson;

import swag.analysis_graphs.execution_engine.AnalysisGraph;
import swag.analysis_graphs.execution_engine.AnalysisGraphsManager;
import swag.analysis_graphs.execution_engine.ConsiderEmptyVariablesStrategy;
import swag.analysis_graphs.execution_engine.ConsiderNavigationVariablesStrategy;
import swag.analysis_graphs.execution_engine.NavigationStrategy;
import swag.analysis_graphs.execution_engine.NavigationStrategyPerformNavigation;
import swag.analysis_graphs.execution_engine.analysis_situations.AnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.Variable;
import swag.analysis_graphs.execution_engine.navigations.NavigationStep;
import swag.analysis_graphs.execution_engine.navigations.NavigationStrategyFactory;
import swag.analysis_graphs.visualization.MDSchemaJSONizer;
import swag.helpers.AutoCompleteData;
import swag.sparql_builder.Configuration;
import swag.sparql_builder.CustomSPARQLQuery;
import swag.sparql_builder.ASElements.IAnalysisSituationToSPARQL;
import swag.sparql_builder.ASElements.SPARQLGeneratorFactory;
import swag.web.formatters.AnalysisSituationWebFormatter;
import swag.web.formatters.NavigationStepWebFormatter;

@WebServlet("/ManipulateAnalysisGraphs")
public class ManipulateAnalysisGraphs extends HttpServlet {

    private static final Logger logger = Logger.getLogger(ManipulateAnalysisGraphs.class);
    private static final int rowsCount = 200;
    private static final String internalPathToAGsFolder = "resources";

    private int maxFileSize = 50 * 1024;
    private int maxMemSize = 4 * 1024;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ManipulateAnalysisGraphs() {
	super();
	// TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
	    throws ServletException, IOException {

	Configuration.createInstance(getServletContext().getRealPath("/WEB-INF/resources/config.properties"));

	HttpSession session = request.getSession(true);
	boolean fromSelectGraph = false;
	AnalysisGraphsManager buildEng = null;
	List<AutoCompleteData> loadedSuggestions = new ArrayList<AutoCompleteData>();
	session.setAttribute("rowsCount", rowsCount);

	try {

	    /*
	     * A new start for the session. When 'New' tab button is clicked.
	     * The session is invalidated and the variable fromSelectGraph is
	     * set to true.
	     */
	    if ("selectGraph".equals(request.getParameter("formTypeForSelectGraph"))) {
		session.invalidate();
		fromSelectGraph = true;
	    }

	    /*
	     * Conditions to show the analysis graph file selection screen.
	     * session.isNew() must be second, otherwise a session is already
	     * invalidated exception
	     */
	    if ((fromSelectGraph || session.isNew() || session.getAttribute("calledFirstTime") == null)
		    && !"selectAnalysisGraphFile".equals(request.getParameter("formType"))
		    && !"selectAnalysisGraphPaste".equals(request.getParameter("formType"))
		    && !"selectAnalysisGraphUpload".equals(request.getParameter("formType"))) {
		prepareAGSelectionOptions(request, response);
		return;
	    }

	    /* Setting nodes positions. */
	    if (!"".equals(request.getParameter("nodesPositions")) && null != request.getParameter("nodesPositions")) {
		System.out.println("Nodes Positions:");
		System.out.println(request.getParameter("nodesPositions"));
		session.setAttribute("nodesPositions", request.getParameter("nodesPositions"));
		request.setAttribute("nodesPositions", request.getParameter("nodesPositions"));
	    }

	    session.setAttribute("nodesPositions",
		    "[{\"x\":234.0,\"y\":360.0},{\"x\":529.0,\"y\":440.0},{\"x\":234.0,\"y\":40.0},{\"x\":234.0,\"y\":200.0},{\"x\":529.0,\"y\":280.0}]");
	    request.setAttribute("nodesPositions",
		    "[{\"x\":234.0,\"y\":360.0},{\"x\":529.0,\"y\":440.0},{\"x\":234.0,\"y\":40.0},{\"x\":234.0,\"y\":200.0},{\"x\":529.0,\"y\":280.0}]");

	    /* Setting nodes positions. */
	    if (!"".equals(request.getParameter("transform")) && null != request.getParameter("transform")) {
		session.setAttribute("transform", request.getParameter("transform"));
		request.setAttribute("transform", request.getParameter("transform"));
	    }

	    /* Setting URI of labeled values. */
	    if (!"".equals(request.getParameter("labelValuePairs"))
		    && null != request.getParameter("labelValuePairs")) {
		session.setAttribute("labelValuePairs",
			ServletPresentationHelper.escapeSpecialCharacters(request.getParameter("labelValuePairs")));
		request.setAttribute("labelValuePairs",
			ServletPresentationHelper.escapeSpecialCharacters(request.getParameter("labelValuePairs")));
	    }

	    /* Setting labels of labeled values. */
	    if (!"".equals(request.getParameter("labelValuePairsLabels"))
		    && null != request.getParameter("labelValuePairsLabels")) {
		session.setAttribute("labelValuePairsLabels", ServletPresentationHelper
			.escapeSpecialCharacters(request.getParameter("labelValuePairsLabels")));
		request.setAttribute("labelValuePairsLabels", ServletPresentationHelper
			.escapeSpecialCharacters(request.getParameter("labelValuePairsLabels")));
	    }

	    String addedNSForSMDInstance = "";
	    String addedNSForAGInstance = "";

	    /*
	     * Initialization based on exEng value.
	     */
	    if (null == session.getAttribute("exEng")) {
		loadedSuggestions = new ArrayList<AutoCompleteData>();
		session.setAttribute("exEng", buildEng);
		session.setAttribute("loadedSuggestions", loadedSuggestions);
	    } else {
		buildEng = (AnalysisGraphsManager) session.getAttribute("exEng");
		loadedSuggestions = (ArrayList<AutoCompleteData>) session.getAttribute("loadedSuggestions");
		addedNSForSMDInstance = (String) session.getAttribute("addedNSForSMDInstance");
		addedNSForAGInstance = (String) session.getAttribute("addedNSForAGInstance");
	    }

	    /* AJAX request case. */
	    if ("ajaxSubmit".equals(request.getParameter("submitType"))) {
		doAJAXProcess(request, response, session, buildEng, addedNSForSMDInstance, addedNSForAGInstance,
			loadedSuggestions);
		return;
	    }

	    /* The main application page case. */
	    if ((session.getAttribute("calledFirstTime") == null) && (!session.isNew())) {

		if ("selectAnalysisGraphFile".equals(request.getParameter("formType"))) {
		    doMainScreenProcess(request, response, session, buildEng, addedNSForSMDInstance,
			    addedNSForAGInstance, 1, "");
		} else {
		    if ("selectAnalysisGraphPaste".equals(request.getParameter("formType"))) {
			String fileName = downloadAndCreateFiles(request.getParameter("pasteAG"));
			doMainScreenProcess(request, response, session, buildEng, addedNSForSMDInstance,
				addedNSForAGInstance, 2, fileName);
		    } else {
			if ("selectAnalysisGraphUpload".equals(request.getParameter("formType"))) {
			    String fileName = handleFileUpload(request, response);
			    doMainScreenProcess(request, response, session, buildEng, addedNSForSMDInstance,
				    addedNSForAGInstance, 3, fileName);
			}
		    }
		}
	    } else {
		/* The navigation case. */
		if ("nvNav".equals(request.getParameter("formType"))) {
		    doNavigationExecutionProcess(request, response, session, buildEng, addedNSForSMDInstance,
			    addedNSForAGInstance, loadedSuggestions);
		} else
		/* The analysis situation execution case. */
		if ("asParams".equals(request.getParameter("formType"))) {
		    doASExecutionProcess(request, response, session, buildEng, addedNSForSMDInstance,
			    addedNSForAGInstance, loadedSuggestions);
		} else {
		    /* Unknown case. */
		    throw new NoRequestParameterException();
		}
	    }
	    /* Exception happens while processing. */
	} catch (Exception ex1) {
	    if (!"ajaxSubmit".equals(request.getParameter("submitType"))) {
		try {
		    logger.error("Trying to handle exception ", ex1);
		    handleException(request, response, session, ex1);
		}
		/* Exception happens while processing exception. */
		catch (Exception ex) {
		    if (!"ajaxSubmit".equals(request.getParameter("submitType"))) {
			if (request.getSession(false) != null) {
			    session.invalidate();
			}
			logger.error("An error happened " + ex);
			fromSelectGraph = true;
			if (fromSelectGraph || session.isNew()) {

			    List<String> errors = new ArrayList<String>();
			    errors.add(ex1.getMessage());

			    request.setAttribute("errors", errors);
			    prepareAGSelectionOptions(request, response);
			}
		    }
		}
	    }
	}
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
	    throws ServletException, IOException {
	// TODO Auto-generated method stub

	doGet(request, response);
    }

    /**
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void prepareAGSelectionOptions(HttpServletRequest request, HttpServletResponse response)
	    throws ServletException, IOException {

	File folder = new File(getServletContext().getRealPath("/WEB-INF/resources/Uploaded/AGs"));
	File[] listOfFiles = folder.listFiles();
	List<String> availableAnalysisGraphsFilesNames = new ArrayList<String>();

	for (int i = 0; i < listOfFiles.length; i++) {
	    if (listOfFiles[i].isFile()) {
		try {

		    String name = listOfFiles[i].getName();

		    String extension = name.substring(name.lastIndexOf('.') + 1, name.length());
		    if (WebConstants.ALLOWED_FILE_TYPES.contains(extension)) {

			availableAnalysisGraphsFilesNames.add(name);
		    }
		} catch (StringIndexOutOfBoundsException ex2) {

		}
	    }
	}
	request.setAttribute("availableAGs", availableAnalysisGraphsFilesNames);
	request.getRequestDispatcher("Start.jsp").forward(request, response);
    }

    /**
     * @param request
     * @param response
     * @param session
     * @param buildEng
     * @param addedNSForSMDInstance
     * @param addedNSForAGInstance
     * @throws Exception
     */
    private void doMainScreenProcess(HttpServletRequest request, HttpServletResponse response, HttpSession session,
	    AnalysisGraphsManager buildEng, String addedNSForSMDInstance, String addedNSForAGInstance, int typ,
	    String fileName) throws Exception {

	try {
	    if (session.getAttribute("finalResults") != null) {
		session.removeAttribute("finalResults");
	    }
	    if (session.getAttribute("resultsHeader") != null) {
		session.removeAttribute("resultsHeader");
	    }

		request.changeSessionId();
	    session.setAttribute("calledFirstTime", true);

	    Map<String, AnalysisSituation> asListMap = new HashMap<String, AnalysisSituation>();
	    List<AnalysisSituation> asList = new ArrayList<AnalysisSituation>();

	    String analysisGraphFileName = "";

	    switch (typ) {
	    case 1: {
		analysisGraphFileName = request.getParameter("analysisGraphFileName");
		break;
	    }
	    case 2: {
		analysisGraphFileName = fileName;
		break;
	    }
	    case 3: {
		analysisGraphFileName = fileName;
		break;
	    }
	    }

	    buildEng = ServletsHelper.createAnalysisGraphExecutionEngine(
		    getServletContext().getRealPath("/WEB-INF/resources"), analysisGraphFileName);

	    addedNSForSMDInstance = buildEng.getGraph().getNameSpace();
	    addedNSForAGInstance = buildEng.getAg().getNamespace();

	    session.setAttribute("addedNSForSMDInstance", addedNSForSMDInstance);
	    session.setAttribute("addedNSForAGInstance", addedNSForAGInstance);
	    session.setAttribute("analysisGraphFileName", analysisGraphFileName);

	    session.setAttribute("exEng", buildEng);

	    AnalysisGraph ag = buildEng.getAg();

	    asList = ag.getAnalysisSituations();

	    for (AnalysisSituation as : asList) {
		asListMap.put(as.getName(), as);
	    }

	    String asjson = JsonUtils.generateASJSON(asList);
	    session.setAttribute("asjson", asjson);
	    request.setAttribute("asjson", asjson);
	    session.setAttribute("asList", asList);
	    session.setAttribute("asListMap", asListMap);

	    Map<String, NavigationStep> nvListMap = new HashMap<String, NavigationStep>();
	    List<NavigationStep> nvList = new ArrayList<NavigationStep>();
	    nvList = ag.getNavigationSteps();
	    for (NavigationStep nv : nvList) {
		nvListMap.put(nv.getAbbName(), nv);
	    }

	    String nvjson = JsonUtils.generateNVJson(nvList, asList);
	    session.setAttribute("nvjson", nvjson);
	    request.setAttribute("nvjson", nvjson);
	    session.setAttribute("nvList", nvList);
	    session.setAttribute("nvListMap", nvListMap);
	    response.setContentType("text/html");
	    PrintWriter out = response.getWriter();

	    Map<String, Map<Integer, Variable>> vars = new HashMap<String, Map<Integer, Variable>>();
	    for (AnalysisSituation as : asList) {
		vars.put(as.getName(), as.getVariables());
	    }

	    session.setAttribute("variables", vars);

	    request.setAttribute("allAS", session.getAttribute("asList"));
	    request.setAttribute("allNV", session.getAttribute("nvList"));

	    Map<String, String[]> allASsResponseStr = AnalysisSituationWebFormatter.fleshCurrASs(asList,
		    session.getAttribute("nodesPositions") == null ? ""
			    : (String) session.getAttribute("nodesPositions"),
		    null, buildEng.getAg());
	    Map<String, String[]> allNVsResponseStr = NavigationStepWebFormatter.fleshCurrNVs(nvList, buildEng.getAg());
	    session.setAttribute("allASsResponseStr", allASsResponseStr);
	    request.setAttribute("allASsResponseStr", allASsResponseStr);
	    session.setAttribute("allNVsResponseStr", allNVsResponseStr);
	    request.setAttribute("allNVsResponseStr", allNVsResponseStr);
	    request.setAttribute("selectedTab", "manipulateButton");

	    StringBuilder sbNodes = new StringBuilder();
	    StringBuilder sbLinks = new StringBuilder();

	    MDSchemaJSONizer.jsonizeMDSchema(buildEng.getGraph(), sbNodes, sbLinks);

	    session.setAttribute("sbNodes", sbNodes.toString());
	    session.setAttribute("sbLinks", sbLinks.toString());

	    request.getRequestDispatcher("index.jsp").forward(request, response);

	    List<AnalysisSituation> asListForSerialize = (List<AnalysisSituation>) session.getAttribute("asList");
	    List<NavigationStep> nvListForSerialize = (List<NavigationStep>) session.getAttribute("nvList");

	    String serializedAsList = ServletSerializationHelper.serialize(asListForSerialize);
	    String serializedNvList = ServletSerializationHelper.serialize(nvListForSerialize);

	    session.setAttribute("serializedAsList", serializedAsList);
	    session.setAttribute("serializedNvList", serializedNvList);

	} catch (Exception ex) {
	    logger.error("failed to read the analysis graph and/or the multidimensional schema.\nRoot cause: ", ex);
	    throw new AnalysisGraphReadException(
		    "failed to read the analysis graph and/or the multidimensional schema.\nRoot cause: "
			    + ex.getMessage(),
		    ex);
	}
    }

    /**
     * @param request
     * @param response
     * @param session
     * @param buildEng
     * @param addedNSForSMDInstance
     * @param addedNSForAGInstance
     * @throws Exception
     */
    private void doASExecutionProcess(HttpServletRequest request, HttpServletResponse response, HttpSession session,
	    AnalysisGraphsManager buildEng, String addedNSForSMDInstance, String addedNSForAGInstance,
	    List<AutoCompleteData> loadedSuggestions) throws Exception {

	Enumeration<String> parameterNames = request.getParameterNames();
	String currASName = request.getParameter("currASName");
	AnalysisSituation as = ((Map<String, AnalysisSituation>) session.getAttribute("asListMap"))
		.get(currASName.replace(addedNSForAGInstance, ""));
	session.setAttribute("as", as);

	// 28.12.2017 added for the right-menu
	request.setAttribute("allAS", session.getAttribute("asList"));
	request.setAttribute("allNV", session.getAttribute("nvList"));

	request.setAttribute("currASName", currASName.replace(addedNSForAGInstance, ""));
	Map<Integer, Variable> variables = as.getVariables();
	Map<Integer, Variable> variablesCopiedFromInitialVars = as.shallowCopyInitialVariablesIntoVariablesStyle();
	Map<Variable, Variable> initialVariables = as.getInitialVariables();
	response.setContentType("text/html");
	PrintWriter out = response.getWriter();

	// In case any new values will be bound to conditions created
	// dynamically
	String[] strs = new String[2];
	strs[0] = (String) session.getAttribute("labelValuePairsLabels");
	strs[1] = (String) session.getAttribute("labelValuePairs");

	Map<String, String[]> addedLabels = new HashMap<>();
	Map<String, String[]> addedVals = new HashMap<>();
	List<AutoCompleteData> addedSuggestions = new ArrayList<>();

	ServletsHelper.processRequestVariables(parameterNames, request, addedNSForSMDInstance, initialVariables,
		variables, buildEng, as, as.getFact().getURI(), addedLabels, addedVals, addedSuggestions);
	doPostProcess(addedLabels, addedVals, addedSuggestions, session, request, loadedSuggestions);

	String navOrNot = request.getParameter("navigateOrNot");
	if (!navOrNot.equals("")) {
	    NavigationStep nv = ((Map<String, NavigationStep>) session.getAttribute("nvListMap")).get(navOrNot);
	    NavigationStrategy strategy = NavigationStrategyFactory.getNavigationStrategy(buildEng.getAg());
	    buildEng.doNavigate(strategy, nv);
	}

	IAnalysisSituationToSPARQL queryGenerator = SPARQLGeneratorFactory.createSPARQLGenerator(buildEng.getGraph(),
		buildEng.getAg());

	CustomSPARQLQuery rootQueryFinal = queryGenerator.generateSPARQLFromAS(as);
	Query query = rootQueryFinal.getSparqlQuery();
	session.setAttribute("rq", rootQueryFinal);
	request.setAttribute("sparql", ServletPresentationHelper.trimQueryString(query.toString()));
	session.setAttribute("sparql", ServletPresentationHelper.trimQueryString(query.toString()));
	ResultSet results = buildEng.sendQueryToEndpointAndGetResults(query);

	List<String> resultsHeader = ServletPresentationHelper.substituteResultHeaderVarNames(results, as);
	List<List<String>> finalResults = ServletPresentationHelper.prepareResults(results);
	ResultSetFormatter.outputAsJSON(results);
	String finalResponse = ServletPresentationHelper.putResultsAsHTMLString(finalResults, resultsHeader, as);

	Map<String, String[]> allASsResponseStr = AnalysisSituationWebFormatter.fleshCurrASs(
		(List<AnalysisSituation>) session.getAttribute("asList"),
		(String) session.getAttribute("nodesPositions"), as, buildEng.getAg());
	Map<String, String[]> allNVsResponseStr = NavigationStepWebFormatter
		.fleshCurrNVs((List<NavigationStep>) session.getAttribute("nvList"), buildEng.getAg());

	session.setAttribute("allASsResponseStr", allASsResponseStr);
	request.setAttribute("allASsResponseStr", allASsResponseStr);
	session.setAttribute("allNVsResponseStr", allNVsResponseStr);
	request.setAttribute("allNVsResponseStr", allNVsResponseStr);

	request.setAttribute("finalResponse", finalResponse);
	request.setAttribute("totalRowCount", finalResults.size());

	request.setAttribute("selectedTab", "manipulateButton");

	request.setAttribute("pagesCount", (finalResults.size() % rowsCount == 0 ? finalResults.size() / rowsCount
		: finalResults.size() / rowsCount + 1));
	request.setAttribute("finalResults", finalResults);
	request.setAttribute("resultsHeader", resultsHeader);
	session.setAttribute("finalResults", finalResults);
	session.setAttribute("resultsHeader", resultsHeader);

	int firstRow = 0;
	List<List<String>> rangedResults = ServletPresentationHelper
		.getResultsRange((ArrayList) ((ArrayList<List<String>>) finalResults).clone(), firstRow, rowsCount);

	List<List<String>> headedRangedResults = ServletPresentationHelper
		.getResultsRange((ArrayList) ((ArrayList<List<String>>) finalResults).clone(), firstRow, rowsCount);
	headedRangedResults.add(0,
		(List<String>) ((ArrayList) (List<String>) session.getAttribute("resultsHeader")).clone());
	String jsonForVisualization = JsonUtils.getRangedResultsASJSON(headedRangedResults);
	session.setAttribute("rangedResults", rangedResults);
	request.setAttribute("jsonForVisualization", jsonForVisualization);

	request.setAttribute("selectedTab", "manipulateButton");

	if ("sparqlSubmit".equals(request.getParameter("typeOfSubmitForSPARQL"))) {
	    request.setAttribute("graphOrResultTabButton", "sparqlButton");
	} else {
	    request.setAttribute("graphOrResultTabButton", "resultsButton");
	}

	request.getRequestDispatcher("index.jsp").forward(request, response);
	/*
	 * out.println(finalResponse);
	 */
	out.close();

	// Serialization after a successful execution
	List<AnalysisSituation> asListForSerialize = (List<AnalysisSituation>) session.getAttribute("asList");
	List<NavigationStep> nvListForSerialize = (List<NavigationStep>) session.getAttribute("nvList");

	String serializedAsList = ServletSerializationHelper.serialize(asListForSerialize);
	String serializedNvList = ServletSerializationHelper.serialize(nvListForSerialize);

	session.setAttribute("serializedAsList", serializedAsList);
	session.setAttribute("serializedNvList", serializedNvList);
    }

    /**
     * @param request
     * @param response
     * @param session
     * @param buildEng
     * @param addedNSForSMDInstance
     * @param addedNSForAGInstance
     * @throws Exception
     */
    private void doNavigationExecutionProcess(HttpServletRequest request, HttpServletResponse response,
	    HttpSession session, AnalysisGraphsManager buildEng, String addedNSForSMDInstance,
	    String addedNSForAGInstance, List<AutoCompleteData> loadedSuggestions) throws Exception {

	Enumeration<String> parameterNames = request.getParameterNames();
	String currNVName = request.getParameter("currNVName");
	NavigationStep nv = ((Map<String, NavigationStep>) session.getAttribute("nvListMap"))
		.get(currNVName.replace(addedNSForAGInstance, ""));
	session.setAttribute("nv", nv);
	request.setAttribute("currNVName", currNVName.replace(addedNSForAGInstance, ""));
	response.setContentType("text/html");
	PrintWriter out = response.getWriter();

	request.setAttribute("allAS", session.getAttribute("asList"));
	request.setAttribute("allNV", session.getAttribute("nvList"));

	Map<Integer, Variable> variables = nv.getVariables();
	Map<Integer, Variable> variablesCopiedFromInitialVars = nv.shallowCopyInitialVariablesIntoVariablesStyle();
	Map<Variable, Variable> initialVariables = nv.getInitialVariables();

	Map<String, String[]> addedLabels = new HashMap<>();
	Map<String, String[]> addedVals = new HashMap<>();
	List<AutoCompleteData> addedSuggestions = new ArrayList<>();

	ServletsHelper.processRequestVariables(parameterNames, request, addedNSForSMDInstance, initialVariables,
		variables, buildEng, nv, nv.getTarget().getFact().getURI(), addedLabels, addedVals, addedSuggestions);
	doPostProcess(addedLabels, addedVals, addedSuggestions, session, request, loadedSuggestions);

	NavigationStrategy strategy = NavigationStrategyFactory.getNavigationStrategy(buildEng.getAg());
	buildEng.doNavigate(strategy, nv);

	request.setAttribute("currASName", nv.getTarget().getName());
	request.setAttribute("prevASName", nv.getSource().getURI());

	request.setAttribute("fromNavigate", "fromNavigate");

	String currASName = nv.getTarget().getName();
	AnalysisSituation as = ((Map<String, AnalysisSituation>) session.getAttribute("asListMap")).get(currASName);
	session.setAttribute("as", as);

	request.setAttribute("currASName", currASName.replace(addedNSForAGInstance, ""));

	IAnalysisSituationToSPARQL queryGenerator = SPARQLGeneratorFactory.createSPARQLGenerator(buildEng.getGraph(),
		buildEng.getAg());

	CustomSPARQLQuery rootQueryFinal = queryGenerator.generateSPARQLFromAS(nv.getTarget());
	Query query = rootQueryFinal.getSparqlQuery();
	session.setAttribute("rq", rootQueryFinal);
	request.setAttribute("sparql", ServletPresentationHelper.trimQueryString(query.toString()));
	session.setAttribute("sparql", ServletPresentationHelper.trimQueryString(query.toString()));

	ResultSet results = buildEng.sendQueryToEndpointAndGetResults(query);
	List<String> resultsHeader = ServletPresentationHelper.substituteResultHeaderVarNames(results, nv.getTarget());
	List<List<String>> finalResults = ServletPresentationHelper.prepareResults(results);
	ResultSetFormatter.outputAsJSON(results);
	String finalResponse = ServletPresentationHelper.putResultsAsHTMLString(finalResults, resultsHeader,
		nv.getTarget());
	
	doScreen(request, response, session, buildEng);

	Map<String, String[]> allASsResponseStr = AnalysisSituationWebFormatter.fleshCurrASs(
		(List<AnalysisSituation>) session.getAttribute("asList"),
		(String) session.getAttribute("nodesPositions"), nv.getTarget(), buildEng.getAg());
	Map<String, String[]> allNVsResponseStr = NavigationStepWebFormatter
		.fleshCurrNVs((List<NavigationStep>) session.getAttribute("nvList"), buildEng.getAg());

	session.setAttribute("allASsResponseStr", allASsResponseStr);
	request.setAttribute("allASsResponseStr", allASsResponseStr);
	session.setAttribute("allNVsResponseStr", allNVsResponseStr);
	request.setAttribute("allNVsResponseStr", allNVsResponseStr);
	request.setAttribute("finalResponse", finalResponse);
	request.setAttribute("totalRowCount", finalResults.size());
	request.setAttribute("selectedTab", "manipulateButton");

	request.setAttribute("pagesCount", (finalResults.size() % rowsCount == 0 ? finalResults.size() / rowsCount
		: finalResults.size() / rowsCount + 1));
	request.setAttribute("finalResults", finalResults);
	request.setAttribute("resultsHeader", resultsHeader);
	session.setAttribute("finalResults", finalResults);
	session.setAttribute("resultsHeader", resultsHeader);

	int firstRow = 0;
	List<List<String>> rangedResults = ServletPresentationHelper
		.getResultsRange((ArrayList) ((ArrayList<List<String>>) finalResults).clone(), firstRow, rowsCount);

	List<List<String>> headedRangedResults = ServletPresentationHelper
		.getResultsRange((ArrayList) ((ArrayList<List<String>>) finalResults).clone(), firstRow, rowsCount);
	headedRangedResults.add(0,
		(List<String>) ((ArrayList) (List<String>) session.getAttribute("resultsHeader")).clone());
	String jsonForVisualization = JsonUtils.getRangedResultsASJSON(headedRangedResults);
	session.setAttribute("rangedResults", rangedResults);
	request.setAttribute("jsonForVisualization", jsonForVisualization);
	request.setAttribute("selectedTab", "manipulateButton");
	request.setAttribute("graphOrResultTabButton", "resultsButton");
	request.getRequestDispatcher("index.jsp").forward(request, response);
	out.println(finalResponse);
	out.close();

	// Serialization after a successful execution
	List<AnalysisSituation> asListForSerialize = (List<AnalysisSituation>) session.getAttribute("asList");
	List<NavigationStep> nvListForSerialize = (List<NavigationStep>) session.getAttribute("nvList");

	String serializedAsList = ServletSerializationHelper.serialize(asListForSerialize);
	String serializedNvList = ServletSerializationHelper.serialize(nvListForSerialize);

	session.setAttribute("serializedAsList", serializedAsList);
	session.setAttribute("serializedNvList", serializedNvList);
    }

    /**
     * @param request
     * @param response
     * @param session
     * @param ex1
     * @throws IOException
     * @throws ServletException
     */
    private void handleException(HttpServletRequest request, HttpServletResponse response, HttpSession session,
	    Exception ex1) throws Exception {

	// session.setAttribute("nodesPositions",
	// session.getAttribute("nodesPositions");
	request.setAttribute("nodesPositions", session.getAttribute("nodesPositions"));

	// session.setAttribute("transform", "");
	request.setAttribute("transform", session.getAttribute("transform"));

	Map<String, String[]> allASsResponseStr = AnalysisSituationWebFormatter.fleshCurrASs(
		(List<AnalysisSituation>) session.getAttribute("asList"),
		(String) session.getAttribute("nodesPositions"), null,
		((AnalysisGraphsManager) session.getAttribute("exEng")).getAg());

	Map<String, String[]> allNVsResponseStr = NavigationStepWebFormatter.fleshCurrNVs(
		(List<NavigationStep>) session.getAttribute("nvList"),
		((AnalysisGraphsManager) session.getAttribute("exEng")).getAg());

	session.setAttribute("allASsResponseStr", allASsResponseStr);
	request.setAttribute("allASsResponseStr", allASsResponseStr);

	session.setAttribute("allNVsResponseStr", allNVsResponseStr);
	request.setAttribute("allNVsResponseStr", allNVsResponseStr);

	request.setAttribute("selectedTab", "manipulateButton");

	// if the exception is NoRequestParameterException, no need to show
	// errors!
	if (!(ex1 instanceof NoRequestParameterException)) {

	    List<String> errors = new ArrayList<String>();
	    errors.add(ex1.getMessage());
	    request.setAttribute("errors", errors);
	}

	request.getRequestDispatcher("index.jsp").forward(request, response);
	// out.close();

	// ex1.printStackTrace();
	// System.out.println(ex1.getCause() + ex1.getMessage());
    }

    /**
     * @param request
     * @param response
     * @param session
     * @param buildEng
     * @param addedNSForSMDInstance
     * @param addedNSForAGInstance
     * @param loadedSuggestions
     * @throws Exception
     */
    private void doAJAXProcess(HttpServletRequest request, HttpServletResponse response, HttpSession session,
	    AnalysisGraphsManager buildEng, String addedNSForSMDInstance, String addedNSForAGInstance,
	    List<AutoCompleteData> loadedSuggestions) throws Exception {

	try {
	    String reqVal = request.getParameter("value");
	    String reqName = request.getParameter("name");

	    /*
	     * AJAX request for possible values on a variable level.
	     */
	    if (reqName.contains("level_suggestion")) {

		JSONObject obj = new JSONObject(reqVal);
		String str = obj.getString("dimensionURI");
		response.setContentType("application/json");
		Set<AutoCompleteData> levelValues = buildEng.getUniquePossibleLevelsOnDimensionWithLabels(str);
		final List<AutoCompleteData> result = new ArrayList<AutoCompleteData>();
		for (AutoCompleteData val : levelValues) {
		    result.add(val);
		}
		response.getWriter().write(new Gson().toJson(result));
		loadedSuggestions.addAll(result);
		session.setAttribute("loadedSuggestions", loadedSuggestions);

	    } else {
		/*
		 * AJAX request for possible values on a variable dice node.
		 */
		if (reqName.contains("diceNode_suggestion") || reqName.contains("diceNode_slice_suggestion")
			|| reqName.contains("diceNode_slice_multiple_suggestion")
			|| reqName.contains("diceNode_slice_noPosition_suggestion")
			|| reqName.contains("diceNode_slice_noPosition_typed_suggestion")) {

		    if (// reqName.contains("diceNode_slice_suggestion") ||
		    reqName.contains("diceNode_slice_multiple_suggestion")) {

			String factName = addedNSForSMDInstance + request.getParameter("factName").replace(" ", "");
			JSONObject obj = new JSONObject(reqVal);
			String str = obj.getString("levelName");

			response.setContentType("application/json");
			Map<String, String> diceValues = buildEng
				.getPredicatePossibleValuePairsOfASpecificMDElement(str);
			final List<AutoCompleteData> result = new ArrayList<AutoCompleteData>();
			for (Map.Entry<String, String> entry : diceValues.entrySet()) {

			    result.add(new AutoCompleteData(entry.getValue(), entry.getKey()));
			}

			response.getWriter().write(new Gson().toJson(result));
			loadedSuggestions.addAll(result);
			session.setAttribute("loadedSuggestions", loadedSuggestions);

		    } else {
			if (reqName.contains("diceNode_slice_noPosition_suggestion")
				|| reqName.contains("diceNode_slice_noPosition_typed_suggestion")) {

			    String factName = addedNSForSMDInstance + request.getParameter("factName").replace(" ", "");
			    JSONObject obj = new JSONObject(reqVal);
			    String str = obj.getString("levelName");

			    String conditionType = obj.getString("conditionType");

			    response.setContentType("application/json");
			    Map<String, String> diceValues;

			    if (StringUtils.isEmpty(conditionType)) {
				diceValues = buildEng.getConditionAndPredicatePossibleValuePairs(str);
			    } else {
				diceValues = buildEng.getConditionAndPredicatePossibleValuePairsOfType(str,
					conditionType);
			    }
			    final List<AutoCompleteData> result = new ArrayList<AutoCompleteData>();
			    for (Map.Entry<String, String> entry : diceValues.entrySet()) {

				result.add(new AutoCompleteData(entry.getValue(), entry.getKey()));
			    }

			    response.getWriter().write(new Gson().toJson(result));
			    loadedSuggestions.addAll(result);
			    session.setAttribute("loadedSuggestions", loadedSuggestions);

			} else {
			    String factName = addedNSForSMDInstance + request.getParameter("factName").replace(" ", "");
			    JSONObject obj = new JSONObject(reqVal);
			    String str = obj.getString("levelName");
			    response.setContentType("application/json");
			    Map<String, String> diceValues = buildEng.getMDItemPossibleValuesPairs(str);
			    final List<AutoCompleteData> result = new ArrayList<AutoCompleteData>();
			    for (Map.Entry<String, String> entry : diceValues.entrySet()) {

				result.add(new AutoCompleteData(entry.getValue(), entry.getKey()));
			    }
			    response.getWriter().write(new Gson().toJson(result));
			    loadedSuggestions.addAll(result);
			    session.setAttribute("loadedSuggestions", loadedSuggestions);
			}
		    }
		} else {

		    /*
		     * AJAX request for next page of results
		     */
		    if ("next".equals(reqName)) {
			int firstRow = Integer.parseInt(request.getParameter("firstRow"));
			List<List<String>> finalResults = (List<List<String>>) session.getAttribute("finalResults");
			List<List<String>> rangedResults = ServletPresentationHelper.getResultsRange(
				(ArrayList) ((ArrayList<List<String>>) finalResults).clone(), firstRow + rowsCount,
				rowsCount);
			session.setAttribute("rangedResults", rangedResults);
			String rangedResponse = ServletPresentationHelper.getRangedResponse(rangedResults);
			session.setAttribute("rangedResults", rangedResults);
			List<String> resultsHeader = (List<String>) ((ArrayList) (List<String>) session
				.getAttribute("resultsHeader")).clone();
			rangedResults.add(0, resultsHeader);
			String jsonForVisualization = JsonUtils.getRangedResultsASJSON(rangedResults);
			response.getWriter().write(jsonForVisualization);
		    } else {

			/*
			 * AJAX request for previous page of results
			 */
			if ("previous".equals(reqName)) {
			    int firstRow = Integer.parseInt(request.getParameter("firstRow"));
			    List<List<String>> finalResults = (List<List<String>>) session.getAttribute("finalResults");
			    List<List<String>> rangedResults = ServletPresentationHelper.getResultsRange(
				    (ArrayList) ((ArrayList<List<String>>) finalResults).clone(), firstRow - rowsCount,
				    rowsCount);
			    session.setAttribute("rangedResults", rangedResults);
			    String rangedResponse = ServletPresentationHelper.getRangedResponse(rangedResults);
			    session.setAttribute("rangedResults", rangedResults);
			    List<String> resultsHeader = (List<String>) ((ArrayList) (List<String>) session
				    .getAttribute("resultsHeader")).clone();
			    rangedResults.add(0, resultsHeader);
			    String jsonForVisualization = JsonUtils.getRangedResultsASJSON(rangedResults);
			    response.getWriter().write(jsonForVisualization);
			} else {

			    /*
			     * AJAX request for refreshing contents
			     */
			    if ("refreshContents".equals(reqName)) {

				/*
				 * AJAX request for refreshing contents. an
				 * analysis situation is currently selected.
				 */
				if (request.getParameter("currASName") != null) {

				    Enumeration<String> parameterNames = request.getParameterNames();
				    String currASName = request.getParameter("currASName");
				    AnalysisSituation as = ((Map<String, AnalysisSituation>) session
					    .getAttribute("asListMap"))
						    .get(currASName.replace(addedNSForAGInstance, ""));
				    Map<Integer, Variable> variables = as.getVariables();
				    Map<Variable, Variable> initialVariables = as.getInitialVariables();
				    response.setContentType("text/html");

				    Map<String, String[]> addedLabels = new HashMap<>();
				    Map<String, String[]> addedVals = new HashMap<>();
				    List<AutoCompleteData> addedSuggestions = new ArrayList<>();

				    ServletsHelper.processRequestVariables(parameterNames, request,
					    addedNSForSMDInstance, initialVariables, variables, buildEng, as,
					    as.getFact().getURI(), addedLabels, addedVals, addedSuggestions);
				    doPostProcess(addedLabels, addedVals, addedSuggestions, session, request,
					    loadedSuggestions);

				    List<AnalysisSituation> asListForSerialize = (List<AnalysisSituation>) session
					    .getAttribute("asList");
				    List<NavigationStep> nvListForSerialize = (List<NavigationStep>) session
					    .getAttribute("nvList");
				    String serializedAsList = ServletSerializationHelper.serialize(asListForSerialize);
				    String serializedNvList = ServletSerializationHelper.serialize(nvListForSerialize);
				    session.setAttribute("serializedAsList", serializedAsList);
				    session.setAttribute("serializedNvList", serializedNvList);
				} else {

				    /*
				     * AJAX request for refreshing contents. a
				     * navigation step is currently selected.
				     */
				    if (request.getParameter("currNVName") != null) {

					Enumeration<String> parameterNames = request.getParameterNames();
					String currNVName = request.getParameter("currNVName");
					NavigationStep nv = ((Map<String, NavigationStep>) session
						.getAttribute("nvListMap"))
							.get(currNVName.replace(addedNSForAGInstance, ""));
					session.setAttribute("nv", nv);
					request.setAttribute("currNVName",
						currNVName.replace(addedNSForAGInstance, ""));
					response.setContentType("text/html");
					PrintWriter out = response.getWriter();

					Map<Integer, Variable> variables = nv.getVariables();
					Map<Integer, Variable> variablesCopiedFromInitialVars = nv
						.shallowCopyInitialVariablesIntoVariablesStyle();
					Map<Variable, Variable> initialVariables = nv.getInitialVariables();

					Map<String, String[]> addedLabels = new HashMap<>();
					Map<String, String[]> addedVals = new HashMap<>();
					List<AutoCompleteData> addedSuggestions = new ArrayList<>();

					ServletsHelper.processRequestVariables(parameterNames, request,
						addedNSForSMDInstance, initialVariables, variables, buildEng, nv,
						nv.getTarget().getFact().getURI(), addedLabels, addedVals,
						addedSuggestions);

					doPostProcess(addedLabels, addedVals, addedSuggestions, session, request,
						loadedSuggestions);

					List<AnalysisSituation> asListForSerialize = (List<AnalysisSituation>) session
						.getAttribute("asList");
					List<NavigationStep> nvListForSerialize = (List<NavigationStep>) session
						.getAttribute("nvList");

					String serializedAsList = ServletSerializationHelper
						.serialize(asListForSerialize);
					String serializedNvList = ServletSerializationHelper
						.serialize(nvListForSerialize);

					session.setAttribute("serializedAsList", serializedAsList);
					session.setAttribute("serializedNvList", serializedNvList);
				    }
				}
			    } else

			    /*
			     * AJAX request for SPARQL.
			     */
			    if ("sparql".equals(reqName)) {

				Query query = QueryFactory.create(request.getParameter("sparqlContent"));
				ResultSet results = buildEng.sendQueryToEndpointAndGetResults(query);
				List<RDFNode> result = new ArrayList<RDFNode>();
				List<String> vars = results.getResultVars();
				List<List<String>> finalResults = new ArrayList<List<String>>();
				;
				List<String> resultsHeader = new ArrayList<String>();
				resultsHeader.addAll(results.getResultVars());
				ListIterator<String> itr = resultsHeader.listIterator();
				int rowsCntr = 0;
				for (; results.hasNext();) {
				    List<String> varValues = new ArrayList<String>();
				    QuerySolution soln = results.nextSolution();
				    for (String v : vars) {
					varValues.add((soln.get(v) != null) ? soln.get(v).toString() : null);
				    }
				    finalResults.add(varValues);
				}

				List<List<String>> rangedResults = ServletPresentationHelper.getResultsRange(
					(ArrayList) ((ArrayList<List<String>>) finalResults).clone(), 0,
					finalResults.size());

				List<List<String>> headedRangedResults = ServletPresentationHelper.getResultsRange(
					(ArrayList) ((ArrayList<List<String>>) finalResults).clone(), 0,
					finalResults.size());
				headedRangedResults.add(0, resultsHeader);

				String jsonForVisualization = JsonUtils
					.getRangedResultsASJSONForPlainSPARQL(headedRangedResults);

				response.getWriter().write(jsonForVisualization);
			    }
			}
		    }
		}
	    }
	} catch (Exception ex) {
	    throw (ex);
	}
    }

    /**
     * 
     * Given a URI for an analysis graph, this function downloads the analysis
     * graph and saves it among the saved analysis graphs, also downloads the
     * corresponding MD schema and saves it.
     * 
     * @param analysisGraphFileURI
     *            the URI to the analysis graph
     * 
     * @return the local name of the analysis graph file
     * 
     * @throws Exception
     */
    private String downloadAndCreateFiles(String analysisGraphFileURI) throws Exception {

	FileOutputStream fos = null;
	FileOutputStream fos1 = null;

	try {
	    URL website = new URL(analysisGraphFileURI);
	    ReadableByteChannel rbc = Channels.newChannel(website.openStream());

	    String fileName = analysisGraphFileURI.substring(analysisGraphFileURI.lastIndexOf("/") + 1,
		    analysisGraphFileURI.length());

	    fos = new FileOutputStream(getServletContext().getRealPath("/WEB-INF/resources/Uploaded/AGs/" + fileName));

	    fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

	    String mdSchemNameURI = ServletsHelper
		    .getMDSchemaURI(getServletContext().getRealPath("/WEB-INF/resources/"), fileName);

	    URL website1 = new URL(mdSchemNameURI);
	    ReadableByteChannel rbc1 = Channels.newChannel(website1.openStream());

	    String fileName1 = mdSchemNameURI.substring(mdSchemNameURI.lastIndexOf("/") + 1, mdSchemNameURI.length());

	    fos1 = new FileOutputStream(
		    getServletContext().getRealPath("/WEB-INF/resources/Uploaded/SMDs/" + fileName1));

	    fos1.getChannel().transferFrom(rbc1, 0, Long.MAX_VALUE);
	    // System.out.println("anything");

	    return fileName;

	} catch (Exception ex) {
	    logger.error("Cannot create local files from provided links.", ex);
	    throw new AnalysisGraphReadException(
		    "Failed to read the analysis graph and/or the multidimensional schema.\nRoot cause: "
			    + ex.getMessage(),
		    ex);
	} finally {
	    try {
		if (fos != null) {
		    fos.close();
		}
		if (fos1 != null) {
		    fos1.close();
		}
	    } catch (IOException ex) {
		logger.error("Failed to close streams.", ex);
	    }
	}
    }

    private String handleFileUpload(HttpServletRequest request, HttpServletResponse response)
	    throws AnalysisGraphReadException {

	FileOutputStream fos1 = null;
	FileOutputStream fos2 = null;

	try {
	    Part filePart = request.getPart("myFileAG");
	    String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
	    InputStream fileContent = filePart.getInputStream();

	    ReadableByteChannel rbc1 = Channels.newChannel(fileContent);

	    fos1 = new FileOutputStream(getServletContext().getRealPath("/WEB-INF/resources/Uploaded/AGs/" + fileName));

	    fos1.getChannel().transferFrom(rbc1, 0, Long.MAX_VALUE);

	    fos1.close();

	    Part filePart2 = request.getPart("myFileSMD");
	    String fileName2 = Paths.get(filePart2.getSubmittedFileName()).getFileName().toString();
	    InputStream fileContent2 = filePart2.getInputStream();

	    ReadableByteChannel rbc2 = Channels.newChannel(fileContent2);

	    fos2 = new FileOutputStream(
		    getServletContext().getRealPath("/WEB-INF/resources/Uploaded/SMDs/" + fileName2));

	    fos2.getChannel().transferFrom(rbc2, 0, Long.MAX_VALUE);

	    fos2.close();
	    return fileName;

	} catch (Exception ex) {
	    logger.error("Cannot create local files from provided links.", ex);
	    throw new AnalysisGraphReadException(
		    "Failed to read the analysis graph and/or the multidimensional schema.\nRoot cause: "
			    + ex.getMessage(),
		    ex);

	} finally {
	    try {
		if (fos1 != null) {
		    fos1.close();
		}
		if (fos2 != null) {
		    fos1.close();
		}
	    } catch (IOException ex) {
		logger.error("Failed to close streams.", ex);
	    }
	}

    }

    private void doPostProcess(Map<String, String[]> addedLabels, Map<String, String[]> addedVals,
	    List<AutoCompleteData> addedSuggestions, HttpSession session, HttpServletRequest request,
	    List<AutoCompleteData> sessionloadedSuggestions) throws Exception {

	String[] strs = new String[2];
	strs[0] = (String) session.getAttribute("labelValuePairsLabels");
	strs[1] = (String) session.getAttribute("labelValuePairs");

	Map<String, Map<String, String>> labels = JsonUtils.decodeJsonStringToMap(strs[0]);
	if (!addedLabels.isEmpty()) {
	    ServletPresentationHelper.appendToMap(labels, addedLabels);
	    strs[0] = JsonUtils.encodeMapAsJsonString(labels);
	}

	Map<String, Map<String, String>> vals = JsonUtils.decodeJsonStringToMap(strs[1]);
	if (!addedLabels.isEmpty()) {
	    ServletPresentationHelper.appendToMap(vals, addedVals);
	    strs[1] = JsonUtils.encodeMapAsJsonString(vals);
	}

	session.setAttribute("labelValuePairsLabels", strs[0]);
	request.setAttribute("labelValuePairsLabels", strs[0]);
	session.setAttribute("labelValuePairs", strs[1]);
	request.setAttribute("labelValuePairs", strs[1]);

	sessionloadedSuggestions.addAll(addedSuggestions);
	session.setAttribute("loadedSuggestions", sessionloadedSuggestions);

    }
    
    private void doScreen(HttpServletRequest request, HttpServletResponse response, HttpSession session,
    	    AnalysisGraphsManager buildEng){
    	
    	if (Configuration.getInstance().is("dynamicGenerationOfTarget")) {
        AnalysisGraph ag = buildEng.getAg();
        
        Map<String, AnalysisSituation> asListMap = new HashMap<String, AnalysisSituation>();
	    List<AnalysisSituation> asList = new ArrayList<AnalysisSituation>();

	     asList = ag.getAnalysisSituations();

	    for (AnalysisSituation as : asList) {
		asListMap.put(as.getName(), as);
	    }

	    String asjson = JsonUtils.generateASJSON(asList);
	    
	    session.setAttribute("asjson", asjson);
	    request.setAttribute("asjson", asjson);
	    session.setAttribute("asList", asList);
	    session.setAttribute("asListMap", asListMap);

	    Map<String, NavigationStep> nvListMap = new HashMap<String, NavigationStep>();
	    
	    List<NavigationStep> nvList = new ArrayList<NavigationStep>();
	    nvList = ag.getNavigationSteps();
	    for (NavigationStep nv : nvList) {
		nvListMap.put(nv.getAbbName(), nv);
	    }

	    String nvjson = JsonUtils.generateNVJson(nvList, asList);
	    session.setAttribute("nvjson", nvjson);
	    request.setAttribute("nvjson", nvjson);
	    session.setAttribute("nvList", nvList);
	    session.setAttribute("nvListMap", nvListMap);
	    response.setContentType("text/html");
    	}
	   
    }

}
