package swag.analysis_graphs.execution_engine.analysis_situations;

import java.io.Serializable;

import swag.md_elements.*;
import swag.sparql_builder.CustomSPARQLQuery;

public class MeasureToAnalysisSituation implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1762161103021931863L;
	private AnalysisSituation as;	
	private MeasureInAnalysisSituation measure;
	// TODO define the aggregationOperation type
	private AggregationOperationInAnalysisSituation aggregationOperation;		
	
	public AnalysisSituation getAs() {return as;}
	public void setAs(AnalysisSituation as) {this.as = as;}
	
	public MeasureInAnalysisSituation getMeasure() {return measure;}
	public void setMeasure(MeasureInAnalysisSituation measure) {this.measure = measure;}
	
	public AggregationOperationInAnalysisSituation getAggregationOperation() {return aggregationOperation;}
	public void setAggregationOperation(AggregationOperationInAnalysisSituation aggregationOperation) {this.aggregationOperation = aggregationOperation;}
	
	public MeasureToAnalysisSituation() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Clone constructor
	 * @param as
	 * @param measure
	 * @param aggregationOperation
	 */
	public MeasureToAnalysisSituation(MeasureToAnalysisSituation measToAS) {
		super();
		this.as = measToAS.getAs();		
		this.measure = measToAS.getMeasure().shallowCopy();
		this.aggregationOperation = measToAS.getAggregationOperation().shallowCopy();
	}
	
	public MeasureToAnalysisSituation(AnalysisSituation as, MeasureInAnalysisSituation measure, AggregationOperationInAnalysisSituation aggregationOperation) {
		super();
		this.as = as;
		this.measure = measure;
		this.aggregationOperation = aggregationOperation;
	}
	
	public CustomSPARQLQuery generateSPARQLFromMeasureToAS(){
		
		// TODO consider revising insertAggOpIntoMeasureQuery
		// this.measure.getMapping().getQuery().insertAggOpIntoMeasureQuery(this.aggregationOperation);
		return this.measure.getMapping().getQuery();
	}
	
}
