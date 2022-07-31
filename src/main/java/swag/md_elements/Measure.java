package swag.md_elements;

import java.io.Serializable;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import swag.analysis_graphs.execution_engine.analysis_situations.IMeasure;
import swag.web.IVariableVisitor;

public class Measure extends MDElement implements IMeasure, Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 2047425399912542053L;

  public Measure(String uri, String name, Mapping mapping, String label) {
    super(uri, name, mapping, label);
  }

  public Measure(String uri, String name, Mapping mapping, String identifyingName, String label) {
    super(uri, name, mapping, identifyingName, label);
  }

  /**
   * Clone construct
   * 
   * @param m
   */
  public Measure(Measure m) {
    super((MDElement) m);
  }

  @Override
  public Measure deepCopy() {
    return new Measure(this);
  }

  /*
   * private List <Measure> subMeasureOf;
   * 
   * public List<Measure> getSubMeasureOf() {return subMeasureOf;} public void
   * setSubMeasureOf(List<Measure> subMeasureOf) {this.subMeasureOf = subMeasureOf;}
   * 
   * public Measure(String name, Mapping mapping, List<Measure> subMeasureOf) { this(name, mapping);
   * this.subMeasureOf = subMeasureOf; }
   */

  @Override
  public boolean equals(Object o) {

    if (o instanceof Measure) {
      Measure m = (Measure) o;
      if (this.getIdentifyingName()
          .equals(m.getIdentifyingName()) /* && this.getMapping().equals(m.getMapping()) */)
        return true;
    }
    return false;
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
        append(getIdentifyingName()).toHashCode();
  }

  @Override
  public void acceptVisitor(IVariableVisitor visitor) throws Exception {
    visitor.visit(this);
  }
}
