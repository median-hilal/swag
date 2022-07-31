package swag.md_elements;

/**
 * 
 * Represents that a level belongs to a hierarchy. Should act like an inverse of qb4o:hasLevel
 * (qb4o:hasLevel is from qb4o:Hierarchy to qb:LevelAttribute).
 * 
 * @author swag
 *
 */
public class QB4OInHierarchy extends MDRelation {

  /**
   * 
   */
  private static final long serialVersionUID = -5776458739730992772L;

  protected QB4OInHierarchy(MDElement elem, Level from, QB4OHierarchy to) {
    super(elem, from, to);
  }


}
