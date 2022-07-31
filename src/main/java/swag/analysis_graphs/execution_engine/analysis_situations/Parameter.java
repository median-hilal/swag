package swag.analysis_graphs.execution_engine.analysis_situations;

import java.io.Serializable;

public class Parameter implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5509223772630914232L;
	private String paramType;
	private Object paramValue;
	
	public String getParamType() {return paramType;}
	public void setParamType(String paramType) {this.paramType = paramType;}
	public Object getParamValue() {return paramValue;}
	public void setParamValue(Object paramValue) {this.paramValue = paramValue;}
	
	public Parameter(String paramType, Object paramValue) {
		super();
		this.paramType = paramType;
		this.paramValue = paramValue;
	}
	
}
