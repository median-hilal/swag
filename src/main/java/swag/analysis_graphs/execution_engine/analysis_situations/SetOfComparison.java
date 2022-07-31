package swag.analysis_graphs.execution_engine.analysis_situations;

public class SetOfComparison implements ISetOfComparison {



  /**
   * 
   */
  private static final long serialVersionUID = -2255930835448291339L;


  public SetOfComparison(String name, String uRI, String description) {
    super();
    this.name = name;
    URI = uRI;
    this.description = description;
  }

  private String name;
  private String URI;
  private String description;

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String getURI() {
    return URI;
  }

  @Override
  public void setURI(String uRI) {
    URI = uRI;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((URI == null) ? 0 : URI.hashCode());
    result = prime * result + ((description == null) ? 0 : description.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    SetOfComparison other = (SetOfComparison) obj;
    if (URI == null) {
      if (other.URI != null)
        return false;
    } else if (!URI.equals(other.URI))
      return false;
    if (description == null) {
      if (other.description != null)
        return false;
    } else if (!description.equals(other.description))
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    return true;
  }

  @Override
  public ISetOfComparison shallowCopy() {
    return new SetOfComparison(name, URI, description);
  }
}
