package swag.data_handler;

import java.util.List;
import java.util.Map;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.query.Query;
import org.apache.jena.rdf.model.Statement;

import swag.analysis_graphs.execution_engine.NoMappingExistsForElementException;
import swag.analysis_graphs.execution_engine.NoSuchElementExistsException;
import swag.md_elements.Mapping;

public interface MappingRepInterface {

	/**
	 * @param uri
	 * @return
	 * @throws NoSuchElementExistsException
	 * @throws NoMappingExistsForElementException
	 */
	public Mapping getMappingByRelationURI(String uri)
			throws NoSuchElementExistsException, NoMappingExistsForElementException;

	/**
	 * @param varName
	 * @param varType
	 * @return
	 * @throws NoSuchElementExistsException
	 * @throws NoMappingExistsForElementException
	 */
	public Mapping generateMappingByElementURI(Class<?> typ, String varName, String varType)
			throws NoSuchElementExistsException, NoMappingExistsForElementException;

	/**
	 * @param fromVarName
	 * @param toVarName
	 * @param varsProp
	 * @return
	 * @throws NoSuchElementExistsException
	 * @throws NoMappingExistsForElementException
	 */
	public Mapping generateMappingByRelationURI(Class<?> typ, String fromVarName, String toVarName, String varsProp)
			throws NoSuchElementExistsException, NoMappingExistsForElementException;

	/**
	 * @param uri
	 * @return
	 * @throws NoSuchElementExistsException
	 * @throws NoMappingExistsForElementException
	 */
	public Mapping getMappingByElementURI(String uri)
			throws NoSuchElementExistsException, NoMappingExistsForElementException;

	/**
	 * This function should be only called for paths that have mapping;
	 * otherwise its behavior is not expectable
	 * 
	 * @param p
	 *            the path to generate its mapping
	 * @return
	 */
	public List<Query> getPathQueries(swag.data_handler.Path p);

	/**
	 * gets the query connecting mdElementURI1 and mdElementURI1 by joining the
	 * queries on the path. Throws a NoMappingExistsForElementException when an
	 * error occurs
	 * 
	 * @param mdElementURI2
	 * @param mdElementURI1
	 * @return
	 * @throws NoMappingExistsForElementException
	 */
	public Mapping getMappingQueryBetweenTwoElementsByURI(String mdElementURI2, String mdElementURI1)
			throws NoMappingExistsForElementException;

	/**
	 * @param ind
	 * @return
	 */
	public Map<OntProperty, List<Individual>> getOutMDInPMappedPropsAndValues(Individual ind);

	/**
	 * @param ind
	 * @return
	 */
	public Map<OntProperty, List<Individual>> getOutMDInPMappedPropsAndValuesThatHaveMapping(Individual ind);

	/**
	 * Each edge is considered to be uniquely defined by its start and end
	 * nodes.
	 * 
	 * @param stmt
	 *            a triple
	 * @return true if the statement has a mapping query
	 */
	public boolean checkIfMDPropertyHasMapping(Statement stmt);

	/**
	 * @param typ
	 * @param varName
	 * @param dataType
	 * @return
	 * @throws NoSuchElementExistsException
	 * @throws NoMappingExistsForElementException
	 */
	public Mapping generateMappingByElementURIForMeasure(Class<?> typ, String varName, String dataType)
			throws NoSuchElementExistsException, NoMappingExistsForElementException;

}
