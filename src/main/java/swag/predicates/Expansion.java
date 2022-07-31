package swag.predicates;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Expansion {

  private EExpansionType expansionType;
  private Object expansionOn;

  public EExpansionType getExpansionType() {
    return expansionType;
  }

  public void setExpansionType(EExpansionType expansionType) {
    this.expansionType = expansionType;
  }

  public Object getExpansionOn() {
    return expansionOn;
  }

  public void setExpansionOn(Object expansionOn) {
    this.expansionOn = expansionOn;
  }

  public Expansion(EExpansionType expansionType, Object expansionOn) {
    super();
    this.expansionType = expansionType;
    this.expansionOn = expansionOn;
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
        append(expansionType).append(expansionOn).toHashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    } else {
      if (o instanceof Expansion) {
        Expansion that = (Expansion) o;
        if (this.expansionType.equals(that.getExpansionType())
            && ((this.getExpansionOn() == null && that.getExpansionOn() == null)
                || this.getExpansionOn().equals(that.getExpansionOn()))) {
          return true;
        }
      }
    }
    return false;
  }
}
