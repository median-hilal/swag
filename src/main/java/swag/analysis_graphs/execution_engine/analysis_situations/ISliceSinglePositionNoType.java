package swag.analysis_graphs.execution_engine.analysis_situations;

import javax.naming.OperationNotSupportedException;

import swag.md_elements.MDElement;
import swag.web.IVariableVisitor;

public interface ISliceSinglePositionNoType<T extends ISignatureType>
    extends ISliceSinglePosition<T> {

  public default void createAndBind(ISliceSinglePosition<?> dn, MDElement elem, String value,
      String uri) {


    if (dn instanceof SliceCondition<?>) {
      /* commented after refactoring */
      /*
       * SliceCondition dn1 = (SliceCondition) dn;
       * 
       * if (dn.isDimSlice()) { SliceCondition<IDimensionQualification> dnTemp = new
       * SliceCondition<>(elem, value, uri); dn1.bind(dnTemp); } else { if (dn.isMultipleSlice()) {
       * SliceCondition<AnalysisSituation> dnTemp = new SliceCondition<>(elem, value, uri);
       * dn1.bind(dnTemp); } else { if (dn.isMsrSlice()) {
       * SliceCondition<IMeasureToAnalysisSituation> dnTemp = new SliceCondition<>(elem, value,
       * uri); dn1.bind(dnTemp); } } }
       */
    } else {
      if (dn instanceof SliceConditionWritten<?>) {

        SliceConditionWritten dn1 = (SliceConditionWritten) dn;

        if (dn.isDimSlice()) {
          SliceConditionWritten<IDimensionQualification> dnTemp =
              new SliceConditionWritten<>(elem, value, uri);
          dn1.bind(dnTemp);
        } else {
          if (dn.isMultipleSlice()) {
            SliceConditionWritten<AnalysisSituation> dnTemp =
                new SliceConditionWritten<>(elem, value, uri);
            dn1.bind(dnTemp);
          } else {
            if (dn.isMsrSlice()) {
              SliceConditionWritten<IMeasureToAnalysisSituation> dnTemp =
                  new SliceConditionWritten<>(elem, value, uri);
              dn1.bind(dnTemp);
            }
          }
        }
      }
    }
  }

  @Override
  public default void acceptVisitor(IVariableVisitor v)
      throws OperationNotSupportedException, Exception {
    v.visit(this);
  }
}
