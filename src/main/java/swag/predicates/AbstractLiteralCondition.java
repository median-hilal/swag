package swag.predicates;

import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import swag.analysis_graphs.execution_engine.analysis_situations.PredicateVariableToMDElementMapping;
import swag.analysis_graphs.execution_engine.operators.IRDFElement;
import swag.md_elements.MDElement;

/**
 * 
 * Abstraction of a predicate.
 * 
 * @author swag
 *
 */
public abstract class AbstractLiteralCondition implements IPredicateNode, IRDFElement {

  private String uri;
  private String comment;
  private String name;
  private String expression;
  private String label;
  private PredicateSyntacticTypes syntacticType = PredicateSyntacticTypes.DEFAULT;
  private Set<PredicateInputVar> positionVar = new HashSet<>();
  private Set<MDElement> mdElems = new HashSet<>();
  private Set<PredicateVariableToMDElementMapping> mappings = new HashSet<>();

  public AbstractLiteralCondition() {
    super();
  }

  public AbstractLiteralCondition(String uri, String comment, String name, String label,
      String expression, PredicateSyntacticTypes syntacticType, Set<PredicateInputVar> positionVar,
      Set<MDElement> mdElems, Set<PredicateVariableToMDElementMapping> mappings) {
    super();
    this.uri = uri;
    this.comment = comment;
    if (StringUtils.isEmpty(name)) {
      if (uri.contains("#")) {
        name = uri.substring(uri.lastIndexOf("#"), uri.length());
      } else {
        name = uri.substring(uri.lastIndexOf("/"), uri.length());
      }
    }
    this.name = name;
    if (!StringUtils.isEmpty(label)) {
      this.label = label;
    } else {
      this.label = name;
    }
    this.expression = expression;
    this.syntacticType = syntacticType;
    this.positionVar = positionVar;
    this.mdElems = mdElems;
    this.mappings = mappings;
  }

  public void addToPositionVars(PredicateInputVar var) {
    this.positionVar.add(var);
  }

  public void addToMDElems(MDElement elm) {
    this.mdElems.add(elm);
  }

  public void addToMappings(PredicateVariableToMDElementMapping mapping) {
    this.mappings.add(mapping);
  }

  public PredicateInputVar getPositionVariableByURI(String varURI) {
    for (PredicateInputVar var : getPositionVar()) {
      if (var.getUri().equals(varURI)) {
        return var;
      }
    }
    return null;
  }

  @Override
  public String getIdentifyingName() {
    return this.getURI();
  }

  public String getExpression() {
    return expression;
  }

  public void setExpression(String expression) {
    this.expression = expression;
  }

  public PredicateSyntacticTypes getSyntacticType() {
    return syntacticType;
  }

  public void setSyntacticType(PredicateSyntacticTypes syntacticType) {
    this.syntacticType = syntacticType;
  }

  public Set<PredicateInputVar> getPositionVar() {
    return positionVar;
  }

  public void setPositionVar(Set<PredicateInputVar> positionVar) {
    this.positionVar = positionVar;
  }

  public Set<MDElement> getMdElems() {
    return mdElems;
  }

  public void setMdElems(Set<MDElement> mdElems) {
    this.mdElems = mdElems;
  }

  public Set<PredicateVariableToMDElementMapping> getMappings() {
    return mappings;
  }

  public void setMappings(Set<PredicateVariableToMDElementMapping> mappings) {
    this.mappings = mappings;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    } else {
      if (o instanceof AbstractLiteralCondition) {
        AbstractLiteralCondition that = (AbstractLiteralCondition) o;
        if (this.getURI().equals(that.getURI())) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
        append(getURI()).toHashCode();
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getURI() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

}
