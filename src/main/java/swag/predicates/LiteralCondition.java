package swag.predicates;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import swag.analysis_graphs.execution_engine.analysis_situations.PredicateVariableToMDElementMapping;
import swag.md_elements.MDElement;

/**
 * 
 * A expression composed of a uri, a position, and a string conditoin.
 * 
 * @author swag
 *
 */
public class LiteralCondition extends AbstractLiteralCondition {

    /**
     * 
     */
    private static final long serialVersionUID = 8697955623759125163L;

    private LiteralConditionType directType;
    private Map<String, String> bindings = new HashMap<>();
    private Set<String> types = new HashSet<>();

    /**
     * Gets the input variables of the direct type of the condition.
     * 
     * @return a set of input variables
     */
    public Set<PredicateInputVar> getDirectTypeInputVars() {
	return directType.getInputVars();
    }

    public void addToTypes(String type) {
	this.types.add(type);
    }

    /**
     * Gets the ID of an input variable by uri
     * 
     * @param uri
     *            the uri of the input variable
     * @return the ID of the variable
     */
    public String getDirectInputVarByUri(String uri) {
	return directType.getInputVarByUri(uri);
    }

    public LiteralCondition(LiteralConditionType directType, Set<String> types, String uri, String comment, String name,
	    String label, String expression, PredicateSyntacticTypes syntacticType, Set<PredicateInputVar> positionVar,
	    Set<MDElement> mdElems, Set<PredicateVariableToMDElementMapping> mappings) {

	super(uri, comment, name, label, expression, syntacticType, positionVar, mdElems, mappings);
	setDirectType(directType);
	setTypes(types);
    }

    /**
     * 
     * Creates a literal condition where its type is already providwd
     * 
     * @param type
     *            the type of the literal condition to get the data from
     * @param paramValue
     *            the value to bind for the condition
     * @param expression
     *            the expression of the condition
     */
    public LiteralCondition(LiteralConditionType type, String paramValue, String expression) {

	super(type.getURI() + paramValue, type.getComment() + paramValue, type.getName() + " " + paramValue,
		type.getLabel() + " " + paramValue, expression, type.getSyntacticType(), type.getPositionVar(),
		type.getMdElems(), type.getMappings());

	getTypes().add(type.getURI());
	setDirectType(directType);
    }

    public LiteralCondition() {
	// TODO Auto-generated constructor stub
    }

    public Map<String, String> getBindings() {
	return bindings;
    }

    public void setBindings(Map<String, String> bindings) {
	this.bindings = bindings;
    }

    public boolean positionsContainsMDElement(MDElement elem) {
	return getMdElems().contains(elem);
    }

    public LiteralConditionType getDirectType() {
	return directType;
    }

    public void setDirectType(LiteralConditionType directType) {
	this.directType = directType;
    }

    public Set<String> getTypes() {
	return types;
    }

    public void setTypes(Set<String> types) {
	this.types = types;
    }

    @Override
    public String getTypeLabel() {
	// TODO Auto-generated method stub
	return "";
    }

}
