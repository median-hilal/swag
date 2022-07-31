package swag.analysis_graphs.execution_engine.analysis_situations;

import java.io.Serializable;

public interface ISliceSpecificationNoPosition extends ISpecification, Serializable {

  /*
   * public default List<String> acceptSPARQLGeneratorVisitor(IASElementGenerateSPARQLVisitor
   * visitor) throws Exception { visitor.visit(this); return visitor.getReturn(); }
   * 
   * @Override public MDElement getPosition();
   * 
   * public void setPosition(MDElement slicePosition);
   * 
   * public SliceConditionInAnalysisSituation getSliceConditionInAnalysisSituation();
   * 
   * public void setSliceConditionInAnalysisSituation( SliceConditionInAnalysisSituation
   * sliceCondition);
   * 
   * public ISliceSpecification shallowCopy();
   * 
   * /**
   * 
   * @return true if both dice position and value are not nulls and not variables.
   * 
   * 
   * 
   * public default boolean isDue() { return null != this.getPosition() &&
   * !this.getPosition().getSignature().isVariable() && null !=
   * this.getSliceConditionInAnalysisSituation() &&
   * !this.getSliceConditionInAnalysisSituation().getSignature().isVariable(); }
   */

}
