package swag.analysis_graphs.execution_engine.operators;

import swag.analysis_graphs.execution_engine.analysis_situations.IDiceSpecification;
import swag.md_elements.Dimension;
import swag.md_elements.QB4OHierarchy;

public class MoveToDiceNodeOperator extends DimOperation {

    /**
     * 
     */
    private static final long serialVersionUID = -5388584251598985918L;
    private final static String OPERATOR_NAME = "Move to dice node";

    @Override
    public String getOperatorName() {
	return OPERATOR_NAME;
    }

    private IDiceSpecification diceSpecification;

    public IDiceSpecification getDiceSpecification() {
	return diceSpecification;
    }

    public void setDiceSpecification(IDiceSpecification diceSpecification) {
	this.diceSpecification = diceSpecification;
    }

    public MoveToDiceNodeOperator(String uri, String name, Dimension opOnDimension,
	    IDiceSpecification diceSpecification, QB4OHierarchy hier) {
	super(uri, name, opOnDimension, hier);
	setDiceSpecification(diceSpecification);
    }

    /**
     * 
     * Constructs a new {@code MoveToDiceNodeOperator} with label and comment
     * being set.
     * 
     * @param uri
     *            uri of the operation
     * @param name
     *            local name of the operation
     * @param label
     *            the label
     * @param comment
     *            the comment
     * @param dim
     *            dimension of the operation
     * @param hier
     *            hierarchy of the operation
     * @param diceSpecification
     */
    public MoveToDiceNodeOperator(String uri, String name, String label, String comment, Dimension dim,
	    IDiceSpecification diceSpecification, QB4OHierarchy hier) {
	super(uri, name, label, comment, dim, hier);
	setDiceSpecification(diceSpecification);
    }

    @Override
    public void accept(IOperatorVisitor visitor) throws Exception {
	visitor.visit(this);
    }

    @Override
    public String getTypeLabel() {
	return "Move to Dice Node";
    }
}
