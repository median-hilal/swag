package swag.sparql_builder.reporting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.expr.Expr;

import swag.analysis_graphs.execution_engine.analysis_situations.AnalysisSituation;
import swag.md_elements.Level;
import swag.md_elements.MDSchema;
import swag.sparql_builder.ASElements.configuration.DimensionConfigurationObject;

/**
 * 
 * Abstract implementation of Dimension reporting
 * 
 * @author swag
 *
 */
public abstract class AbstractDimensionReporter implements IDimensionReoprter {

  private MDSchema mdSchema;
  private Level level;
  private AnalysisSituation as;
  private Map<Var, Expr> lvlStatementVariablesAndExpressions = new HashMap<>();
  private Map<Var, String> bindStatementVariablesAndExpressions = new HashMap<>();
  private Map<Var, Expr> mostOuterVariablesAndExpressions = new HashMap<>();

  public static List<IDimensionReoprter> createReporter(MDSchema mdSchema, Level level,
      AnalysisSituation as) {

    List<IDimensionReoprter> reporters = new ArrayList<>();

    if (DimensionConfigurationObject.isIncompleteNone(mdSchema, as.getDimConfigs(), level)) {
      reporters.add(new DimensionReporterDiscardMissing(level, mdSchema, as));
    }

    if (DimensionConfigurationObject.isNonStrictNone(mdSchema, as.getDimConfigs(), level)) {
      reporters.add(new DimensionReporterPureVsNonPureFacts(level, mdSchema, as));
    }

    return reporters;
  }

  public AbstractDimensionReporter(Level level, MDSchema mdSchema, AnalysisSituation as) {
    super();
    this.level = level;
    this.mdSchema = mdSchema;
    this.as = as;
  }

  public void setLevelStatementVariablesAndExpressions(
      Map<Var, Expr> measureStatementVariablesAndExpressions) {
    this.lvlStatementVariablesAndExpressions = measureStatementVariablesAndExpressions;
  }

  public void setBindStatementVariablesAndExpressions(
      Map<Var, String> bindStatementVariablesAndExpressions) {
    this.bindStatementVariablesAndExpressions = bindStatementVariablesAndExpressions;
  }

  public void setMostOuterVariablesAndExpressions(Map<Var, Expr> mostOuterVariablesAndExpressions) {
    this.mostOuterVariablesAndExpressions = mostOuterVariablesAndExpressions;
  }

  public Map<Var, Expr> getLevelStatementVariablesAndExpressions() {
    return lvlStatementVariablesAndExpressions;
  }

  public Map<Var, String> getBindStatementVariablesAndExpressions() {
    return bindStatementVariablesAndExpressions;
  }

  public Map<Var, Expr> getMostOuterVariablesAndExpressions() {
    return mostOuterVariablesAndExpressions;
  }

  public Level getLevel() {
    return level;
  }

  public void setLevel(Level level) {
    this.level = level;
  }

  public MDSchema getMdSchema() {
    return mdSchema;
  }

  public void setMdSchema(MDSchema mdSchema) {
    this.mdSchema = mdSchema;
  }

  public AnalysisSituation getAs() {
    return as;
  }

  public void setAs(AnalysisSituation as) {
    this.as = as;
  }
}
