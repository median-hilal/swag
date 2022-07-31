package swag.analysis_graphs.execution_engine.analysis_situations;

import org.apache.jena.sparql.core.Var;

public class VarMetaData {
  private Var var;
  private String varDisplayName;
  private String varDescription;

  public VarMetaData(Var var, String varDisplayName, String varDescription) {
    super();
    this.var = var;
    this.varDisplayName = varDisplayName;
    this.varDescription = varDescription;
  }

  public Var getVar() {
    return var;
  }

  public void setVar(Var var) {
    this.var = var;
  }

  public String getVarDisplayName() {
    return varDisplayName;
  }

  public void setVarDisplayName(String varDisplayName) {
    this.varDisplayName = varDisplayName;
  }

  public String getVarDescription() {
    return varDescription;
  }

  public void setVarDescription(String varDescription) {
    this.varDescription = varDescription;
  }
}
