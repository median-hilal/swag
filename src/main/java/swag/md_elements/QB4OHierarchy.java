package swag.md_elements;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class QB4OHierarchy extends MDElement {

  public QB4OHierarchy() {
    super();
    // TODO Auto-generated constructor stub
  }

  public QB4OHierarchy(MDElement e) {
    super(e);
    // TODO Auto-generated constructor stub
  }

  public QB4OHierarchy(String uri, String name, Mapping mapping, String label) {
    super(uri, name, mapping, label);
    // TODO Auto-generated constructor stub
  }

  /**
   * Clone construct
   * 
   * @param l
   */
  public QB4OHierarchy(Level l) {
    super((MDElement) l);
  }

  @Override
  public QB4OHierarchy deepCopy() {
    return new QB4OHierarchy(this);
  }



  /**
   * 
   */
  private static final long serialVersionUID = -7167103285160090122L;

  public static QB4OHierarchy getDefaultHierarchy() {
    return DefaultHierarchy.getDefaultHierarchy();
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof QB4OHierarchy) {
      QB4OHierarchy l = (QB4OHierarchy) o;
      if (this.getIdentifyingName()
          .equals(l.getIdentifyingName()) /* && this.getMapping().equals(l.getMapping()) */)
        return true;
    }
    return false;
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
        append(getIdentifyingName()).toHashCode();
  }

}
