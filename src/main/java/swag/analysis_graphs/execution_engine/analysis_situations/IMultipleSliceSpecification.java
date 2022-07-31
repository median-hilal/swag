package swag.analysis_graphs.execution_engine.analysis_situations;

public interface IMultipleSliceSpecification extends ISpecification {

  public PredicateInASMultiple getPredicate();

  public void setPredicate(PredicateInASMultiple predicate);

  public IMultipleSliceSpecification shallowCopy();

}
