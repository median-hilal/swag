package swag.predicates;

public class VariableBinding {

  PredicateInputVar var;
  String value;

  public PredicateInputVar getVar() {
    return var;
  }

  public void setVar(PredicateInputVar var) {
    this.var = var;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public VariableBinding(PredicateInputVar var, String value) {
    super();
    this.var = var;
    if ((value.startsWith("<") && value.endsWith(">")) || value.contains("^^")) {
      this.value = value;
    } else {
      this.value = "<" + value + ">";
    }
  }

  public String generateFilteringString() {
    return var.getVariable() + " = " + value;
  }

  @Override
  public String toString() {
    return this.var.toString() + ":" + this.value;
  }

}
