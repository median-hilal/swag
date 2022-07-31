package swag.analysis_graphs.execution_engine.analysis_situations;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import swag.md_elements.Mapping;
import swag.md_elements.Measure;
import swag.web.IVariableVisitor;

/**
 * 
 * This class represents a measure, either a simple basic measure or an
 * expression derived measure.
 * 
 * @author swag
 *
 */
public class MeasureDerived extends Measure implements IMeasure {

	/**
	 * 
	 */
	private static final long serialVersionUID = 207932859258193090L;

	private String comment;
	private String expression;
	private List<Measure> measures;
	private String dataType = StringUtils.EMPTY;

	/**
	 * Generates an alias from an expression. Returns the alias if exists or the
	 * measure name used as a variable otherwise. In case the measure is a
	 * simple measure, it returns the head var name of the project of the
	 * mapping query. A question mark (?) is always appended to the name.
	 * 
	 * @return
	 */
	public String getAliasVarname() {

		if (isDerived()) {
			if (this.expression != null) {
				String[] strs = expression.split(" (?i)AS ");
				if (strs.length == 2) {
					return strs[1];
				}
			}
			return "?" + getName();
		} else {
			return "?" + getHeadVar().getName();
		}
	}

	/**
	 * Checks whether a measure is derived or simple.
	 * 
	 * @return
	 */
	public boolean isDerived() {
		if (measures.size() == 0 && expression == null) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * Create a derived measure
	 * 
	 * @param uri
	 * @param name
	 * @param comment
	 * @param expression
	 * @param measures
	 * @param label
	 */
	public MeasureDerived(String uri, String name, String comment, String expression, List<Measure> measures,
			String label) {
		super(uri, name, new Mapping(), label);
		super.setURI(uri);
		super.setName(name);
		this.comment = comment;
		this.expression = expression;
		this.measures = measures;
	}

	/**
	 * 
	 * Create a base measure
	 * 
	 * @param uri
	 * @param name
	 * @param comment
	 * @param expression
	 * @param measures
	 * @param label
	 */
	public MeasureDerived(String uri, String name, String comment, String expression, String label, Mapping map) {
		super(uri, name, map, label);
		super.setURI(uri);
		super.setName(name);
		this.comment = comment;
		this.expression = expression;
		this.measures = new ArrayList<Measure>();
	}

	/**
	 * 
	 * Create a base measure
	 * 
	 * @param uri
	 * @param name
	 * @param comment
	 * @param expression
	 * @param measures
	 * @param label
	 * @param dataType
	 */
	public MeasureDerived(String uri, String name, String comment, String expression, String label, Mapping map,
			String dataType) {
		super(uri, name, map, label);
		super.setURI(uri);
		super.setName(name);
		this.comment = comment;
		this.expression = expression;
		this.dataType = dataType;
		this.measures = new ArrayList<Measure>();
	}

	/**
	 * shallow copy constructor
	 * 
	 * @param meas
	 */
	public MeasureDerived(MeasureDerived meas) {
		super(meas.getURI(), meas.getName(), new Mapping(), meas.getLabel());
		this.comment = meas.comment;
		this.expression = meas.expression;
		this.measures = meas.measures.stream().map(x -> x.deepCopy()).collect(Collectors.toList());
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof MeasureDerived) {
			MeasureDerived ms = (MeasureDerived) o;
			if (this.getURI().equals(ms
					.getURI()) /*
								 * && this.getMapping().equals(ms.getMapping())
								 */)
				return true;
		}
		return false;
	}

	@Override
	public void acceptVisitor(IVariableVisitor visitor) throws Exception {
		visitor.visit(this);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
				appendSuper(super.hashCode()).append(this.getURI()).toHashCode();
	}

	public List<Measure> getMeasures() {
		return this.measures;
	}

	public void setMeasures(List<Measure> measures) {
		this.measures = measures;
	}

	public String getExpression() {
		return this.expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
}
