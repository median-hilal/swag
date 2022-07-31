package swag.analysis_graphs.execution_engine.analysis_situations;

import java.io.Serializable;

public interface ISetOfComparison extends Serializable {

  public String getName();

  public String getURI();

  public String getDescription();

  public void setName(String name);

  public void setURI(String uri);

  public void setDescription(String description);

  public ISetOfComparison shallowCopy();
}
