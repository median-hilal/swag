package swag.analysis_graphs.execution_engine.analysis_situations;

import java.io.Serializable;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import swag.md_elements.Measure;

public class MeasureToAnalysisSituationItemSignature implements Serializable {

  /**
  * 
  */
  private static final long serialVersionUID = 7731812821895724167L;
  private VariableState variableType;
  private IMeasureToAnalysisSituation measToAS;
  private ItemInAnalysisSituationType itemType;
  private String variableName;
  private ISpecification parentSpecification;

  public MeasureToAnalysisSituationItemSignature(IMeasureToAnalysisSituation measToAS,
      ItemInAnalysisSituationType itemType, VariableState variableType, String name,
      ISpecification parentSpecification) {
    this.measToAS = measToAS;
    this.itemType = itemType;
    this.variableType = variableType;
    this.variableName = name;
    this.parentSpecification = parentSpecification;
  }


  /**
   * shallow copy constructor
   * 
   * @param sig
   */
  public MeasureToAnalysisSituationItemSignature(MeasureToAnalysisSituationItemSignature sig) {
    this.measToAS = sig.getMeasToAS();
    this.itemType = sig.getItemType();
    this.variableType = sig.getVariableType();
    this.variableName = sig.getVriableName();
    this.parentSpecification = sig.getParentSpecification();
  }

  /**
   * Shallow copy
   * 
   * @param sig
   */
  public MeasureToAnalysisSituationItemSignature shallowCopy() {
    return new MeasureToAnalysisSituationItemSignature(this);
  }

  // comparing measToAS is done by reference
  @Override
  public boolean equals(Object o) {
    if (o instanceof MeasureToAnalysisSituationItemSignature) {
      MeasureToAnalysisSituationItemSignature sig = (MeasureToAnalysisSituationItemSignature) o;
      // Measure m1 = new Measure((Measure)this.getMeasToAS().getMeasureSpecificationInterface().get
      // Measure m2 = new Measure((Measure)sig.getMeasToAS().getMeasure());
      if (// m1.equals(m2) &&
      this.getItemType().equals(sig.getItemType())
          && this.getVriableName().equals(sig.getVriableName()) && this.getParentSpecification()
              .positionAsMDElementBasedEquals(sig.getParentSpecification()))
        return true;
    }
    return false;
  }

  @Override
  public int hashCode() {
    Measure m1 =
        new Measure((Measure) this.getMeasToAS().getMeasureSpecificationInterface().getPosition());
    return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
    // if deriving: appendSuper(super.hashCode()).
        append(m1.hashCode()).append(this.getItemType()).append(this.getVriableName())
        .append(this.getParentSpecification().getPosition().getIdentifyingName()).toHashCode();
  }

  public void assignFromSourceSignatureWithoutMeasurToASPointerOrParentSpecificationPointer(
      MeasureToAnalysisSituationItemSignature sig) {
    this.itemType = sig.getItemType();
    this.variableType = sig.getVariableType();
    this.variableName = sig.getVriableName();
  }

  public VariableState getVariableType() {
    return variableType;
  }

  public void setVariableType(VariableState isVariable) {
    this.variableType = isVariable;
  }

  public boolean isVariable() {
    return (this.variableType == VariableState.VARIABLE ? true : false);
  }

  public boolean isBoundVariable() {
    return (this.variableType == VariableState.BOUND_VARIABLE ? true : false);
  }

  public boolean isVariableOrBoundVariable() {
    return (this.variableType == VariableState.BOUND_VARIABLE
        || this.variableType == VariableState.VARIABLE ? true : false);
  }


  public IMeasureToAnalysisSituation getMeasToAS() {
    return measToAS;
  }

  public void setMeasToAS(IMeasureToAnalysisSituation measToAS) {
    this.measToAS = measToAS;
  }

  public ItemInAnalysisSituationType getItemType() {
    return itemType;
  }

  public void setItemType(ItemInAnalysisSituationType itemType) {
    this.itemType = itemType;
  }

  public String getVriableName() {
    return variableName;
  }

  public void setVariableName(String name) {
    this.variableName = name;
  }

  public ISpecification getParentSpecification() {
    return parentSpecification;
  }

  public void setParentSpecification(ISpecification parentSpecification) {
    this.parentSpecification = parentSpecification;
  }
}
