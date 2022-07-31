package swag.analysis_graphs.execution_engine.analysis_situations;

import java.io.Serializable;

public interface IMeasureToASItem extends Serializable{
	
	MeasureToAnalysisSituationItemSignature getSignature();
	void setSignature(MeasureToAnalysisSituationItemSignature signature);
	
}
