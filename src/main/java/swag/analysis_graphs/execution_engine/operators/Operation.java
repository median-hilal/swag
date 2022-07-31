package swag.analysis_graphs.execution_engine.operators;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;

/**
 * Abstract class for navigation operation.
 * 
 * @author swag
 */
public abstract class Operation implements Serializable, IRDFElement {
    /**
     * 
     */
    private static final long serialVersionUID = -1163527971453748251L;
    private String uri;
    private String name;
    private String label = "";
    private String comment = "";

    /**
     * This is a class-level method. It cannot be defined on static,
     * unfortunately...
     * 
     * @return
     */
    abstract public String getOperatorName();

    /**
     * Creates a new {@code Operation} (Remember, abstract class cannot create
     * instances of it).
     * 
     * @param uri
     *            the uri of the operation
     * @param name
     *            the local name of the operation
     */
    public Operation(String uri, String name) {
	super();
	setURI(uri);
	setName(name);
    }

    /**
     * 
     * Creates a new {@code Operation} with label and comment fields being set.
     * 
     * @param uri
     * @param name
     * @param label
     * @param comment
     */
    public Operation(String uri, String name, String label, String comment) {
	super();
	this.uri = uri;
	this.name = name;
	if (!StringUtils.isEmpty(label)) {
	    this.label = label;
	} else {
	    this.label = name;
	}
	if (!StringUtils.isEmpty(comment)) {
	    this.comment = comment;
	} else {
	    this.comment = "";
	}
    }

    /**
     * Abstract generic function to accept a visitor.
     * 
     * @param visitor
     *            the visitor to accept
     */
    public abstract void accept(IOperatorVisitor visitor) throws Exception;

    @Override
    public String getLabel() {
	return label;
    }

    public void setLabel(String label) {
	this.label = label;
    }

    @Override
    public String getComment() {
	return comment;
    }

    public void setComment(String comment) {
	this.comment = comment;
    }

    public void setURI(String uri) {
	this.uri = Preconditions.checkNotNull(uri);
    }

    public void setName(String name) {
	this.name = Preconditions.checkNotNull(name);
    }

    @Override
    public String getURI() {
	return uri;
    }

    @Override
    public String getName() {
	return name;
    }

}
