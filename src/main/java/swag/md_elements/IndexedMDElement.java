package swag.md_elements;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 
 * A pair of an MD element and an index.
 * 
 * @author swag
 *
 */
public class IndexedMDElement {

  private MDRelation cameFrom;
  private MDElement elm;
  private long index;

  public IndexedMDElement(MDElement elm, long index, MDRelation cameFrom) {
    super();
    this.elm = elm;
    this.index = index;
    this.cameFrom = cameFrom;
  }

  public MDElement getElm() {
    return elm;
  }

  public void setElm(MDElement elm) {
    this.elm = elm;
  }

  public long getIndex() {
    return index;
  }

  public void setIndex(long index) {
    this.index = index;
  }

  public static IndexedMDElement getMin(MDElement elemToFind, Collection<IndexedMDElement> coll) {

    Set<IndexedMDElement> matches =
        coll.stream().filter(x -> x.getElm().equals(elemToFind)).collect(Collectors.toSet());

    Set<Long> indexes = matches.stream().map(x -> x.getIndex()).collect(Collectors.toSet());
    Long min = Collections.min(indexes);

    return matches.stream().filter(x -> x.getIndex() == min).findFirst().orElse(null);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((elm == null) ? 0 : elm.hashCode());
    result = prime * result + (int) (index ^ (index >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    IndexedMDElement other = (IndexedMDElement) obj;
    if (elm == null) {
      if (other.elm != null)
        return false;
    } else if (!elm.equals(other.elm))
      return false;
    if (index != other.index)
      return false;
    return true;
  }

  public MDRelation getCameFrom() {
    return cameFrom;
  }

  public void setCameFrom(MDRelation cameFrom) {
    this.cameFrom = cameFrom;
  }

}
