package swag.md_elements;

import java.util.logging.Logger;

import swag.data_handler.Constants;

public class MappableRelationFactory {

  private static final Logger logger = Logger.getLogger(MappableRelationFactory.class.getName());

  private static HasLevel createHasLevel(MDElement elem, Fact fact, Level level) {
    return new HasLevel(elem, fact, level);
  }

  private static HasMeasure createHasMeasure(MDElement elem, Fact fact, Measure measure) {
    return new HasMeasure(elem, fact, measure);
  }

  private static HasDescriptor createHasDesriptor(MDElement elem, Level level,
      Descriptor descriptor) {
    return new HasDescriptor(elem, level, descriptor);
  }

  private static QB4OHierarchyStep createHierarchyStep(MDElement elem, Level level1, Level level2) {
    return new QB4OHierarchyStep(elem, level1, level2);
  }

  private static InDimension createInDimension(MDElement elem, Level level1, Dimension dim) {
    return new InDimension(elem, level1, dim);
  }

  /**
   * qb4o:inDimensoin edge from a hierarchy to a dimension.
   * 
   * @param elem
   * @param hier
   * @param dim
   * @return
   */
  private static QB4OHierarchyInDimension createQB4OInDimension(MDElement elem, QB4OHierarchy hier,
      Dimension dim) {
    return new QB4OHierarchyInDimension(elem, hier, dim);
  }

  private static QB4OHasHierarchy createHasHierarchy(MDElement elem, Dimension dim,
      QB4OHierarchy hier) {
    return new QB4OHasHierarchy(elem, dim, hier);
  }

  private static QB4OInHierarchy createLevelInHierarchy(MDElement elem, Level level1,
      QB4OHierarchy hier) {
    return new QB4OInHierarchy(elem, level1, hier);
  }

  public static MDRelation createMappableRelation(String relType, MDElement relationElem,
      MDElement fromElem, MDElement toElem) {

    try {

      if (relType.equals(Constants.HAS_LEVEL))
        return createHasLevel(relationElem, (Fact) fromElem, (Level) toElem);

      if (relType.equals(Constants.HAS_MEASURE))
        return createHasMeasure(relationElem, (Fact) fromElem, (Measure) toElem);

      if (relType.equals(Constants.HAS_ATTRIBUTE))
        return createHasDesriptor(relationElem, (Level) fromElem, (Descriptor) toElem);

      if (relType.equals(Constants.HIEREARCHY_STEP))
        return createHierarchyStep(relationElem, (Level) fromElem, (Level) toElem);

      if (relType.equals(Constants.IN_DIMENSION))
        return createInDimension(relationElem, (Level) fromElem, (Dimension) toElem);

      if (relType.equals(Constants.QB4O_HAS_HIERARCHY)) {
        return createHasHierarchy(relationElem, (Dimension) fromElem, (QB4OHierarchy) toElem);
      }

      // QB4O HasLevel or SMD inHierarchy
      if (relType.equals(Constants.QB4O_HAS_LEVEL) || relType.equals(Constants.IN_HIERARCHY)) {
        return createLevelInHierarchy(relationElem, (Level) fromElem, (QB4OHierarchy) toElem);
      }

      if (relType.equals(Constants.QB4O_IN_DIMENSION)) {
        return createQB4OInDimension(relationElem, (QB4OHierarchy) fromElem, (Dimension) toElem);
      }


      if (relType.equals("TREAT_AS_FACT"))
        return new MDRelation(relationElem, fromElem, toElem);

    } catch (Exception ex) {

      logger.log(java.util.logging.Level.ALL, "exception: Cannot create an instance of" + relType,
          ex);
      ex.printStackTrace();
      return null;
    }

    return null;
  }

  public static MDRelation createDeepCopyMappableRelation(String relType, MDElement relationElem,
      MDElement fromElem, MDElement toElem) {

    try {

      if (relType.equals(Constants.HAS_LEVEL))
        return createHasLevel(relationElem, (Fact) fromElem, (Level) toElem);

      if (relType.equals(Constants.HAS_MEASURE))
        return createHasMeasure(relationElem, (Fact) fromElem, (Measure) toElem);

      if (relType.equals(Constants.HAS_ATTRIBUTE))
        return createHasDesriptor(relationElem, (Level) fromElem, (Descriptor) toElem);

      if (relType.equals(Constants.HIEREARCHY_STEP))
        return createHierarchyStep(relationElem, (Level) fromElem, (Level) toElem);

      if (relType.equals(Constants.IN_DIMENSION))
        return createInDimension(relationElem, (Level) fromElem, (Dimension) toElem);

      if (relType.equals(Constants.QB4O_HAS_HIERARCHY)) {
        return createHasHierarchy(relationElem, (Dimension) fromElem, (QB4OHierarchy) toElem);
      }

      if (relType.equals(Constants.QB4O_HAS_LEVEL)) {
        return createLevelInHierarchy(relationElem, (Level) fromElem, (QB4OHierarchy) toElem);
      }

      if (relType.equals(Constants.QB4O_IN_DIMENSION)) {
        return createQB4OInDimension(relationElem, (QB4OHierarchy) fromElem, (Dimension) toElem);
      }


      if (relType.equals("TREAT_AS_FACT"))
        return new MDRelation(relationElem, fromElem, toElem);

    } catch (Exception ex) {

      logger.log(java.util.logging.Level.ALL, "exception: Cannot create an instance of" + relType,
          ex);
      ex.printStackTrace();
      return null;
    }

    return null;
  }



  public static MDRelation copyMappableRelation(MDRelation rel) {


    MDElement from = new MDElement((MDElement) rel.getFrom());
    MDElement to = new MDElement((MDElement) rel.getTo());
    MDElement relation = new MDElement((MDElement) rel);

    return createMappableRelation("TREAT_AS_FACT", relation, from, to);
  }

}
