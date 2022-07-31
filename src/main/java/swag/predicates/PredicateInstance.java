package swag.predicates;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.jena.query.QuerySolution;

public class PredicateInstance implements IPredicateNode {

  private String uri;
  private String name;
  private String description;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public boolean isSubjectInstance() {
    if (bindingsContainOnlyVariable(getInstanceOf().getSubjectVar())) {
      return true;
    }
    return false;
  }


  private Predicate instanceOf;
  private List<PredicateOutputVar> projectionVars = new ArrayList<>();
  private List<VariableBinding> bindings = new ArrayList<>();
  private List<String> topics = new ArrayList<>();
  private List<QuerySolution> solutions = new ArrayList<>();


  public Predicate getInstanceOf() {
    return instanceOf;
  }

  public void setInstanceOf(Predicate instanceOf) {
    this.instanceOf = instanceOf;
  }

  public List<PredicateOutputVar> getSelectionVars() {
    return projectionVars;
  }

  public void setProjectionVars(List<PredicateOutputVar> projectionVars) {
    this.projectionVars = projectionVars;
  }

  public void addProjectionVar(PredicateOutputVar var) {
    this.projectionVars.add(var);
  }

  public List<VariableBinding> getBindings() {
    return bindings;
  }

  public void setBindings(List<VariableBinding> bindings) {
    this.bindings = bindings;
  }

  public void addVarBinding(VariableBinding binding) {
    this.bindings.add(binding);
  }

  public List<String> getTopics() {
    return topics;
  }

  public void setTopics(List<String> topics) {
    this.topics = topics;
  }

  public void addTopic(String topic) {
    this.topics.add(topic);
  }

  public List<QuerySolution> getSolutions() {
    return solutions;
  }

  public void setSolutions(List<QuerySolution> solutions) {
    this.solutions = solutions;
  }

  public void addSolution(QuerySolution sol) {
    this.solutions.add(sol);
  }

  public PredicateInstance(String uri, String name, String description, Predicate instanceOf,
      List<VariableBinding> bindings) {
    super();
    this.uri = uri;
    this.name = name;
    this.description = description;
    this.instanceOf = instanceOf;
    this.bindings = bindings;

  }

  public PredicateInstance(Predicate instanceOf, List<VariableBinding> bindings) {
    super();
    this.instanceOf = instanceOf;
    this.bindings = bindings;
    this.uri = getIdentifyingName();
    this.name = getGeneratedName();
    this.description = getGeneratedDescription();
  }

  public boolean bindingsContainVariable(PredicateInputVar var) {

    for (VariableBinding bind : this.getBindings()) {
      if (bind.getVar().equals(var)) {
        return true;
      }
    }
    return false;
  }

  public boolean bindingsContainOnlyVariable(PredicateInputVar var) {

    if (this.getBindings().size() == 1) {
      for (VariableBinding bind : this.getBindings()) {
        if (bind.getVar().equals(var)) {
          return true;
        }
      }
    }
    return false;
  }

  public PredicateInstance(Predicate instanceOf, List<PredicateOutputVar> projectionVars,
      List<VariableBinding> bindings, List<String> topics, List<QuerySolution> solutions) {
    super();
    this.instanceOf = instanceOf;
    this.projectionVars = projectionVars;
    this.bindings = bindings;
    this.topics = topics;
    this.solutions = solutions;
  }

  public PredicateInstance() {
    super();
  }

  @Override
  public String getIdentifyingName() {

    String vars = "";
    String binds = "";

    for (PredicateOutputVar var : this.getSelectionVars()) {
      vars += var.toString() + "//";
    }

    for (VariableBinding bind : this.getBindings()) {
      binds += bind.toString() + "//";
    }

    if (StringUtils.isEmpty(this.getUri())) {
      return this.instanceOf.getIdentifyingName() + "//" + vars + "//" + binds;
    } else {
      return this.getUri();
    }
  }

  public String getGeneratedName() {

    return "";
  }

  public String getGeneratedDescription() {

    return "";
  }



  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
        append(getUri()).toHashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    } else {
      if (o instanceof Predicate) {
        PredicateInstance that = (PredicateInstance) o;
        if (this.getUri().equals(that.getUri())) {
          return true;
        }
      }
    }
    return false;
  }

}
