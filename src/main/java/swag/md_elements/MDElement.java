package swag.md_elements;

import java.io.Serializable;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.jena.query.QueryParseException;
import org.apache.jena.sparql.core.Var;

import com.google.common.base.Preconditions;

import swag.analysis_graphs.execution_engine.operators.IRDFElement;
import swag.graph.Node;
import swag.sparql_builder.CustomSPARQLQuery;

public class MDElement implements Serializable, Node, IRDFElement {

    public Var getHeadVar() {
	return Optional.ofNullable(this.getMapping()).map(Mapping::getQuery)
		.map(CustomSPARQLQuery::getUnaryProjectionVar).orElse(null);
    }

    public String getNameOfHeadVar() {
	return this.getMapping().getQuery().getNameOfUnaryProjectionVar();
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String uri;
    private String name;
    private Mapping mapping;
    private String label;
    private String comment = "";

    public MDElement() {
	setURI("");
	setName("");
	setMapping(new Mapping());
	setLabel("");
    }

    public MDElement(String uri, String name, Mapping mapping, String label) {
	super();
	setURI(uri);
	setName(name);
	setMapping(mapping);
	if (StringUtils.isEmpty(label)) {
	    setLabel(name);
	} else {
	    setLabel(label);
	}
    }

    public MDElement(String uri, String name, Mapping mapping, String identifyingName, String label) {
	super();
	setIdentifyingName(identifyingName);
	setURI(uri);
	setName(name);
	setMapping(mapping);
	if (StringUtils.isEmpty(label)) {
	    setLabel(name);
	} else {
	    setLabel(label);
	}
    }

    /**
     * Clone Constructor
     * 
     * @param e
     */
    public MDElement(MDElement e) {
	Preconditions.checkNotNull(e);
	setURI(e.getURI());
	setName(e.getName());
	setIdentifyingName(e.getIdentifyingName());
	try {
	    setMapping(new Mapping(e.getMapping()));
	} catch (QueryParseException ex) {
	    setMapping(new Mapping());
	}
	setLabel(e.getLabel());
    }

    /**
     * For polymorphismic copying of MDElement subclasses
     * 
     * @return
     */
    public MDElement deepCopy() {

	MDElement elem = new MDElement();

	Preconditions.checkNotNull(this);
	elem.setURI(this.getURI());
	elem.setName(this.getName());
	// setIdentifyingName(e.getIdentifyingName());
	try {
	    elem.setMapping(new Mapping(this.getMapping()));
	} catch (QueryParseException ex) {
	    elem.setMapping(new Mapping());
	}
	elem.setLabel(this.getLabel());
	return elem;
    }

    @Override
    public boolean equals(Object e) {
	if (e instanceof MDElement) {
	    MDElement mdElem = (MDElement) e;
	    if (this.getIdentifyingName().equals(
		    mdElem.getIdentifyingName()) /*
						  * && this.getMapping().equals(
						  * mdElem.getMapping())
						  */)
		return true;
	}
	return false;
    }

    /**
     * 
     * Compares this object with the passed objects, disregarding the actual
     * type. This method should not be overriden, this is how it is expected
     * always to work.
     * 
     * @param that
     * @return true if the this and that have the same identifying name
     */
    public final boolean equalsIgnoreType(MDElement that) {
	if (this.getIdentifyingName().equals(that.getIdentifyingName())) {
	    return true;
	} else {
	    return false;
	}
    }

    @Override
    public int hashCode() {
	return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
		append(getIdentifyingName()).toHashCode();
    }

    /**
     * 
     * Checks if the current element, treated as an MDElement instance equals
     * the passed element treated as an MDElement.
     * 
     * @param mdElem
     *            the element to compare with
     * 
     * @return true if the two elements are equal considering only MD
     *         attributes, false otherwise
     * 
     */
    public final <T extends MDElement> boolean compareASMDElement(T mdElem) {

	MDElement elem1 = new MDElement(this);
	MDElement elem2 = new MDElement(mdElem);
	if (elem1.equals(elem2)) {
	    return true;
	}
	return false;
    }

    @Override
    public String toString() {
	return this.getIdentifyingName() + "//type: " + this.getClass() + "//mapping: "
		+ getMapping().getQuery().getSparqlQuery().toString();
    }

    public static final boolean isMultipleElement(MDElement elem) {
	return elem == MultipleMDElement.getInstance();
    }

    public static final boolean isMultipleElement(String elem) {
	return MultipleMDElement.getInstance().getIdentifyingName().equals(elem);
    }

    @Override
    public String getLabel() {
	return label;
    }

    public void setLabel(String label) {
	this.label = label;
    }

    @Override
    public String getURI() {
	return uri;
    }

    public void setURI(String uri) {
	this.uri = Preconditions.checkNotNull(uri);
    }

    private String identifyingName;

    public void setIdentifyingName(String identifyingName) {
	this.identifyingName = identifyingName;
    }

    @Override
    public String getIdentifyingName() {
	if (identifyingName == null) {
	    return getURI();
	} else {
	    return identifyingName;
	}
    }

    public void setName(String name) {
	this.name = Preconditions.checkNotNull(name);
    }

    @Override
    public String getName() {
	return name;
    }

    public Mapping getMapping() {
	return mapping;
    }

    public void setMapping(Mapping mapping) {
	this.mapping = Preconditions.checkNotNull(mapping);
    }

    @Override
    public String getComment() {
	return comment;
    }

    public void setComment(String comment) {
	this.comment = comment;
    }

    @Override
    public String getTypeLabel() {
	// TODO Auto-generated method stub
	return null;
    }
    
    public boolean isNullElement(){
    	return StringUtils.isEmpty(getIdentifyingName());
    }

}
