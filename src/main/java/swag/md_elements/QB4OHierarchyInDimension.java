package swag.md_elements;

/**
 * 
 * Used to represent qb4o:inDimensoin edge from a hierarchy to a dimension.
 * 
 * @author swag
 *
 */
public class QB4OHierarchyInDimension extends MDRelation {

  protected QB4OHierarchyInDimension(MDElement elem, QB4OHierarchy from, Dimension to) {
    super(elem, from, to);
  }

  /**
   * 
   */
  private static final long serialVersionUID = -5233224064361598264L;

}
