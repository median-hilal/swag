package swag.web;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.RDFS;
import org.apache.log4j.Logger;

import swag.analysis_graphs.execution_engine.AnalysisGraph;
import swag.analysis_graphs.execution_engine.AnalysisGraphsManager;
import swag.analysis_graphs.execution_engine.IExecutionEngineFactory;
import swag.analysis_graphs.execution_engine.IVariablesList;
import swag.analysis_graphs.execution_engine.MultiSliceSignature;
import swag.analysis_graphs.execution_engine.OWLAndSPARQLExecutionEngineFactory;
import swag.analysis_graphs.execution_engine.Signature;
import swag.analysis_graphs.execution_engine.analysis_situations.AggregationOperationInAnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.AnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.DiceNodeInAnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.IDimensionQualification;
import swag.analysis_graphs.execution_engine.analysis_situations.IMeasureToAnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.ISliceSinglePositionNoType;
import swag.analysis_graphs.execution_engine.analysis_situations.ISliceSinglePositionTyped;
import swag.analysis_graphs.execution_engine.analysis_situations.LevelInAnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.MeasureInAnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.PredicateInASMultiple;
import swag.analysis_graphs.execution_engine.analysis_situations.PredicateInASSimple;
import swag.analysis_graphs.execution_engine.analysis_situations.SliceCondition;
import swag.analysis_graphs.execution_engine.analysis_situations.SliceConditionTyped;
import swag.analysis_graphs.execution_engine.analysis_situations.Variable;
import swag.analysis_graphs.execution_engine.navigations.NavigationStep;
import swag.data_handler.Constants;
import swag.data_handler.OWLConnectionFactory;
import swag.data_handler.OWlConnection;
import swag.helpers.AutoCompleteData;
import swag.sparql_builder.Configuration;

public class ServletsHelper {

	private static final Logger logger = Logger.getLogger(ServletsHelper.class);

	/**
	 * 
	 * Given an analysis graph file, this method looks for the underlying MD
	 * schema instance of the analysis graph, and retrieves its local name.
	 * 
	 * @param path
	 *            internal path to AG and SMD ontologies
	 * @param analysisGraphFileName
	 *            Name of the analysis graph file
	 * 
	 * @return the name of the MD schema, or an empty String if the name is not
	 *         found.
	 * 
	 */
	public static String getMDSchemaName(String path, String analysisGraphFileName) {

		Individual schemaIndiv = getMDSchemaIndiv(path, analysisGraphFileName);
		if (schemaIndiv != null) {
			return schemaIndiv.getLocalName();
		}
		return "";
	}

	/**
	 * @param fileUri
	 * @return
	 * @throws URISyntaxException
	 */
	public File readOnlineFile(String fileUri) throws URISyntaxException {
		URI uri = new URI(fileUri);
		File f = new File(uri);
		return f;
	}

	/**
	 * 
	 * Given an analysis graph file, this method looks for the AG instance in
	 * the file and retrieves its local name.
	 * 
	 * @param path
	 *            internal path to AG and SMD ontologies
	 * @param analysisGraphFileName
	 *            Name of the analysis graph file
	 * 
	 * @return the name of the analysis graph, or an empty String if the name is
	 *         not found.
	 * 
	 */
	public static String getAGName(String path, String analysisGraphFileName) {

		Individual agIndiv = getAGIndiv(path, analysisGraphFileName);
		if (agIndiv != null) {
			return agIndiv.getLocalName();
		}
		return "";
	}

	/**
	 * 
	 * Given an analysis graph file, this method looks for the underlying MD
	 * schema FILE individual of the analysis graph, and retrieves its name.
	 * 
	 * @param path
	 *            internal path to AG and SMD ontologies
	 * @param analysisGraphFileName
	 *            Name of the analysis graph file
	 * 
	 * @return the name of the MD schema FILE, or an empty String if the name is
	 *         not found.
	 * 
	 */
	public static String getMDSchemaFileName(String path, String analysisGraphFileName) {

		Individual schemaFileIndiv = getMDSchemaFileIndiv(path, analysisGraphFileName);
		if (schemaFileIndiv != null) {
			return schemaFileIndiv.getLocalName();
		}
		return "";
	}

	public static String getMDSchemaFileNameIRI(String path, String analysisGraphFileName) {

		Individual schemaFileIndiv = getMDSchemaFileIndiv(path, analysisGraphFileName);
		if (schemaFileIndiv != null) {
			return schemaFileIndiv.getURI();
		}
		return "";
	}

	/**
	 * 
	 * Given an analysis graph file, this method looks for the underlying MD
	 * schema individual of the analysis graph, and retrieves its URI.
	 * 
	 * @param path
	 *            internal path to AG and SMD ontologies
	 * @param analysisGraphFileName
	 *            Name of the analysis graph file
	 * 
	 * @return the name of the MD schema, or an empty String if the name is not
	 *         found.
	 * 
	 */
	public static String getMDSchemaURI(String path, String analysisGraphFileName) {

		Individual schemaIndiv = getMDSchemaFileIndiv(path, analysisGraphFileName);
		if (schemaIndiv != null) {
			return schemaIndiv.getURI();
		}
		return "";
	}

	/**
	 * @param path
	 * @param analysisGraphFileName
	 * @return
	 */
	private static Individual getAGIndiv(String path, String analysisGraphFileName) {

		OWlConnection tempConn = new OWlConnection();
		tempConn.createOntologyModel();

		OWLConnectionFactory.appendSMD(tempConn, path, Constants.SMDFile);
		OWLConnectionFactory.appendAG(tempConn, path, Constants.AGFile);

		tempConn.readOwlFromFile(path + "/Uploaded/AGs", analysisGraphFileName);

		Individual ag = null;
		OntClass res = null;

		res = tempConn.getModel().getOntClass(OWLConnectionFactory.getAGNamespace(tempConn) + Constants.ANALYSIS_GRAPH);

		if (res != null) {

			ExtendedIterator<? extends OntResource> itr1 = res.listInstances();

			while (itr1.hasNext()) {
				ag = (Individual) itr1.next();
				break;
			}

			if (ag != null) {
				return ag;
			}
		}

		logger.error("Cannot find Analysis graph instance.");
		return null;

	}

	/**
	 * Gets the MD schema on which the analysis graph is based. This method uses
	 * the RDFS.isDefinedBy property in the analysis graph file to determine the
	 * name of the MD schema and upon that its location.
	 * 
	 * @param path
	 * @param analysisGraphFileName
	 * @return
	 */
	private static Individual getMDSchemaFileIndiv(String path, String analysisGraphFileName) {

		OWlConnection tempConn = new OWlConnection();
		tempConn.createOntologyModel();

		OWLConnectionFactory.appendSMD(tempConn, path, Constants.SMDFile);
		OWLConnectionFactory.appendAG(tempConn, path, Constants.AGFile);

		tempConn.readOwlFromFile(path + "/Uploaded/AGs", analysisGraphFileName);

		Individual ag = getAGIndiv(path, analysisGraphFileName);
		if (ag != null) {
			RDFNode schemaNode = tempConn.getPropertyValueEnc(ag, tempConn.getModel()
					.getOntProperty(OWLConnectionFactory.getAGNamespace(tempConn) + Constants.onSchema));

			RDFNode schemaNode1 = tempConn.getPropertyValueEnc(schemaNode.as(Individual.class), RDFS.isDefinedBy);

			Individual schemaIndiv = schemaNode1.as(Individual.class);
			return schemaIndiv;
		}
		logger.error("Cannot find MD schema file.");
		return null;
	}

	/**
	 * @param path
	 * @param analysisGraphFileName
	 * @return
	 */
	private static Individual getMDSchemaIndiv(String path, String analysisGraphFileName) {

		OWlConnection tempConn = new OWlConnection();
		tempConn.createOntologyModel();

		OWLConnectionFactory.appendSMD(tempConn, path, Constants.SMDFile);
		OWLConnectionFactory.appendAG(tempConn, path, Constants.AGFile);

		tempConn.readOwlFromFile(path + "/Uploaded/AGs", analysisGraphFileName);

		Individual ag = getAGIndiv(path, analysisGraphFileName);
		if (ag != null) {
			RDFNode schemaNode = tempConn.getPropertyValueEnc(ag, tempConn.getModel()
					.getOntProperty(OWLConnectionFactory.getAGNamespace(tempConn) + Constants.onSchema));

			Individual schemaIndiv = schemaNode.as(Individual.class);
			return schemaIndiv;
		}
		logger.error("Cannot find MD schema instance.");
		return null;
	}

	/**
	 * 
	 * Prepare to call a builder method to create an instance of
	 * {@code AnalysisGraphsEngine} from local files uploaded to the server. The
	 * first individual of type {@code Constants.ANALYSIS_GRAPH} found is
	 * considered the wanted analysis graph.
	 * 
	 * @param path
	 *            internal path to AG and SMD ontologies
	 * @param analysisGraphFileName
	 *            Name of the analysis graph file
	 *
	 * @return an initiated {@code AnalysisGraphsEngine} instance
	 * @throws Exception
	 */
	public static AnalysisGraphsManager createAnalysisGraphExecutionEngine(String path, String analysisGraphFileName)
			throws Exception {

		logger.info("Reading analysis graph " + analysisGraphFileName);

		IExecutionEngineFactory factory = new OWLAndSPARQLExecutionEngineFactory();
		String analysisGraphName = getAGName(path, analysisGraphFileName);
		String mdSchemaFileName = getMDSchemaFileNameIRI(path, analysisGraphFileName);
		String mdSchemaName = getMDSchemaName(path, analysisGraphFileName);
		if (Configuration.getInstance().isLocal()) {
			mdSchemaFileName = mdSchemaName + ".ttl";
		}
		try {
			return factory.initiateExecutionEngin(path, mdSchemaFileName, mdSchemaName, analysisGraphFileName,
					analysisGraphName);
		} catch (Exception ex) {
			logger.error("Cannot create a build engine instance.", ex);
			throw ex;
		}
	}

	/**
	 * 
	 * Generates the variable String for a variable in a navigation step
	 * 
	 * @param var
	 *            the variable to generate the string for
	 * @param nv
	 *            the navigation step that contains the variable
	 * @return the generated string of the variable
	 * 
	 */
	public static String getVariableString(Variable var, NavigationStep nv, AnalysisGraph graph) {
		return getVarString(var, nv.getVariables().entrySet(), graph);
	}

	/**
	 * 
	 * Generates the variable String for a variable in an analysis situation
	 * 
	 * @param var
	 *            the variable to generate the string for
	 * @param as
	 *            the analysis situation that contains the variable
	 * @return the generated string of the variable
	 * 
	 */
	public static String getVariableString(Variable var, AnalysisSituation as, AnalysisGraph graph) {
		return getVarString(var, as.getVariables().entrySet(), graph);
	}

	/**
	 * 
	 * Actual function to generate the variable String.
	 * 
	 * @param var
	 * @param set
	 * @return
	 */
	private static String getVarString(Variable var, Set<Entry<Integer, Variable>> set, AnalysisGraph graph) {

		int variableIndex = -1;
		for (Map.Entry<Integer, Variable> entry : set) {
			if (entry.getValue().equals(var)) {
				variableIndex = entry.getKey();
			}
		}
		if (variableIndex == -1) {

			System.out.println("ssss");

		}
		GenerateVariableStringVisitor visitor = GenerateVariableStringVisitor.createGenerateVariableStringVisitor(set,
				graph);
		try {
			return var.acceptStringVisitor(visitor, variableIndex);
		} catch (Exception ex) {
			logger.warn("failed to get variable String, returning empty String.");
			logger.warn(ex);
			return StringUtils.EMPTY;
		}
	}

	/**
	 * 
	 * Binds/unbinds the variables received from the request. Writes to the
	 * bindings of the analysis situation/navigation step bindigns in the
	 * webpage when a new condition value is created without using the
	 * autocomplleter.
	 * 
	 * @param parameterNames
	 * @param request
	 * @param addedNSForSMDInstance
	 * @param initialVariables
	 * @param variables
	 * @param buildEng
	 * @param as
	 * @param factURI
	 *            the uri of the fact
	 * @param strs
	 *            the session stored label pairs and value pairs to store the
	 *            bindings of the variables of the situations/navigations in the
	 *            UI webpage. Note that this parameter is an out parameter and
	 *            the new values should be written back to the session and to
	 *            the request to take effect.
	 * @throws Exception
	 */
	public static void processRequestVariables(Enumeration<String> parameterNames, HttpServletRequest request,
			String addedNSForSMDInstance, Map<Variable, Variable> initialVariables, Map<Integer, Variable> variables,
			AnalysisGraphsManager buildEng, IVariablesList as, String factURI, Map<String, String[]> addedLabels,
			Map<String, String[]> addedVals, List<AutoCompleteData> addedSuggestions) throws Exception {

		VariableVisitorProcessParameter visitor = new VariableVisitorProcessParameter(addedNSForSMDInstance,
				initialVariables, variables, buildEng, as, factURI);

		while (parameterNames.hasMoreElements()) {

			String paramName = parameterNames.nextElement();
			visitor.init(paramName, request.getParameter(paramName));

			Signature<IDimensionQualification> sigDummy = new Signature<IDimensionQualification>(null, null, null, null,
					null);
			Signature<IDimensionQualification> sSigDummy = new Signature<IDimensionQualification>(null, null, null,
					null, null);
			MultiSliceSignature mSigDummy = new MultiSliceSignature(null, null, null, null, null);
			Signature<IMeasureToAnalysisSituation> msSigDummy = new Signature<IMeasureToAnalysisSituation>(null, null,
					null, null, null);

			if (paramName.startsWith("level_")) {
				LevelInAnalysisSituation lDummy = new LevelInAnalysisSituation(sigDummy);
				lDummy.acceptVisitor(visitor);
				continue;
			}

			if (paramName.startsWith("diceNode_slice_noPosition_typed_")) {
				ISliceSinglePositionTyped sDummy = new SliceConditionTyped(sigDummy);
				sDummy.acceptVisitor(visitor);
				continue;
			}

			if (paramName.startsWith("diceNode_slice_noPosition_")) {
				ISliceSinglePositionNoType sDummy = new SliceCondition(sigDummy);
				sDummy.acceptVisitor(visitor);
				continue;
			}

			if (paramName.startsWith("diceNode_slice_multiple_")) {
				PredicateInASMultiple pDummy = new PredicateInASMultiple(mSigDummy);
				pDummy.acceptVisitor(visitor);
				continue;
			}

			if (paramName.startsWith("diceNode_slice")) {
				PredicateInASSimple pDummy = new PredicateInASSimple(sSigDummy);
				pDummy.acceptVisitor(visitor);
				continue;
			}

			if (paramName.startsWith("diceNode_")) {
				DiceNodeInAnalysisSituation dDummy = new DiceNodeInAnalysisSituation("");
				dDummy.acceptVisitor(visitor);
				continue;
			}

			if (paramName.startsWith("measure_")) {
				MeasureInAnalysisSituation mDummy = new MeasureInAnalysisSituation(msSigDummy);
				mDummy.acceptVisitor(visitor);
				continue;
			}

			if (paramName.startsWith("agg_")) {
				AggregationOperationInAnalysisSituation agDummy = new AggregationOperationInAnalysisSituation(
						msSigDummy);
				agDummy.acceptVisitor(visitor);
				continue;
			}
		}

		addedLabels.putAll(visitor.getAddedLabelBindings());
		addedVals.putAll(visitor.getAddedValueBindings());
		addedSuggestions.addAll(visitor.getAutocompleteData());
	}

	public static void processAJAXSuggestionsRequest(String reqName, String addedNSForSMDInstance,
			HttpServletRequest request, HttpSession session, HttpServletResponse response) {
	}

}
