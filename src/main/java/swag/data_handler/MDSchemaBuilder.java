package swag.data_handler;

import java.util.HashSet;
import java.util.Set;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.util.iterator.ExtendedIterator;

import swag.analysis_graphs.execution_engine.NoMappingExistsForElementException;
import swag.analysis_graphs.execution_engine.NoSuchElementExistsException;
import swag.analysis_graphs.execution_engine.analysis_situations.MeasureDerived;
import swag.graph.Graph;
import swag.md_elements.Descriptor;
import swag.md_elements.Dimension;
import swag.md_elements.Fact;
import swag.md_elements.Level;
import swag.md_elements.MDElement;
import swag.md_elements.MDRelation;
import swag.md_elements.MDSchema;
import swag.md_elements.MDSchemaGraphSMD;
import swag.md_elements.MDSchemaType;
import swag.md_elements.MappableRelationFactory;
import swag.md_elements.Mapping;
import swag.md_elements.QB4OHierarchy;

/**
 * 
 * This class offers a method to construct a multidimensional schema from an OWL connection.
 * 
 * @author swag
 *
 */
public class MDSchemaBuilder extends MDSchemaBuilderAbstract {

  private static final org.apache.log4j.Logger logger =
      org.apache.log4j.Logger.getLogger(MDSchemaBuilder.class);

  private OWlConnection conn;

  /**
   * 
   * Creates an instance of {@code MDSchemaBuilder}.
   * 
   * @param conn data connection with model containing the MD schema.
   * 
   */
  public MDSchemaBuilder(OWlConnection conn) {
    this.conn = conn;
  }

  @Override
  public MDSchema buildSpecificMDSchema() {
    return buildMDSchema(conn);
  }

  /**
   * 
   * Recursively traverses the data MD schema instance starting from the fact, and builds the
   * corresponding {@code MDSchema} instance.
   * 
   * @param owlConnection data connection with model containing the MD schema
   * @param graph the MDSchema being built
   * @param outEdges the edges outgoing from the current node
   */
  private static void propagate(OWlConnection owlConnection, Graph<MDElement, MDRelation> graph,
      Set<MDRelation> outEdges) {

    for (MDRelation rel : outEdges) {
      graph.addEdge(rel);

      if (graph.addNode(rel.getTarget())) {
        propagate(owlConnection, graph,
            getOutMDInPMappedPropsAndValuesThatHaveMapping1(owlConnection, rel.getTarget()));
      }
    }
  }

  /**
   * 
   * Builds the MD schema from the provided OWL conneciton which should have a model containing and
   * MD schema (instance of MDSchema).
   * 
   * @param conn OWL connection with model containing the MD schema
   * 
   * @return the built multidimensional schema, null in case of an exception
   * 
   */
  private static MDSchema buildMDSchema(OWlConnection owlConnection) {

    logger.info("Reading MD schema...");
    try {

      // Getting the MD schema instance
      Individual mdSchemaInd = null;
      OntClass oc = owlConnection.getModel()
          .getOntClass(OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.mdSchema);
      for (ExtendedIterator<? extends OntResource> ii = oc.listInstances(); ii.hasNext();) {
        mdSchemaInd = (Individual) ii.next();
        break;
      }

      logger.info("Building MD schema " + mdSchemaInd.getURI());
      // Getting the SPARQL endpoint of the schema
      String endpointURI = mdSchemaInd
          .getPropertyValue(owlConnection.getModel().getDatatypeProperty(
              OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.hasSPARQLService))
          .toString();

      MappingRepInterface mappRepInterface = new MappingRepImpl(owlConnection);
      OntClass clazz = owlConnection.getModel()
          .getOntClass(OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.fact);
      Individual ind;

      if (clazz != null) {
        for (ExtendedIterator<? extends org.apache.jena.ontology.OntResource> i =
            clazz.listInstances(); i.hasNext();) {

          // Getting the fact of the MD schema
          ind = (Individual) i.next();
          if (mdSchemaInd.hasProperty(
              owlConnection.getModel().getDatatypeProperty(
                  OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.HAS_SCHEMA_FACT),
              ind)) {

            MDSchemaGraphSMD graph = new MDSchemaGraphSMD("", "",
                OWLConnectionFactory.getSMDInstanceNamespace(owlConnection), endpointURI,
                "http://www.w3.org/2000/01/rdf-schema#label", MDSchemaType.SMD);

            try {
              Fact fact = new Fact(ind.getURI(), ind.getLocalName(),
                  mappRepInterface.getMappingByElementURI(ind.getURI()), ind.getLabel("en"));// getFactByURI(ind.getURI());

              // Starting building the MD schema by adding the fact
              if (graph.addNode(fact)) {
                Set<MDRelation> outEdges =
                    getOutMDInPMappedPropsAndValuesThatHaveMapping1(owlConnection, fact);

                propagate(owlConnection, graph, outEdges);
                handleDerivedMeasures(ind, owlConnection, fact, graph, outEdges);
                //handleAggregatedMeasures(ind, owlConnection, fact, graph, outEdges);
                buildAggMeasures(graph, owlConnection.getModel());

              }
            } catch (Exception ex) {
              logger.error("Exception building MD schema.", ex);
            }



            return graph;
          }
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
   * Gets a set of outgoing edges from a specific nodes. This means the MD relationships of the
   * current MD element being visited.
   * 
   * @param owlConnection OWL connection with model containing the MD schema
   * @param elem current MD element being visited
   * 
   * @return the outgoing relationships from {@param elem}
   */
  private static Set<MDRelation> getOutMDInPMappedPropsAndValuesThatHaveMapping1(
      OWlConnection owlConnection, MDElement elem) {

    Individual ind = owlConnection.getModel().getIndividual(elem.getURI());

    Set<MDRelation> set = new HashSet<>();

    addEdgesToSet(owlConnection, set, ind, elem, Constants.HAS_LEVEL,
        OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.HAS_LEVEL,
        OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.FACT_OF_LEVEL,
        OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.TO_LEVEL);

    addEdgesToSet(owlConnection, set, ind, elem, Constants.HAS_MEASURE,
        OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.HAS_MEASURE,
        OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.FACT_OF_MEASURE,
        OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.TO_MEASURE);

    addEdgesToSet(owlConnection, set, ind, elem, Constants.HAS_ATTRIBUTE,
        OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.HAS_ATTRIBUTE,
        OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.FROM_LEVEL,
        OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.TO_ATTRIBUTE);

    addEdgesToSet(owlConnection, set, ind, elem, Constants.HIEREARCHY_STEP,
        OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.HIEREARCHY_STEP,
        OWLConnectionFactory.getQB4ONamespace(owlConnection) + Constants.CHILD_LEVEL,
        OWLConnectionFactory.getQB4ONamespace(owlConnection) + Constants.PARENT_LEVEL);

    addInDimensionEdgesToSet(owlConnection, set, ind, elem, Constants.IN_DIMENSION,
        OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.IN_DIMENSION);

    addInHierarchyEdgesToSet(owlConnection, set, ind, elem, Constants.IN_HIERARCHY,
        OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.IN_HIERARCHY);

    return set;
  }

  /**
   * 
   * checkIfPropHasMapping
   * 
   * @param owlConnection
   * @param indiv
   * 
   * @return
   * 
   */
  private static boolean checkIfPropHasMapping(OWlConnection owlConnection, Individual indiv) {

    Individual mappingIndiv =
        (Individual) owlConnection.getPropertyValueEnc(indiv, owlConnection.getModel().getProperty(
            OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.propMapping));

    if (mappingIndiv != null) {
      String mapping = owlConnection
          .getPropertyValueEnc(mappingIndiv,
              owlConnection.getModel().getProperty(
                  OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.hasString))
          .toString();

      if (!mapping.equals("") && mapping != null) {
        return true;
      }
    }

    return false;
  }

  /**
   * 
   * addInHierarchyEdgesToSet
   * 
   * @param owlConnection
   * @param set
   * @param sourceInd
   * @param elem
   * @param constantStr
   * @param clazzStr
   * 
   */
  private static void addInHierarchyEdgesToSet(OWlConnection owlConnection, Set<MDRelation> set,
      Individual sourceInd, MDElement elem, String constantStr, String clazzStr) {

    Individual dimIndiv = null;
    String inDimURIStr = OWLConnectionFactory.getSMDNamespace(owlConnection) + constantStr;
    if (sourceInd != null
        && sourceInd.hasProperty(owlConnection.getModel().getObjectProperty(inDimURIStr))) {

      try {
        dimIndiv = owlConnection
            .getPropertyValueEnc(sourceInd, owlConnection.getModel().getObjectProperty(inDimURIStr))
            .as(Individual.class);

        MDRelation rel;
        MDElement mdElem;
        MDElement elem1;

        mdElem = new QB4OHierarchy(dimIndiv.getURI(), dimIndiv.getLocalName(), new Mapping(),
            dimIndiv.getLabel("en"));
        elem1 = new MDElement("", "", new Mapping(), "");
        rel = MappableRelationFactory.createMappableRelation(constantStr, elem1, elem, mdElem);

        set.add(rel);

        Individual dimInd = owlConnection.getPropertyValueEncAsIndividual(dimIndiv,
            owlConnection.getModel()
                .getObjectProperty(OWLConnectionFactory.getSMDNamespace(owlConnection)
                    + Constants.HIERARCHY_IN_DIMENSION));

        // Adding connection between dimension and hierarchy
        if (dimInd != null) {

          Dimension dim =
              new Dimension(dimInd.getURI(), dimInd.getNameSpace(), dimIndiv.getLabel("en"));

          MDRelation relA;
          MDElement elem1A;

          elem1A = new MDElement("", "", new Mapping(), "");
          relA = MappableRelationFactory.createMappableRelation(Constants.QB4O_IN_DIMENSION, elem1A,
              mdElem, dim);

          set.add(relA);
        }



      } catch (Exception ex) {
        return;
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
   * @param elem
   * @param constantStr
   * @param clazzStr
   * 
   */
  private static void addInDimensionEdgesToSet(OWlConnection owlConnection, Set<MDRelation> set,
      Individual sourceInd, MDElement elem, String constantStr, String clazzStr) {

    Individual dimIndiv = null;
    String inDimURIStr = OWLConnectionFactory.getSMDNamespace(owlConnection) + constantStr;
    if (sourceInd != null
        && sourceInd.hasProperty(owlConnection.getModel().getObjectProperty(inDimURIStr))) {

      try {
        dimIndiv = owlConnection
            .getPropertyValueEnc(sourceInd, owlConnection.getModel().getObjectProperty(inDimURIStr))
            .as(Individual.class);

        MDRelation rel;
        MDElement mdElem;
        MDElement elem1;

        mdElem = new Dimension(dimIndiv.getURI(), dimIndiv.getLocalName(), dimIndiv.getLabel("en"));
        elem1 = new MDElement("", "", new Mapping(), "");
        rel = MappableRelationFactory.createMappableRelation(constantStr, elem1, elem, mdElem);
        set.add(rel);

      } catch (Exception ex) {
        return;
      }
    }
  }

  /**
   * 
   * addEdgesToSet
   * 
   * @param owlConnection
   * @param set
   * @param sourceInd
   * @param elem
   * @param constantStr
   * @param clazzStr
   * @param fromStr
   * @param toStr
   */
  private static void addEdgesToSet(OWlConnection owlConnection, Set<MDRelation> set,
      Individual sourceInd, MDElement elem, String constantStr, String clazzStr, String fromStr,
      String toStr) {

    MappingRepInterface mappRepInterface = new MappingRepImpl(owlConnection);
    OntClass clazz = owlConnection.getModel().getOntClass(clazzStr);

    for (ExtendedIterator<? extends org.apache.jena.ontology.OntResource> i =
        clazz.listInstances(); i.hasNext();) {

      org.apache.jena.ontology.Individual indiv = (Individual) i.next();
      org.apache.jena.ontology.Individual from =
          owlConnection.getPropertyValueEnc(indiv, owlConnection.getModel().getOntProperty(fromStr))
              .as(Individual.class);
      org.apache.jena.ontology.Individual to =
          owlConnection.getPropertyValueEnc(indiv, owlConnection.getModel().getOntProperty(toStr))
              .as(Individual.class);

      if (from != null && to != null && from.equals(sourceInd)) {

        MDRelation rel;
        Mapping map;
        Mapping relaMapping;
        MDElement mdElem;
        MDElement elem1;
        String str = OWLConnectionFactory.getSMDNamespace(owlConnection);

        try {
          switch (constantStr) {

            case Constants.HAS_LEVEL:

              map = mappRepInterface.getMappingByElementURI(to.getURI());
              relaMapping = mappRepInterface.getMappingByRelationURI(indiv.getURI());
              mdElem = new Level(to.getURI(), to.getLocalName(), map, to.getLabel("en"));
              elem1 = new MDElement(indiv.getURI(), indiv.getLocalName(), relaMapping,
                  indiv.getLabel("en"));
              rel =
                  MappableRelationFactory.createMappableRelation(constantStr, elem1, elem, mdElem);
              set.add(rel);
              break;

            case Constants.HAS_MEASURE:

              if (DataHandlerUtils.isOfType(to, owlConnection.getClassByName(
                  OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.SMD_MEASURE))) {

                map = mappRepInterface.getMappingByElementURI(to.getURI());
                relaMapping = mappRepInterface.getMappingByRelationURI(indiv.getURI());
                mdElem = new MeasureDerived(to.getURI(), to.getLocalName(), "", "",
                    to.getLabel("en"), map);

                elem1 = new MDElement(indiv.getURI(), indiv.getLocalName(), relaMapping,
                    indiv.getLabel("en"));
                rel = MappableRelationFactory.createMappableRelation(constantStr, elem1, elem,
                    mdElem);
                set.add(rel);
              }
              break;

            case Constants.HAS_ATTRIBUTE:
              map = mappRepInterface.getMappingByElementURI(to.getURI());
              relaMapping = mappRepInterface.getMappingByRelationURI(indiv.getURI());
              mdElem = new Descriptor(to.getURI(), to.getLocalName(), map, to.getLabel("en"));
              elem1 = new MDElement(indiv.getURI(), indiv.getLocalName(), relaMapping,
                  indiv.getLabel("en"));
              rel =
                  MappableRelationFactory.createMappableRelation(constantStr, elem1, elem, mdElem);
              set.add(rel);

              break;

            case Constants.HIEREARCHY_STEP:
              map = mappRepInterface.getMappingByElementURI(to.getURI());
              relaMapping = mappRepInterface.getMappingByRelationURI(indiv.getURI());
              mdElem = new Level(to.getURI(), to.getLocalName(), map, to.getLabel("en"));
              elem1 = new MDElement(indiv.getURI(), indiv.getLocalName(), relaMapping,
                  indiv.getLabel("en"));
              rel =
                  MappableRelationFactory.createMappableRelation(constantStr, elem1, elem, mdElem);
              set.add(rel);
              break;
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
   * @param set the set of MD relations being built
   * @param sourceInd the fact individual
   * @param elem
   */
  public static final void handleDerivedMeasures(Individual sourceInd, OWlConnection owlConnection,
      MDElement elem, MDSchema graph, Set<MDRelation> set) {

    ExtendedIterator<Individual> itr =
        owlConnection.getModel().listIndividuals(owlConnection.getModel().getOntClass(
            OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.HAS_MEASURE));

    Set<Individual> msrIndivs = new HashSet<>();

    while (itr.hasNext()) {
      msrIndivs.add(owlConnection.getPropertyValueEncAsIndividual(itr.next(),
          owlConnection.getModel().getProperty(
              OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.TO_MEASURE)));
    }
    handleDerivedMeasures(msrIndivs, owlConnection, set, elem);
    set.stream().forEach(e -> {
      graph.addEdge(e);
      graph.addNode(e.getTarget());
    });
  }

  /**
   * Read aggregated measures defined for the schema
   * 
   * @param owlConnection the OWL connection
   * @param set the set of MD relations being built
   * @param sourceInd the fact individual
   * @param elem
   */
  public static final void handleAggregatedMeasures(Individual sourceInd,
      OWlConnection owlConnection, MDElement elem, MDSchema graph, Set<MDRelation> set) {

    ExtendedIterator<Individual> itr =
        owlConnection.getModel().listIndividuals(owlConnection.getModel().getOntClass(
            OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.HAS_MEASURE));

    Set<Individual> msrIndivs = new HashSet<>();

    while (itr.hasNext()) {
      msrIndivs.add(owlConnection.getPropertyValueEncAsIndividual(itr.next(),
          owlConnection.getModel().getProperty(
              OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.TO_MEASURE)));
    }
    handleAggregatedMeasures(msrIndivs, owlConnection, set, elem);
    set.stream().forEach(e -> {
      graph.addEdge(e);
      graph.addNode(e.getTarget());
    });
  }
}
