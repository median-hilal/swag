package swag.sparql_builder.ASElements;

import swag.graph.Path;
import swag.md_elements.MDElement;
import swag.md_elements.MDRelation;
import swag.md_elements.MDSchema;

public class MDPathFragment {

  private MDElement source;
  private MDElement target;
  private MDPathFragmentType typ;

  public MDElement getSource() {
    return source;
  }

  public MDElement getTarget() {
    return target;
  }

  public MDPathFragmentType getTyp() {
    return typ;
  }

  public MDPathFragment(MDElement source, MDElement target, MDPathFragmentType typ) {
    super();
    this.source = source;
    this.target = target;
    this.typ = typ;
  }

  /**
   * 
   * @param mdSchema the MD schema.
   * 
   * @return the path between source and target of the current fragment. If there multiple paths,
   *         arbitrary one is returned. If there are no paths, null is returned.
   * 
   */
  public Path<MDElement, MDRelation> getPathOfFragment(MDSchema mdSchema) {

    java.util.List<Path<MDElement, MDRelation>> paths =
        mdSchema.getAllMappedPathsBetweenTwoVertices(source, target);
    if (paths.size() > 0) {
      return paths.get(0);
    } else {
      return null;
    }
  }

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }

    if (o instanceof MDPathFragment) {
      MDPathFragment casted = (MDPathFragment) o;

      MDElement sourceThis = new MDElement((MDElement) this.getSource());
      MDElement sourceThat = new MDElement((MDElement) casted.getSource());
      MDElement targetThis = new MDElement((MDElement) this.getTarget());
      MDElement targetThat = new MDElement((MDElement) casted.getTarget());

      if (sourceThis.equals(sourceThat) && targetThis.equals(targetThat)
          && this.getTyp().equals(casted.getTyp())) {
        return true;
      }
    }
    return false;
  }

}
