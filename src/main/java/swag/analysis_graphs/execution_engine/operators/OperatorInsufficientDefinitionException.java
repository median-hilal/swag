package swag.analysis_graphs.execution_engine.operators;

public class OperatorInsufficientDefinitionException extends Exception {
	
	public OperatorInsufficientDefinitionException (){
		super();
	}
	
	public OperatorInsufficientDefinitionException (String ex){
		super(ex);
	}

}
