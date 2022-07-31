package swag.predicates;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import swag.analysis_graphs.execution_engine.analysis_situations.PredicateVariableToMDElementMapping;
import swag.md_elements.MDElement;

public class LiteralConditionType extends AbstractLiteralCondition {

    private Set<PredicateInputVar> inputVars = new HashSet<>();

    public LiteralConditionType() {
	super();
    }

    public LiteralConditionType(String uri, String comment, String name, String label, String expression,
	    PredicateSyntacticTypes syntacticType, Set<PredicateInputVar> inputVars, Set<PredicateInputVar> positionVar,
	    Set<MDElement> mdElems, Set<PredicateVariableToMDElementMapping> mappings) {

	super(uri, comment, name, label, expression, syntacticType, positionVar, mdElems, mappings);
	this.inputVars = inputVars;
    }

    public String getInputVariableByID(String id) {

	Optional<PredicateInputVar> opt = this.inputVars.stream().filter(x -> x.getVariable().equals(id)).findFirst();

	if (opt.isPresent()) {
	    return opt.get().getUri();
	} else {
	    return null;
	}
    }

    public String getInputVarByUri(String uri) {

	Optional<PredicateInputVar> opt = this.inputVars.stream().filter(x -> x.getUri().equals(uri)).findFirst();

	if (opt.isPresent()) {
	    return opt.get().getVariable();
	} else {
	    return null;
	}
    }

    public void addToInputVars(PredicateInputVar var) {
	this.inputVars.add(var);
    }

    public PredicateInputVar getInputVariableByURI(String varURI) {
	for (PredicateInputVar var : getInputVars()) {
	    if (var.getUri().equals(varURI)) {
		return var;
	    }
	}
	return null;
    }

    public PredicateVar getVariableByURI(String varURI) {

	for (PredicateVar var : getInputVars()) {
	    if (var.getUri().equals(varURI)) {
		return var;
	    }
	}
	for (PredicateVar var : getPositionVar()) {
	    if (var.getUri().equals(varURI)) {
		return var;
	    }
	}
	return null;
    }

    public Set<PredicateInputVar> getInputVars() {
	return inputVars;
    }

    public void setInputVars(Set<PredicateInputVar> inputVars) {
	this.inputVars = inputVars;
    }

    @Override
    public String getTypeLabel() {
	return "";
    }

}
