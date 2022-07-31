package swag.data_handler;

import org.apache.log4j.Logger;

import swag.analysis_graphs.execution_engine.Signature;
import swag.analysis_graphs.execution_engine.analysis_situations.AnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.IMeasure;
import swag.analysis_graphs.execution_engine.analysis_situations.IMeasureInAS;
import swag.analysis_graphs.execution_engine.analysis_situations.ItemInAnalysisSituationType;
import swag.analysis_graphs.execution_engine.analysis_situations.MeasureAggregated;
import swag.analysis_graphs.execution_engine.analysis_situations.MeasureAggregatedInAS;
import swag.analysis_graphs.execution_engine.analysis_situations.MeasureDerived;
import swag.analysis_graphs.execution_engine.analysis_situations.MeasureDerivedInAS;
import swag.analysis_graphs.execution_engine.analysis_situations.VariableState;
import swag.md_elements.MDSchema;

/**
 * 
 * Responsible for creation of measure objects.
 * 
 * @author swag
 *
 */
public class MeasureFactory {

  private static final Logger logger = Logger.getLogger(MeasureFactory.class);

  /**
   * 
   * Checks the uri against the passed MD schema, and gets the aggregated measure node, then creates
   * a corresponding aggregated measure in analysis situation object.
   * 
   * @param schema the MD schema
   * @param an analysis situation
   * @param uri the uri of the measure to create and attach to the analysis situation
   * 
   * @return the created measure, null otherwise.
   * 
   */
  public static MeasureAggregatedInAS createResultMeasureFromURI(MDSchema schema,
      AnalysisSituation as, String uri) {

    IMeasure msr = (IMeasure) schema.getNode(uri);

    if (msr instanceof MeasureAggregated) {
      Signature<AnalysisSituation> sig = new Signature<AnalysisSituation>(as,
          ItemInAnalysisSituationType.AggregatedMeasure, VariableState.NON_VARIABLE, "", null);
      return new MeasureAggregatedInAS((MeasureAggregated) msr, sig);
    }
    logger.warn("Cannot create an aggregated measure from " + uri);
    return null;
  }

  /**
   * 
   * Checks the uri against the passed MD schema, and gets the measure node, then checks its
   * original type if a derived or an aggregated measure, then creates a corresponding measure in
   * analysis situation object.
   * 
   * @param schema the MD schema
   * @param an analysis situation
   * @param uri the uri of the measure to create and attach to the analysis situation
   * 
   * @return the created measure, null otherwise.
   * 
   */
  public static IMeasureInAS createMeasureFromURI(MDSchema schema, AnalysisSituation as,
      String uri) {

    IMeasure msr = (IMeasure) schema.getNode(uri);

    if (msr instanceof MeasureDerived) {
      Signature<AnalysisSituation> sig = new Signature<AnalysisSituation>(as,
          ItemInAnalysisSituationType.DerivedMeasure, VariableState.NON_VARIABLE, "", null);
      return new MeasureDerivedInAS((MeasureDerived) msr, sig);
    } else {
      if (msr instanceof MeasureAggregated) {
        Signature<AnalysisSituation> sig = new Signature<AnalysisSituation>(as,
            ItemInAnalysisSituationType.AggregatedMeasure, VariableState.NON_VARIABLE, "", null);
        return new MeasureAggregatedInAS((MeasureAggregated) msr, sig);
      }
    }
    logger.warn("Cannot create a measure from " + uri);
    return null;
  }
}
