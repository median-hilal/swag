package swag.analysis_graphs.execution_engine.analysis_situations;

import java.io.Serializable;

import javax.naming.OperationNotSupportedException;

import swag.analysis_graphs.execution_engine.IVariablesList;
import swag.web.IVariableVisitor;
import swag.web.VariableStringVisitor;

/**
 * 
 * Super interface for all elements that are allowed to be variables
 * 
 * @author swag
 *
 */
public interface Variable<T extends ISignatureType> extends Serializable, IClonableTo<T> {

    /**
     * Creates a new shallow copy of the calling object
     * 
     * @return a {@code Variable} instance shallow copy of the calling object
     */
    public Variable shallowCopy();

    /**
     * Moves the value of a variable reference to another variable reference.
     * This is done when navigating and moving the variable value from source to
     * target analysis situation. It is undefined whether this value moving is
     * done by reference or by value.
     * 
     * @param sourceVar
     *            variable in the source analysis situation to copy value from.
     */
    public void assignFromSourceVar(Variable sourceVar);

    /**
     * Gets the name of the variable.
     * 
     * @return the name of the variable
     */
    public String getVariableName();

    /**
     * Gets the value of the variable
     * 
     * @return the value of the variable
     */
    public String getVariableValue();

    /**
     * This equality check ignores the value assigned, just checks whether the
     * compared elements express the same compared MD position.
     * 
     * @param o
     *            the object to check positional equality
     * 
     * @return true in case of positional equality, false otherwise
     */
    public boolean equalsPositional(Object o);

    /**
     * Equality check based on each of: Name of the variable, MD position, and
     * state.
     * 
     * @param v
     *            the other variable to check equality against
     * 
     * @return true in case of name, positional, and state equality. Otherwise
     *         false
     */
    public boolean equalsByNameAndPositionAndState(Variable v);

    // actually those are useless, they cannot guarantee the override in its
    // implementing classes.
    // Because it is anyways inherited from Object
    /**
     * Useless
     */
    @Override
    public boolean equals(Object o);

    /**
     * Useless
     */
    @Override
    public int hashCode();

    /**
     * Used to generate web string form the variable.
     * 
     * @param visitor
     * @param variableIndex
     * 
     * @return a web string
     * @throws OperationNotSupportedException
     */
    public String acceptStringVisitor(VariableStringVisitor visitor, int variableIndex)
	    throws OperationNotSupportedException;

    /**
     * Used to bind the variable from another variable by moving the other
     * variable values to the current one.
     * 
     * @param tempVariable
     *            the variable to bind from
     * 
     * @throws OperationNotSupportedException
     *             when binding is not supported
     */
    public void bind(Variable tempVariable) throws OperationNotSupportedException;

    /**
     * Used to unbind the variable values.
     * 
     * @throws OperationNotSupportedException
     *             when unbinding is not supported
     */
    public void unBind() throws OperationNotSupportedException;

    /**
     * Generic method to accept visitors.
     * 
     * @param v
     *            the visitor
     * 
     * @throws OperationNotSupportedException
     *             when the operation performed is not supported
     * @throws Exception
     */
    public void acceptVisitor(IVariableVisitor v) throws OperationNotSupportedException, Exception;

    /**
     * @return
     */
    public Variable getParent();

    /**
     * Gets the base object that contains the variable.
     * 
     * @return the object that contains the variable
     */
    public Object getContainingObject();

    /**
     * If the current element is a variable, it is added to the passed analysis
     * situation variables.
     * 
     * @param asORnv
     */
    public void addToAnalysisSituationOrNavigationStepVariables(IVariablesList asORnv);
}
