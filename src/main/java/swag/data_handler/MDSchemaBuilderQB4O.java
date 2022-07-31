package swag.data_handler;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.RDFS;

import swag.analysis_graphs.execution_engine.NoMappingExistsForElementException;
import swag.analysis_graphs.execution_engine.NoSuchElementExistsException;
import swag.analysis_graphs.execution_engine.analysis_situations.MeasureDerived;
import swag.graph.Graph;
import swag.md_elements.Descriptor;
import swag.md_elements.Dimension;
import swag.md_elements.Fact;
import swag.md_elements.HasDescriptor;
import swag.md_elements.HasLevel;
import swag.md_elements.HasMeasure;
import swag.md_elements.Level;
import swag.md_elements.MDElement;
import swag.md_elements.MDRelation;
import swag.md_elements.MDSchema;
import swag.md_elements.MDSchemaGraphQB4OLAP;
import swag.md_elements.MDSchemaType;
import swag.md_elements.MappableRelationFactory;
import swag.md_elements.Mapping;
import swag.md_elements.Measure;
import swag.md_elements.QB4OHierarchy;
import swag.md_elements.QB4OHierarchyStep;
import swag.sparql_builder.Configuration;

/**
 * 
 * This class offers a method to construct a qb4olap multidimensional schema
 * from an OWL connection.
 * 
 * @author swag
 *
 */
public class MDSchemaBuilderQB4O extends MDSchemaBuilderAbstract {

	public MDSchemaBuilderQB4O() {

	}

	private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(MDSchemaBuilderQB4O.class);

	private OWlConnection conn;

	public OWlConnection initOWLConnection(String path) {

		OWlConnection owlConn = OWLConnectionFactory.createOWLConnectionWithoutReasoning();

		OWLConnectionFactory.appendQB(owlConn, path, Constants.qbFile, Configuration.getInstance().isLocal());
		OWLConnectionFactory.appendQB4O(owlConn, path, Constants.qb4oFile, Configuration.getInstance().isLocal());
		OWLConnectionFactory.appendSMD(owlConn, path, Constants.SMDFile);
		OWLConnectionFactory.appendAG(owlConn, path, Constants.AGFile);

		return owlConn;
	}

	public OWlConnection initiateExecutionEngin(String pathToSourceOntologies, String pathToNewFile, String name)
			throws Exception {

		OWlConnection owlConn = initOWLConnection(pathToSourceOntologies);

		owlConn.readOwlFromFile(pathToSourceOntologies, name);
		return owlConn;
	}

	public static void main(String[] args) throws Exception {

		MDSchemaBuilderQB4O b = new MDSchemaBuilderQB4O();

		b.SetOwlConn(b.initiateExecutionEngin("C:\\Data", "C:\\Data", "eurostat.ttl"));
		MDSchema schema = b.buildMDSchema();
		System.out.println(schema.stringifyGraph());
	}

	public void SetOwlConn(OWlConnection con) {
		this.conn = con;
	}

	/**
	 * 
	 * Creates an instance of {@code MDSchemaBuilder}.
	 * 
	 * @param conn data connection with model containing the MD schema.
	 * 
	 */
	public MDSchemaBuilderQB4O(OWlConnection conn) {
		this.conn = conn;
	}

	@Override
	public MDSchema buildSpecificMDSchema() {
		return buildMDSchema(conn);
	}

	/**
	 * 
	 * Recursively traverses the data MD schema instance starting from the fact, and
	 * builds the corresponding {@code MDSchema} instance.
	 * 
	 * @param owlConnection data connection with model containing the MD schema
	 * @param graph         the MDSchema being built
	 * @param outEdges      the edges outgoing from the current node
	 */
	private static void propagate(OWlConnection owlConnection, Graph<MDElement, MDRelation> graph,
			Set<MDRelation> outEdges) {

		// System.out.println("Now before: ");
		for (MDRelation rel : outEdges) {
			if (graph.addEdge(rel)) {
				// System.out.println("Now rel: " + rel.getIdentifyingName() + "
				// -- to "
				// + rel.getTarget().getIdentifyingName());
				if (graph.addNode(rel.getTarget())) {
					// System.out.println("Now propagating: " +
					// rel.getTarget().getIdentifyingName());
					propagate(owlConnection, graph,
							getOutMDInPMappedPropsAndValuesThatHaveMapping1(owlConnection, rel.getTarget()));
				} else {
					// System.out.println("Not propagated: " + rel.getTarget());
				}
			}
		}
	}

	/**
	 * 
	 * Builds the MD schema from the provided OWL conneciton which should have a
	 * model containing and MD schema (instance of MDSchema).
	 * 
	 * @param owlConnection OWL connection with model containing the MD schema
	 * 
	 * @return the built multidimensional schema, null in case of an exception
	 * 
	 */
	private static MDSchema buildMDSchema(OWlConnection owlConnection) {

		try {

			// Getting the MD schema instance
			Individual mdSchemaInd = null;
			OntClass oc = owlConnection.getModel()
					.getOntClass(OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.mdSchema);
			for (ExtendedIterator<? extends OntResource> ii = oc.listInstances(); ii.hasNext();) {
				mdSchemaInd = (Individual) ii.next();
				break;
			}

			// Getting the SPARQL endpoint of the schema
			String endpointURI = mdSchemaInd
					.getPropertyValue(owlConnection.getModel().getDatatypeProperty(
							OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.hasSPARQLService))
					.toString();

			MappingRepInterface mappRepInterface = new MappingRepImpl(owlConnection);
			OntClass clazz = owlConnection.getModel().getOntClass(Constants.QB4O_DATA_STRUCTURE_DEF);
			Individual ind;

			ExtendedIterator<OntClass> itrrr = owlConnection.getModel().listClasses();

			if (clazz != null) {
				for (ExtendedIterator<? extends org.apache.jena.ontology.OntResource> i = clazz.listInstances(); i
						.hasNext();) {

					// Getting the fact of the MD schema
					ind = (Individual) i.next();

					// TODO last two parameters
					MDSchemaGraphQB4OLAP graph = new MDSchemaGraphQB4OLAP("", "",
							"OWLConnectionFactory.getSMDInstanceNamespace(owlConnection)", endpointURI,
							"http://www.w3.org/2004/02/skos/core#prefLabel", MDSchemaType.QB4OLAP);

					try {
						Fact fact = new Fact(ind.getURI(), ind.getLocalName(), mappRepInterface
								.generateMappingByElementURI(Fact.class, ind.getLocalName(), ind.getURI()),
								ind.getLabel("en"));// getFactByURI(ind.getURI());

						// Starting building the MD schema by adding the fact
						if (graph.addNode(fact)) {
							Set<MDRelation> outEdges = getOutMDInPMappedPropsAndValuesThatHaveMapping1(owlConnection,
									fact);
							propagate(owlConnection, graph, outEdges);
						}

					} catch (Exception ex) {
						logger.error("Exception building MD schema.", ex);
					}

					graph.injectInitiate();
					//graph.injectIdentifyingNames();
					// System.out.println(graph.stringifyGraph());

					graph.stringifyGraphMappings();

					return graph;

				}
			}
		} catch (Exception ex) {
			logger.error("Exception building MD schema.", ex);
			return null;
		}
		return null;
	}

	/**
	 * 
	 * Gets a set of outgoing edges from a specific nodes. This means the MD
	 * relationships of the current MD element being visited.
	 * 
	 * @param owlConnection OWL connection with model containing the MD schema
	 * @param elem          current MD element being visited
	 * 
	 * @return the outgoing relationships from {@param elem}
	 */
	private static Set<MDRelation> getOutMDInPMappedPropsAndValuesThatHaveMapping1(OWlConnection owlConnection,
			MDElement elem) {

		Individual ind = owlConnection.getModel().getIndividual(elem.getURI());

		Set<MDRelation> set = new HashSet<>();

		if (elem instanceof Fact) {
			// order of calls matters measure -> derived measure -> aggregated
			// measure.
			addFactOutEdgesToSet(owlConnection, set, ind, elem);
			handleDerivedMeasures(ind, owlConnection, set, elem);
			handleAggregatedMeasures(ind, owlConnection, set, elem);
		} else {
			if (elem instanceof Level) {
				addEdgesToSet(owlConnection, set, ind, elem, Constants.QB4O_HIERARCHY_STEP,
						Constants.QB4O_HIERARCHY_STEP, Constants.QB4O_CHILD_LEVEL, Constants.QB4O_PARENT_LEVEL);
				addHasAttributeEdgesToSet(owlConnection, set, ind, elem);
				addInverseHasLevelEdgesToSet(owlConnection, set, ind, elem);
			} else {
				if (elem instanceof QB4OHierarchy) {
					addInDimensionEdgesToSet(owlConnection, set, ind, elem);
				} else {
					if (elem instanceof Dimension) {
						addHasHierarchyEdgesToSet(owlConnection, set, ind, elem);
					}
				}
			}
		}
		return set;
	}

	private static void addInverseHasLevelEdgesToSet(OWlConnection owlConnection, Set<MDRelation> set,
			Individual sourceInd, MDElement levelElem) {

		OntModel model = owlConnection.getModel();
		MappingRepInterface mappRepInterface = new MappingRepImpl(owlConnection);
		OntClass hier = model.getOntClass(Constants.QB4O_HIERARCHY);

		if (hier != null) {
			ExtendedIterator<? extends org.apache.jena.ontology.OntResource> itr1 = hier.listInstances();

			while (itr1.hasNext()) {
				Object res = itr1.next();

				if (res != null) {

					Individual hierInd = (Individual) res;
					NodeIterator itr2 = hierInd.listPropertyValues(model.getProperty(Constants.QB4O_HAS_LEVEL));

					while (itr2.hasNext()) {
						RDFNode hasLevelObjectNode = itr2.next();
						if (hasLevelObjectNode != null) {
							Individual hasLevelObjectNodeIndiv = hasLevelObjectNode.as(Individual.class);

							if (hasLevelObjectNodeIndiv.equals(sourceInd)) {
								try {
									MDRelation rel;
									MDElement hierarchyTargetElem;
									MDElement relationElem;
									Mapping map = new Mapping();
									Mapping relaMapping = new Mapping();
									hierarchyTargetElem = new QB4OHierarchy(hierInd.getURI(), hierInd.getLocalName(),
											map, hierInd.getLabel("en"));
									relationElem = new MDElement(hasLevelObjectNodeIndiv.getURI(),
											hasLevelObjectNodeIndiv.getLocalName(), relaMapping,
											hasLevelObjectNodeIndiv.getLabel("en"));
									rel = MappableRelationFactory.createMappableRelation(Constants.QB4O_HAS_LEVEL,
											relationElem, levelElem, hierarchyTargetElem);
									set.add(rel);

								} catch (Exception ex) {
									logger.error("exception:", ex);
									continue;
								}
							}
						}
					}
				}
			}
		}
	}

	private static void addHasAttributeEdgesToSet(OWlConnection owlConnection, Set<MDRelation> set,
			Individual sourceInd, MDElement elem) {

		MappingRepInterface mappRepInterface = new MappingRepImpl(owlConnection);

		NodeIterator itr = sourceInd
				.listPropertyValues(owlConnection.getModel().getObjectProperty(Constants.QB4O_HAS_ATTRIBUTE));

		while (itr.hasNext()) {

			RDFNode node = itr.next();
			Individual indivNode = node.as(Individual.class);

			try {

				MDRelation rel;
				MDElement mdElem;
				MDElement elem1;

				Mapping map = mappRepInterface.generateMappingByElementURI(Descriptor.class, indivNode.getLocalName(),
						indivNode.getURI());
				Mapping relaMapping = mappRepInterface.generateMappingByRelationURI(HasDescriptor.class,
						sourceInd.getLocalName(), indivNode.getLocalName(), indivNode.getURI());
				mdElem = new Descriptor(indivNode.getURI(), indivNode.getLocalName(), map, indivNode.getLabel("en"));
				elem1 = new MDElement(indivNode.getURI(), indivNode.getLocalName(), relaMapping,
						indivNode.getLabel("en"));
				rel = MappableRelationFactory.createMappableRelation(Constants.HAS_ATTRIBUTE, elem1, elem, mdElem);
				set.add(rel);

			} catch (NoMappingExistsForElementException | NoSuchElementExistsException ex) {

				logger.error("exception:", ex);
				continue;
			}
		}
	}

	private static void addHasHierarchyEdgesToSet(OWlConnection owlConnection, Set<MDRelation> set,
			Individual sourceInd, MDElement elem) {

		MappingRepInterface mappRepInterface = new MappingRepImpl(owlConnection);

		NodeIterator itr1 = sourceInd
				.listPropertyValues(owlConnection.getModel().getObjectProperty(Constants.QB4O_HAS_HIERARCHY));

		while (itr1.hasNext()) {

			RDFNode node = itr1.next();
			Individual indivNode = node.as(Individual.class);

			try {

				MDRelation rel;
				MDElement mdElem;
				MDElement elem1;

				Mapping map = new Mapping();
				Mapping relaMapping = new Mapping();
				mdElem = new QB4OHierarchy(indivNode.getURI(), indivNode.getLocalName(), map, indivNode.getLabel("en"));
				elem1 = new MDElement(indivNode.getURI(), indivNode.getLocalName(), relaMapping,
						indivNode.getLabel("en"));
				rel = MappableRelationFactory.createMappableRelation(Constants.QB4O_HAS_HIERARCHY, elem1, elem, mdElem);
				set.add(rel);

			} catch (Exception ex) {

				logger.error("exception:", ex);
				continue;
			}
		}
	}

	private static void addFactOutEdgesToSet(OWlConnection owlConnection, Set<MDRelation> set, Individual sourceInd,
			MDElement elem) {

		MappingRepInterface mappRepInterface = new MappingRepImpl(owlConnection);

		NodeIterator itr = sourceInd
				.listPropertyValues(owlConnection.getModel().getObjectProperty(Constants.QB4O_COMPONENT));

		while (itr.hasNext()) {

			RDFNode node = itr.next();
			Individual indiv1 = node.as(Individual.class);

			RDFNode levelNode = owlConnection.getPropertyValueEnc(indiv1,
					owlConnection.getModel().getObjectProperty(Constants.QB4O_LEVEL));
			if (levelNode != null) {

				Individual indivNode = levelNode.as(Individual.class);

				MDRelation rel;
				MDElement mdElem;
				MDElement elem1;

				try {

					Mapping map = mappRepInterface.generateMappingByElementURI(Level.class, indivNode.getLocalName(),
							indivNode.getURI());
					Mapping relaMapping = mappRepInterface.generateMappingByRelationURI(HasLevel.class,
							sourceInd.getLocalName(), indivNode.getLocalName(), indivNode.getURI());
					mdElem = new Level(indivNode.getURI(), indivNode.getLocalName(), map, indivNode.getLabel("en"));
					elem1 = new MDElement(indivNode.getURI(), indivNode.getLocalName(), relaMapping,
							indivNode.getLabel("en"));
					rel = MappableRelationFactory.createMappableRelation(Constants.HAS_LEVEL, elem1, elem, mdElem);
					set.add(rel);

				} catch (NoMappingExistsForElementException | NoSuchElementExistsException ex) {

					logger.error("exception:", ex);
					continue;
				}
			}

			RDFNode measureNode = owlConnection.getPropertyValueEnc(indiv1,
					owlConnection.getModel().getObjectProperty(Constants.QB_MEASURE));

			if (measureNode != null && DataHandlerUtils.isOfType(measureNode.as(Individual.class),
					owlConnection.getModel().getOntClass(Constants.QB_MEASURE_PROPERTY))) {

				Individual indivNode = measureNode.as(Individual.class);

				MDRelation rel;
				MDElement mdElem;
				MDElement elem1;

				try {

					RDFNode msrDataType = indivNode.getPropertyValue(RDFS.range);

					String dataType = StringUtils.EMPTY;
					if (msrDataType != null) {
						dataType = msrDataType.as(Individual.class).getURI();
					}

					Mapping map = mappRepInterface.generateMappingByElementURI(Measure.class, indivNode.getLocalName(),
							dataType);

					Mapping relaMapping = mappRepInterface.generateMappingByRelationURI(HasMeasure.class,
							sourceInd.getLocalName(), indivNode.getLocalName(), indivNode.getURI());

					mdElem = new MeasureDerived(indivNode.getURI(), indivNode.getLocalName(), "", "",
							indivNode.getLabel("en"), map, dataType);
					elem1 = new MDElement(indivNode.getURI(), indivNode.getLocalName(), relaMapping,
							indivNode.getLabel("en"));
					rel = MappableRelationFactory.createMappableRelation(Constants.HAS_MEASURE, elem1, elem, mdElem);
					set.add(rel);

				} catch (NoMappingExistsForElementException | NoSuchElementExistsException ex) {

					logger.error("exception:", ex);
					continue;
				}
			}
		}
	}

	/**
	 * 
	 * addInDimensionEdgesToSet
	 * 
	 * @param owlConnection
	 * @param set
	 * @param sourceInd
	 * @param fromElem
	 * 
	 */
	private static void addInDimensionEdgesToSet(OWlConnection owlConnection, Set<MDRelation> set, Individual sourceInd,
			MDElement fromElem) {

		Individual dimIndiv = null;

		if (sourceInd != null
				&& sourceInd.hasProperty(owlConnection.getModel().getObjectProperty(Constants.QB4O_IN_DIMENSION))) {

			NodeIterator itr = owlConnection.getModel().listObjectsOfProperty(sourceInd,
					owlConnection.getModel().getObjectProperty(Constants.QB4O_IN_DIMENSION));

			while (itr.hasNext()) {
				try {
					dimIndiv = itr.next().as(Individual.class);

					MDRelation rel;
					MDElement toElem;
					MDElement relElem;

					toElem = new Dimension(dimIndiv.getURI(), dimIndiv.getLocalName(), dimIndiv.getLabel("en"));
					relElem = new MDElement("", "", new Mapping(), "");
					rel = MappableRelationFactory.createMappableRelation(Constants.QB4O_IN_DIMENSION, relElem, fromElem,
							toElem);
					set.add(rel);

				} catch (Exception ex) {
					logger.error("Cannot read a dimension of the hierarchy " + sourceInd.getURI(), ex);
					continue;
				}
			}
		}
	}

	/**
	 * 
	 * addEdgesToSet
	 * 
	 * @param owlConnection
	 * @param set
	 * @param sourceInd     the individual for we which we are getting related
	 *                      individuals
	 * @param elem          the MD element that was built on the source individual
	 *                      {@code sourceInd}
	 * @param constantStr
	 * @param clazzStr      the class we are generating instances for
	 * @param fromStr       the string used for the name of the property that
	 *                      connects the edge individual with its from
	 * @param toStr         the string used for the name of the property that
	 *                      connects the edge individual with its to
	 */
	private static void addEdgesToSet(OWlConnection owlConnection, Set<MDRelation> set, Individual sourceInd,
			MDElement elem, String constantStr, String clazzStr, String fromStr, String toStr) {

		MappingRepInterface mappRepInterface = new MappingRepImpl(owlConnection);

		OntClass clazz = owlConnection.getModel().getOntClass(clazzStr);

		for (ExtendedIterator<? extends org.apache.jena.ontology.OntResource> i = clazz.listInstances(); i.hasNext();) {

			org.apache.jena.ontology.Individual indiv = (Individual) i.next();

			org.apache.jena.ontology.Individual from = fromStr == null || owlConnection.getPropertyValueEnc(indiv,
					owlConnection.getModel().getOntProperty(fromStr)) == null ? null
							: owlConnection.getPropertyValueEnc(indiv, owlConnection.getModel().getOntProperty(fromStr))
									.as(Individual.class);

			org.apache.jena.ontology.Individual to = toStr == null
					|| owlConnection.getPropertyValueEnc(indiv, owlConnection.getModel().getOntProperty(toStr)) == null
							? null
							: owlConnection.getPropertyValueEnc(indiv, owlConnection.getModel().getOntProperty(toStr))
									.as(Individual.class);

			if ((from != null || to != null) && from.equals(sourceInd)) {

				MDRelation rel;
				Mapping map;
				Mapping relaMapping;
				MDElement mdElem;
				MDElement elem1;
				String str = OWLConnectionFactory.getSMDNamespace(owlConnection);

				try {
					if (constantStr.equals(Constants.QB4O_HIERARCHY_STEP)) {

						RDFNode rollUpPropNode = owlConnection.getPropertyValueEnc(indiv,
								owlConnection.getModel().getProperty(Constants.QB4O_ROLLUP));

						Individual rollUpPropIndiv = null;
						if (rollUpPropNode != null) {
							rollUpPropIndiv = rollUpPropNode.as(Individual.class);
						}

						map = mappRepInterface.generateMappingByElementURI(Level.class, to.getLocalName(), to.getURI());
						relaMapping = mappRepInterface.generateMappingByRelationURI(QB4OHierarchyStep.class,
								sourceInd.getLocalName(), to.getLocalName(), rollUpPropIndiv.getURI());
						mdElem = new Level(to.getURI(), to.getLocalName(), map, to.getLabel("en"));

						elem1 = new MDElement(rollUpPropIndiv.getURI(), rollUpPropIndiv.getLocalName(), relaMapping,
								rollUpPropIndiv.getLabel("en"));
						rel = MappableRelationFactory.createMappableRelation(Constants.HIEREARCHY_STEP, elem1, elem,
								mdElem);
						set.add(rel);
						continue;
					}
				} catch (NoMappingExistsForElementException | NoSuchElementExistsException ex) {
					logger.error("exception:", ex);
					continue;
				}
			}
		}
	}

	/**
	 * Read derived measures defined for the schema
	 * 
	 * @param owlConnection the OWL connection
	 * @param set           the set of MD relations being built
	 * @param sourceInd     the fact individual
	 * @param elem
	 */
	public static final void handleDerivedMeasures(Individual sourceInd, OWlConnection owlConnection,
			Set<MDRelation> set, MDElement elem) {

		NodeIterator itr = sourceInd
				.listPropertyValues(owlConnection.getModel().getObjectProperty(Constants.QB4O_COMPONENT));
		Set<Individual> msrIndivs = new HashSet<>();

		while (itr.hasNext()) {
			RDFNode node = itr.next();
			Individual indiv1 = node.as(Individual.class);
			Individual measureNode = owlConnection.getPropertyValueEncAsIndividual(indiv1,
					owlConnection.getModel().getObjectProperty(Constants.QB_MEASURE));

			if (measureNode != null && !DataHandlerUtils.isOfType(measureNode,
					owlConnection.getModel().getOntClass(Constants.QB_MEASURE_PROPERTY))) {
				msrIndivs.add(measureNode);
			}
		}
		handleDerivedMeasures(msrIndivs, owlConnection, set, elem);
	}

	/**
	 * Read derived measures defined for the schema
	 * 
	 * @param owlConnection the OWL connection
	 * @param set           the set of MD relations being built
	 * @param sourceInd     the fact individual
	 * @param elem
	 */
	public static final void handleAggregatedMeasures(Individual sourceInd, OWlConnection owlConnection,
			Set<MDRelation> set, MDElement elem) {

		NodeIterator itr = sourceInd
				.listPropertyValues(owlConnection.getModel().getObjectProperty(Constants.QB4O_COMPONENT));
		Set<Individual> msrIndivs = new HashSet<>();

		while (itr.hasNext()) {
			RDFNode node = itr.next();
			Individual indiv1 = node.as(Individual.class);
			Individual measureNode = owlConnection.getPropertyValueEncAsIndividual(indiv1,
					owlConnection.getModel().getObjectProperty(Constants.QB_MEASURE));

			if (measureNode != null && !DataHandlerUtils.isOfType(measureNode,
					owlConnection.getModel().getOntClass(Constants.QB_MEASURE_PROPERTY))) {
				msrIndivs.add(measureNode);
			}
		}
		handleAggregatedMeasures(msrIndivs, owlConnection, set, elem);
	}

}
