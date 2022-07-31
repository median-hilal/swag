package swag.analysis_graphs.execution_engine.analysis_situations;

public interface ISliceSetSpecification extends ISpecification {

  public ISliceSetDim getSliceSet();

  public void setSliceSet(ISliceSetDim sliceSetDim);

  public void setSliceSetPosition(SlicePositionInAnalysisSituation hm);

  public ISliceSetSpecification shallowCopy();
}
