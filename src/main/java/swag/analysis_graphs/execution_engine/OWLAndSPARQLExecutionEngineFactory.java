package swag.analysis_graphs.execution_engine;

import org.apache.jena.ontology.Individual;
import org.apache.log4j.Logger;

import swag.analysis_graphs.dao.IAnalysisGraphDAO;
import swag.analysis_graphs.dao.IDataDAO;
import swag.analysis_graphs.dao.IMDSchemaDAO;
import swag.analysis_graphs.dao.MDSchemaBuilderFactory;
import swag.analysis_graphs.execution_engine.navigations.NavigationStep;
import swag.data_handler.Constants;
import swag.data_handler.DataRepImpl;
import swag.data_handler.OWLConnectionFactory;
import swag.data_handler.OWLRepsAccessImpl;
import swag.data_handler.OWlConnection;
import swag.data_handler.connection_to_rdf.SPARQLEndpointConnection;
import swag.md_elements.MDSchema;
import swag.predicates.IPredicateGraph;
import swag.predicates.IPredicateGraphBuilder;
import swag.predicates.PredicateGraphBuilder;
import swag.sparql_builder.Configuration;

/**
 * An execution engine factory based on OWL files.
 * 
 * @author swag
 *
 */
public class OWLAndSPARQLExecutionEngineFactory implements IExecutionEngineFactory {

	private static final Logger logger = Logger.getLogger(OWLAndSPARQLExecutionEngineFactory.class);

	/**
	 * Prepares the OWL connection.
	 * 
	 * @param path
	 *            The path to the location of the ontologies
	 * @return the OWL connection instance
	 */
	public OWlConnection initOWLConnection(String path) {

		OWlConnection owlConn = OWLConnectionFactory.createOWLConnectionWithoutReasoning();
		OWLConnectionFactory.appendQB(owlConn, path, Constants.qbFile, Configuration.getInstance().isLocal());
		OWLConnectionFactory.appendQB4O(owlConn, path, Constants.qb4oFile, Configuration.getInstance().isLocal());
		OWLConnectionFactory.appendSMD(owlConn, path, Constants.SMDFile);
		OWLConnectionFactory.appendAG(owlConn, path, Constants.AGFile);
		OWLConnectionFactory.appendPredicates(owlConn, path, Constants.PredicatesFile);

		return owlConn;
	}

	@Override
	public AnalysisGraphsManager initiateExecutionEngin(String pathToSourceOntologies, String mdSchemaFileName,
			String mdSchemaName, String analysisGraphFileName, String analysisGraphName) throws Exception {

		OWlConnection owlConn = initOWLConnection(pathToSourceOntologies);

		OWLConnectionFactory.appendSMDIns(owlConn, pathToSourceOntologies + "/Uploaded/SMDs", mdSchemaFileName, Configuration.getInstance().isLocal());

		OWLConnectionFactory.appendAGIns(owlConn, pathToSourceOntologies + "/Uploaded/AGs", analysisGraphFileName);
		SPARQLEndpointConnection sparqlEndpointConn = new SPARQLEndpointConnection();
		IMDSchemaDAO mdBuilder = MDSchemaBuilderFactory.getMDMDSchemaDAO(owlConn);
		MDSchema schema = mdBuilder.buildMDSchema();

		if (schema != null) {
			IPredicateGraphBuilder builder = new PredicateGraphBuilder(schema);
			IPredicateGraph predicateGraph = builder.buildPredicatesGraphWitoutRemoteAccess(
					OWlConnection.convertPathToURI(pathToSourceOntologies, "predicates.ttl"));
			sparqlEndpointConn.setSparqlEndpointURI(schema.getEndpoint());
			IDataDAO dataRep = new DataRepImpl(sparqlEndpointConn, schema);
			IAnalysisGraphDAO owlInterface = new OWLRepsAccessImpl(owlConn, schema, predicateGraph);
			Individual agIndiv = owlConn.getModel()
					.getIndividual(OWLConnectionFactory.getAGInstanceNamespace(owlConn) + analysisGraphName);
			AnalysisGraph ag = owlInterface.buildAnalysisGraph(agIndiv.getLocalName(), agIndiv.getURI(),
					OWLConnectionFactory.getAGInstanceNamespace(owlConn), schema);
			
			performNavigationGeneration(dataRep, owlInterface, mdBuilder, schema, ag);

			if (Configuration.getInstance().is("singleHierarchy")) {

			}

			ag.toString();

			if (ag != null) {
				AnalysisGraphsManager buildEng = new AnalysisGraphsManager(owlInterface, dataRep, mdBuilder);
				buildEng.setState(ExecutionEngineState.INITIATED);
				buildEng.setMDSchema(schema);
				buildEng.setAg(ag);
				buildEng.setPredicateGraph(predicateGraph);

				return buildEng;
			}
		}
		// schema.stringifyGraph();
		Exception ex = new Exception("Cannot initiate analysis graph or MD schema.");
		logger.error("Error occurred reading analysis graph and/or MD schema.", ex);
		throw ex;
	}

	private void performNavigationGeneration(IDataDAO dataRep, IAnalysisGraphDAO owlInterface, IMDSchemaDAO mdBuilder,
			MDSchema schema, AnalysisGraph ag) {

		if (Configuration.getInstance().is("dynamicGenerationOfTarget")) {

			NavigationStrategy strategy = new NavigationStrategyPerformNavigation(ag);

			for (NavigationStep step : ag.getNavigationSteps()) {
				strategy.doNavigate(step, schema, mdBuilder, owlInterface, dataRep);
			}
		}
	}
}
