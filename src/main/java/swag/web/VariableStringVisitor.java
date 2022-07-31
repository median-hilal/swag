package swag.web;

import javax.naming.OperationNotSupportedException;

import swag.analysis_graphs.execution_engine.analysis_situations.AggregationOperationInAnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.DiceNodeInAnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.IMeasure;
import swag.analysis_graphs.execution_engine.analysis_situations.IMeasureInAS;
import swag.analysis_graphs.execution_engine.analysis_situations.ISliceSetMsr;
import swag.analysis_graphs.execution_engine.analysis_situations.ISliceSetMultiple;
import swag.analysis_graphs.execution_engine.analysis_situations.LevelInAnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.MeasureAggregated;
import swag.analysis_graphs.execution_engine.analysis_situations.MeasureDerivedInAS;
import swag.analysis_graphs.execution_engine.analysis_situations.PredicateInASMultiple;
import swag.analysis_graphs.execution_engine.analysis_situations.PredicateInASSimple;
import swag.analysis_graphs.execution_engine.analysis_situations.SliceCondition;
import swag.analysis_graphs.execution_engine.analysis_situations.SliceConditionTyped;
import swag.analysis_graphs.execution_engine.analysis_situations.SliceConditionTypedWritten;
import swag.analysis_graphs.execution_engine.analysis_situations.SliceConditionWritten;
import swag.analysis_graphs.execution_engine.analysis_situations.SliceConditoinGeneric;
import swag.analysis_graphs.execution_engine.analysis_situations.SlicePositionInAnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.SliceSet;

/**
 * 
 * Generates web strings for different types of variables.
 * 
 * @author swag
 *
 */
public interface VariableStringVisitor {

  public String visit(LevelInAnalysisSituation level, int variableIndex);

  public String visit(DiceNodeInAnalysisSituation node, int variableIndex);

  public String visit(SlicePositionInAnalysisSituation position, int variableIndex);

  public String visit(SliceCondition conditoin, int variableIndex);

  public String visit(SliceSet conditoin, int variableIndex);

  public String visit(ISliceSetMultiple sliceSet, int variableIndex);

  public String visit(PredicateInASMultiple predicate, int variableIndex);

  public String visit(PredicateInASSimple predicate, int variableIndex);

  public String visit(ISliceSetMsr sliceSetMsr, int variableIndex);

  public String visit(IMeasure meas, int variableIndex);

  public String visit(MeasureDerivedInAS meas, int variableIndex);

  public String visit(MeasureAggregated meas, int variableIndex);

  public String visit(SliceConditionTyped conditoin, int variableIndex);

  public String visit(SliceConditionTypedWritten conditoin, int variableIndex);

  public String visit(IMeasureInAS meas, int variableIndex) throws OperationNotSupportedException;

  public String visit(
      AggregationOperationInAnalysisSituation aggregationOperationInAnalysisSituation,
      int variableIndex);

  public String visit(SliceConditionWritten<?> sliceConditionWritten, int variableIndex);

  public String visit(SliceConditoinGeneric<?> sliceConditoinGeneric, int variableIndex);

}
