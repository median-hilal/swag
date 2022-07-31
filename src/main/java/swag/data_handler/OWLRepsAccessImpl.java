package swag.data_handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.query.Query;
import org.apache.jena.rdf.model.Statement;
import org.apache.log4j.Logger;

import swag.analysis_graphs.execution_engine.AnalysisGraph;
import swag.analysis_graphs.execution_engine.DefinedAGPredicates;
import swag.analysis_graphs.execution_engine.NoMappingExistsForElementException;
import swag.analysis_graphs.execution_engine.NoSuchElementExistsException;
import swag.analysis_graphs.execution_engine.analysis_situations.AnalysisSituation;
import swag.analysis_graphs.execution_engine.navigations.EncapsulationOfNavigation;
import swag.analysis_graphs.execution_engine.navigations.NavigationStep;
import swag.analysis_graphs.execution_engine.operators.Operation;
import swag.md_elements.Dimension;
import swag.md_elements.Fact;
import swag.md_elements.Level;
import swag.md_elements.MDSchema;
import swag.md_elements.Mapping;
import swag.md_elements.Measure;
import swag.predicates.IPredicateGraph;

public class OWLRepsAccessImpl implements OWLRepsAccessInterface {

  private static final Logger logger = Logger.getLogger(OWLRepsAccessImpl.class);

  private OWlConnection owlConnection;
  private MappingRepInterface mappInterface;
  private MDSchemaRepInterface mdInterface;
  private IAnalysisGraphRep asInterfact;
  private OperatorsRepInterface operatorInterface;
  private IPredicateGraph predicateGraph;
  private IPredicateRep predRep;

  public IPredicateGraph getPredicateGraph() {
    return predicateGraph;
  }

  public void setPredicateGraph(IPredicateGraph predicateGraph) {
    this.predicateGraph = predicateGraph;
  }

  public OperatorsRepInterface getOperatorInterface() {
    return operatorInterface;
  }

  public void setOperatorInterface(OperatorsRepInterface operatorInterface) {
    this.operatorInterface = operatorInterface;
  }

  public MappingRepInterface getMappInterface() {
    return mappInterface;
  }

  public void setMappInterface(MappingRepInterface mappInterface) {
    this.mappInterface = mappInterface;
  }

  public IAnalysisGraphRep getAsInterfact() {
    return asInterfact;
  }

  public void setAsInterfact(IAnalysisGraphRep asInterfact) {
    this.asInterfact = asInterfact;
  }

  public MDSchemaRepInterface getMdInterface() {
    return mdInterface;
  }

  public void setMdInterface(MDSchemaRepInterface mdInterface) {
    this.mdInterface = mdInterface;
  }

  public OWlConnection getOwlConnection() {
    return owlConnection;
  }

  public void setOwlConnection(OWlConnection owlConnection) {
    this.owlConnection = owlConnection;
  }

  public OWLRepsAccessImpl(OWlConnection owlConnection, MDSchema mdSchema,
      IPredicateGraph predicateGraph) {
    super();

    this.owlConnection = owlConnection;

    this.mappInterface = new MappingRepImpl(owlConnection);
    this.mdInterface = new MDSchemaRepImpl(owlConnection, mappInterface, mdSchema);
    this.predicateGraph = predicateGraph;
    this.predRep = new PredicateRep(owlConnection, mdSchema, predicateGraph);

    MeasToASRepInterface mesToASRepInterface =
        new MeasToASRepImpl(owlConnection, mdInterface, mappInterface, mdSchema);
    IDimsReader dimToASRepInterafce =
        new DimsReader(owlConnection, mdInterface, mappInterface, mdSchema, predicateGraph);



    this.asInterfact = new AnalysisGraphRepRDF(owlConnection, mdInterface, mesToASRepInterface,
        dimToASRepInterafce, mdSchema, predRep, predicateGraph);

    this.operatorInterface =
        new OperatorRepImpl(owlConnection, mdInterface, mappInterface, mdSchema, predicateGraph);
  }


  public List<NavigationStep> connectAnalysisGraph(List<AnalysisSituation> asList,
      MDSchema mdSchema) {

    List<NavigationStep> nvList = new ArrayList<NavigationStep>();
    List<EncapsulationOfNavigation> envList = new ArrayList<EncapsulationOfNavigation>();
    List<AnalysisSituation> syncASList = Collections.synchronizedList(asList);

    synchronized (syncASList) {
      ListIterator<AnalysisSituation> asItr = syncASList.listIterator();

      while (asItr.hasNext()) {
        AnalysisSituation as = asItr.next();
        List<NavigationStep> syncInNVList = Collections.synchronizedList(as.getInNavigations());

        synchronized (syncInNVList) {
          ListIterator<NavigationStep> inNvItr = syncInNVList.listIterator();
          while (inNvItr.hasNext()) {
            NavigationStep nv = inNvItr.next();
            if (envList.contains(new EncapsulationOfNavigation(nv))) {
              EncapsulationOfNavigation tempEnv =
                  envList.get(envList.indexOf(new EncapsulationOfNavigation(nv)));
              NavigationStep tempNv = tempEnv.getNvStep();
              tempNv.setTarget(as);
              inNvItr.remove();
              inNvItr.add(tempNv);
            } else {
              envList.add(new EncapsulationOfNavigation(nv));
            }
          }
        }

        List<NavigationStep> syncOutNVList = Collections.synchronizedList(as.getOutNavigations());

        synchronized (syncOutNVList) {
          ListIterator<NavigationStep> outNvItr = syncOutNVList.listIterator();

          while (outNvItr.hasNext()) {
            NavigationStep nv = outNvItr.next();

            if (envList.contains(new EncapsulationOfNavigation(nv))) {
              EncapsulationOfNavigation tempEnv =
                  envList.get(envList.indexOf(new EncapsulationOfNavigation(nv)));
              NavigationStep tempNv = tempEnv.getNvStep();
              tempNv.setSource(as);
              outNvItr.remove();
              outNvItr.add(tempNv);
            } else {
              envList.add(new EncapsulationOfNavigation(nv));
            }
          }
        }
      }
    }

    for (EncapsulationOfNavigation env : envList) {
      nvList.add(env.getNvStep());
    }

    for (NavigationStep nv : nvList) {
      nv.getOperators().addAll(getNavigationStepOperators(nv, nv.getTarget().getFact().getURI()));
    }
    return nvList;
  }


  @Override
  public AnalysisGraph buildAnalysisGraph(String agName, String agURI, String namespace,
      MDSchema mdSchema) {

    List<AnalysisSituation> asList = new ArrayList<>();

    for (String tempAsName : getAllAvailableAnalysisSituationsURIs(agURI)) {
      AnalysisSituation as;
      try {
        as = getAnalysisSituationByURI(tempAsName, 0);
        asList.add(as);
      } catch (Exception ex) {
        logger.error("Error building the analysis graph " + agName
            + ". While trying to retrieve analysis situation " + tempAsName + ".", ex);
        return null;
      }
    }
    List<NavigationStep> navs = connectAnalysisGraph(asList, mdSchema);

    return new AnalysisGraph(agName, agURI, namespace, asList, navs,
        new DefinedAGPredicates()/* asInterfact.getDefinedAGPredicates(predicateGraph) */,
        asInterfact.getDefinedAGConditions(predicateGraph),
        asInterfact.getDefinedAGConditionTypes(predicateGraph), mdSchema);

  }

  @Override
  public DefinedAGPredicates getDefinedAGPredicates() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * Gets all the names of the available analysis situations that are in the passed analysis graph
   * name.
   * 
   * @param agName the name of the analysis graph to which the analysis situations belong.
   * 
   * @return list of the names of the available ASs
   */
  public List<String> getAllAvailableAnalysisSituationsURIs(String agName) {
    return asInterfact.getAllAvailableAnalysisSituationsURIs(agName);
  }

  /**
   * given analysis situation name, this function scans the OWL file and constructs the analysis
   * situation
   * 
   * @param uri the name of the analysis situation
   * @return the analysis situation
   * @throws Exception
   */
  public AnalysisSituation getAnalysisSituationByURI(String uri, int getType) throws Exception {
    return asInterfact.getAnalysisSituationByURI(uri, getType);
  }

  /**
   * @param uri
   * @return
   * @throws NoSuchElementExistsException
   * @throws NoMappingExistsForElementException
   */
  public Mapping getMappingByElementURI(String uri)
      throws NoSuchElementExistsException, NoMappingExistsForElementException {
    return mappInterface.getMappingByElementURI(uri);
  }

  /**
   * This function should be only called for paths that have mapping; otherwise its behavior is not
   * expectable
   * 
   * @param p the path to generate its mapping
   * @return
   */
  public List<Query> getPathQueries(swag.data_handler.Path p) {
    return mappInterface.getPathQueries(p);
  }

  /**
   * gets the query connecting mdElementURI1 and mdElementURI1 by joining the queries on the path.
   * Throws a NoMappingExistsForElementException when an error occurs
   * 
   * @param mdElementURI2
   * @param mdElementURI1
   * @return
   * @throws NoMappingExistsForElementException
   */
  public Mapping getMappingQueryBetweenTwoElementsByURI(String mdElementURI2, String mdElementURI1)
      throws NoMappingExistsForElementException {
    return mappInterface.getMappingQueryBetweenTwoElementsByURI(mdElementURI2, mdElementURI1);
  }

  /**
   * @param ind
   * @return
   */
  public Map<OntProperty, List<Individual>> getOutMDInPMappedPropsAndValues(Individual ind) {
    return mappInterface.getOutMDInPMappedPropsAndValues(ind);
  }

  /**
   * @param ind
   * @return
   */
  public Map<OntProperty, List<Individual>> getOutMDInPMappedPropsAndValuesThatHaveMapping(
      Individual ind) {
    return mappInterface.getOutMDInPMappedPropsAndValuesThatHaveMapping(ind);
  }

  /**
   * Each edge is considered to be uniquely defined by its start and end nodes.
   * 
   * @param stmt a triple
   * @return true if the statement has a mapping query
   */
  public boolean checkIfMDPropertyHasMapping(Statement stmt) {
    return mappInterface.checkIfMDPropertyHasMapping(stmt);
  }

  /**
   * @return
   */
  public MDSchema getMDSchemaByByURI() {
    return null;
    /*
     * comment return mdInterface.getMDSchemaByByURI();
     */
  }

  /**
   * @return
   */
  public List<Fact> getMDSchemaFact() {
    return null;
    /*
     * comment return mdInterface.getMDSchemaFact();
     */
  }

  /**
   * If the uri declares a dimension, the method builds returns the dimension, otherwise throws and
   * exception
   * 
   * @param uri the URI of the dimension
   * @return new Dimension
   * @throws NoSuchElementExistsException when uri doesn't lead to an element or leads to an element
   *         that is not a dimension
   */
  public Dimension getDimensionByURI(String uri) throws NoSuchElementExistsException {
    return mdInterface.getDimensionByURI(uri);
  }

  /**
   * @param uri
   * @param factURI
   * @return
   * @throws NoSuchElementExistsException
   * @throws NoMappingExistsForElementException
   */
  public Level getLevelByURI(String uri, String factURI)
      throws NoSuchElementExistsException, NoMappingExistsForElementException {
    return mdInterface.getLevelByURI(uri, factURI);
  }

  /**
   * creates unmapped level depending on the uri passed
   * 
   * @param uri
   * @return Level
   * @throws NoSuchElementExistsException
   * @throws NoMappingExistsForElementException
   */
  public Level getLevelByURI(String uri) throws NoSuchElementExistsException {
    return mdInterface.getLevelByURI(uri);
  }

  /**
   * @param uri
   * @return
   * @throws NoSuchElementExistsException
   * @throws NoMappingExistsForElementException
   */
  public Fact getFactByURI(String uri)
      throws NoSuchElementExistsException, NoMappingExistsForElementException {
    return mdInterface.getFactByURI(uri);
  }

  /**
   * creates unmapped measure
   * 
   * @param uri
   * @param factURI
   * @return new Measure, which has no Mapping
   * @throws NoSuchElementExistsException
   * @throws NoMappingExistsForElementException
   */
  public Measure getMeasureByURI(String uri) throws NoSuchElementExistsException {
    return mdInterface.getMeasureByURI(uri);
  }

  /**
   * creates a mapped measure
   * 
   * @param uri
   * @param factURI
   * @return new Measure
   * @throws NoSuchElementExistsException
   * @throws NoMappingExistsForElementException
   */
  public Measure getMeasureByURI(String uri, String factURI) throws NullPointerException,
      NoSuchElementExistsException, NoMappingExistsForElementException {
    return mdInterface.getMeasureByURI(uri, factURI);
  }

  /**
   * Gets the next level in a hierarchy on a dimension
   * 
   * @param currLevelURI
   * @param factURI
   * @return
   * @throws NoMappingExistsForElementException
   */
  public Level getNextRollUpLevel(String currLevelURI, String factURI)
      throws NoSuchElementExistsException, NoMappingExistsForElementException {
    return mdInterface.getFirstNextRollUpLevel(currLevelURI, factURI);
  }

  /**
   * Gets the previous level in a hierarchy on a dimension
   * 
   * @param currLevelURI
   * @param factURI
   * @return
   * @throws NoMappingExistsForElementException
   */
  public Level getPreviousRollUpLevel(String currLevelURI, String factURI)
      throws NoSuchElementExistsException, NoMappingExistsForElementException {
    return mdInterface.getFirstPreviousRollUpLevel(currLevelURI, factURI);
  }

  /**
   * Gets all the levels on a dimension
   * 
   * @param dimURI the dimension URI to get the levels on
   * @param factURI
   * @param con
   * @return a list of level URI if they exist, otherwise n empty list
   */
  public List<String> getLevelPossibleValues(String dimURI, String factURI) {
    return mdInterface.getLevelsOnDimension(dimURI, factURI);
  }

  /**
   * gets all the names of the available navigation steps
   * 
   * @return list of the names of the available navigation steps
   */
  public List<String> getAllAvailableNavigationStepsNames() {
    return asInterfact.getAllAvailableNavigationStepsNames();
  }

  /**
   * gets the operators of the navigation step passed by URI
   * 
   * @param nvURI the URI of the intended navigation step
   * @return a list of navigation operators
   */
  public List<Operation> getNavigationStepOperators(NavigationStep nv, String factURI) {
    return operatorInterface.getNavigationStepOperators(nv, factURI);
  }

  public void readOWL(String owlPath, String owlName) {
    owlConnection.readOWL(owlPath, owlName);

  }

  public void appendOWL(String owlPath, String owlName) {
    owlConnection.appendOWL(owlPath, owlName);

  }

  public String getEndpointURI() {
    return mdInterface.getEndpointURI();
  }

}
