package swag.predicates;

public class PredicateClass implements IPredicateNode {

  private Expansion expansion;
  private static final PredicateClass singeltonInstance = new PredicateClass();

  public static PredicateClass createInstance() {
    return singeltonInstance;
  }

  private PredicateClass() {
    super();
  }

  @Override
  public String getIdentifyingName() {
    return "PredicateClass";
  }
}
