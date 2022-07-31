package swag.data_handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntProperty;

/**
 * @author swag
 *
 */
class Edge {
    private org.apache.jena.rdf.model.Resource edge; // plays identifier role
    private Vertex src;
    private Vertex des;
    private String justification = "";

    public Edge() {
    }

    public Edge(Vertex _src, org.apache.jena.rdf.model.Resource _edge, Vertex _des, String justification) {
	// this (_src, _edge, _des, _edge_type);
	this.edge = _edge;
	this.src = _src;
	this.des = _des;
	this.justification = justification;
    }

    public boolean equalsIgnoreSrcAndDes(Edge e) {
	if (this.getEdge().equals(e.getEdge()))
	    return true;
	else
	    return false;
    }

    public org.apache.jena.rdf.model.Resource getEdge() {
	return edge;
    }

    public void setEdge(org.apache.jena.rdf.model.Resource edge) {
	this.edge = edge;
    }

    public Vertex getSrc() {
	return src;
    }

    public void setSrc(Vertex src) {
	this.src = src;
    }

    public Vertex getDes() {
	return des;
    }

    public void setDes(Vertex des) {
	this.des = des;
    }

    public String getJustification() {
	return justification;
    }

    public void setJustification(String justification) {
	this.justification = justification;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException { //
	Vertex src = (Vertex) this.src.clone();
	org.apache.jena.rdf.model.Resource edge = this.edge;
	Vertex des = (Vertex) this.des.clone();
	String just = this.justification;
	return new Edge(src, edge, des, just);
    }

    @Override
    public boolean equals(Object e) {
	// no need to compare FD and cardinality as an Edge is fully specified
	// by its source,
	// destination and edge
	boolean res = false;
	if (e instanceof Edge) {
	    Edge new_edge = (Edge) e;
	    if (this.src.equals(new_edge.src))
		if (this.edge.equals(new_edge.edge))
		    if (this.des.equals(new_edge.des))
			res = true;
	}
	return res;
    }

    /**
     * This function builds a reverse edge from an Edge we don't care about
     * FDType and Cardinality, as this function is dedicated just to test the
     * existence of an inverse edge in a path, nothing furher
     * 
     * @return the inverse of the edge
     */
    public Edge getEdgeInverse() {
	Edge e = null;
	try {
	    Vertex new_src = (Vertex) this.getDes().clone();
	    Vertex new_des = (Vertex) this.getSrc().clone();
	    if (this.getEdge() instanceof OntProperty) {
		OntProperty new_edge = ((OntProperty) this.getEdge()).getInverse();
		e = new Edge(new_src, new_edge, new_des, "");
	    } else {
		e = new Edge(new_src, this.getEdge(), new_des, "");
	    }
	} catch (Exception ex) {
	    System.out.println(ex);
	}
	return e;
    }

    public void printFull() {
	System.out.println(
		"edge : " + this.getSrc().getVertex() + " " + this.getEdge() + " " + this.getDes().getVertex());
    }

    public void printWithCardinality() {
	System.out.println("edge: " + this.getSrc().getVertex().getLocalName() + " " + this.getEdge().getLocalName()
		+ " " + this.getDes().getVertex().getLocalName() + "\n");
    }

    public void printWithCardinalityAndJustification() {
	System.out.println("edge: " + this.getSrc().getVertex().getLocalName() + " " + this.getEdge().getLocalName()
		+ " " + this.getDes().getVertex().getLocalName() + "\n" + this.getJustification());
    }

    public String getEdgeLocalName() {
	return (this.getEdge().getLocalName());
    }

    @Override
    public String toString() {
	return "Edge: " + this.getSrc().getVertex().getLocalName() + " " + this.getEdge().getLocalName() + " "
		+ this.getDes().getVertex().getLocalName() + "\n" + this.getJustification();
    }

    public void print() {
	System.out.println("edge : " + this.getEdge());
    }
}

/**
 * @author swag
 *
 */
class Vertex {
    private Individual vertex; // plays identifier role

    public Vertex(Individual vertex) {
	this.vertex = vertex;
    }

    public Individual getVertex() {
	return vertex;
    }

    public void setVertex(Individual vertex) {
	this.vertex = vertex;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException { // shallow
								 // clone
	return new Vertex(this.vertex);
    }

    @Override
    public boolean equals(Object o) {
	boolean res = false;
	if (o instanceof Vertex) {
	    Vertex v = (Vertex) o;
	    if (this.vertex.equals(v.vertex))
		res = true;
	}
	return res;
    }
}

/**
 * @author swag
 *
 */
class TwoVertices {
    private Vertex src;
    private Vertex des;

    public TwoVertices(Vertex src, Vertex des) {
	super();
	this.src = src;
	this.des = des;
    }

    public Vertex getSrc() {
	return src;
    }

    public void setSrc(Vertex src) {
	this.src = src;
    }

    public Vertex getDes() {
	return des;
    }

    public void setDes(Vertex des) {
	this.des = des;
    }

    @Override
    public boolean equals(Object o) {
	if (o instanceof TwoVertices) {
	    TwoVertices tv = (TwoVertices) o;
	    if (this.src.equals(tv.src) && this.des.equals(tv.des))
		return true;
	}
	return false;
    }

    // #implement: check the hashcode generation
    @Override
    public int hashCode() {
	return new HashCodeBuilder(17, 31).append(this.src.getVertex().getURI() + this.des.getVertex().getURI())
		.toHashCode();
    }
}

/**
 * @author swag
 *
 */
class DTVertex { // data type vertex
    private OntClass dtClass;
    private OntProperty dtProperty;

    public OntClass getDtClass() {
	return dtClass;
    }

    public void setDtClass(OntClass dtClass) {
	this.dtClass = dtClass;
    }

    public OntProperty getDtProperty() {
	return dtProperty;
    }

    public void setDtProperty(OntProperty dtProperty) {
	this.dtProperty = dtProperty;
    }

    public DTVertex(OntClass dtClass, OntProperty dtProperty) {
	super();
	this.dtClass = dtClass;
	this.dtProperty = dtProperty;
    }

    public DTVertex(OntProperty dtProperty) {
	super();
	this.dtProperty = dtProperty;
    }

    public DTVertex(OntClass dtClass) {
	super();
	this.dtClass = dtClass;
    }

    @Override
    public boolean equals(Object o) {
	boolean res = false;
	if (o instanceof DTVertex) {
	    DTVertex v = (DTVertex) o;

	    if (this.dtProperty != null && this.dtClass != null) {
		if (v.dtProperty != null && v.dtClass != null)
		    if (this.dtProperty.equals(v.dtProperty) && this.dtClass.equals(v.dtClass))
			res = true;
	    } else {
		if (this.dtProperty != null && this.dtClass == null) {
		    if (v.dtProperty != null && v.dtClass == null)
			if (this.dtProperty.equals(v.dtProperty))
			    res = true;
		} else {
		    if (this.dtProperty == null && this.dtClass != null) {
			if (v.dtProperty == null && v.dtClass != null)
			    if (this.dtClass.equals(v.dtClass))
				res = true;
		    }
		}

	    }
	}
	return res;
    }
}

/**
 * @author swag
 *
 */
class Path {
    private LinkedList<Edge> path;

    public boolean insertEdge(Edge e) {
	return this.path.add(e);
    }

    public void printPath() {
	for (Edge e : this.getPath()) {
	    e.printFull();
	}
    }

    public boolean removePathFormAnother(Path p) {
	boolean res = false;
	if (this.checkIfPathContainsOther(p)) {
	    this.getPath().removeAll(p.getPath());
	    res = true;
	}
	return res;
    }

    public boolean checkIfPathContainsVertex(Vertex v) {
	boolean res = false;
	for (Edge e : this.getPath()) {
	    if (e.getSrc().equals(v) || e.getDes().equals(v))
		return true;
	}
	return res;
    }

    public boolean checkIfPathContainsEdge(Edge edge, boolean fullEdge) {
	if (fullEdge) {
	    for (Edge e : this.getPath()) {
		if (e.equals(edge)) {
		    return true;
		}
	    }
	} else {
	    for (Edge e : this.getPath()) {
		if (e.equalsIgnoreSrcAndDes(edge)) {
		    return true;
		}
	    }
	}
	return false;
    }

    /**
     * if the parameter path is contained in the current path, this function
     * removes the whole chain of edges in the current path that starts from the
     * source of the parameter and ends in the current path last vertex
     * 
     * @param p
     * @return true whether the parameter path p is contained in the current
     *         path
     */
    public boolean removePathFormAnotherCascade(Path p) {
	boolean res = false;

	if (p != null) {
	    // checking the current path contains the one to delete (the
	    // parameter)
	    if (this.checkIfPathContainsOther(p)) {
		Path pathFormPSourceToCurrentPathDes = new Path();
		int EIndex = this.path.indexOf(p.getFirstEdge()); // the index
								  // in the
								  // current
								  // path where
								  // the
								  // parameter
								  // path starts

		for (int i = EIndex; i < this.getPath().size(); i++) { // looping
								       // in the
								       // current
								       // path
								       // starting
								       // form
								       // EIndex
		    pathFormPSourceToCurrentPathDes.insertEdgeWithCheck(this.path.get(i)); // inserting
											   // all
											   // the
											   // current
											   // path
											   // edges
											   // that
											   // starts
											   // from
											   // EIndex
		}

		if (this.removePathFormAnother(pathFormPSourceToCurrentPathDes)) {
		    res = true;
		}
	    }
	}
	return res;
    }

    /**
     * @param p
     *            the path to check if contained in the current path
     * @return true whether the parameter path p is contained in the current
     *         path
     */
    public boolean checkIfPathContainsOther(Path p) {
	boolean res = false;
	int eIndex = -1;

	if (this.getPath().contains(p.getFirstEdge())) {
	    res = true;
	    eIndex = this.getPath().indexOf(p.getFirstEdge());
	    for (int i = 1; i < p.getPath().size(); i++) { // i starts from 1
							   // because 0 is
							   // checked in the
							   // previous if
		if (!this.getPath().get(++eIndex).equals(p.getPath().get(i))) {
		    res = false;
		    break;
		}
	    }
	}
	return res;
    }

    /**
     * @param p1
     *            the path to extend the current path
     * @return true if the extension succeed
     */
    public boolean extendPathByOtherPath(Path p1) {
	if (p1.getPath().size() < 1 || !this.getLastVertex().equals(p1.getFirstVertex())) // the
											  // extension
											  // doesn't
											  // exist
	    return false;
	else {
	    // adding edge by edge to the current path
	    for (Edge e : p1.getPath()) {
		try {
		    this.insertEdgeWithCheck((Edge) e.clone());
		} catch (Exception ex) {
		    System.out.println(ex);
		    return false;
		}
	    }
	}
	return true;
    }

    public String getPathLocalName() {
	String localName = "";

	for (Edge e : this.getPath()) {
	    localName += e.getEdgeLocalName() + "-";
	}

	return localName;
    }

    // -1 non cousins
    // 0 equal
    // 1 p1 is parent of p2
    // 2 p2 is parent of p1
    /*
     * public static int checkIfTwoPathsAreCousins (Path p1, Path p2) { int res
     * = 0;
     * 
     * if (p1.getPath().size() == p2.getPath().size()){ // i is 1 to avoid first
     * and last vertices as they are tagged for (int i = 1; i <
     * p1.getPath().size(); i++){ if
     * (!(p1.getPath().get(i).equals(p2.getPath().get(i)))){ if () } } }
     * 
     * return res; }
     */

    public boolean insertEdgeWithCheck(Edge e) {
	if (this.getPath().size() == 0)
	    return this.path.add(e);
	else // the path contains one edge at least
	if (this.getLastVertex().equals(e.getSrc()) && // check the last path
						       // vertex is identical to
						       // the
						       // edge source
		!this.path.contains(e)) // check the new edge doesn't exist in
					// the path
	    return this.path.add(e);
	else
	    return false;
    }

    public Edge getFirstEdge() {
	Edge e = new Edge();
	e = this.path.getFirst();
	return e;
    }

    public Edge getLastEdge() {
	Edge e = new Edge();
	e = this.path.getLast();
	return e;
    }

    public Path() {
	this.path = new LinkedList<Edge>();
    }

    public Path(Edge e) {
	this();
	this.insertEdgeWithCheck(e);
    }

    public static void PrintPathsList(List<Path> paths) {
	System.out.println("Paths List");
	for (Path p : paths) {
	    System.out.println(
		    "source: " + p.getFirstVertex().getVertex() + "\n destination: " + p.getLastVertex().getVertex());
	    for (Edge e : p.getPath()) {
		e.printFull();
	    }
	    System.out.println(
		    "-------------------------------------------------------------------------------------------");
	}
    }

    @Override
    public boolean equals(Object o) {
	if (o instanceof Path) {
	    // #implement check the equlas definition if true (here in next it
	    // depends on linked list
	    // equlas)
	    Path new_path = (Path) o;
	    if (this.path.equals(new_path.path))
		return true;
	}
	return false;
    }

    public static List<Path> clonePathsList(List<Path> pathsToBeCloned) {
	List<Path> res = new ArrayList<Path>();

	try {
	    for (Path p : pathsToBeCloned) {
		res.add((Path) p.clone());
	    }
	} catch (Exception ex) {
	    System.out.println(ex);
	}
	return res;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException { //
	Path clonedPath = new Path(); // big P in Path
	LinkedList<Edge> clonedpath = new LinkedList<Edge>(); // small p in path
	for (Edge e : this.path) {
	    Edge colnedEdge = (Edge) e.clone();
	    clonedpath.add(colnedEdge);
	}
	clonedPath.path = clonedpath;
	return clonedPath;
    }

    @Override
    public String toString() {
	String res = "";
	for (Edge e : this.path) {
	    res += e.getSrc().getVertex().getLocalName() + "/" + e.getEdge().getLocalName() + "/"
		    + e.getDes().getVertex().getLocalName() + " + ";
	}
	return res;
    }

    public String toString(int type) {
	String res = "";
	if (type == 1) {
	    for (Edge e : this.getPath()) {
		res += "\n" + e.toString();
	    }
	}
	return res;
    }

    public Vertex getFirstVertex() {
	return path.getFirst().getSrc();
    }

    public Vertex getLastVertex() {
	return path.getLast().getDes();
    }

    public LinkedList<Edge> getPath() {
	return path;
    }

    public void setPath(LinkedList<Edge> path) {
	this.path = path;
    }
}

class MappedPath extends Path {
    private Map<Object, String> map;

    public MappedPath() {
	super();
	this.map = new HashMap<Object, String>();
    }

    public MappedPath(Map<Object, String> map) {
	super();
	this.map = map;
    }

    public Map<Object, String> getMap() {
	return map;
    }

    public void setMap(Map<Object, String> map) {
	this.map = map;
    }
}

// ==============================================================================================
/**
 * @author swag
 *
 */
public class DepreciatedOWLGraph {
    private OWlConnection owlConnection;
    private MappingRepInterface mappRepInterface;

    public MappingRepInterface getMappRepInterface() {
	return mappRepInterface;
    }

    public void setMappRepInterface(MappingRepInterface mappRepInterface) {
	this.mappRepInterface = mappRepInterface;
    }

    private boolean extendPropertyRanges = false;
    private boolean completeFD = false;

    public DepreciatedOWLGraph(OWlConnection owlConnection, MappingRepInterface mappRepInterface) {
	this.owlConnection = owlConnection;
	this.mappRepInterface = mappRepInterface;
    }

    public DepreciatedOWLGraph(OWlConnection owlConnection, boolean extendPropertyRanges, boolean completeFD) {
	super();
	this.owlConnection = owlConnection;
	this.extendPropertyRanges = extendPropertyRanges;
	this.completeFD = completeFD;
    }

    public OWlConnection getOwlConnection() {
	return owlConnection;
    }

    public void setOwlConnection(OWlConnection owlConnection) {
	this.owlConnection = owlConnection;
    }

    public boolean isExtendPropertyRanges() {
	return extendPropertyRanges;
    }

    public void setExtendPropertyRanges(boolean extendPropertyRanges) {
	this.extendPropertyRanges = extendPropertyRanges;
    }

    public boolean isCompleteFD() {
	return completeFD;
    }

    public void setCompleteFD(boolean completeFD) {
	this.completeFD = completeFD;
    }

    /**
     * gets the paths that has a specific source vertex
     * 
     * @param source
     *            the source vertex
     * @param paths
     *            the set of paths
     * @return the paths in @paths that has @vertex as their source
     */
    public List<Path> getPathsFormSource(Vertex source, List<Path> paths) {
	List<Path> res = new ArrayList<Path>();

	for (Path p : paths) {

	    if (p.getFirstVertex().equals(source))
		res.add(p);
	}
	return res;
    }

    /**
     * check whether a path contains a vertex
     * 
     * @param path
     * @param v
     * @return true if v is deleteable
     */
    public boolean checkIfPathContainVertex(Path path, Vertex v) {
	boolean res = false;

	for (Edge e : path.getPath()) {
	    if (e.getSrc().getVertex().equals(v) || e.getDes().getVertex().equals(v)) {
		res = true;
		break;
	    }
	}
	return res;
    }

    /**
     * @param path
     *            the path to check whether contains one vertex of a set
     * @param vList
     *            the vertex list to check if any of it is contained in the path
     * @param excludeVertex
     *            the vertex to be excluded from the vList in the check
     */
    public boolean checkIfPathContainOneVertexOfSet(Path path, List<Vertex> vList, Vertex excludeVertex) {
	// excludeVertex is the fact that is a source for the current path which
	// mustn't be counted
	boolean res = false;

	for (Edge e : path.getPath()) {
	    for (Vertex v : vList) {
		if (!v.equals(excludeVertex)) {
		    if (e.getSrc().equals(v) || e.getDes().equals(v)) {
			res = true;
			break;
		    }
		}
	    }
	    if (res == true)
		break;
	}
	return res;
    }

    // ==============================================================================================
    public void printPaths(List<Path> paths) {
	for (Path p : paths) {
	    System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
	    p.printPath();
	}
    }

    // ==============================================================================================
    public Path convertEdgeToPath(Edge e) {
	Path path = new Path();
	path.insertEdge(e);
	return path;
    }

    // ==============================================================================================
    public List<Path> convertEdgeListToPathList(List<Edge> edges) {
	List<Path> paths = new LinkedList<Path>();
	for (Edge e : edges) {
	    Path p = this.convertEdgeToPath(e);
	    paths.add(p);
	}
	return paths;
    }

    // ==============================================================================================
    public List<Edge> convertPropListToEdgeList(Individual startInd,
	    Map<org.apache.jena.ontology.OntProperty, List<Individual>> map) {
	List<Edge> edges = new LinkedList<Edge>();

	for (Map.Entry<OntProperty, List<Individual>> entry : map.entrySet()) {
	    for (Individual currInd : entry.getValue()) {

		Edge e = new Edge(new Vertex(startInd), entry.getKey(), new Vertex(currInd), "");
		edges.add(e);
	    }
	}
	return edges;
    }

    // ==============================================================================================
    public List<Edge> getOutEdges(Vertex v) {
	return convertPropListToEdgeList(v.getVertex(),
		mappRepInterface.getOutMDInPMappedPropsAndValuesThatHaveMapping(v.getVertex()));
    }

    /**
     * returns the paths between two vertices, each path is a sequence of the
     * form (node, edge, node...., edge, node) never exploring a path twice is
     * guaranteed on the vertex level; each vertex's out edges are explored once
     * 
     * @param startNode
     *            the source node; first node in the path
     * @param endNode
     *            the destination node; last node in the path
     * @return A list of paths from source to destination
     */
    public List<Path> getAllPathsBetweenTwoVertices(Vertex startNode, Vertex endNode) {

	List<Path> paths = new LinkedList<Path>();
	List<Path> maxLengthPaths = Collections.synchronizedList(new LinkedList<Path>());

	maxLengthPaths.addAll(convertEdgeListToPathList(convertPropListToEdgeList(startNode.getVertex(),
		mappRepInterface.getOutMDInPMappedPropsAndValuesThatHaveMapping(startNode.getVertex()))));

	while (maxLengthPaths.size() > 0) {
	    for (ListIterator<Path> iterator = maxLengthPaths.listIterator(); iterator.hasNext();) {
		Path p = iterator.next();
		Vertex firstVertex = p.getFirstVertex();
		Vertex lastVertex = p.getLastVertex();
		Edge lastEdge = p.getLastEdge();

		// ensuring not reaching a tagged node
		if (lastVertex.equals(endNode)) {
		    if (!paths.contains(p))
			paths.add(p);
		    iterator.remove();
		    break;
		} else {
		    // re-launching from last reached vertex
		    if (this.getOutEdges(p.getLastVertex())
			    .size() > 0 /*
					 * && // there are out edges
					 * !launchedVertices.contains(p.
					 * getLastVertex())
					 */) { // avoiding re-launching twice
					      // from the same vertex
			try {
			    Path p_ext;

			    iterator.remove();
			    for (Edge e : this.getOutEdges(p.getLastVertex())) {
				if (!p.checkIfPathContainsEdge(e.getEdgeInverse(), false)
					&& !p.checkIfPathContainsVertex(e.getDes())) {
				    p_ext = (Path) p.clone();
				    p_ext.insertEdgeWithCheck(e);
				    iterator.add(p_ext);
				}
			    }
			} catch (Exception ex) {
			    System.out.println(ex + ". Exception cloning path: " + p);
			}
		    } else {
			iterator.remove();
			break;
		    }
		}
	    }
	}
	// Path.PrintPathsList(paths);
	return paths;
    }

    // ===================================================================================
    public static void main(String[] args) {

	// Level l = new Level("0", null, new Mapping (new
	// CustomSPARQLQuery(QueryFactory.create("PREFIX
	// pubo: <http://ontology.ontotext.com/publishing#> select ?x where { ?x
	// pubo:containsMention
	// ?z. } limit 100"))));
	// Level l1 = (Level) new MDElement(l);
	// Level l2 = new Level(l);

	String str = " <center> Analysis Situation Name: AS1 </center><br/>-------------------------------------------------  <br/><center> Analysis Situation Facts: </center>  <br/> Fact: Mention</center>  <br/>-------------------------------------------------  <br/><center> Analysis Situation Measures	: </center> <br/>Measure: NumOfMentions<br/>Aggregation Operator:  <br/>------------------------------------------------- <br/><center> Analysis Situation Dimensions: </center>  <br/>Entity<br/> <br/>-------------------------------------------------  <br/><center> Analysis Situation DimensionsToAnalysisSituation: </center>  <br/><center> Dimension: Entity</center>  <br/>Dice level: EntityType    Dice node: <http://dbpedia.org/ontology/Person> <br/>Granularity level: EntityType <br/> <br/> ";
	System.out.println("str: " + str);
	str.replace("<http", "&lt;http");
	System.out.println("str: " + str);

	/*
	 * //System.getProperties().put("proxySet","true");
	 * //System.getProperties().put("http.proxyHost", "140.78.58.10");
	 * //System.getProperties().put("http.proxyPort", "3128");
	 * 
	 * InetAddress [] addresses = {}; int timeout = 2000; try { InetAddress
	 * str = InetAddress.getByName("http://www.google.com/");
	 * System.out.println(str); addresses =
	 * InetAddress.getAllByName("http://www.google.com/"); } catch
	 * (UnknownHostException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } for (InetAddress address : addresses) { try {
	 * if (address.isReachable(timeout))
	 * System.out.printf("%s is reachable%n", address); else
	 * System.out.printf("%s could not be contacted%n", address); } catch
	 * (IOException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } }
	 * 
	 * Query query =
	 * QueryFactory.create("select ?x where {?x ?z ?y} limit 100") ;
	 * 
	 * QueryExecution qexec = QueryExecutionFactory.sparqlService(
	 * "http://ff-news.ontotext.com/repositories/factforge_2016", query);
	 * ResultSet results = qexec.execSelect();
	 * 
	 * OntModelSpec owlModelSpec = OntModelSpec.OWL_DL_MEM;
	 * 
	 * 
	 * BasicOWLHandler handler = new
	 * BasicOWLHandler(openllet.jena.PelletReasonerFactory.THE_SPEC,
	 * OntModelSpec.OWL_DL_MEM_RULE_INF); handler.
	 * readOWL("C:\\Users\\*****\\Desktop\\Implementation files\\MDandAG",
	 * "testing-4-MDOnt-lightWeight-ex-with-ag-ex.owl");
	 * 
	 * 
	 * String myNS = handler.getModel().getNsPrefixURI(""); Individual ind =
	 * handler.getModel().getIndividual(myNS + "Mention"); NodeIterator
	 * nodeItr =
	 * ind.listPropertyValues(handler.getModel().getObjectProperty(myNS +
	 * "hasLevel")); RDFNode currNode; Individual tempIndDim;
	 * System.out.println("=========== properties: ===================");
	 * while (nodeItr.hasNext()){ tempIndDim =
	 * nodeItr.next().as(Individual.class);
	 * System.out.println(tempIndDim.getLocalName()); }
	 * System.out.println("=========== properties: ===================");
	 * 
	 * OWLGraph owlGraph = new OWLGraph(handler); List<Path> paths =
	 * owlGraph.getAllPathsBetweenTwoVertices(new
	 * Vertex(handler.getModel().getIndividual(myNS + "Mention")), new
	 * Vertex(handler.getModel().getIndividual(myNS + "EntityType")));
	 * 
	 * System.out.println("-------------------------------------------");
	 * Path.PrintPathsList(paths); for (Path p : paths){ for (Query q :
	 * handler.generatePathMapping(p)){ System.out.println(q.toString()); }
	 * 
	 * System.out.println("/////////////////////////////////");
	 * System.out.println(handler.generatePathMappingRootedQuery(p,
	 * QueryType.DIMENSION).getSparqlQuery().toString()); }
	 */
    }
}
