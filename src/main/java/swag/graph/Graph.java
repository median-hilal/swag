package swag.graph;

import java.util.List;
import java.util.Set;

import swag.md_elements.MDElement;

/**
 * 
 * A graph structure made of Nodes and edges. Names of the elements are unique.
 * 
 * @author swag
 *
 * @param <N>
 * @param <E>
 */
public interface Graph<N extends Node, E extends Edge> {

  /**
   * 
   * Get a node from the graph by name or null if it's not found.
   * 
   * @param name
   * @return
   */
  public N getNode(String name);


  /**
   * 
   * Gets all nodes that have a similar identifying name
   * 
   * @param name
   * @return
   */
  public Set<N> getNodesByIdentifyingNameSimilarity(String name);

  /**
   * 
   * Gets all nodes that have the name
   * 
   * @param name
   * @return
   */
  public Set<N> getNodesByName(String name);

  /**
   * 
   * Get an edge from the graph by name, or null if it's not found.
   * 
   * @param name
   * @return
   */
  public E getEdge(String name);

  /**
   * 
   * Gets the outgoing edges of a node
   * 
   * @param node
   * @return a set of the outgoing edges of a node, an empty set if none exists
   */
  public Set<E> getEdgesOfNode(N node);

  /**
   * @param node the node to add
   * @return true if the node is added (does not already exist), false otherwise.
   */
  public boolean addNode(N node);

  /**
   * @param node the edge to add
   * @return true if the edge is added (no failure), false otherwise.
   */
  public boolean addEdge(E edge);

  /**
   * returns the paths between two nodes, each path is a sequence of edges. never exploring a path
   * twice is guaranteed on the node level; each node's out edges are explored once
   * 
   * @param startNode the source node; first node in the path
   * @param endNode the destination node; last node in the path
   * @return A list of paths from source to destination
   */
  public List<Path<N, E>> getAllPathsBetweenTwoVertices(N startNode, N endNode);


  /**
   * 
   * Gets all edges of the graph.
   * 
   * @return
   */
  public Set<E> getAllEdges();


  /**
   * 
   * Gets all nods of the graph
   * 
   * @return
   */
  public Set<N> getAllNodes();

  /**
   * 
   * print the graph
   * 
   * @return a string representing the print of the graph
   * 
   */
  public default String stringifyGraph() {
    StringBuilder builder = new StringBuilder(100);
    for (N n : getAllNodes()) {
      builder.append("--Node: ").append(n.toString()).append("\n\r");
      builder.append("----Edges of node:").append("\n\r");
      for (E e : getEdgesOfNode(n)) {
        builder.append("------Edge: ").append(e.toString()).append(" -> ")
            .append(e.getTarget().toString()).append("\n\r");
      }
    }

    return builder.toString();
  }

  public default String stringifyGraphMappings() {
    StringBuilder builder = new StringBuilder(100);
    for (N n : getAllNodes()) {
      builder.append(((MDElement) n).getMapping().getQuery().getSparqlQuery());
      for (E e : getEdgesOfNode(n)) {
        builder.append(((MDElement) e).getMapping().getQuery().getSparqlQuery());
      }
    }

    return builder.toString();
  }
}
