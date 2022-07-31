package swag.predicates;

import java.util.HashSet;
import java.util.Set;

public class Utils {

  public static String getStringValueIfNotNull(Object obj) {
    if (obj != null) {
      return obj.toString();
    } else {
      return null;
    }
  }

  public static String stringToUriString(String uri) {
    if (uri != null) {
      return "<" + uri + ">";
    } else {
      return "<>";
    }

  }

  public static String removeEscapeCharacter(String query) {

    query = query.replaceAll("\\\\\"", "\"");
    return query;
  }

  public static Set<Set<Object>> cartesianProduct(Set<?>... sets) {
    if (sets.length < 2)
      throw new IllegalArgumentException(
          "Can't have a product of fewer than two sets (got " + sets.length + ")");

    return _cartesianProduct(0, sets);
  }

  private static Set<Set<Object>> _cartesianProduct(int index, Set<?>... sets) {
    Set<Set<Object>> ret = new HashSet<Set<Object>>();
    if (index == sets.length) {
      ret.add(new HashSet<Object>());
    } else {
      for (Object obj : sets[index]) {
        for (Set<Object> set : _cartesianProduct(index + 1, sets)) {
          set.add(obj);
          ret.add(set);
        }
      }
    }
    return ret;
  }
}
