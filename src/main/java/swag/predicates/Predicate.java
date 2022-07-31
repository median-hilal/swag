package swag.predicates;

import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Predicate implements IPredicateNode {

  private String uri;
  private Set<PredicateInputVar> inputVars = new HashSet<>();
  private Set<PredicateOutputVar> outputVars = new HashSet<>();
  private Set<String> topics = new HashSet<>();
  private PredicateOutputVar subjectVar;
  private PredicateOutputVar descriptionVar;
  private Query query;

  public PredicateInputVar getInputVariableByURI(String varURI) {
    for (PredicateInputVar var : getInputVars()) {
      if (var.getUri().equals(varURI)) {
        return var;
      }
    }
    return null;
  }

  public PredicateOutputVar getOutputVariableByURI(String varURI) {
    for (PredicateOutputVar var : getOutputVars()) {
      if (var.getUri().equals(varURI)) {
        return var;
      }
    }
    return null;
  }



  public PredicateVar getVariableByURI(String varURI) {

    if (varURI.equals(getSubjectVar().getUri())) {
      return subjectVar;
    }

    if (varURI.equals(getDescriptionVar().getUri())) {
      return descriptionVar;
    }

    for (PredicateVar var : getInputVars()) {
      if (var.getUri().equals(varURI)) {
        return var;
      }
    }
    for (PredicateVar var : getOutputVars()) {
      if (var.getUri().equals(varURI)) {
        return var;
      }
    }
    return null;
  }

  public Query getQuery() {
    return query;
  }

  public void setQuery(Query query) {
    this.query = query;
  }

  public PredicateOutputVar getDescriptionVar() {
    return descriptionVar;
  }

  public void setDescriptionVar(PredicateOutputVar descriptionVar) {
    this.descriptionVar = descriptionVar;
  }

  public Set<PredicateInputVar> getInputVars() {
    return inputVars;
  }

  public void addToInputVars(PredicateInputVar var) {
    this.inputVars.add(var);
  }

  public Set<PredicateOutputVar> getOutputVars() {
    return outputVars;
  }

  public void addTooUTputVars(PredicateOutputVar var) {
    this.outputVars.add(var);
  }

  public Set<String> getTopics() {
    return topics;
  }

  public void addToTopics(String topic) {
    this.topics.add(topic);
  }

  public PredicateOutputVar getSubjectVar() {
    return subjectVar;
  }

  public void setSubjectVar(PredicateOutputVar subjectVar) {
    this.subjectVar = subjectVar;
  }

  public Predicate() {
    super();
  }

  public String getURI() {
    return uri;
  }

  public void setURI(String uri) {
    this.uri = uri;
  }

  public Predicate(String name, Set<PredicateInputVar> inputVars,
      Set<PredicateOutputVar> outputVars, Set<String> topics, PredicateOutputVar subjectVar,
      PredicateOutputVar descriptionVar, Query query) {
    super();
    this.uri = name;
    this.inputVars = inputVars;
    this.outputVars = outputVars;
    this.topics = topics;
    this.subjectVar = subjectVar;
    this.descriptionVar = descriptionVar;
    this.query = query;
  }

  @Override
  public String getIdentifyingName() {
    return this.getURI();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
        append(getURI()).toHashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    } else {
      if (o instanceof Predicate) {
        Predicate that = (Predicate) o;
        if (this.getURI().equals(that.getURI())) {
          return true;
        }
      }
    }
    return false;
  }
}
