package swag.predicates;

import java.util.ArrayList;
import java.util.List;

public class Test {

  public List<Object> objects = new ArrayList<>();

  public static void main(String[] args) {

    List<Object> objects11 = new ArrayList<>();

    Test t = new Test();
    t.objects = objects11;

    Object o;
    for (int i = 0; i < 3; i++) {
      o = new Integer(i);
      objects11.add(o);
    }

    objects11 = null;

    Object o1;
    Object o2 = null;

    o1 = o2;

    o2 = "StringHAHA";
  }

}
