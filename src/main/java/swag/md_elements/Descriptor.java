package swag.md_elements;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Descriptor extends MDElement {

  public Descriptor(String uri, String name, Mapping mapping, String label) {
    super(uri, name, mapping, label);
  }

  private String identifyingName;

  @Override
  public String getIdentifyingName() {
    if (identifyingName == null) {
      return getURI();
    } else {
      return identifyingName;
    }
  }

  public void setIdentifyingName(String identifyingName) {
    this.identifyingName = identifyingName;
  }

  /**
   * Clone construct
   * 
   * @param d
   */
  public Descriptor(Descriptor d) {
    super((MDElement) d);
  }

  @Override
  public Descriptor deepCopy() {
    return new Descriptor(this);
  }

  @Override
  public boolean equals(Object o) {

    if (o instanceof Descriptor) {
      Descriptor d = (Descriptor) o;
      if (this.getIdentifyingName()
          .equals(d.getIdentifyingName()) /* && this.getMapping().equals(d.getMapping()) */)
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
