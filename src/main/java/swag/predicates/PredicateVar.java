package swag.predicates;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class PredicateVar {

  private String type;
  private String uri;
  private String variable;

  public String getVariable() {
    return variable;
  }

  public void setVariable(String variable) {
    this.variable = variable;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getUri() {
    return uri;
  }

  public void setUri(String name) {
    this.uri = name;
  }

  public PredicateVar(String type, String uri, String variable) {
    super();
    this.type = type;
    this.uri = uri;
    this.variable = variable;
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 31).append(this.getUri()).toHashCode();
  }

  @Override
  public boolean equals(Object obj) {

    if (this == obj) {
      return true;
    }

    if (obj instanceof PredicateVar) {
      PredicateVar var = (PredicateVar) obj;

      if (this.getUri().equals(var.getUri())) {
        return true;
      }
    }

    return false;
  }

  /**
   * 
   * Creates a deep copy of the object
   * 
   * @return a new deeply copied {@code PredicateVar}
   * 
   */
  public PredicateVar deepCopy() {
    return new PredicateVar(this.type, this.uri, this.variable);
  }

  @Override
  public String toString() {
    return variable;
  }



}
