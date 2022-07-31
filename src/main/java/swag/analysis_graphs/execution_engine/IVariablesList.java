package swag.analysis_graphs.execution_engine;

import java.util.List;
import java.util.Map;

import swag.analysis_graphs.execution_engine.analysis_situations.Variable;

/**
 * 
 * Contract for variables list functionalities.
 * 
 * @author swag
 *
 */
public interface IVariablesList {

  /**
   * @return
   */
  public String getUri();

  /**
   * gets the key of a specific variable in variables, -1 if not found
   * 
   * @param var the variable to get its index
   * @return the index
   */
  public Integer getKeyOfVariableInVariables(Variable var);

  /**
   * positional compare avoids comparing actual values of bound variables gets the key of a specific
   * variable in variables, -1 if not found
   * 
   * @param var the variable to get its index
   * @return the index
   */
  public Integer getKeyOfVariableInVariablesByPositionalCompare(Variable var);

  /**
   * shallow copies each key variable in initial variables into a map
   * 
   * @return
   */
  public Map<Integer, Variable> shallowCopyInitialVariablesIntoVariablesStyle();

  /**
   * @return
   */
  public List<Variable> getInitialVariablesAllAsList();

  /**
   * Gets the variables of the object which are unbound
   * 
   * @return a list of variables
   */
  public List<Variable> getUnBoundVariables();

  /**
   * Gets the initial variables of the object, which are bound
   * 
   * @return a list of variables
   */
  public List<Variable> getInitialVariablesThatAreBound();

  /**
   * gets how the value of the variable became after being bound
   * 
   * @param var the variable to see its bound value
   * @return the bound variable, null if not found
   */
  public Variable getBoundVarOfInitialVar(Variable var);

  /**
   * gets how the initial value of a bound variable
   * 
   * @param var the bound variable to see its initial value
   * @return the initial variable, null if not found
   */
  public Variable getInitialVarOfBoundVar(Variable var);

  /**
   * Adds the passed variable to the variables of the object
   * 
   * @param obj the variable to add
   */
  public void addToVariables(Variable obj);

  /**
   * Removes the passed variable to the variables of the object
   * 
   * @param obj the variable to remove
   */
  public void removeFromVariables(Variable obj);

  /**
   * Adds the passed variable to the initial variables of the object
   * 
   * @param obj the variable to add
   */
  public void addToInitialVariables(Variable v1, Variable v2);

  /**
   * Shallow copy
   * 
   * @return a shallow copy
   */
  public Map<Integer, Variable> shallowCopyVariables();

  /**
   * @param variables
   */
  public void fillVariablesFrom(List<Object> variables);

}
