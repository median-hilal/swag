package swag.analysis_graphs.execution_engine.analysis_situations;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import swag.md_elements.Mapping;
import swag.md_elements.Measure;
import swag.web.IVariableVisitor;

/**
 * 
 * This class represents a measure, either a simple basic measure or an expression derived measure.
 * 
 * @author swag
 *
 */
public class MeasureAggregated extends Measure implements IMeasure {

  /**
   * 
   */
  private static final long serialVersionUID = 8244817018538521440L;

  private String comment;

  private MeasureDerived measure;
  private AggregationFunction agg;

  public MeasureAggregated(String uri) {

    super(uri, "", new Mapping(), "");
  }

  public MeasureAggregated(String uri, String name, String comment, MeasureDerived measure,
      AggregationFunction agg, String label) {

    super(uri, name, new Mapping(), label);
    this.comment = comment;
    this.measure = measure;
    this.agg = agg;
  }

  /**
   * shallow copy constructor
   * 
   * @param meas
   */
  public MeasureAggregated(MeasureAggregated meas) {

    super(meas.getURI(), meas.getName(), new Mapping(), meas.getLabel());
    this.comment = meas.comment;
    this.measure = new MeasureDerived(meas.measure);
    this.agg = meas.agg;
  }


  @Override
  public boolean equals(Object o) {
    if (o instanceof MeasureAggregated) {
      MeasureAggregated ms = (MeasureAggregated) o;
      if (this.getURI().equals(ms.getURI()) /*
                                             * && this.getMapping().equals(ms.getMapping())
                                             */)
        return true;
    }
    return false;
  }


  @Override
  public void acceptVisitor(IVariableVisitor visitor) throws Exception {
    visitor.visit(this);
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
        appendSuper(super.hashCode()).append(this.getURI()).toHashCode();
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public MeasureDerived getMeasure() {
    return measure;
  }

  public void setMeasure(MeasureDerived measure) {
    this.measure = measure;
  }

  public AggregationFunction getAgg() {
    return agg;
  }

  public void setAgg(AggregationFunction agg) {
    this.agg = agg;
  }

}
