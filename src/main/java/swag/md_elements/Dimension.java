package swag.md_elements;

import java.io.Serializable;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Dimension extends MDElement implements Serializable {

  private static final long serialVersionUID = 4622239505643080751L;

  /**
   * 
   */

  public Dimension(String uri, String name, String label) {
    super(uri, name, new Mapping(), label);
    // TODO Auto-generated constructor stub
  }

  public Dimension() {
    super();
  }

  /**
   * Clone construct
   * 
   * @param l
   */
  public Dimension(Dimension l) {
    super((MDElement) l);
  }

  @Override
  public Dimension deepCopy() {
    return new Dimension(this);
  }


  @Override
  public boolean equals(Object o) {
    if (o instanceof Dimension) {
      Dimension l = (Dimension) o;
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
