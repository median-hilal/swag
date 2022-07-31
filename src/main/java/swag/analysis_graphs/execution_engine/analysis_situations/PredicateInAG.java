package swag.analysis_graphs.execution_engine.analysis_situations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import swag.md_elements.MDElement;
import swag.predicates.PredicateInstance;

/**
 * 
 * A predicate defined in an analysis graph. The predicate is not bound to a specific analysis
 * situation.
 * 
 * @author swag
 *
 */
public class PredicateInAG {

  /**
   * 
   */
  private static final long serialVersionUID = 1246509359136376297L;
  private Set<PredicateVariableToMDElementMapping> varMappings = new HashSet<>();
  private String predicateInstanceURI;
  private PredicateInstance predicateInstance;


  public Set<PredicateVariableToMDElementMapping> getVarMappings() {
    return varMappings;
  }

  public void setVarMappings(Set<PredicateVariableToMDElementMapping> varMappings) {
    this.varMappings = varMappings;
  }

  public String getURI() {
    return predicateInstanceURI;
  }

  public void setURI(String predicateInstanceURI) {
    this.predicateInstanceURI = predicateInstanceURI;
  }

  public PredicateInstance getPredicateInstance() {
    return predicateInstance;
  }

  public void setPredicateInstance(PredicateInstance predicateInstance) {
    this.predicateInstance = predicateInstance;
  }

  public PredicateInAG() {}

  public PredicateInAG(Set<PredicateVariableToMDElementMapping> varMappings,
      String predicateInstanceURI, PredicateInstance predicateInstance) {

    this.varMappings = varMappings;
    this.predicateInstanceURI = predicateInstanceURI;
    this.predicateInstance = predicateInstance;
  }


  public List<MDElement> getElems() {
    List<MDElement> elems = new ArrayList<>();

    for (PredicateVariableToMDElementMapping m : getVarMappings()) {
      elems.add(m.getElem());
    }

    return elems;
  }

  /**
   * 
   * Shallow copy constructor
   * 
   * @param pred a predicate in AS to copy
   * 
   */
  private PredicateInAG(PredicateInAG pred) {

    this.varMappings = pred.getVarMappings();
    this.predicateInstanceURI = pred.getURI();
    this.predicateInstance = pred.getPredicateInstance();
  }


  @Override
  public boolean equals(Object o) {
    if (o instanceof PredicateInAG) {
      PredicateInAG sc = (PredicateInAG) o;
      if (asUtilities.equalsWithNull(this.getPredicateInstance(), sc.getPredicateInstance())
          && asUtilities.equalsWithNull(this.getURI(), sc.getURI())
          && asUtilities.equalsWithNull(this.getVarMappings(), sc.getVarMappings())) {
        return true;
      }
      return true;
    }
    return false;
  }

}
