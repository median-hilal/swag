package swag.web;

public class SliceRepresentatoin {

  private String varName;
  private String comparisonOperator;
  private String comparedValue;

  public static SliceRepresentatoin createSliceRepresentationFormThreePartsString(String str) {

    String[] subStrs = str.split("\\s+");

    String varName = "";
    String comparisonOperator = "";
    String comparedValue = "";

    if (subStrs.length >= 3) {
      varName = subStrs[0];
      comparisonOperator = subStrs[1];
      comparedValue = subStrs[2];
    }

    return new SliceRepresentatoin(varName, comparisonOperator, comparedValue);
  }

  public static SliceRepresentatoin createSliceRepresentationFormTwoPartsString(String str) {

    String[] subStrs = str.split("\\s+");

    String varName = "";
    String comparisonOperator = "";
    String comparedValue = "";

    if (subStrs.length >= 2) {
      comparisonOperator = subStrs[0];
      comparedValue = subStrs[1];
    }

    return new SliceRepresentatoin(varName, comparisonOperator, comparedValue);
  }

  public SliceRepresentatoin() {
    super();
  }

  public SliceRepresentatoin(String varName, String comparisonOperator, String comparedValue) {
    super();
    this.varName = varName;
    this.comparisonOperator = comparisonOperator;
    this.comparedValue = comparedValue;
  }

  public String getVarName() {
    return varName;
  }

  public void setVarName(String varName) {
    this.varName = varName;
  }

  public String getComparisonOperator() {
    return comparisonOperator;
  }

  public void setComparisonOperator(String comparisonOperator) {
    this.comparisonOperator = comparisonOperator;
  }

  public String getComparedValue() {
    return comparedValue;
  }

  public void setComparedValue(String comparedValue) {
    this.comparedValue = comparedValue;
  }


}
