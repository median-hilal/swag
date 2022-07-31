package swag.predicates;

public class VariableSelection {

  PredicateVar var;
  String value;

  public PredicateVar getVar() {
    return var;
  }

  public void setVar(PredicateVar var) {
    this.var = var;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }



  public VariableSelection(PredicateVar var, String value) {
    super();
    this.var = var;
    this.value = value;
  }

  public String toStringConditoin() {
    return var + " = " + value;
  }
}
