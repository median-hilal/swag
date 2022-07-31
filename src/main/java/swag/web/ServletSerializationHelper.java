package swag.web;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;

public class ServletSerializationHelper {

  public static Object deSerialize(String s) throws IOException, ClassNotFoundException {
    byte[] data = Base64.getDecoder().decode(s);
    ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
    Object o = ois.readObject();
    ois.close();
    return o;
  }

  /** Write the object to a Base64 string. */
  public static String serialize(Object o) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(baos);
    oos.writeObject(o);
    oos.close();
    return Base64.getEncoder().encodeToString(baos.toByteArray());
  }

  public static String serialize1(Object myObject) throws Exception {
    String serializedObject = "";
    // serialize the object
    try {
      ByteArrayOutputStream bo = new ByteArrayOutputStream();
      ObjectOutputStream so = new ObjectOutputStream(bo);
      so.writeObject(myObject);
      so.flush();
      serializedObject = bo.toString();
      return serializedObject;
    } catch (Exception e) {
      e.printStackTrace();
      throw (e);
    }
  }

  public static Object deSerialize1(String serializedObject) throws Exception {

    try {
      byte b[] = serializedObject.getBytes();
      ByteArrayInputStream bi = new ByteArrayInputStream(b);
      ObjectInputStream si = new ObjectInputStream(bi);
      Object obj = si.readObject();
      return obj;
    } catch (Exception e) {
      throw (e);
    }
  }
}
