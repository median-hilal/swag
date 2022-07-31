package swag.md_elements;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import swag.analysis_graphs.execution_engine.analysis_situations.SlicePositionInAnalysisSituation;

public class Level extends MDElement {

  public void setIdentifyingName(String identifyingName) {
    this.identifyingName = identifyingName;
  }

  private String identifyingName;

  @Override
  public String getIdentifyingName() {
    if (identifyingName == null) {
      return getURI();
    } else {
      return identifyingName;
    }

  }

  public Level(String uri, String name, Mapping mapping, String identifyingName, String label) {
    super(uri, name, mapping, label);
    this.identifyingName = identifyingName;
  }


  public Level(String uri, String name, Mapping mapping, String label) {
    super(uri, name, mapping, label);
    // TODO Auto-generated constructor stub
  }

  public Level() {
    super();
  }

  /**
   * Clone construct
   * 
   * @param l
   */
  public Level(Level l) {
    super((MDElement) l);
  }

  @Override
  public Level deepCopy() {
    return new Level(this);
  }


  @Override
  public boolean equals(Object o) {
    if (o instanceof Level) {
      Level l = (Level) o;
      if (this.getIdentifyingName()
          .equals(l.getIdentifyingName()) /* && this.getMapping().equals(l.getMapping()) */)
        return true;
    }
    if (o instanceof SlicePositionInAnalysisSituation) {
      if (this.getIdentifyingName().equals(((SlicePositionInAnalysisSituation) o)
          .getIdentifyingName()) /* && this.getMapping().equals(l.getMapping()) */)
        return true;
    }
    return false;
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
        append(getIdentifyingName()).toHashCode();
  }
}
