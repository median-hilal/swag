package swag.md_elements;

public final class DefaultHierarchy extends QB4OHierarchy {

  /**
   * 
   */
  private static final long serialVersionUID = 7964995712782809107L;

  private static DefaultHierarchy instance;

  public static final DefaultHierarchy getDefaultHierarchy() {
    if (instance == null) {
      instance = new DefaultHierarchy();
    }
    return instance;
  }

  private DefaultHierarchy() {
    super("http://www.defaultHierarchy.com/defaultHierarchy", "Default Hierarchy", new Mapping(),
        "Default Hierarchy");
  }

}
