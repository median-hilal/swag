package swag.analysis_graphs.execution_engine.operators;

import swag.md_elements.Dimension;
import swag.md_elements.QB4OHierarchy;

/**
 * Abstract class for dimension operations.
 * 
 * @author swag
 */
public abstract class DimOperation extends Operation {

  /**
   * 
   */
  private static final long serialVersionUID = 1579181141170417453L;
  private Dimension onDimension;
  private QB4OHierarchy onHierarchy;

  /**
   * Constructs a new {@code DimOperation}. Remember, abstract classs, cannot be instantiated.
   * 
   * @param uri uri of the operation
   * @param name local name of the operation
   * @param dim dimension of the operation
   * @param hier hierarchy of the operation
   */
  public DimOperation(String uri, String name, Dimension dim, QB4OHierarchy hier) {
    super(uri, name);
    this.onDimension = dim;
    this.onHierarchy = hier;
  }

  /**
   * 
   * Constructs a new {@code DimOperation} with label and comment being set.
   * 
   * @param uri uri of the operation
   * @param name local name of the operation
   * @param label the label
   * @param comment the comment
   * @param dim dimension of the operation
   * @param hier hierarchy of the operation
   */
  public DimOperation(String uri, String name, String label, String comment, Dimension dim,
      QB4OHierarchy hier) {
    super(uri, name, label, comment);
    this.onDimension = dim;
    this.onHierarchy = hier;
  }

  /**
   * @return
   */
  public Dimension getOnDimension() {
    return onDimension;
  }

  /**
   * @param onDimension
   */
  public void setOnDimension(Dimension onDimension) {
    this.onDimension = onDimension;
  }

  /**
   * @return
   */
  public QB4OHierarchy getOnHierarchy() {
    return onHierarchy;
  }

  /**
   * @param onHierarchy
   */
  public void setOnHierarchy(QB4OHierarchy onHierarchy) {
    this.onHierarchy = onHierarchy;
  }
}
