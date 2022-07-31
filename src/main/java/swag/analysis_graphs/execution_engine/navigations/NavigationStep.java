package swag.analysis_graphs.execution_engine.navigations;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import swag.analysis_graphs.execution_engine.IVariablesList;
import swag.analysis_graphs.execution_engine.analysis_situations.AnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.Variable;
import swag.analysis_graphs.execution_engine.operators.Operation;
import swag.md_elements.Level;

/**
 * 
 * This class represents a navigation step, that is by being performed
 * transforms the source analysis situation into the target analysi ssituatoin.
 * 
 * @author swag
 *
 */
public class NavigationStep implements Serializable, IVariablesList {

    /**
     * 
     */
    private static final long serialVersionUID = -3363569700080632681L;
    private AnalysisSituation source;
    private AnalysisSituation target;
    private String summary;
    private String name;
    private String abbName;
    private List<Operation> operators;
    private Map<Integer, Variable> variables;
    private Map<Variable, Variable> initialVariables; // a copy of variables
						      // that is kept unmodified
						      // during application life
						      // time
    private int numOfVariables;
    private String label;

    /**
     * Constructor
     */
    public NavigationStep() {
	super();
	this.operators = new ArrayList<Operation>();
	this.variables = new HashMap<Integer, Variable>();
	this.initialVariables = new HashMap<Variable, Variable>();
    }

    /**
     * 
     * Constructor
     * 
     * @param source
     *            source analysis situation of the navigation step
     * @param target
     *            target analysis situation of the navigation step
     * @param summary
     *            summary of the navigation step
     * @param name
     *            name of the navigation step
     * @param abbName
     *            abbreviated name of the navigation step
     * @param operators
     *            a list of navigation step operators
     */
    public NavigationStep(AnalysisSituation source, AnalysisSituation target, String summary, String name,
	    String abbName, List<Operation> operators) {
	super();
	this.source = source;
	this.target = target;
	this.summary = summary;
	this.name = name;
	this.abbName = abbName;
	this.operators = operators;
    }

    /**
     * Compares navigations steps based on their URI (name)
     * 
     * @param nv
     *            the navigation operator to compare the value with
     * @return true if have the same URI; false otherwise
     */
    public boolean shallowCompare(NavigationStep nv) {
	if (this.getName().equals(nv.getName()))
	    return true;
	return false;
    }

    @Override
    public Integer getKeyOfVariableInVariables(Variable var) {

	for (Map.Entry<Integer, Variable> entry : variables.entrySet()) {
	    if (entry.getValue().equals(var))
		return entry.getKey();
	}
	return -1;
    }

    @Override
    public Integer getKeyOfVariableInVariablesByPositionalCompare(Variable var) {

	for (Map.Entry<Integer, Variable> entry : variables.entrySet()) {
	    if (entry.getValue().equalsPositional(var))
		return entry.getKey();
	}
	return -1;
    }

    @Override
    public Map<Integer, Variable> shallowCopyInitialVariablesIntoVariablesStyle() {

	Map<Integer, Variable> copied = new HashMap<Integer, Variable>();
	int i = 0;
	for (Map.Entry<Variable, Variable> entry : initialVariables.entrySet()) {
	    copied.put(i++, entry.getKey().shallowCopy());
	}
	return copied;
    }

    @Override
    public List<Variable> getInitialVariablesAllAsList() {
	List<Variable> allInit = new ArrayList<Variable>(initialVariables.keySet());
	return allInit;
    }

    @Override
    public List<Variable> getUnBoundVariables() {

	List<Variable> unbound = new ArrayList<Variable>();
	for (Map.Entry<Variable, Variable> entry : initialVariables.entrySet()) {
	    if (entry.getValue() == null) {
		unbound.add(entry.getKey());
	    }
	}
	return unbound;
    }

    @Override
    public List<Variable> getInitialVariablesThatAreBound() {

	List<Variable> bound = new ArrayList<Variable>();
	for (Map.Entry<Variable, Variable> entry : initialVariables.entrySet()) {
	    if (entry.getValue() != null) {
		bound.add(entry.getKey());
	    }
	}
	return bound;
    }

    @Override
    public Variable getBoundVarOfInitialVar(Variable var) {
	return initialVariables.get(var);
    }

    @Override
    public Variable getInitialVarOfBoundVar(Variable var) {
	for (Map.Entry<Variable, Variable> entry : initialVariables.entrySet()) {
	    if (entry.getValue() != null)
		if (entry.getValue().equals(var))
		    return entry.getKey();
	}
	return null;
    }

    @Override
    public void addToVariables(Variable obj) {
	this.variables.put(numOfVariables++, obj);
    }

    @Override
    public void removeFromVariables(Variable obj) {
	this.variables.remove(obj);
    }

    @Override
    public void addToInitialVariables(Variable v1, Variable v2) {
	try {
	    this.initialVariables.put(v1, v2);
	} catch (Exception ex) {
	    ex.printStackTrace();
	}

    }

    @Override
    public Map<Integer, Variable> shallowCopyVariables() {
	Map<Integer, Variable> copied = new HashMap<Integer, Variable>();

	for (Map.Entry<Integer, Variable> entry : variables.entrySet()) {
	    copied.put(entry.getKey(), entry.getValue().shallowCopy());
	}
	return copied;
    }

    @Override
    public void fillVariablesFrom(List<Object> variables) {

	for (Object o : variables) {
	    if (o instanceof Level) {

	    }
	}
    }

    public Map<Integer, Variable> getVariables() {
	return variables;
    }

    public void setVariables(Map<Integer, Variable> variables) {
	this.variables = variables;
    }

    public Map<Variable, Variable> getInitialVariables() {
	return initialVariables;
    }

    public void setInitialVariables(Map<Variable, Variable> initialVariables) {
	this.initialVariables = initialVariables;
    }

    public String getAbbName() {
	return abbName;
    }

    public void setAbbName(String abbName) {
	this.abbName = abbName;
    }

    public List<Operation> getOperators() {
	return operators;
    }

    public void setOperators(List<Operation> operators) {
	this.operators = operators;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public AnalysisSituation getSource() {
	return source;
    }

    public void setSource(AnalysisSituation source) {
	this.source = source;
    }

    public AnalysisSituation getTarget() {
	return target;
    }

    public void setTarget(AnalysisSituation target) {
	this.target = target;
    }

    public String getSummary() {
	return summary;
    }

    public void setSummary(String summary) {
	this.summary = summary;
    }

    @Override
    public String getUri() {
	return getName();
    }

    public String getLabel() {
	return label;
    }

    public void setLabel(String label) {
	this.label = label;
    }

}
