package swag.analysis_graphs.execution_engine.operators;

public interface IRDFElement {

    public default String getPrefferredDisplayName() {
	return this.getTypeLabel();
    }

    public String getURI();

    public String getName();

    public String getLabel();

    public String getTypeLabel();

    public String getComment();
}
