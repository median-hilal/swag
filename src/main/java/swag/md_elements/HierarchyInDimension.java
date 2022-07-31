package swag.md_elements;

public class HierarchyInDimension extends MDElement {

  /**
   * 
   */
  private static final long serialVersionUID = -5233224064361598264L;

  private QB4OHierarchy hier;
  private Dimension dim;


  public QB4OHierarchy getHier() {
    return hier;
  }

  public void setHier(QB4OHierarchy hier) {
    this.hier = hier;
  }

  public Dimension getDim() {
    return dim;
  }

  public void setDim(Dimension dim) {
    this.dim = dim;
  }

  public HierarchyInDimension(QB4OHierarchy hier, Dimension dim) {
    super(hier.getURI() + dim.getURI(), hier.getName() + dim.getName(), new Mapping(),
        hier.getLabel() + dim.getLabel());
    this.hier = hier;
    this.dim = dim;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((dim == null) ? 0 : dim.hashCode());
    result = prime * result + ((hier == null) ? 0 : hier.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    HierarchyInDimension other = (HierarchyInDimension) obj;
    if (dim == null) {
      if (other.dim != null)
        return false;
    } else if (!dim.equals(other.dim))
      return false;
    if (hier == null) {
      if (other.hier != null)
        return false;
    } else if (!hier.equals(other.hier))
      return false;
    return true;
  }

}
