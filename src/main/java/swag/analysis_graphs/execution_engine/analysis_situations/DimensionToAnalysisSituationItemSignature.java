package swag.analysis_graphs.execution_engine.analysis_situations;

import java.io.Serializable;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class DimensionToAnalysisSituationItemSignature implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 7470595984498318144L;
  private VariableState variableState;
  private IDimensionQualification dimToAS;
  private ItemInAnalysisSituationType itemType;
  private String variableName;
  private ISpecification parentSpecification;

  public DimensionToAnalysisSituationItemSignature(IDimensionQualification dimToAS,
      ItemInAnalysisSituationType itemType, VariableState variableState, String name,
      ISpecification parentSpecification) {
    this.dimToAS = dimToAS;
    this.itemType = itemType;
    this.variableState = variableState;
    this.variableName = name;
    this.parentSpecification = parentSpecification;
  }

  /**
   * shallow copy constructor
   * 
   * @param sig
   */
  private DimensionToAnalysisSituationItemSignature(DimensionToAnalysisSituationItemSignature sig) {
    this.dimToAS = sig.getDimToAS();
    this.itemType = sig.getItemType();
    this.variableState = sig.getVariableState();
    this.variableName = sig.getVriableName();
    this.parentSpecification = sig.getParentSpecification();
  }

  /**
   * Shallow copy
   * 
   * @param sig
   */
  public DimensionToAnalysisSituationItemSignature shallowCopy() {
    return new DimensionToAnalysisSituationItemSignature(this);
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof DimensionToAnalysisSituationItemSignature) {
      DimensionToAnalysisSituationItemSignature sig = (DimensionToAnalysisSituationItemSignature) o;
      if (this.getDimToAS().isOnSameHierarchyAndDimension(sig.getDimToAS())
          && this.getItemType().equals(sig.getItemType())
          && this.getVriableName().equals(sig.getVriableName()) && this.getParentSpecification()
              .positionAsMDElementBasedEquals(sig.getParentSpecification()))
        return true;
    }
    return false;
  }

  @Override
  public int hashCode() {
    boolean bol = false;

    return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
    // if deriving: appendSuper(super.hashCode()).
        append(this.getDimToAS().getD().hashCode())
        .append(this.getDimToAS().getHierarchy().hashCode()).append(this.getItemType())
        .append(this.getVriableName())
        .append(this.getParentSpecification().getPosition().getIdentifyingName()).toHashCode();
  }

  public void assignFromSourceSignatureWithoutDimToASPointerOrParentSpecificationPointer(
      DimensionToAnalysisSituationItemSignature sig) {
    this.itemType = sig.getItemType();
    this.variableState = sig.getVariableState();
    this.variableName = sig.getVriableName();
  }

  public boolean isVariable() {
    return (this.variableState == VariableState.VARIABLE ? true : false);
  }

  public boolean isBoundVariable() {
    return (this.variableState == VariableState.BOUND_VARIABLE ? true : false);
  }

  public boolean isVariableOrBoundVariable() {
    return (this.variableState == VariableState.BOUND_VARIABLE
        || this.variableState == VariableState.VARIABLE ? true : false);
  }

  public VariableState getVariableState() {
    return variableState;
  }

  public void setVariableState(VariableState variableState) {
    this.variableState = variableState;
  }

  public IDimensionQualification getDimToAS() {
    return dimToAS;
  }

  public void setDimToAS(IDimensionQualification dimToAS) {
    this.dimToAS = dimToAS;
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
