package swag.predicates;

public class LiteralConditionRootClass implements IPredicateNode {

  private Expansion expansion;
  private static final LiteralConditionRootClass singeltonInstance =
      new LiteralConditionRootClass();

  public static LiteralConditionRootClass createInstance() {
    return singeltonInstance;
  }

  private LiteralConditionRootClass() {
    super();
  }

  @Override
  public String getIdentifyingName() {
    return "LiteralConditionRootClass";
  }
}
