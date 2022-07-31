package swag.predicates;

public interface IPredicateGraphBuilder {

  /**
   * 
   * Provided the swag.predicates rdf file, this function creates a graph of all swag.predicates and their
   * defined instances. Remote SPARQL endpoint is not considered for generating new predicate
   * instances from defined swag.predicates.
   * 
   * @param pathToPredicatesFile the path to the file of the swag.predicates
   * 
   * @return a graph of swag.predicates and their instances
   * 
   */
  public IPredicateGraph buildPredicatesGraphWitoutRemoteAccess(String pathToPredicatesFile);

}
