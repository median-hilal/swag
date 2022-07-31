package swag.analysis_graphs.execution_engine.analysis_situations;

import java.util.ArrayList;
import java.util.List;

import swag.md_elements.MDElement;

public class MultipleSliceSpecification implements IMultipleSliceSpecification {


  /**
   * Should be a fact
   */
  private MDElement slicePosition;

  private static final long serialVersionUID = 5546474950238597990L;
  private List<MDElement> slicePositions;

  private ISetOfComparison set = NoneSet.getNoneSet();

  @Override
  public ISetOfComparison getSet() {
    return set;
  }

  @Override
  public void setSet(ISetOfComparison set) {
    this.set = set;
  }

  public List<MDElement> getSlicePositions() {
    return slicePositions;
  }

  public void setSlicePositions(List<MDElement> slicePositions) {
    this.slicePositions = slicePositions;
  }

  private PredicateInASMultiple predicate;

  public PredicateInASMultiple getPredicate() {
    return predicate;
  }

  @Override
  public void setPredicate(PredicateInASMultiple predicate) {
    this.predicate = predicate;
  }

  public MultipleSliceSpecification(ISetOfComparison set) {
    this.set = set;
  }

  public MultipleSliceSpecification() {
    super();
    this.slicePositions = new ArrayList<MDElement>();
  }

  public MultipleSliceSpecification(SlicePositionInAnalysisSituation slicePosition,
      List<MDElement> slicePositions, PredicateInASMultiple predicate) {
    this.slicePosition = slicePosition;
    this.slicePositions = slicePositions;
    this.predicate = predicate;
  }

  public MultipleSliceSpecification(SlicePositionInAnalysisSituation slicePosition,
      List<MDElement> slicePositions, PredicateInASMultiple predicate, ISetOfComparison set) {
    this(slicePosition, slicePositions, predicate);
    this.set = set;
  }


  @Override
  public void addToAnalysisSituationVariables(AnalysisSituation as) {
    if (this.getPredicate() != null && this.getPredicate().getSignature().isVariable())
      as.addToVariables(this.getPredicate());
  }

  @Override
  public boolean positionAsMDElementBasedEquals(ISpecification si) {
    return this.getListOfPositions().equals(si.getListOfPositions());
  }

  @Override
  public MultipleSliceSpecification shallowCopy() {
    MultipleSliceSpecification sliceSpecificaiotn = new MultipleSliceSpecification();
    sliceSpecificaiotn.slicePosition = this.slicePosition;
    sliceSpecificaiotn.slicePositions = this.slicePositions;
    sliceSpecificaiotn.predicate = (PredicateInASMultiple) this.predicate.shallowCopy();
    sliceSpecificaiotn.set = this.set.shallowCopy();
    return sliceSpecificaiotn;

  }

  @Override
  public boolean isDue() {
    // TODO Auto-generated method stub
    return false;
  }

  public void setPosition(MDElement slicePosition) {
    this.slicePosition = slicePosition;
  }

  @Override
  public MDElement getPosition() {
    return this.slicePosition;
  }

}
