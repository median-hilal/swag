package swag.md_elements;

/**
 * 
 * Represents a complex MD element composed of a set of MD elements
 * 
 * @author swag
 *
 */
public class MultipleMDElement extends MDElement {

  private static final MultipleMDElement inst = new MultipleMDElement();

  /**
   * 
   */
  private static final long serialVersionUID = 2255528553611211132L;


  private MultipleMDElement() {
    super("http://www.example.com/MultipleMDElement", "MultipleMDElement", new Mapping(),
        "MultipleMDElement");
  }

  public static MultipleMDElement getInstance() {
    return inst;
  }

  public boolean equals(Object o) {
    return o == inst;
  }

}
