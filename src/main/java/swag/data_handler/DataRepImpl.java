package swag.data_handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;

import swag.analysis_graphs.dao.IDataDAO;
import swag.analysis_graphs.execution_engine.NoMappingExistsForElementException;
import swag.analysis_graphs.execution_engine.NoOrderFunctionApplicableException;
import swag.analysis_graphs.execution_engine.NoValueFoundException;
import swag.data_handler.connection_to_rdf.DataRetriever;
import swag.data_handler.connection_to_rdf.SPARQLEndpointConnection;
import swag.data_handler.connection_to_rdf.exceptions.RemoteSPARQLQueryExecutionException;
import swag.md_elements.MDSchema;
import swag.md_elements.Mapping;
import swag.md_elements.MappingFunctions;
import swag.sparql_builder.BasicAnalysisSituationSPARQLQueryGenerator;
import swag.sparql_builder.CustomSPARQLQuery;
import swag.sparql_builder.SPARQLUtilities;
import swag.sparql_builder.ASElements.IAnalysisSituationToSPARQL;

public class DataRepImpl implements IDataDAO {

    private MDSchema graph;
    private SPARQLEndpointConnection endpointConn;

    public DataRepImpl(SPARQLEndpointConnection endpointConn, MDSchema graph) {
	super();
	this.endpointConn = endpointConn;
	this.graph = graph;
    }

    @Override
    public ResultSet sendQueryToEndpointAndGetResults(String queryString) throws RemoteSPARQLQueryExecutionException {
	return endpointConn.sendQueryToEndpointAndGetResults(QueryFactory.create(queryString));
    }

    @Override
    public String getNextUpDiceValue(MDSchema mdSchema, String currDiceVal, String currLevelURI)
	    throws RemoteSPARQLQueryExecutionException, NoValueFoundException, NoMappingExistsForElementException {
	try {

	    IAnalysisSituationToSPARQL queryGenerator = new BasicAnalysisSituationSPARQLQueryGenerator();

	    Mapping m = MappingFunctions.getPathQuery(graph, currLevelURI,
		    mdSchema.getNextLevel(currLevelURI).getIdentifyingName());
	    String currLevelVariableName = MappingFunctions
		    .getPathQuery(graph, mdSchema.getFactOfSchema().getURI(), currLevelURI).getQuery()
		    .getNameOfUnaryProjectionVar();

	    return DataRetriever.getFirstQueryResultAsString(endpointConn,
		    queryGenerator.generateQueryForNextUpDiceValue(m.getQuery().getSparqlQuery(), currDiceVal,
			    currLevelVariableName));
	} catch (NoValueFoundException | RemoteSPARQLQueryExecutionException | NoMappingExistsForElementException ex) {

	    throw ex;

	}
    }

    @Override
    public Map<String, String> getMDItemPossibleValuesPairs(MDSchema mdSchema, String itemURI)
	    throws NoValueFoundException, NoMappingExistsForElementException, RemoteSPARQLQueryExecutionException {
	try {
	    Mapping m = MappingFunctions.getQuery(graph, graph.getFactOfSchema().getURI(), itemURI);
	    CustomSPARQLQuery cusQ = m.getQuery();
	    cusQ.removeQueryPatternDuplications();
	    cusQ.setSparqlQuery(SPARQLUtilities.addLabelsToQuery(cusQ, mdSchema.getPreferredLabelProperty()));

	    if (SPARQLUtilities.insertDistinctToDoubleHeadedQuery(cusQ))
		return DataRetriever.getLabeledQueryResults(endpointConn, cusQ.getSparqlQuery());
	    else
		return new HashMap<String, String>();
	} catch (NoValueFoundException | NoMappingExistsForElementException | RemoteSPARQLQueryExecutionException ex) {
	    throw ex;
	}
    }

    @Override
    public String getNextDiceValue(MDSchema mdSchema, String currDiceVal, String currLevelURI)
	    throws NoValueFoundException, NoMappingExistsForElementException, RemoteSPARQLQueryExecutionException,
	    NoOrderFunctionApplicableException {

	IAnalysisSituationToSPARQL queryGenerator = new BasicAnalysisSituationSPARQLQueryGenerator();

	try {
	    Mapping m = MappingFunctions.getPathQuery(graph, graph.getFactOfSchema().getURI(), currLevelURI);
	    String currLevelVariableName = m.getQuery().getNameOfUnaryProjectionVar();

	    return DataRetriever.getFirstQueryResultAsString(endpointConn,
		    queryGenerator.generateQueryForNextOrPreviousDiceValue(m.getQuery().getSparqlQuery(), currDiceVal,
			    currLevelVariableName, true));
	} catch (NoValueFoundException | NoMappingExistsForElementException | RemoteSPARQLQueryExecutionException
		| NoOrderFunctionApplicableException ex) {
	    throw ex;
	}
    }

    @Override
    public String getPreviousDiceValue(MDSchema mdSchema, String currDiceVal, String currLevelURI)
	    throws NoValueFoundException, NoMappingExistsForElementException, RemoteSPARQLQueryExecutionException,
	    NoOrderFunctionApplicableException {

	IAnalysisSituationToSPARQL queryGenerator = new BasicAnalysisSituationSPARQLQueryGenerator();

	try {
	    Mapping m = MappingFunctions.getPathQuery(graph, graph.getFactOfSchema().getURI(), currLevelURI);
	    String currLevelVariableName = m.getQuery().getNameOfUnaryProjectionVar();
	    return DataRetriever.getFirstQueryResultAsString(endpointConn,
		    queryGenerator.generateQueryForNextOrPreviousDiceValue(m.getQuery().getSparqlQuery(), currDiceVal,
			    currLevelVariableName, false));
	} catch (NoValueFoundException | NoMappingExistsForElementException | RemoteSPARQLQueryExecutionException
		| NoOrderFunctionApplicableException ex) {
	    throw ex;
	}
    }

    @Override
    public List<String> getMDItemPossibleValues(MDSchema mdSchema, String itemURI)
	    throws NoValueFoundException, NoMappingExistsForElementException, RemoteSPARQLQueryExecutionException {

	try {
	    Mapping m = MappingFunctions.getPathQuery(graph, graph.getFactOfSchema().getURI(), itemURI);
	    CustomSPARQLQuery cusQ = m.getQuery();
	    cusQ.removeQueryPatternDuplications();

	    if (SPARQLUtilities.insertDistinctToSingleHeadedQuery(cusQ))
		return DataRetriever.getQueryResultsAsStringList(endpointConn, cusQ.getSparqlQuery());
	    else
		return new ArrayList<String>();
	} catch (NoValueFoundException | NoMappingExistsForElementException | RemoteSPARQLQueryExecutionException ex) {
	    throw ex;
	}
    }
}
