package swag.data_handler;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.RDF;

import swag.analysis_graphs.execution_engine.NoMappingExistsForElementException;
import swag.analysis_graphs.execution_engine.NoSuchElementExistsException;
import swag.md_elements.Dimension;
import swag.md_elements.Fact;
import swag.md_elements.Level;
import swag.md_elements.MDSchema;
import swag.md_elements.MDSchemaGraphSMD;
import swag.md_elements.Mapping;
import swag.md_elements.MappingFunctions;
import swag.md_elements.Measure;

public class MDSchemaRepImpl implements MDSchemaRepInterface {

  private static final org.apache.log4j.Logger logger =
      org.apache.log4j.Logger.getLogger(MDSchemaRepImpl.class);

  private OWlConnection owlConnection;
  private MappingRepInterface mappRepInterface;
  private MDSchema graph;

  public MDSchema getGraph() {
    return graph;
  }

  public void setGraph(MDSchemaGraphSMD graph) {
    this.graph = graph;
  }

  public MappingRepInterface getMappRepInterface() {
    return mappRepInterface;
  }

  public void setMappRepInterface(MappingRepInterface mappRepInterface) {
    this.mappRepInterface = mappRepInterface;
  }

  public OWlConnection getOwlConnection() {
    return owlConnection;
  }

  public void setOwlConnection(OWlConnection owlConnection) {
    this.owlConnection = owlConnection;
  }

  public MDSchemaRepImpl(OWlConnection owlConnection, MappingRepInterface mappRepInterface,
      MDSchema graph) {
    super();
    this.owlConnection = owlConnection;
    this.mappRepInterface = mappRepInterface;
    this.graph = graph;
  }

  @Override
  public Dimension getDimensionByURI(String uri) throws NoSuchElementExistsException {

    Individual ind = this.owlConnection.getModel().getIndividual(uri);
    if (ind != null && ind.hasProperty(RDF.type, this.owlConnection.getModel().getOntClass(
        OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.dimension))) {
      return new Dimension(uri, ind.getLocalName(), ind.getLabel("en"));
    } else {
      NoSuchElementExistsException ex = new NoSuchElementExistsException("dimension: " + uri);
      logger.error("Exception: ", ex);
      ex.printStackTrace();
      throw ex;
    }
  }

  @Override
  public Level getLevelByURI(String uri, String factURI)
      throws NoSuchElementExistsException, NoMappingExistsForElementException {

    try {
      Individual ind = this.owlConnection.getModel().getIndividual(uri);
      if (ind != null && ind.hasProperty(RDF.type, this.owlConnection.getModel()
          .getOntClass(OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.level))) {
        Mapping m = MappingFunctions.getPathQuery(graph, factURI, uri);
        return new Level(uri, ind.getLocalName(), m, uri, ind.getLabel("en"));
      } else {
        NoSuchElementExistsException ex = new NoSuchElementExistsException("level: " + uri);
        logger.error("Exception: ", ex);
        ex.printStackTrace();
        throw ex;
      }
    } catch (NoMappingExistsForElementException ex) {
      throw ex;
    }
  }

  @Override
  public Level getLevelByURI(String uri) throws NoSuchElementExistsException {

    Individual ind = this.owlConnection.getModel().getIndividual(uri);
    if (ind != null && ind.hasProperty(RDF.type, this.owlConnection.getModel()
        .getOntClass(OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.level))) {
      Mapping m = new Mapping();
      return new Level(uri, ind.getLocalName(), m, uri, ind.getLabel("en"));
    } else {
      NoSuchElementExistsException ex = new NoSuchElementExistsException("level: " + uri);
      logger.error("Exception: ", ex);
      ex.printStackTrace();
      throw ex;
    }
  }

  @Override
  public Fact getFactByURI(String uri)
      throws NoSuchElementExistsException, NoMappingExistsForElementException {

    try {
      Individual ind = this.owlConnection.getModel().getIndividual(uri);
      if (ind != null && ind.hasProperty(RDF.type, this.owlConnection.getModel()
          .getOntClass(OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.fact))) {
        Mapping m = mappRepInterface.getMappingByElementURI(uri);
        return new Fact(uri, ind.getLocalName(), m, ind.getLabel("en"));
      } else {
        NoSuchElementExistsException ex = new NoSuchElementExistsException("fact: " + uri);
        logger.error("Exception: ", ex);
        ex.printStackTrace();
        throw ex;
      }
    } catch (NoMappingExistsForElementException ex) {
      throw ex;
    }
  }

  @Override
  public Measure getMeasureByURI(String uri) throws NoSuchElementExistsException {

    Individual ind = this.owlConnection.getModel().getIndividual(uri);
    if (ind != null && ind.hasProperty(RDF.type, this.owlConnection.getModel()
        .getOntClass(OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.measure))) {
      Mapping m = new Mapping();
      return new Measure(uri, ind.getLocalName(), m, ind.getLabel("en"));
    } else {
      NoSuchElementExistsException ex = new NoSuchElementExistsException("measure: " + uri);
      logger.error("Exception: ", ex);
      ex.printStackTrace();
      throw ex;
    }
  }

  @Override
  public Measure getMeasureByURI(String uri, String factURI) throws NullPointerException,
      NoSuchElementExistsException, NoMappingExistsForElementException {
    try {
      Individual ind = this.owlConnection.getModel().getIndividual(uri);
      if (ind != null && ind.hasProperty(RDF.type, this.owlConnection.getModel().getOntClass(
          OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.measure))) {
        Mapping m = MappingFunctions.getPathQuery(graph, factURI, uri);
        return new Measure(uri, ind.getLocalName(), m, ind.getLabel("en"));
      } else {
        NoSuchElementExistsException ex = new NoSuchElementExistsException("measure: " + uri);
        logger.error("Exception: ", ex);
        ex.printStackTrace();
        throw ex;
      }
    } catch (NoMappingExistsForElementException ex) {
      throw ex;
    }
  }

  @Override
  public Level getNextRollUpLevel(String nextLevelURI, String currLevelURI, String factURI)
      throws NoSuchElementExistsException, NoMappingExistsForElementException {

    try {
      Individual currLevelIndividual = this.owlConnection.getModel().getIndividual(currLevelURI);
      Individual nextLevelIndividual = this.owlConnection.getModel().getIndividual(nextLevelURI);
      // case the currLevelURI doesn't lead to an individual
      if (currLevelIndividual == null || nextLevelIndividual == null) {
        NoSuchElementExistsException ex = new NoSuchElementExistsException(
            "Current level:" + currLevelURI + " or Next level: " + nextLevelURI);
        logger.error("Exception: ", ex);
        ex.printStackTrace();
        throw ex;
      }
      NodeIterator nodeItr = currLevelIndividual.listPropertyValues(this.owlConnection.getModel()
          .getObjectProperty(OWLConnectionFactory.getSMDNamespace(owlConnection)
              + Constants.rollsUpDirectlyTo));
      String levelURI = "";
      boolean found = false;
      // TODO-soon this gets the first value it encounters
      while (nodeItr.hasNext()) {
        levelURI = nodeItr.next().as(Individual.class).getURI();
        if (levelURI.equals(nextLevelURI))
          found = true;
      }
      // case the iterator is empty
      if (!found) {
        NoSuchElementExistsException ex = new NoSuchElementExistsException(
            "RollUp from: " + currLevelURI + " to: " + nextLevelURI);
        logger.error("Exception: ", ex);
        ex.printStackTrace();
        throw ex;
      }
      Level lvl = getLevelByURI(levelURI, factURI);
      return lvl;
    } catch (NoSuchElementExistsException ex) {
      throw ex;
    } catch (NoMappingExistsForElementException ex) {
      throw (ex);
    }
  }

  @Override
  public Level getFirstNextRollUpLevel(String currLevelURI, String factURI)
      throws NoSuchElementExistsException, NoMappingExistsForElementException {

    try {
      Individual ind = this.owlConnection.getModel().getIndividual(currLevelURI);
      // case the currLevelURI doesn't lead to an individual
      if (ind == null) {
        NoSuchElementExistsException ex =
            new NoSuchElementExistsException("Current level:" + currLevelURI);
        logger.error("Exception: ", ex);
        ex.printStackTrace();
        throw ex;
      }
      NodeIterator nodeItr = ind.listPropertyValues(this.owlConnection.getModel().getObjectProperty(
          OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.rollsUpDirectlyTo));
      String levelURI = "";
      int i = 0;

      while (nodeItr.hasNext()) {
        levelURI = nodeItr.next().as(Individual.class).getURI();
        break;
      }

      // case the iterator is empty
      if (levelURI.equals("")) {
        NoSuchElementExistsException ex =
            new NoSuchElementExistsException("Next Level of:" + currLevelURI);
        logger.error("Exception: ", ex);
        ex.printStackTrace();
        throw ex;
      }
      Level lvl = getLevelByURI(levelURI, factURI);
      return lvl;
    } catch (NoSuchElementExistsException ex) {
      throw ex;
    } catch (NoMappingExistsForElementException ex) {
      throw (ex);
    }
  }

  @Override
  public Level getPreviousRollUpLevel(String previousLevelURI, String currLevelURI, String factURI)
      throws NoSuchElementExistsException, NoMappingExistsForElementException {

    try {
      Individual currLevelIndividual = this.owlConnection.getModel().getIndividual(currLevelURI);
      Individual prevLevelIndividual =
          this.owlConnection.getModel().getIndividual(previousLevelURI);

      // case the currLevelURI doesn't lead to an individual
      if (currLevelIndividual == null || prevLevelIndividual == null) {
        NoSuchElementExistsException ex = new NoSuchElementExistsException(
            "Current level: " + currLevelURI + " or Previous level: " + previousLevelURI);
        logger.error("Exception: ", ex);
        ex.printStackTrace();
        throw ex;
      }

      StmtIterator stmts = this.owlConnection.getModel().listStatements(prevLevelIndividual,
          this.owlConnection.getModel().getObjectProperty(Constants.rollsUpDirectlyTo),
          currLevelIndividual);
      // TODO-soon this gets the first value it encounters
      boolean found = false;
      if (stmts.hasNext()) {
        found = true;
      }
      // case there is no previous level of currLevelURI
      if (found)
        return getLevelByURI(prevLevelIndividual.getURI(), factURI);
      else {
        NoSuchElementExistsException ex = new NoSuchElementExistsException(
            "RollUp from: " + previousLevelURI + " to: " + currLevelURI);
        logger.error("Exception: ", ex);
        ex.printStackTrace();
        throw ex;
      }
    } catch (NoSuchElementExistsException ex) {
      throw (ex);
    } catch (NoMappingExistsForElementException ex) {
      throw (ex);
    }
  }

  @Override
  public Level getFirstPreviousRollUpLevel(String currLevelURI, String factURI)
      throws NoSuchElementExistsException, NoMappingExistsForElementException {

    try {
      Individual ind = this.owlConnection.getModel().getIndividual(currLevelURI);
      // case the currLevelURI doesn't lead to an individual
      if (ind == null)
        throw new NoSuchElementExistsException(currLevelURI);
      String levelURI = "";
      Resource s;
      StmtIterator stmts = this.owlConnection.getModel().listStatements(null,
          this.owlConnection.getModel().getObjectProperty(Constants.rollsUpDirectlyTo), ind);
      // TODO-soon this gets the first value it encounters
      boolean stop = false;
      while (stmts.hasNext() && !stop) {
        s = stmts.next().getSubject();
        if (s.as(Individual.class).hasProperty(RDF.type, this.owlConnection.getModel().getOntClass(
            OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.level))) {
          NodeIterator nodeItr = s.as(Individual.class)
              .listPropertyValues(this.owlConnection.getModel()
                  .getObjectProperty(OWLConnectionFactory.getSMDNamespace(owlConnection)
                      + Constants.rollsUpDirectlyTo));
          int i = 0;
          // TODO-soon this gets the first value it encounters
          while (nodeItr.hasNext()) {
            if (nodeItr.next().as(Individual.class).getURI().equals(currLevelURI)) {
              levelURI = s.getURI();
              stop = true;
              break;
            }
          }
        }
      }
      // case there is no previous level of currLevelURI
      if (levelURI.equals(""))
        throw new NoSuchElementException("Previous Level Of: " + currLevelURI);
      Level l = getLevelByURI(levelURI, factURI);
      return l;
    } catch (NoSuchElementExistsException ex) {
      logger.error("Exception. ", ex);
      throw (ex);
    } catch (NoMappingExistsForElementException ex) {
      logger.error("Exception. ", ex);
      throw (ex);
    }
  }

  @Override
  public List<String> getLevelsOnDimension(String dimURI, String factURI) {

    List<String> levelURIs = new ArrayList<String>();
    ExtendedIterator levelsIndivs = this.owlConnection.getModel()
        .getOntClass(OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.levelClass)
        .listInstances();
    while (levelsIndivs.hasNext()) {
      Individual thisInstance = (Individual) levelsIndivs.next();
      if (dimURI.equals(thisInstance.getPropertyValue(this.owlConnection.getModel()
          .getObjectProperty(OWLConnectionFactory.getSMDNamespace(owlConnection)
              + Constants.belongsToDimension)) == null
                  ? dimURI + "arbitrary"
                  : thisInstance.getPropertyValue(this.owlConnection.getModel()
                      .getObjectProperty(OWLConnectionFactory.getSMDNamespace(owlConnection)
                          + Constants.belongsToDimension))
                      .toString())) {
        levelURIs.add(thisInstance.getURI());
      }
    }
    return levelURIs;
  }


  @Override
  public String getEndpointURI() {

    try {
      Individual mdSchemaInd = null;
      OntClass oc = owlConnection.getModel()
          .getOntClass(OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.mdSchema);
      for (ExtendedIterator<? extends OntResource> i = oc.listInstances(); i.hasNext();) {
        mdSchemaInd = (Individual) i.next();
      }

      return mdSchemaInd
          .getPropertyValue(this.owlConnection.getModel().getDatatypeProperty(
              OWLConnectionFactory.getSMDNamespace(owlConnection) + Constants.hasSPARQLService))
          .toString();
    } catch (NullPointerException ex) {
      return "";
    }
  }

}
