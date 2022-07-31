package swag.analysis_graphs.execution_engine;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import swag.md_elements.MDElement;
import swag.predicates.LiteralCondition;

/**
 * 
 * Keeps track of the conditions defined in an analysis graph, apart from the
 * fact whether the predicate is used in some analysis situation or not.
 * 
 * @author swag
 *
 */
public class DefinedAGConditions {

    private Set<LiteralCondition> conditions;

    public DefinedAGConditions() {
	conditions = new HashSet<>();
    }

    /**
     * Adds a condition to set of conditions
     * 
     * @param pred
     *            the condition to add
     */
    public void addCondition(LiteralCondition pred) {
	conditions.add(pred);
    }

    /**
     * Gets a condition form the set of conditions using the identifying name of
     * the condition.
     * 
     * @param identifyingName
     * @return
     */
    public LiteralCondition getConditoinByIdentifyingName(String identifyingName) {
	// @formatter:off
	return this.conditions.stream().filter(x -> x.getIdentifyingName().equals(identifyingName))
		.collect(Collectors.toList()).stream().findAny().orElse(null);
	// @formatter:on
    }

    /**
     * Given an MD element, this method returns the conditions in the set that
     * are applicable to the element
     * 
     * @param elem
     *            the MD element to get conditions of.
     * @return a set of conditions applicable to the passed MD element
     */
    public Set<LiteralCondition> getConditionsOfMDPositoin(MDElement elem) {

	Set<LiteralCondition> conds = new HashSet<>();

	if (MDElement.isMultipleElement(elem)) {
	    return conditions;
	} else {
	    for (LiteralCondition cond : this.conditions) {
		if (cond.positionsContainsMDElement(elem)) {
		    conds.add(cond);
		}
	    }
	    return conds;
	}
    }

    /**
     * Given an MD element, this method returns the conditions in the set that
     * are applicable to the element and are of a specific type.
     * 
     * @param elem
     *            the MD element to get conditions of.
     * @param type
     *            the type to check conditions that are instances of it
     * @return a set of conditions applicable to the passed MD element and of
     *         the passed type
     */
    public Set<LiteralCondition> getConditionsOfTypeOfMDPositoin(MDElement elem, String type) {

	Set<LiteralCondition> conds = new HashSet<>();

	if (MDElement.isMultipleElement(elem)) {

	    for (LiteralCondition cond : this.conditions) {
		if (cond.getTypes().contains(type)) {
		    conds.add(cond);
		}
	    }
	    return conds.stream().sorted(Comparator.comparing(LiteralCondition::getLabel)).collect(Collectors.toSet());
	} else {

	    for (LiteralCondition cond : this.conditions) {
		if (cond.positionsContainsMDElement(elem) && cond.getTypes().contains(type)) {
		    conds.add(cond);
		}
	    }
	    return conds.stream().sorted(Comparator.comparing(LiteralCondition::getLabel)).collect(Collectors.toSet());
	}
    }

    public String generateConditionsQuery(LiteralCondition pred) {
	return "";
    }
}
