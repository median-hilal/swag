package swag.analysis_graphs.execution_engine.analysis_situations;

import java.util.Set;
import java.util.stream.Collectors;
import org.apache.jena.query.Query;
import org.apache.log4j.Logger;

import swag.md_elements.MDElement;
import swag.predicates.PredicateVar;

public class PredicateVariableToMDElementMapping {

  private static final Logger logger = Logger.getLogger(PredicateVariableToMDElementMapping.class);

  private PredicateVar var;
  private MDElement elem;
  private Query connectOver;


  public Query getConnectOver() {
    return connectOver;
  }

  public void setConnectOver(Query connectOver) {
    this.connectOver = connectOver;
  }

  public PredicateVar getVar() {
    return var;
  }

  public void setVar(PredicateVar var) {
    this.var = var;
  }

  public MDElement getElem() {
    return elem;
  }

  public void setElem(MDElement elem) {
    this.elem = elem;
  }

  public PredicateVariableToMDElementMapping() {

  }

  public PredicateVariableToMDElementMapping(PredicateVar var, MDElement elem, Query connectOver) {
    super();
    this.var = var;
    this.elem = elem;
    this.connectOver = connectOver;
    if (connectOver == null) {
      logger.warn("ConnectOver string is null");
    }
  }

  public PredicateVariableToMDElementMapping(PredicateVar var, MDElement elem) {
    this(var, elem, null);
  }

  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }

    if (o instanceof PredicateVariableToMDElementMapping) {
      PredicateVariableToMDElementMapping that = (PredicateVariableToMDElementMapping) o;
      if (this.getElem().equals(that.getElem()) && this.getVar().equals(that.getVar())
          && this.connectOver.equals(that.connectOver)) {
        return true;
      }
    }
    return false;
  }

  /**
   * 
   * Creates a new deep copy of the object.
   * 
   * @return
   */
  public PredicateVariableToMDElementMapping deepCopy() {
    return new PredicateVariableToMDElementMapping(this.var.deepCopy(), this.elem.deepCopy(),
        this.connectOver.cloneQuery());
  }

  /**
   * 
   * Creates a new deep copy of the passed set.
   * 
   * @param mappings the set to deeply copy
   * 
   * @return a new set that is a deep copy of the passed set
   * 
   */
  public static Set<PredicateVariableToMDElementMapping> deepCopySetOfPredicateVariableToMDElementMapping(
      Set<PredicateVariableToMDElementMapping> mappings) {
    return mappings.stream().map(x -> x.deepCopy()).collect(Collectors.toSet());
  }

}
