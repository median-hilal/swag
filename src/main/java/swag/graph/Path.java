package swag.graph;

import java.util.List;

/**
 * @author swag
 *
 * @param <N>
 * @param <E>
 */
public interface Path<N extends Node, E extends Edge> {

  public boolean insertEdgeWithCheck(E e);

  public E getLastEdge();

  public boolean checkIfPathContainsEdge(E edge);

  public N getFirstNode();

  public N getLastNode();

  public Path<N, E> copy();

  public List<E> getPathEdges();

  public int compareWith(Path<N, E> otherPath, List<E> intersection);

}
