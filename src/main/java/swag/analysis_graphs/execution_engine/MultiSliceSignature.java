package swag.analysis_graphs.execution_engine;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import swag.analysis_graphs.execution_engine.analysis_situations.AnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.ISpecification;
import swag.analysis_graphs.execution_engine.analysis_situations.ItemInAnalysisSituationType;
import swag.analysis_graphs.execution_engine.analysis_situations.VariableState;
import swag.analysis_graphs.execution_engine.analysis_situations.asUtilities;

public class MultiSliceSignature extends Signature<AnalysisSituation> {

  public MultiSliceSignature(AnalysisSituation containingObject,
      ItemInAnalysisSituationType itemType, VariableState variableState, String name,
      ISpecification parentSpecification) {
    super(containingObject, itemType, variableState, name, null);
  }

  @Override
  public AnalysisSituation getContainingObject() {
    return (AnalysisSituation) this.getContainingObject();
  }

  @Override
  public void setContainingObject(AnalysisSituation as) {
    super.setContainingObject(as);
  }

  /**
   * 
   */
  private static final long serialVersionUID = 4383799261010171800L;

  /**
   * Shallow copy
   * 
   * @param sig
   */
  public MultiSliceSignature shallowCopy() {
    return (MultiSliceSignature) new Signature<AnalysisSituation>(
        (Signature<AnalysisSituation>) this);
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof MultiSliceSignature) {
      MultiSliceSignature sig = (MultiSliceSignature) o;
      if (this.getItemType().equals(sig.getItemType())
          && this.getVriableName().equals(sig.getVriableName())
          && asUtilities.equalsWithNull(this.getParentSpecification(), sig.getParentSpecification())
          && this.getContainingObject().equals(sig.getContainingObject())
          && this.getVariableState().equals(sig.getVariableState()))
        return true;
    }
    return false;
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 31) // two randomly chosen prime numbers
        .append(this.getVriableName()).append(this.getContainingObject()).toHashCode();
  }

}
