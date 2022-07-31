package swag.sparql_builder.reporting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.expr.Expr;

import swag.analysis_graphs.execution_engine.analysis_situations.AnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.MeasureAggregatedInAS;
import swag.md_elements.MDSchema;
import swag.sparql_builder.ASElements.configuration.MeasureConfigurationObject;

/**
 * 
 * Abstract implementation of Measure reporting
 * 
 * @author swag
 *
 */
public abstract class AbstractMeasureReporter implements IMeasureReoprter {

  private MDSchema mdSchema;
  private MeasureAggregatedInAS aggregatedMeasure;
  private AnalysisSituation as;
  private Map<Var, Expr> measureStatementVariablesAndExpressions = new HashMap<>();
  private Map<Var, String> bindStatementVariablesAndExpressions = new HashMap<>();
  private Map<Var, Expr> mostOuterVariablesAndExpressions = new HashMap<>();

  public static List<IMeasureReoprter> createReporter(MDSchema mdSchema,
      MeasureAggregatedInAS aggregatedMeasure, AnalysisSituation as) {

    List<IMeasureReoprter> reporters = new ArrayList<>();

    if (MeasureConfigurationObject.isNonStrictInternAgg(as.getMsrConfigs(), aggregatedMeasure)) {
      reporters.add(new MeasureReporterInterAggregation(aggregatedMeasure, mdSchema, as));
    }
    if (MeasureConfigurationObject.isNonStrictNone(as.getMsrConfigs(), aggregatedMeasure)) {
      reporters.add(new MeasureReporterSeparateFact(aggregatedMeasure, mdSchema, as));
    }

    if (MeasureConfigurationObject.isIncompleteNone(mdSchema, as.getMsrConfigs(),
        aggregatedMeasure)) {
      reporters.add(new MeasureReporterDiscardMissing(aggregatedMeasure, mdSchema, as));
    }

    return reporters;
  }

  public AbstractMeasureReporter(MeasureAggregatedInAS aggregatedMeasure, MDSchema mdSchema,
      AnalysisSituation as) {
    super();
    this.aggregatedMeasure = aggregatedMeasure;
    this.mdSchema = mdSchema;
    this.as = as;
  }

  public void setMeasureStatementVariablesAndExpressions(
      Map<Var, Expr> measureStatementVariablesAndExpressions) {
    this.measureStatementVariablesAndExpressions = measureStatementVariablesAndExpressions;
  }

  public void setBindStatementVariablesAndExpressions(
      Map<Var, String> bindStatementVariablesAndExpressions) {
    this.bindStatementVariablesAndExpressions = bindStatementVariablesAndExpressions;
  }

  public void setMostOuterVariablesAndExpressions(Map<Var, Expr> mostOuterVariablesAndExpressions) {
    this.mostOuterVariablesAndExpressions = mostOuterVariablesAndExpressions;
  }

  public Map<Var, Expr> getMeasureStatementVariablesAndExpressions() {
    return measureStatementVariablesAndExpressions;
  }

  public Map<Var, String> getBindStatementVariablesAndExpressions() {
    return bindStatementVariablesAndExpressions;
  }

  public Map<Var, Expr> getMostOuterVariablesAndExpressions() {
    return mostOuterVariablesAndExpressions;
  }

  public MeasureAggregatedInAS getAggregatedMeasure() {
    return aggregatedMeasure;
  }

  public void setAggregatedMeasure(MeasureAggregatedInAS aggregatedMeasure) {
    this.aggregatedMeasure = aggregatedMeasure;
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
