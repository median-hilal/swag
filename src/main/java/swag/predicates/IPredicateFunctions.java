package swag.predicates;

import java.util.List;
import org.apache.jena.query.Query;

import swag.md_elements.MDSchema;

/**
 * 
 * Main functionalities related to swag.predicates
 * 
 * @author swag
 *
 */
public interface IPredicateFunctions {


  /**
   * 
   * Gets all the literal condition types defined in a specific scope (e.g., a graph made from a
   * read file)
   * 
   * @return a {@code List} of all {@code LiteralConditionType} in scope.
   * 
   */
  public List<LiteralConditionType> getAllLiteralConditionTypes(MDSchema schema);


  /**
   * 
   * Gets all the literal conditions in a specific scope (e.g., a graph made from a read file)
   * 
   * @return a {@code List} of all {@code LiteralCondition} in scope.
   * 
   */
  public List<LiteralCondition> getAllConditions(MDSchema schema);



  /**
   * @param schema
   * @return
   */
  public List<LiteralCondition> getAllConditionTypeInstances(MDSchema schema, String conditionType);


  /**
   * 
   * Gets all the swag.predicates in a specific scope (e.g., a graph made from a read file)
   * 
   * @return a {@code List} of all {@code Predicate} in scope.
   * 
   */
  public List<Predicate> getAllPredicates();

  /**
   * 
   * Gets all the instances of the given {@code predicateURI} in a specific scope (e.g., a graph
   * made from a read file).
   * 
   * @param predicateURI the URI identifier of the predicate to get its instances.
   * 
   * @return a {@code List} of all {@code PredicateInstance}, of the provided predicate, that are in
   *         scope.
   * 
   */
  public List<PredicateInstance> getAllPredicateInstances(String predicateURI);

  /**
   * 
   * Gets all the instances of the given {@code predicateURI} in a specific scope (e.g., a graph
   * made from a read file). These instances should have been created by assigning a value only to
   * the subject variable. Refer to {@link Predicate}.
   * 
   * @param predicateURI the URI identifier of the predicate to get its subject instances.
   * 
   * @return a {@code List} of all {@code PredicateInstance}, of the provided predicate, that are in
   *         scope.
   * 
   */
  public List<PredicateInstance> generateAllSubjectPredicateInstances(String predicateURI);

  /**
   * 
   * Creates a {@code PredicateInstance}, as an instance of {@code predicateURI}, by attaching the
   * provided bindings {@code bindings} to the created {@code PredicateInstance}
   * 
   * @param predicateURI the URI identifier of the predicate to create an instance of.
   * @param bindings a {@code List} of {@code VariableBinding} that attaches a value to a variable
   *        each.
   * 
   * @return a {@code PredicateInstance}
   * 
   */
  public PredicateInstance createPredicateInstance(String predicateURI,
      List<VariableBinding> bindings);

  /**
   * 
   * Instantiates the query of the {@code Predicate} that the passed instance
   * {@code predicateURIInstance} is an instance of, by injecting the variables bindings of the
   * predicate in the query.
   * 
   * @param predicateURIInstance the instance to generate a query for
   *
   * @return a SPARQL query
   * 
   */
  public Query generatePredicateInstanceQuery(String predicateURIInstance);
}
