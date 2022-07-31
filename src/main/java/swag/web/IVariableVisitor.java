package swag.web;

import javax.naming.OperationNotSupportedException;

import swag.analysis_graphs.execution_engine.analysis_situations.AggregationOperationInAnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.DiceNodeInAnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.IMeasure;
import swag.analysis_graphs.execution_engine.analysis_situations.IMeasureInAS;
import swag.analysis_graphs.execution_engine.analysis_situations.ISliceMultiplePosition;
import swag.analysis_graphs.execution_engine.analysis_situations.ISliceSetMsr;
import swag.analysis_graphs.execution_engine.analysis_situations.ISliceSetMultiple;
import swag.analysis_graphs.execution_engine.analysis_situations.ISliceSinglePositionNoType;
import swag.analysis_graphs.execution_engine.analysis_situations.ISliceSinglePositionTyped;
import swag.analysis_graphs.execution_engine.analysis_situations.LevelInAnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.MeasureAggregated;
import swag.analysis_graphs.execution_engine.analysis_situations.MeasureAggregatedInAS;
import swag.analysis_graphs.execution_engine.analysis_situations.MeasureDerived;
import swag.analysis_graphs.execution_engine.analysis_situations.MeasureDerivedInAS;
import swag.analysis_graphs.execution_engine.analysis_situations.MeasureInAnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.PredicateInASMultiple;
import swag.analysis_graphs.execution_engine.analysis_situations.PredicateInASSimple;
import swag.analysis_graphs.execution_engine.analysis_situations.SliceConditoinGeneric;
import swag.analysis_graphs.execution_engine.analysis_situations.SlicePositionInAnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.SliceSet;

/**
 * 
 * Interface that abstractly represents a visitor than can be accepted by IVariable implementations
 * in order to perform a specific type of processing.
 * 
 * @author swag
 *
 */
public interface IVariableVisitor {

  public void visit(ISliceSetMsr sliceSetMsr);

  public void visit(IMeasure msr);

  public void visit(MeasureInAnalysisSituation msr);

  public void visit(IMeasureInAS msr);

  public void visit(MeasureDerivedInAS msr) throws Exception;

  public void visit(MeasureDerived msr) throws Exception;

  public void visit(MeasureAggregatedInAS msr);

  public void visit(MeasureAggregated msr) throws Exception;

  public void visit(LevelInAnalysisSituation level);

  public void visit(DiceNodeInAnalysisSituation node) throws OperationNotSupportedException;

  public void visit(SlicePositionInAnalysisSituation position);

  public void visit(SliceSet conditoin);

  public void visit(ISliceSetMultiple conditoin);

  public void visit(PredicateInASMultiple predicate) throws OperationNotSupportedException;

  public void visit(PredicateInASSimple predicate) throws OperationNotSupportedException;

  public void visit(AggregationOperationInAnalysisSituation aggOp);

  public void visit(ISliceMultiplePosition<?> pos) throws Exception;

  public void visit(ISliceSinglePositionNoType<?> pos) throws Exception;

  public void visit(ISliceSinglePositionTyped<?> pos) throws Exception;

  public void visit(SliceConditoinGeneric<?> sliceConditoinGeneric);
}
