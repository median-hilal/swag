package swag.analysis_graphs.execution_engine.analysis_situations;

import javax.naming.OperationNotSupportedException;

import swag.md_elements.MDElement;

public class SliceConditionFactory {



  public static SliceCondition convertSliceCondition(ISliceSinglePosition i) {

    if (i instanceof SliceCondition) {
      return (SliceCondition) i;
    }

    SliceCondition sc = new SliceCondition<>(i.getPositionOfCondition(), i.getConditoin(),
        i.getType(), i.getSignature(), SliceConditionStatus.UNKNOWN);
    return sc;
  }

  public static SliceConditionTypedWritten convertToSliceConditionTypedWritten(
      ISliceSinglePosition i) {

    if (i instanceof SliceConditionTypedWritten) {
      return (SliceConditionTypedWritten) i;
    }

    SliceConditionTypedWritten sc = new SliceConditionTypedWritten<>(i.getPositionOfCondition(),
        i.getConditoin(), i.getType(), i.getSignature());
    return sc;
  }

  public static SliceConditionTyped convertToSliceConditionTyped(ISliceSinglePosition i) {

    if (i instanceof SliceConditionTyped) {
      return (SliceConditionTyped) i;
    }

    SliceConditionTyped sc = new SliceConditionTyped<>(i.getPositionOfCondition(), i.getConditoin(),
        i.getType(), i.getSignature(), SliceConditionStatus.UNKNOWN);
    return sc;
  }

  public static PredicateInASSimple convertToPredicateInASSimple(ISliceSinglePosition i)
      throws OperationNotSupportedException {

    if (i instanceof PredicateInASSimple) {
      return (PredicateInASSimple) i;
    }

    throw new OperationNotSupportedException();

  }

  public static SliceCondition createSliceConditionOfSignature(ISliceSinglePosition dn,
      MDElement elem, String value, String uri) throws OperationNotSupportedException {

    if (dn.isDimSlice()) {
      SliceCondition<IDimensionQualification> dnTemp =
          new SliceCondition<>(elem, value, uri, SliceConditionStatus.NON_WRITTEN);
      return dnTemp;
    } else {
      if (dn.isMultipleSlice()) {
        SliceCondition<AnalysisSituation> dnTemp =
            new SliceCondition<>(elem, value, uri, SliceConditionStatus.NON_WRITTEN);
        return dnTemp;
      } else {
        if (dn.isMsrSlice()) {
          SliceCondition<IMeasureToAnalysisSituation> dnTemp =
              new SliceCondition<>(elem, value, uri, SliceConditionStatus.NON_WRITTEN);
          return dnTemp;
        }
      }
    }
    throw new OperationNotSupportedException();
  }

  public static SliceCondition createSliceConditionWrittenOfSignature(ISliceSinglePosition dn,
      MDElement elem, String value, String uri) throws OperationNotSupportedException {

    if (dn.isDimSlice()) {
      SliceCondition<IDimensionQualification> dnTemp =
          new SliceCondition(elem, value, uri, SliceConditionStatus.WRITTEN);
      return dnTemp;
    } else {
      if (dn.isMultipleSlice()) {
        SliceCondition<AnalysisSituation> dnTemp =
            new SliceCondition<>(elem, value, uri, SliceConditionStatus.WRITTEN);
        return dnTemp;
      } else {
        if (dn.isMsrSlice()) {
          SliceCondition<IMeasureToAnalysisSituation> dnTemp =
              new SliceCondition<>(elem, value, uri, SliceConditionStatus.WRITTEN);
          return dnTemp;
        }
      }
    }
    throw new OperationNotSupportedException();
  }

  public static SliceConditionTyped createSliceConditionTypedOfSignature(ISliceSinglePosition dn,
      MDElement elem, String value, String uri, String type) throws OperationNotSupportedException {

    if (dn.isDimSlice()) {
      SliceConditionTyped<IDimensionQualification> dnTemp =
          new SliceConditionTyped<>(elem, value, uri, type, SliceConditionStatus.NON_WRITTEN);
      return dnTemp;
    } else {
      if (dn.isMultipleSlice()) {
        SliceConditionTyped<AnalysisSituation> dnTemp =
            new SliceConditionTyped<>(elem, value, uri, type, SliceConditionStatus.NON_WRITTEN);
        return dnTemp;
      } else {
        if (dn.isMsrSlice()) {
          SliceConditionTyped<IMeasureToAnalysisSituation> dnTemp =
              new SliceConditionTyped<>(elem, value, uri, type, SliceConditionStatus.NON_WRITTEN);
          return dnTemp;
        }
      }
    }
    throw new OperationNotSupportedException();
  }

  public static SliceConditionTyped createSliceConditionTypedWrittenOfSignature(
      ISliceSinglePosition dn, MDElement elem, String value, String uri, String type)
      throws OperationNotSupportedException {

    if (dn.isDimSlice()) {
      SliceConditionTyped<IDimensionQualification> dnTemp =
          new SliceConditionTyped<>(elem, value, uri, type, SliceConditionStatus.WRITTEN);
      return dnTemp;
    } else {
      if (dn.isMultipleSlice()) {
        SliceConditionTyped<AnalysisSituation> dnTemp =
            new SliceConditionTyped<>(elem, value, uri, type, SliceConditionStatus.WRITTEN);
        return dnTemp;
      } else {
        if (dn.isMsrSlice()) {
          SliceConditionTyped<IMeasureToAnalysisSituation> dnTemp =
              new SliceConditionTyped<>(elem, value, uri, type, SliceConditionStatus.WRITTEN);
          return dnTemp;
        }
      }
    }
    throw new OperationNotSupportedException();
  }


}
