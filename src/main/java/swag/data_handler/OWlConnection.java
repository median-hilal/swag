package swag.data_handler;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.log4j.Logger;

import swag.data_handler.connection_to_rdf.SPARQLEndpointConnection;
import swag.web.WebConstants;

public class OWlConnection {

  private static final Logger logger = Logger.getLogger(OWlConnection.class);

  private SPARQLEndpointConnection sparqlEndPoint;

  private Map<String, String> namespaces = new HashMap<>();

  public void addToNamespaces(String namespaceKey, String namespaceValue) {
    namespaces.put(namespaceKey, namespaceValue);
  }

  public boolean checkIfInNameSpaces(String str) {
    return namespaces.containsKey(str);
  }

  public String getNamespacebyKey(String key) {
    if (namespaces.get(key) != null && !namespaces.get(key).equals("")) {
      return namespaces.get(key);
    }
    return "";
  }

  private String myNS;
  private String addedNS;

  public String getAddedNS() {
    return addedNS;
  }

  public void setAddedNS(String addedNS) {
    this.addedNS = addedNS;
  }

  private org.apache.jena.ontology.OntModelSpec owlModelSpec =
      org.apache.jena.ontology.OntModelSpec.OWL_DL_MEM_TRANS_INF;
  private org.apache.jena.ontology.OntModelSpec owlModelSpecHelper =
      org.apache.jena.ontology.OntModelSpec.OWL_MEM;
  private OntModel model;
  private OntModel modelHelper;

  public OntModelSpec getOwlModelSpec() {
    return owlModelSpec;
  }

  public void setOwlModelSpec(OntModelSpec owlModelSpec) {
    this.owlModelSpec = owlModelSpec;
  }

  public OntModelSpec getOwlModelSpecHelper() {
    return owlModelSpecHelper;
  }

  public void setOwlModelSpecHelper(OntModelSpec owlModelSpecHelper) {
    this.owlModelSpecHelper = owlModelSpecHelper;
  }

  public OntModel getModel() {
    return model;
  }

  public void setModel(OntModel model) {
    this.model = model;
  }

  public OntModel getModelHelper() {
    return modelHelper;
  }

  public void setModelHelper(OntModel modelHelper) {
    this.modelHelper = modelHelper;
  }

  public SPARQLEndpointConnection getSparqlEndPoint() {
    return sparqlEndPoint;
  }

  public void setSparqlEndPoint(SPARQLEndpointConnection sparqlEndPoint) {
    this.sparqlEndPoint = sparqlEndPoint;
  }

  public OWlConnection() {}

  public OWlConnection(OntModelSpec owlModelSpec, OntModelSpec owlModelSpecHelper) {
    this();
    setOwlModelSpec(owlModelSpec);
    setOwlModelSpecHelper(owlModelSpecHelper);
  }

  public OWlConnection(OntModelSpec owlModelSpec, OntModelSpec owlModelSpecHelper,
      SPARQLEndpointConnection sparqlEndPoint) {
    this();
    setOwlModelSpec(owlModelSpec);
    setOwlModelSpecHelper(owlModelSpecHelper);
    setSparqlEndPoint(sparqlEndPoint);
  }


  /**
   * @param path
   * @param fileName
   * @return
   */
  public static String convertPathToURI(String path, String fileName) {
    String res = "";
    try {
      Path input = Paths.get(path, fileName);
      res = input.toUri().toString();
    } catch (Exception ex) {
      logger.error(ex);
    }
    return res;
  }

  public void createOntologyModel() {
    this.model = ModelFactory.createOntologyModel(this.owlModelSpec);
  }

  public void readOwlFromFile(String owlPath, String owlName) {

    this.model.read(convertPathToURI(owlPath, owlName),
        WebConstants.TYPES_MAP.get(StringUtils.substring(owlName, owlName.lastIndexOf("."))));
  }

  public void readOwlFromURI(String fileURI) {
    this.model.read(fileURI,
        WebConstants.TYPES_MAP.get(StringUtils.substring(fileURI, fileURI.lastIndexOf("."))));
  }

  /**
   * The main function to load the OWL file into the model
   * 
   * @param owlPath path to the OWL file
   * @param owlName name of the OWL file
   */
  public void readOWL(String owlPath, String owlName) {
    this.model = ModelFactory.createOntologyModel(this.owlModelSpec);
    this.model.read(convertPathToURI(owlPath, owlName));
    // myNS = model.getNsPrefixURI("");
    // Jena doesn't support OWL2 at the moment. However, you can just call setStrictMode(false) on
    // your OntModel,
    // and it will allow you to view that resource as a class by switching off strict checking.
    // this.model.setStrictMode(false);
    if (this.owlModelSpec.equals(this.owlModelSpecHelper)) {
      modelHelper = model;
    } else {
      this.modelHelper = ModelFactory.createOntologyModel(this.owlModelSpecHelper);
      this.modelHelper.read(convertPathToURI(owlPath, owlName));
      this.modelHelper.setStrictMode(false);
    }
  }

  /**
   * reads an owl file that is supposed to contain the analysis graph
   * 
   * @param owlPath the path to the folder containing the owl file to be appended
   * @param owlName the name of the owl file to be appended
   */
  public void appendOWL(String owlPath, String owlName) {
    this.model.read(convertPathToURI(owlPath, owlName));
    setAddedNS(model.getNsPrefixURI(""));
  }

  /**
   * this is an encapsulation for the buggy Jena getPropertyValue, which returns some value even if
   * {@code prop} is null
   * 
   * @param res usually an individual calling the method
   * @param prop the property of which we need the value
   * @return null if {@code prop} is null, otherwise the value of the property
   */
  public RDFNode getPropertyValueEnc(OntResource res, Property prop) {
    if (prop == null)
      return null;
    else
      return res.getPropertyValue(prop);
  }

  /**
   * this is an encapsulation for the buggy Jena getPropertyValue, which returns some value even if
   * {@code prop} is null
   * 
   * @param res usually an individual calling the method
   * @param prop the property of which we need the value
   * @return null if {@code prop} is null, otherwise the value of the property as an Individual
   */
  public org.apache.jena.ontology.Individual getPropertyValueEncAsIndividual(OntResource res,
      Property prop) {
    if (prop == null || res == null || res.getPropertyValue(prop) == null)
      return null;
    else
      return res.getPropertyValue(prop).as(org.apache.jena.ontology.Individual.class);
  }

  /**
   * this is an encapsulation for the buggy Jena getPropertyValue, which returns some value even if
   * {@code prop} is null
   * 
   * @param res usually an individual calling the method
   * @param prop the property of which we need the value
   * @return null if {@code prop} is null, otherwise the String value of the property as an
   *         Individual
   */
  public String getPropertyValueEncAsString(OntResource res, Property prop) {
    if (prop == null || res == null || res.getPropertyValue(prop) == null)
      return null;
    else
      return res.getPropertyValue(prop).toString().replace("\\\"", "\"");
  }

  /**
   * gets a class by its name
   * 
   * @param the class name without the prefix
   * @return a reference of the class
   */
  public OntClass getClassByName(String className) {
    Resource r = model.getResource(className);
    OntClass cls = (OntClass) r.as(OntClass.class);
    return cls;
  }

}
