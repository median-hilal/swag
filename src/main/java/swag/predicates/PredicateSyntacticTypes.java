package swag.predicates;

public enum PredicateSyntacticTypes {
  FILTER, DEFAULT, HAVING;

  /**
   * 
   * Given a string this function finds out which predicate type it represents.
   * 
   * @param type
   * @return
   */
  public static PredicateSyntacticTypes findSyntacticType(String type) {

    switch (type.toUpperCase()) {
      case "FILTER":
        return PredicateSyntacticTypes.FILTER;
      case "HAVING":
        return PredicateSyntacticTypes.HAVING;
      case "DEFAULT":
        return PredicateSyntacticTypes.DEFAULT;
      default:
        return PredicateSyntacticTypes.DEFAULT;
    }
  }

}
