package swag.analysis_graphs.execution_engine.analysis_situations;

import java.util.ArrayList;
import java.util.List;
import javax.naming.OperationNotSupportedException;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import swag.analysis_graphs.execution_engine.IVariablesList;
import swag.analysis_graphs.execution_engine.Signature;
import swag.md_elements.MDElement;
import swag.md_elements.MultipleMDElement;
import swag.web.IVariableVisitor;
import swag.web.VariableStringVisitor;

public class SliceConditionMultiple<T extends ISignatureType> extends SliceCondition<T>
    implements ISliceMultiplePosition<T> {


  /**
   * 
   */
  private static final long serialVersionUID = -5330238511752536226L;
  private List<MDElement> positions;

  public SliceConditionMultiple() {
    super();
  }

  public SliceConditionMultiple(MDElement pos, String cond, String uri, List<MDElement> positions) {
    super(pos, cond, uri, /* Added after refactoring */SliceConditionStatus.UNKNOWN);
    this.positions = positions;
  }

  public SliceConditionMultiple(MDElement pos, String cond, String uri, String type,
      List<MDElement> positions) {
    super(pos, cond, uri, type, /* Added after refactoring */SliceConditionStatus.UNKNOWN);
    this.positions = positions;
  }

  public SliceConditionMultiple(Signature<T> signature) {
    super(signature);
  }

  public SliceConditionMultiple(MDElement pos, String cond, String uri, Signature<T> signature,
      List<MDElement> positions) {
    super(pos, cond, uri, signature, /* Added after refactoring */SliceConditionStatus.UNKNOWN);
    this.positions = positions;
  }

  public SliceConditionMultiple(MDElement pos, String cond, String uri, Signature<T> signature,
      String type, List<MDElement> positions) {
    super(pos, cond, uri, type, signature,
        /* Added after refactoring */SliceConditionStatus.UNKNOWN);
    this.positions = positions;
  }

  /**
   * 
   * Shallow copy constructor
   * 
   * 
   */
  private SliceConditionMultiple(SliceConditionMultiple<T> pred) {
    this(pred.getPositionOfCondition(), pred.getCondition(), pred.getURI(), pred.getSignature(),
        pred.getType(), pred.getPositions());
  }

  @Override
  public String getVariableValue() {
    return getCondition();
  }


  @Override
  public boolean equals(Object o) {
    if (o instanceof SliceConditionMultiple) {
      SliceConditionMultiple<T> sc = (SliceConditionMultiple<T>) o;
      if (this.getSignature().equals(sc.getSignature())
          && asUtilities.equalsWithNull(this.getCondition(), sc.getCondition())
          && asUtilities.equalsWithNull(this.getPositionOfCondition(), sc.getPositionOfCondition())
          && asUtilities.equalsWithNull(this.getURI(), sc.getURI())
          && this.getPositions().containsAll(sc.getPositions())
          && sc.getPositions().containsAll(this.getPositions())
          && asUtilities.equalsWithNull(this.getType(), sc.getType())) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean equalsPositional(Object o) {
    if (o instanceof SliceConditionMultiple) {
      SliceConditionMultiple<T> sc = (SliceConditionMultiple<T>) o;
      if (this.getSignature().equals(sc.getSignature()))
        return true;
    }
    return false;
  }


  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
        append(this.getSignature().hashCode()).append(this.getPositionOfCondition())
        .append(this.getURI()).append(this.getCondition()).append(this.getSignature()).toHashCode();
  }

  @Override
  public void assignFromSourceVar(Variable sourceVar) {
    SliceConditionMultiple<T> sc = (SliceConditionMultiple<T>) sourceVar;
    this.setPositionOfCondition(sc.getPositionOfCondition());
    this.setCondition(sc.getCondition());
    this.setURI(sc.getURI());
    this.setType(sc.getType());
    this.getSignature()
        .assignFromSourceSignatureWithoutContainingPointerOrParentSpecificationPointer(
            sc.getSignature());
    this.setPositions(sc.getPositions());
  }

  @Override
  public String acceptStringVisitor(VariableStringVisitor visitor, int variableIndex) {
    return visitor.visit(this, variableIndex);
  }

  @Override
  public void bind(Variable tempVariable) {

    SliceConditionMultiple<T> sc = (SliceConditionMultiple<T>) tempVariable;
    this.setCondition(sc.getCondition());
    this.getSignature().setVariableState(VariableState.BOUND_VARIABLE);
    this.setPositionOfCondition(sc.getPositionOfCondition());
    this.setURI(sc.getURI());
    this.setType(sc.getType());
    this.setPositions(sc.getPositions());
    BindParent();
  }


  @Override
  public void unBind() throws OperationNotSupportedException {
    this.setCondition("");
    this.getSignature().setVariableState(VariableState.VARIABLE);
    this.setPositionOfCondition(null);
    this.setURI("");
    this.setType("");
    this.setPositions(new ArrayList<MDElement>());
    unBindParent();
  }

  public Variable shallowCopy() {
    return new SliceConditionMultiple<T>(this);
  }

  @Override
  public void acceptVisitor(IVariableVisitor v) throws Exception {
    // TODO
  }


  @Override
  public void addToAnalysisSituationOrNavigationStepVariables(IVariablesList asORnv) {
    if (this.getSignature().isVariable())
      asORnv.addToVariables(this);
  }

  @Override
  public MDElement getPositionOfCondition() {
    return MultipleMDElement.getInstance();
  }

  @Override
  public void setPositionOfCondition(MDElement e) {
    ;
  }

  @Override
  public List<MDElement> getPositions() {
    return this.positions;
  }

  @Override
  public void setPositions(List<MDElement> positions) {
    this.positions = positions;

  }
}
