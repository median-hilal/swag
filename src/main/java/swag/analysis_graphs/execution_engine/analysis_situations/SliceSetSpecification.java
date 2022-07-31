package swag.analysis_graphs.execution_engine.analysis_situations;

import swag.md_elements.MDElement;

public class SliceSetSpecification implements ISliceSetSpecification {

  /**
   * 
   */
  private static final long serialVersionUID = -6393709634868874347L;
  private MDElement slicePosition;
  private ISliceSetDim sliceSetDim;

  private ISetOfComparison set = NoneSet.getNoneSet();

  @Override
  public ISetOfComparison getSet() {
    return set;
  }

  @Override
  public void setSet(ISetOfComparison set) {
    this.set = set;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o instanceof SliceSetSpecification) {
      SliceSetSpecification s = (SliceSetSpecification) o;
      if (this.getPosition().equals(s.getPosition())
          && this.getSliceSet().equals(s.getSliceSet())) {
        return true;
      }
    }
    return false;
  }

  public MDElement getPosition() {
    return this.slicePosition;
  }

  public void setPosition(SlicePositionInAnalysisSituation slicePosition) {
    this.slicePosition = slicePosition;
  }

  public ISliceSetDim getSliceSet() {
    return this.sliceSetDim;
  }

  public void setSliceSet(ISliceSetDim sliceSetDim) {
    this.sliceSetDim = sliceSetDim;
  }


  public SliceSetSpecification() {}

  public SliceSetSpecification(ISetOfComparison set) {
    this.set = set;
  }

  public SliceSetSpecification(SlicePositionInAnalysisSituation slicePosition) {
    this.slicePosition = slicePosition;
  }

  public SliceSetSpecification(SlicePositionInAnalysisSituation slicePosition,
      ISliceSetDim sliceSetDim) {
    this(slicePosition);
    this.sliceSetDim = sliceSetDim;
  }

  public SliceSetSpecification(SlicePositionInAnalysisSituation slicePosition,
      ISliceSetDim sliceSetDim, ISetOfComparison set) {
    this(slicePosition, sliceSetDim);
    this.set = set;
  }

  public SliceSetSpecification shallowCopy() {

    SliceSetSpecification sliceSetSpec = new SliceSetSpecification();
    sliceSetSpec.slicePosition = new MDElement(this.slicePosition);
    sliceSetSpec.sliceSetDim = this.sliceSetDim.shallowCopy();
    sliceSetSpec.set = this.set.shallowCopy();
    return sliceSetSpec;
  }

  public boolean positionAsMDElementBasedEquals(ISpecification si) {
    if (si instanceof SliceSetSpecification) {
      SliceSetSpecification imp = (SliceSetSpecification) si;

      if (imp.getPosition().equals(this.getPosition())) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void addToAnalysisSituationVariables(AnalysisSituation as) {
    if (this.getSliceSet() != null && this.getSliceSet().getSignature().isVariable())
      as.addToVariables(this.getSliceSet());
  }


  @Override
  public boolean isDue() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setSliceSetPosition(SlicePositionInAnalysisSituation hm) {
    this.slicePosition = hm;

  }

}
