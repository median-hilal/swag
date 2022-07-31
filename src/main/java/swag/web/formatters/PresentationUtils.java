package swag.web.formatters;

import java.util.Optional;

import swag.analysis_graphs.execution_engine.AnalysisGraph;
import swag.analysis_graphs.execution_engine.analysis_situations.ISliceSinglePosition;

/**
 * 
 * Utility class
 * 
 * @author swag
 *
 */
public final class PresentationUtils {

  /**
   * Class cannot be instantated
   */
  private PresentationUtils() {}

  /**
   * 
   * Get condition label, or the actual expression if the label is not present
   * 
   * @param cond the condition
   * @param graph the analysis graph
   * @return condition label or expression
   */
  public static final String getConditoinDisplayString(ISliceSinglePosition cond,
      AnalysisGraph graph) {
    return Optional
        .ofNullable(
            graph.getDefinedAGConditions().getConditoinByIdentifyingName(cond.getURI()).getName())
        .orElse(cond.getConditoin());
  }
}
