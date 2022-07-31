package swag.md_elements;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.jena.sparql.core.Var;

import swag.data_handler.Constants;
import swag.graph.Edge;

public class MDRelation extends MDElement implements Edge {

    public Var getHeadVarFrom() {
	return this.getMapping().getQuery().getProjectionVarFrom();
    }

    public Var getHeadVarTo() {
	return this.getMapping().getQuery().getProjectionVarTo();
    }

    protected MDRelation(MDElement elem, MDElement from, MDElement to) {
	super(elem);
	this.from = from;
	this.to = to;
    }

    /**
     * For polymorphismic copying of MDElement subclasses
     * 
     * @return
     */
    @Override
    public MDRelation deepCopy() {

	String type = "";

	if (this instanceof HasLevel) {
	    type = Constants.HAS_LEVEL;
	}
	if (this instanceof HasMeasure) {
	    type = Constants.HAS_MEASURE;
	}

	if (this instanceof HasDescriptor) {
	    type = Constants.HAS_ATTRIBUTE;
	}

	if (this instanceof QB4OHierarchyStep) {
	    type = Constants.HIEREARCHY_STEP;
	}

	if (this instanceof InDimension) {
	    type = Constants.IN_DIMENSION;
	}

	if (this instanceof QB4OHierarchyInDimension) {
	    type = Constants.QB4O_IN_DIMENSION;
	}
	if (this instanceof QB4OHasHierarchy) {
	    type = Constants.QB4O_HAS_HIERARCHY;
	}

	if (this instanceof QB4OInHierarchy) {
	    type = Constants.QB4O_HAS_LEVEL;
	}

	return MappableRelationFactory.createMappableRelation(type, super.deepCopy(), this.from.deepCopy(),
		this.to.deepCopy());
    }

    /**
     * 
     */
    private static final long serialVersionUID = -8988936837102757558L;

    private MDElement from;
    private MDElement to;

    @Override
    public String getIdentifyingName() {
	return getFrom().getIdentifyingName() + "//" + getURI() + "//" + getTo().getIdentifyingName();
    }

    public MDElement getFrom() {
	return from;
    }

    public void setFrom(MDElement from) {
	this.from = from;
    }

    public MDElement getTo() {
	return to;
    }

    public void setTo(MDElement to) {
	this.to = to;
    }

    @Override
    public MDElement getSource() {
	return getFrom();
    }

    @Override
    public MDElement getTarget() {
	return getTo();
    }

    @Override
    public boolean equals(Object e) {

	if (e instanceof MDRelation) {
	    MDRelation rel = (MDRelation) e;

	    MDElement mdElemThis = new MDElement(this);
	    MDElement mdElemThat = new MDElement((MDElement) e);

	    MDElement thisFrom = new MDElement(this.getFrom());
	    MDElement thatFrom = new MDElement(rel.getFrom());

	    MDElement thisTo = new MDElement(this.getTo());
	    MDElement thatTo = new MDElement(rel.getTo());

	    if (mdElemThis.equals(mdElemThat) && thisFrom.equals(thatFrom) && thisTo.equals(thatTo)) {
		return true;
	    }
	}
	return false;
    }

    @Override
    public int hashCode() {
	return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
		append(((MDElement) this).getIdentifyingName()).append(this.getFrom().getIdentifyingName())
		.append(this.getTo().getIdentifyingName()).toHashCode();
    }

    @Override
    public String toString() {
	return this.getIdentifyingName() + "// type: " + this.getClass() + "//mapping: "
		+ getMapping().getQuery().getSparqlQuery().toString();
    }

}
