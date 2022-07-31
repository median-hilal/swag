package swag.md_elements;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.query.Query;
import org.apache.log4j.Logger;

import swag.analysis_graphs.execution_engine.NoMappingExistsForElementException;
import swag.graph.Path;
import swag.sparql_builder.Configuration;
import swag.sparql_builder.CustomSPARQLQuery;
import swag.sparql_builder.QueryType;
import swag.sparql_builder.SPARQLUtilities;

public class MappingFunctions {

    private static final Logger logger = Logger.getLogger(MappingFunctions.class);

    private MappingFunctions() {

    }

    /**
     * 
     * Given URIs of the start and the end MDElements, this function generates
     * the mapping of the MD path connecting the start and end elements.
     * 
     * @param graph
     *            the MD graph
     * @param startElementUri
     *            the URI of the start element
     * @param endElementUri
     *            the uri of the end element
     * @return the mapping
     * @throws NoMappingExistsForElementException
     */
    public static Mapping getPathQuery(MDSchema graph, String startElementUri, String endElementUri)
	    throws NoMappingExistsForElementException {

	MDElement startElement = graph.getNode(startElementUri);
	MDElement endElement = graph.getNode(endElementUri);

	List<Path<MDElement, MDRelation>> list;

	list = graph.getAllMappedPathsBetweenTwoVertices(startElement, endElement);
	try {

	    if (list.size() == 0) {
		throw new NoMappingExistsForElementException("No leading path");
	    }
	    CustomSPARQLQuery rq = SPARQLUtilities.joinSubsequentQueries(getPathQueries(list.get(0)),
		    QueryType.DIMENSION);
	    return (new Mapping(rq));
	} catch (Exception ex) {
	    logger.error("Error generating path mapping. Path between: " + startElement.getIdentifyingName() + " - "
		    + endElement.getIdentifyingName(), ex);
	    throw new NoMappingExistsForElementException("Error generating path mapping. Path between: "
		    + startElement.getIdentifyingName() + " - " + endElement.getIdentifyingName(), ex);
	}

    }

    public static Mapping getQuery(MDSchema graph, String startElementUri, String endElementUri)
	    throws NoMappingExistsForElementException {

	if (Configuration.getInstance().is("simpleDiceNodeSuggestions")) {
	    return new Mapping(graph.getNode(endElementUri).getMapping());
	} else {
	    return getPathQuery(graph, startElementUri, endElementUri);
	}
    }

    /**
     * 
     * Given URIs of the start and the end MDElements, this function generates
     * the mapping of the MD path connecting the start and end elements. The
     * mapping of the first element in the path is included. The queries of the
     * mappings are not modified. They stay in their original state.
     * 
     * @param graph
     *            the MD graph
     * @param startElementUri
     *            the URI of the start element
     * @param endElementUri
     *            the URI of the end element
     * 
     * @return the mapping of the path, with the first element mapping included.
     * 
     * @throws NoMappingExistsForElementException
     * 
     */
    public static Mapping getPathQueryAndKeepIndividualMappingBlocks(MDSchema graph, String startElementUri,
	    String endElementUri) throws NoMappingExistsForElementException {

	MDElement startElement = graph.getNode(startElementUri);
	MDElement endElement = graph.getNode(endElementUri);

	List<Path<MDElement, MDRelation>> list;

	list = graph.getAllMappedPathsBetweenTwoVertices(startElement, endElement);
	try {

	    if (list.size() == 0) {
		throw new NoMappingExistsForElementException("No leading path");
	    }
	    CustomSPARQLQuery rq = SPARQLUtilities.createMDPathQuery(getPathQueries(list.get(0)));
	    return (new Mapping(rq));
	} catch (Exception ex) {
	    logger.error("Error generating path mapping. Path between: " + startElement.getIdentifyingName() + " - "
		    + endElement.getIdentifyingName(), ex);
	    throw new NoMappingExistsForElementException("Error generating path mapping. Path between: "
		    + startElement.getIdentifyingName() + " - " + endElement.getIdentifyingName(), ex);
	}

    }

    /**
     * 
     * Given URIs of the start and the end MDElements, this function generates
     * the mapping of the MD path connecting the start and end elements. The
     * mapping of the first element in the path is excluded. The queries of the
     * mappings are not modified. They stay in their original state.
     * 
     * @param graph
     *            the MD graph
     * @param startElementUri
     *            the URI of the start element
     * @param endElementUri
     *            the URI of the end element
     * 
     * @return the mapping of the path, with the first element mapping excluded.
     * 
     * @throws NoMappingExistsForElementException
     * 
     */
    public static Mapping getPathQueryAndKeepIndividualMappingBlocksExclusive(MDSchema graph, String startElementUri,
	    String endElementUri) throws NoMappingExistsForElementException {

	MDElement startElement = graph.getNode(startElementUri);
	MDElement endElement = graph.getNode(endElementUri);

	List<Path<MDElement, MDRelation>> list;

	list = graph.getAllMappedPathsBetweenTwoVertices(startElement, endElement);
	try {

	    if (list.size() == 0) {
		throw new NoMappingExistsForElementException("No leading path");
	    }
	    CustomSPARQLQuery rq = SPARQLUtilities.createMDPathQuery(getPathQueriesExclusive(list.get(0)));
	    // rq.removeQueryPatternDuplications();
	    return (new Mapping(rq));
	} catch (Exception ex) {
	    logger.error("Error generating path mapping. Path between: " + startElement.getIdentifyingName() + " - "
		    + endElement.getIdentifyingName(), ex);
	    throw new NoMappingExistsForElementException("Error generating path mapping. Path between: "
		    + startElement.getIdentifyingName() + " - " + endElement.getIdentifyingName(), ex);
	}

    }

    /**
     * 
     * Given URIs of the start and the end MDElements, this function generates
     * the mapping of the MD path connecting the start and end elements. The
     * mapping of the start element is excluded.
     * 
     * @param graph
     *            the MD graph
     * @param startElementUri
     *            the URI of the start element
     * @param endElementUri
     *            the uri of the end element
     * @return the mapping
     * @throws NoMappingExistsForElementException
     */
    public static Mapping getPathQueryExclusive(MDSchema graph, String startElementUri, String endElementUri)
	    throws NoMappingExistsForElementException {

	MDElement startElement = graph.getNode(startElementUri);
	MDElement endElement = graph.getNode(endElementUri);

	List<Path<MDElement, MDRelation>> list;

	list = graph.getAllMappedPathsBetweenTwoVertices(startElement, endElement);
	try {

	    if (list.size() == 0) {
		throw new NoMappingExistsForElementException("No leading path");
	    }
	    CustomSPARQLQuery rq = SPARQLUtilities.joinSubsequentQueries(getPathQueriesExclusive(list.get(0)),
		    QueryType.DIMENSION);
	    return (new Mapping(rq));
	} catch (Exception ex) {
	    logger.error("Error generating path mapping. Path between: " + startElement.getIdentifyingName() + " - "
		    + endElement.getIdentifyingName(), ex);
	    throw new NoMappingExistsForElementException("Error generating path mapping. Path between: "
		    + startElement.getIdentifyingName() + " - " + endElement.getIdentifyingName(), ex);
	}

    }

    /**
     * 
     * Gets a list of queries of path nodes and edges.
     * 
     * @param path
     *            to get queries of
     * 
     * @return a list of SPARQL queries representing the mappings of the
     *         elements of the path.
     * 
     */
    private static List<Query> getPathQueries(Path<MDElement, MDRelation> path) {

	List<Query> queries = new ArrayList<>();

	List<MDRelation> rels = path.getPathEdges();

	for (MDRelation rel : rels) {

	    // Not duplicating queries in the list
	    if (rel.equals(rels.get(0))) {
		queries.add(rel.getFrom().getMapping().getQuery().getSparqlQuery());
	    }
	    queries.add(rel.getMapping().getQuery().getSparqlQuery());
	    queries.add(rel.getTo().getMapping().getQuery().getSparqlQuery());
	}

	return queries;
    }

    /**
     * 
     * Gets a list of queries of path nodes and edges. Exclude query of start
     * node of the path. Does not repeat queries of elements on the path between
     * the start and the end.
     * 
     * @param path
     *            to get queries of
     * 
     * @return a list of SPARQL queries representing the mappings of the
     *         elements of the path except for its start element.
     * 
     */
    private static List<Query> getPathQueriesExclusive(Path<MDElement, MDRelation> path) {

	List<Query> queries = new ArrayList<>();

	for (MDRelation rel : path.getPathEdges()) {

	    queries.add(rel.getMapping().getQuery().getSparqlQuery());
	    queries.add(rel.getTo().getMapping().getQuery().getSparqlQuery());
	}

	return queries;
    }

}
