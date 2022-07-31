package swag.analysis_graphs.execution_engine.analysis_situations;

/**
 * 
 * This interface represents a measure in an analysis situation
 * 
 * @author swag
 *
 */
public interface IMeasureInAS
	extends IMeasure, Variable<AnalysisSituation>, IASItem<AnalysisSituation>, IConfigurationObject {

    /**
     * Gets the actual measure on top of which the current measure in analysis
     * situation is build
     * 
     * @return the measure
     */
    public IMeasure getMeasure();

    /**
     * Sets the actual measure on top of which the current measure in analysis
     * situation is build
     * 
     * @param measure
     *            the measure to set
     */
    public void setMeasure(IMeasure measure);
}
