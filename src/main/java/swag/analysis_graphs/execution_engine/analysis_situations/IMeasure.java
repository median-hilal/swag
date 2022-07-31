package swag.analysis_graphs.execution_engine.analysis_situations;

import org.apache.commons.lang3.StringUtils;

import swag.web.IVariableVisitor;

public interface IMeasure {

	public String getURI();

	public void setURI(String uri);

	public String getName();

	public void setName(String name);

	public String getLabel();

	public String getComment();

	public void setComment(String comment);

	public void acceptVisitor(IVariableVisitor visitor) throws Exception;

	public default String getMeasureRange() {
		if (this instanceof MeasureAggregated) {
			MeasureAggregated agg = ((MeasureAggregated) this);
			return agg.getMeasure().getMeasureRange();
		}
		if (this instanceof MeasureDerived) {
			return ((MeasureDerived) this).getDataType();
		}
		return StringUtils.EMPTY;
	}

}
