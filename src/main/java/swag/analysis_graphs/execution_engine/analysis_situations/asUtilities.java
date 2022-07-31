package swag.analysis_graphs.execution_engine.analysis_situations;

/**
 * Utility class
 * 
 * @author swag
 */
public final class asUtilities {

  /**
   * 
   * compares two objects while allowing both of them them to be nulls
   * 
   * @param o1 the first object
   * @param o2 the second object
   * 
   * @return true if objects are equal or both of them are null. False otherwise.
   */
  public static boolean equalsWithNull(Object o1, Object o2) {

    if (o1 == null && o2 == null) {
      return true;
    }

    if ((o1 == null && o2 != null) || (o1 != null && o2 == null)) {
      return false;
    }

    return o1.equals(o2);
  }

  /**
   * 
   * compares two objects while not allowing any of them them to be nulls
   * 
   * @param o1 the first object
   * @param o2 the second object
   * 
   * @return true if objects are equal. False if not or any is null.
   */
  public static boolean equalsIfNotNull(Object o1, Object o2) {

    if (o1 == null || o2 == null) {
      return false;
    }

    return o1.equals(o2);
  }

  public static void ifNotNull(Object o) {

  }


}
