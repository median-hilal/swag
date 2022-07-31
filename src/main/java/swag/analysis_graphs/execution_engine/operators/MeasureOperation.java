package swag.analysis_graphs.execution_engine.operators;

public abstract class MeasureOperation extends Operation {

  public MeasureOperation(String uri, String name) {
    super(uri, name);
  }

  public MeasureOperation(String uri, String name, String label, String comment) {
    super(uri, name, label, comment);
  }

  /**
   * 
   */
  private static final long serialVersionUID = -3534119305732112169L;

}
