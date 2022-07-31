package swag.data_handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryException;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.iterator.ExtendedIterator;

import swag.analysis_graphs.execution_engine.NoMappingExistsForElementException;
import swag.analysis_graphs.execution_engine.NoSuchElementExistsException;
import swag.md_elements.Descriptor;
import swag.md_elements.Fact;
import swag.md_elements.HasDescriptor;
import swag.md_elements.HasLevel;
import swag.md_elements.HasMeasure;
import swag.md_elements.Level;
import swag.md_elements.Mapping;
import swag.md_elements.Measure;
import swag.md_elements.QB4OHierarchyStep;
import swag.sparql_builder.CustomSPARQLQuery;
import swag.sparql_builder.QueryType;
import swag.sparql_builder.SPARQLUtilities;

public class MappingRepImpl implements MappingRepInterface {

	private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(MappingRepImpl.class);

	private OWlConnection owlConnection;

	public OWlConnection getOwlConnection() {
		return owlConnection;
	}

	public void setOwlConnection(OWlConnection owlConnection) {
		this.owlConnection = owlConnection;
	}

	public MappingRepImpl(OWlConnection owlConnection) {
		super();
		this.owlConnection = owlConnection;
	}

	@Override
	public Mapping getMappingByRelationURI(String uri)
			throws NoSuchElementExistsException, NoMappingExistsForElementException {

		try {
			Individual ind = this.owlConnection.getModel().getIndividual(uri);
			if (ind != null) {
				Individual mapInd = owlConnection
						.getPropertyValueEnc(ind,
								this.owlConnection.getModel().getObjectProperty(
										OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.propMapping))
						.as(Individual.class);
				if (mapInd != null) {

					String mapBodyString = owlConnection
							.getPropertyValueEnc(mapInd,
									this.owlConnection.getModel().getDatatypeProperty(
											OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.hasString))
							.toString();
					if (mapBodyString != null) {
						// TODO-soon this constructor is not functioning well,
						// repair it!
						CustomSPARQLQuery q = new CustomSPARQLQuery(QueryFactory.create(mapBodyString.toString()));
						Mapping map = new Mapping(q);
						return map;
					} else {
						throw new NoSuchElementExistsException("mapIndString: " + mapInd.toString());
					}
				} else {
					throw new NoSuchElementExistsException("mapInd: " + ind.toString());
				}
			}
		} catch (NoSuchElementExistsException ex) {
			logger.error("Exception. ", ex);
			throw ex;
		} catch (QueryException ex) {
			logger.error("Exception. ", ex);
			throw new NoMappingExistsForElementException(ex.toString());
		} catch (Exception ex) {
			logger.error("Exception. ", ex);
			throw ex;
		}
		return null;
	}

	@Override
	public Mapping getMappingByElementURI(String uri)
			throws NoSuchElementExistsException, NoMappingExistsForElementException {

		try {
			Individual ind = this.owlConnection.getModel().getIndividual(uri);
			if (ind != null) {
				Individual mapInd = ind
						.getPropertyValue(this.owlConnection.getModel().getObjectProperty(
								OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.hasMapping))
						.as(Individual.class);
				if (mapInd != null) {

					String mapBodyString = mapInd
							.getPropertyValue(this.owlConnection.getModel().getDatatypeProperty(
									OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.hasString))
							.toString();
					if (mapBodyString != null) {
						// TODO-soon this constructor is not functioning well,
						// repair it!
						CustomSPARQLQuery q = new CustomSPARQLQuery(QueryFactory.create(mapBodyString.toString()));
						Mapping map = new Mapping(q);
						return map;
					} else {
						throw new NoSuchElementExistsException("mapIndString: " + mapInd.toString());
					}
				} else {
					throw new NoSuchElementExistsException("mapInd: " + ind.toString());
				}
			}
		} catch (NoSuchElementExistsException ex) {
			logger.error("Exception. ", ex);
			throw ex;
		} catch (QueryException ex) {
			logger.error("Exception. ", ex);
			throw new NoMappingExistsForElementException(ex.toString());
		}
		return null;
	}

	@Override
	public List<Query> getPathQueries(swag.data_handler.Path p) {

		List<Query> queries = new LinkedList<Query>();

		// Adding first vertex mapping to the list to avoid looping it in the
		// next loop
		Individual mapInd = p.getFirstVertex().getVertex()
				.getPropertyValue(this.owlConnection.getModel()
						.getObjectProperty(OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.hasMapping))
				.as(Individual.class);
		Individual mapBodyInd = mapInd
				.getPropertyValue(this.owlConnection.getModel().getObjectProperty(
						OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.hasMappingBody))
				.as(Individual.class);
		String mapBodyString = mapBodyInd
				.getPropertyValue(this.owlConnection.getModel()
						.getDatatypeProperty(OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.hasString))
				.toString();
		queries.add(QueryFactory.create(mapBodyString));

		for (Edge e : p.getPath()) {
			// Adding the edge property mapping query
			Individual sub = e.getSrc().getVertex();
			Individual obj = e.getDes().getVertex();
			ExtendedIterator instances = this.owlConnection
					.getClassByName(OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.reifiedMapping)
					.listInstances();
			while (instances.hasNext()) {
				Individual thisInstance = (Individual) instances.next();
				Individual from = this.owlConnection
						.getPropertyValueEnc(thisInstance,
								this.owlConnection.getModel().getProperty(
										OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.fromStr))
						.as(Individual.class);
				Individual to = this.owlConnection
						.getPropertyValueEnc(thisInstance,
								this.owlConnection.getModel().getProperty(
										OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.toStr))
						.as(Individual.class);
				if (sub.equals(from) && obj.equals(to)) {
					String mapping = this.owlConnection
							.getPropertyValueEnc(thisInstance, this.owlConnection.getModel().getProperty(
									OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.propMapping))
							.toString();
					queries.add(QueryFactory.create(mapping));
				}
			}
			// Adding destination node of the edge
			mapInd = e.getDes().getVertex()
					.getPropertyValue(this.owlConnection.getModel().getObjectProperty(
							OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.hasMapping))
					.as(Individual.class);
			mapBodyInd = mapInd
					.getPropertyValue(this.owlConnection.getModel().getObjectProperty(
							OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.hasMappingBody))
					.as(Individual.class);
			mapBodyString = mapBodyInd.getPropertyValue(this.owlConnection.getModel()
					.getDatatypeProperty(OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.hasString))
					.toString();
			queries.add(QueryFactory.create(mapBodyString));
		}
		return queries;
	}

	public Mapping getMappingQueryBetweenTwoElementsByURI1(String mdElementURI2, String mdElementURI1)
			throws NoMappingExistsForElementException {

		try {
			DepreciatedOWLGraph owlGraph = new DepreciatedOWLGraph(this.owlConnection, this);
			List<swag.data_handler.Path> paths = owlGraph.getAllPathsBetweenTwoVertices(
					new Vertex(this.owlConnection.getModel().getIndividual(mdElementURI1)),
					new Vertex(this.owlConnection.getModel().getIndividual(mdElementURI2)));
			if (paths.size() == 0) {
				throw new NoMappingExistsForElementException("No leading path");
			}
			CustomSPARQLQuery rq = SPARQLUtilities.joinSubsequentQueries(getPathQueries(paths.get(0)),
					QueryType.DIMENSION);
			return (new Mapping(rq));
		} catch (Exception ex) {
			throw new NoMappingExistsForElementException(
					"Error generating path mapping. Path between: " + mdElementURI2 + " - " + mdElementURI1,
					ex.getCause());
		}
	}

	@Override
	public Mapping getMappingQueryBetweenTwoElementsByURI(String mdElementURI2, String mdElementURI1)
			throws NoMappingExistsForElementException {

		try {
			DepreciatedOWLGraph owlGraph = new DepreciatedOWLGraph(this.owlConnection, this);
			List<swag.data_handler.Path> paths = owlGraph.getAllPathsBetweenTwoVertices(
					new Vertex(this.owlConnection.getModel().getIndividual(mdElementURI1)),
					new Vertex(this.owlConnection.getModel().getIndividual(mdElementURI2)));
			if (paths.size() == 0) {
				throw new NoMappingExistsForElementException("No leading path");
			}
			CustomSPARQLQuery rq = SPARQLUtilities.joinSubsequentQueries(getPathQueries(paths.get(0)),
					QueryType.DIMENSION);
			return (new Mapping(rq));
		} catch (Exception ex) {
			throw new NoMappingExistsForElementException(
					"Error generating path mapping. Path between: " + mdElementURI2 + " - " + mdElementURI1,
					ex.getCause());
		}
	}

	@Override
	public Map<OntProperty, List<Individual>> getOutMDInPMappedPropsAndValues(Individual ind) {
		Map<OntProperty, List<Individual>> map = new HashMap<OntProperty, List<Individual>>();
		int i = 0;
		for (StmtIterator j = ind.listProperties(); j.hasNext();) {
			Statement s = j.next();
			if (s.getPredicate().getNameSpace().equals(this.owlConnection.getModel().getNsPrefixURI(""))
					&& Constants.P_MAPPED.contains(s.getPredicate().getLocalName())) {
				try {
					OntProperty p = s.getPredicate().as(OntProperty.class);
					map.put(p, new ArrayList<Individual>());
					for (NodeIterator nodeItr = ind.listPropertyValues(p); j.hasNext();) {
						RDFNode node = nodeItr.next();
						map.get(p).add(node.as(Individual.class));
					}
				} catch (Exception ex) {
					System.err.println("problem converting Proprty to OntProperty: " + ex);
					// break;
				}
			}
		}
		return map;
	}

	private void addEdgesToMap(Map<Individual, List<Individual>> map, String clazzStr, String fromStr, String toStr) {

		OntClass clazz = owlConnection.getModel().getOntClass(clazzStr);

		for (ExtendedIterator<? extends org.apache.jena.ontology.OntResource> i = clazz.listInstances(); i.hasNext();) {

			org.apache.jena.ontology.Individual indiv = (Individual) i.next();

			org.apache.jena.ontology.Individual from = (Individual) owlConnection.getPropertyValueEnc(indiv,
					owlConnection.getModel().getOntProperty(fromStr));

			org.apache.jena.ontology.Individual to = (Individual) owlConnection.getPropertyValueEnc(indiv,
					owlConnection.getModel().getOntProperty(toStr));

			if (from != null && to != null && from.equals(indiv)) {
				List<org.apache.jena.ontology.Individual> list = map.get(indiv);
				if (list == null) {
					list = new ArrayList<>();
					map.put(indiv, list);
				}
				list.add(to);
			}
		}
	}

	public Map<Individual, List<Individual>> getOutMDInPMappedPropsAndValuesThatHaveMapping1(Individual ind) {

		Map<Individual, List<Individual>> map = new HashMap<Individual, List<Individual>>();

		addEdgesToMap(map, OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.HAS_LEVEL,
				OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.FACT_OF_LEVEL,
				OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.TO_LEVEL);

		addEdgesToMap(map, OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.HAS_MEASURE,
				OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.FACT_OF_MEASURE,
				OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.TO_MEASURE);

		addEdgesToMap(map, OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.HAS_ATTRIBUTE,
				OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.FROM_LEVEL,
				OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.TO_ATTRIBUTE);

		addEdgesToMap(map, OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.HIEREARCHY_STEP,
				OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.CHILD_LEVEL,
				OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.PARENT_LEVEL);

		return map;
	}

	public boolean checkIfPropHasMapping(Individual indiv) {

		Individual mappingIndiv = (Individual) this.owlConnection.getPropertyValueEnc(indiv, this.owlConnection
				.getModel().getProperty(OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.propMapping));

		if (mappingIndiv != null) {
			String mapping = this.owlConnection
					.getPropertyValueEnc(mappingIndiv,
							this.owlConnection.getModel().getProperty(
									OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.hasString))
					.toString();

			if (!mapping.equals("") && mapping != null) {
				return true;
			}
		}

		return false;
	}

	@Override
	public Map<OntProperty, List<Individual>> getOutMDInPMappedPropsAndValuesThatHaveMapping(Individual ind) {
		Map<OntProperty, List<Individual>> map = new HashMap<OntProperty, List<Individual>>();
		int i = 0;
		for (StmtIterator j = ind.listProperties(); j.hasNext();) {
			Statement s = j.next();
			if (s.getPredicate().getNameSpace().equals(OWLConnectionFactory.getSMDNamespace(owlConnection))
					&& Constants.P_MAPPED.contains(s.getPredicate().getLocalName()) && checkIfMDPropertyHasMapping(s)) {
				try {
					OntProperty p = s.getPredicate().as(OntProperty.class);
					if (!map.containsKey(p)) {
						map.put(p, new ArrayList<Individual>());
					}
					map.get(p).add(s.getObject().as(Individual.class));
				} catch (Exception ex) {
					System.err.println("problem converting Proprty to OntProperty: " + ex);
					// break;
				}
			}
			// System.out.println(i++);
		}
		return map;
	}

	@Override
	public boolean checkIfMDPropertyHasMapping(Statement stmt) {
		Individual sub = stmt.getSubject().as(Individual.class);
		Individual obj = stmt.getObject().as(Individual.class);

		ExtendedIterator instances = this.owlConnection
				.getClassByName(OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.reifiedMapping)
				.listInstances();
		while (instances.hasNext()) {
			Individual thisInstance = (Individual) instances.next();

			Individual from = this.owlConnection
					.getPropertyValueEnc(thisInstance,
							this.owlConnection.getModel().getProperty(
									OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.fromStr))
					.as(Individual.class);
			Individual to = this.owlConnection
					.getPropertyValueEnc(thisInstance,
							this.owlConnection.getModel()
									.getProperty(OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.toStr))
					.as(Individual.class);

			if (sub.equals(from) && obj.equals(to)) {
				String mapping = this.owlConnection
						.getPropertyValueEnc(thisInstance,
								this.owlConnection.getModel().getProperty(
										OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.propMapping))
						.toString();
				// System.out.println("here it is: " + from + " " + mapping + "
				// " + to);
				if (!mapping.equals("")) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public Mapping generateMappingByElementURI(Class<?> typ, String varName, String varType)
			throws NoSuchElementExistsException, NoMappingExistsForElementException {

		String mapStr = "";

		if (typ.equals(Level.class)) {
			mapStr = "SELECT ?" + varName + " WHERE {" + "" + "?" + varName
					+ "<http://purl.org/qb4olap/cubes#memberOf> " + "<" + varType + ">" + "}";
		} else {

			if (typ.equals(Fact.class)) {
				mapStr = "SELECT ?" + varName + " WHERE {" + "" + "?" + varName
						+ " <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> "
						+ " <http://purl.org/linked-data/cube#Observation>" + "}";
			} else {
				if (typ.equals(Measure.class)) {
					mapStr = "SELECT ?" + varName + " WHERE { }";
				} else {
					if (typ.equals(Descriptor.class)) {
						mapStr = "SELECT ?" + varName + " WHERE { }";
					} else {
						mapStr = null;
					}
				}
			}
		}
		return mapStr != null ? new Mapping(new CustomSPARQLQuery(mapStr)) : new Mapping();
	}

	@Override
	public Mapping generateMappingByElementURIForMeasure(Class<?> typ, String varName, String dataType)
			throws NoSuchElementExistsException, NoMappingExistsForElementException {

		String mapStr = "";

		if (typ.equals(Measure.class)) {
			mapStr = "SELECT " + (StringUtils.isNotEmpty(dataType) ? "<" + dataType + ">" : "") + "(?" + varName
					+ ") WHERE { }";
		}

		return mapStr != null ? new Mapping(new CustomSPARQLQuery(mapStr)) : new Mapping();
	}

	@Override
	public Mapping generateMappingByRelationURI(Class<?> typ, String fromVarName, String toVarName, String varsProp)
			throws NoSuchElementExistsException, NoMappingExistsForElementException {

		String mapStr = "";

		if (typ.equals(QB4OHierarchyStep.class) || typ.equals(HasDescriptor.class) || typ.equals(HasLevel.class)) {
			mapStr = "SELECT ?" + fromVarName + " ?" + toVarName + " WHERE {" + "" + "?" + fromVarName + " <" + varsProp
					+ "> " + "?" + toVarName + "}";
		} else {

			if (typ.equals(HasMeasure.class)) {
				mapStr = "SELECT ?" + fromVarName + " ?" + toVarName + " WHERE {" + "" + "?" + fromVarName
						+ "<http://purl.org/linked-data/sdmx/2009/measure#obsValue> " + "?" + toVarName + "}";
			} else {
				mapStr = null;
			}
		}

		return mapStr != null ? new Mapping(new CustomSPARQLQuery(mapStr)) : new Mapping();
	}

}
