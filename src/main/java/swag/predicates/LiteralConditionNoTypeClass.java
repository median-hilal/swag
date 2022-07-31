package swag.predicates;

public class LiteralConditionNoTypeClass implements IPredicateNode {

  private Expansion expansion;
  private static final LiteralConditionNoTypeClass singeltonInstance =
      new LiteralConditionNoTypeClass();

  public static LiteralConditionNoTypeClass createInstance() {
    return singeltonInstance;
  }

  private LiteralConditionNoTypeClass() {
    super();
  }

  @Override
  public String getIdentifyingName() {
    return "LiteralConditionNoTypeClass";
  }
}
