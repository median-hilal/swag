package swag.md_elements;

import java.util.List;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import swag.graph.Node;

public class Fact extends MDElement implements Node {

  public Fact(String uri, String name, Mapping mapping, String label) {
    super(uri, name, mapping, label);
    // TODO Auto-generated constructor stub
  }

  public Fact() {
    super();
  }

  /**
   * Clone construct
   * 
   * @param f
   */
  public Fact(Fact f) {
    super((MDElement) f);
  }

  @Override
  public Fact deepCopy() {
    return new Fact(this);
  }


  private List<Fact> subFactOf;
  private List<Level> hasBaseLevel;
  private List<Level> hasLevel;
  private List<Measure> hasMeasure;
  private List<Dimension> hasDimension;

  public List<Fact> getSubFactOf() {
    return subFactOf;
  }

  public void setSubFactOf(List<Fact> subFactOf) {
    this.subFactOf = subFactOf;
  }

  public List<Level> getHasLevel() {
    return hasLevel;
  }

  public void setHasBaseLevel(List<Level> hasBaseLevel) {
    this.hasBaseLevel = hasBaseLevel;
  }

  public List<Level> getHasBaseLevel() {
    return hasBaseLevel;
  }

  public void setHasLevel(List<Level> hasLevel) {
    this.hasLevel = hasLevel;
  }

  public List<Measure> getHasMeasure() {
    return hasMeasure;
  }

  public void setHasMeasure(List<Measure> hasMeasure) {
    this.hasMeasure = hasMeasure;
  }

  public List<Dimension> getHasDimension() {
    return hasDimension;
  }

  public void setHasDimension(List<Dimension> hasDimension) {
    this.hasDimension = hasDimension;
  }

  public Fact(String name, Mapping mapping, List<Fact> subFactOf, List<Level> hasLevel,
      List<Measure> hasMeasure) {

    this.subFactOf = subFactOf;
    this.hasLevel = hasLevel;
    this.hasMeasure = hasMeasure;
  }

  @Override
  public boolean equals(Object o) {

    if (o instanceof Fact) {
      Fact f = (Fact) o;
      if (this.getIdentifyingName()
          .equals(f.getIdentifyingName()) /* && this.getMapping().equals(f.getMapping()) */)
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
