package swag.analysis_graphs.execution_engine;

public class NoSuchElementExistsException extends Exception {
	
	public NoSuchElementExistsException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public NoSuchElementExistsException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public NoSuchElementExistsException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public NoSuchElementExistsException (){
		super();
	}
	
	public NoSuchElementExistsException (String ex){
		super(ex);
	}
}
