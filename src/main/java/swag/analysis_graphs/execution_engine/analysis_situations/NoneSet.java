package swag.analysis_graphs.execution_engine.analysis_situations;

/**
 * 
 * Represents the shared specifications, where they do not belong to a specific set.
 * 
 * @author swag
 *
 */
public class NoneSet implements ISetOfComparison {

  /**
   * 
   */
  private static final long serialVersionUID = -7668940887785591066L;

  private static final NoneSet instance = new NoneSet();
  private static final String name = "NonSet";
  private static final String URI = "http://www.NonSet.com/NoneSet";
  private static final String description = "Non Set";

  private NoneSet() {

  }

  public static NoneSet getNoneSet() {
    return instance;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public String getURI() {
    return this.URI;
  }

  @Override
  public String getDescription() {
    return this.description;
  }

  @Override
  public void setName(String name) {
    throw new UnsupportedOperationException("Cannot alter NoneSet data members");
  }

  @Override
  public void setURI(String uri) {
    throw new UnsupportedOperationException("Cannot alter NoneSet data members");
  }

  @Override
  public void setDescription(String description) {
    throw new UnsupportedOperationException("Cannot alter NoneSet data members");
  }

  @Override
  public ISetOfComparison shallowCopy() {
    return getNoneSet();
  }

}
