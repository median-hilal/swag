package swag.analysis_graphs.execution_engine;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import swag.predicates.LiteralConditionType;

/**
 * 
 * Keeps track of the condition types defined in an analysis graph.
 * 
 * @author swag
 *
 */
public class DefinedAGConditionsTypes {

  private Set<LiteralConditionType> conditionTypes;

  /**
   * Constructor
   */
  public DefinedAGConditionsTypes() {
    conditionTypes = new HashSet<>();
  }

  /**
   * Adds a condition type to the set of condition types
   * 
   * @param pred the conditoin type to add
   */
  public void addConditioType(LiteralConditionType pred) {
    conditionTypes.add(pred);
  }

  /**
   * Gets a condition type that has the name {@code identifyingName}
   * 
   * @param identifyingName the name to check against
   * @return a condition type
   */
  public LiteralConditionType getConditoinTypeByIdentifyingName(String identifyingName) {
    //@formatter:off
    return this.conditionTypes
        .stream()
        .filter(x -> x.getIdentifyingName().equals(identifyingName))
        .collect(Collectors.toList())
        .stream()
        .findAny()
        .orElse(null);
    //@formatter:on
  }
}
