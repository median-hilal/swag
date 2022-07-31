package swag.analysis_graphs.execution_engine.operators;

public class NullOperator extends Operation {

    /**
     * 
     */
    private static final long serialVersionUID = 7063148320594704118L;
    private final static String OPERATOR_NAME = "null";

    @Override
    public String getOperatorName() {
	return OPERATOR_NAME;
    }

    public NullOperator() {
	super("", "");
    }

    public NullOperator(String name, String abbName) {
	super("", "");
	// TODO Auto-generated constructor stub
    }

    @Override
    public void accept(IOperatorVisitor visitor) throws Exception {
	visitor.visit(this);
    }

    @Override
    public String getTypeLabel() {
	return "Nothing";
    }

}
