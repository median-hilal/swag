package swag.analysis_graphs.execution_engine.analysis_situations;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import swag.md_elements.Dimension;
import swag.md_elements.Level;
import swag.md_elements.MDElement;
import swag.md_elements.QB4OHierarchy;
import swag.sparql_builder.ASElements.configuration.Configuration;

/**
 * 
 * Specifies a dimension qualification, composed of granularity, dice, and slice
 * specifications on a specific dimension for a specific analysis situation.
 * 
 * @author swag
 *
 */
public interface IDimensionQualification
	extends Serializable, ISignatureType, IConfigurationObject, IClonableTo<ISignatureType> {

    /**
     * 
     * Returns all the MD elements that are involved on the dimension/hierarchy.
     * 
     * @return a set of the involved MD elements
     * 
     */
    public default Set<MDElement> getAllInvolvedElms() {

	Set<MDElement> elms = new HashSet<>();

	if (getGanularity() != null) {
	    elms.add(getGanularity().getPosition());
	}
	if (getDiceLevel() != null) {
	    elms.add(getDiceLevel());
	}
	getSliceConditions().stream().map(x -> elms.add(x.getPositionOfCondition()));

	return elms;
    }

    /**
     * 
     * Get the enclosing analysis situation
     * 
     * @return
     */
    public AnalysisSituation getAs();

    /**
     * 
     * Set the enclosing analysis situation
     * 
     * @param as
     */
    public void setAs(AnalysisSituation as);

    /**
     * 
     * Checks whether the passed {@code IDimensionQualification} object shares
     * the dimension and the hierarchy with the current one.
     * 
     * @param that
     *            other {@code IDimensionQualification} to compare with
     * 
     * @return true if the passed {@code that} shares the dimension and the
     *         hierarchy with the current one.
     * 
     */
    public default boolean isOnSameHierarchyAndDimensionByURIs(String dimURI, String hierURI) {

	if (this.getD().getIdentifyingName().equals(dimURI)
		&& this.getHierarchy().getIdentifyingName().equals(hierURI)) {
	    return true;
	}
	return false;
    }

    /**
     * 
     * Checks whether the passed {@code IDimensionQualification} object shares
     * the dimension and the hierarchy with the current one.
     * 
     * @param that
     *            other {@code IDimensionQualification} to compare with
     * 
     * @return true if the passed {@code that} shares the dimension and the
     *         hierarchy with the current one.
     * 
     */
    public default boolean isOnSameHierarchyAndDimension(IDimensionQualification that) {

	if (this.getD().equals(that.getD()) && this.getHierarchy().equals(that.getHierarchy())) {
	    return true;
	}
	return false;
    }

    /**
     * 
     * Get the hierarchy on which the specification is defined
     * 
     * @return
     */
    public QB4OHierarchy getHierarchy();

    /**
     * 
     * Set the hierarchy on which the specification is defined
     * 
     * @param hier
     */
    public void setHierarchy(QB4OHierarchy hier);

    /**
     * 
     * Get the dimension on which the specification is defined
     * 
     * @return
     */
    public Dimension getD();

    /**
     * 
     * Set the dimension on which the specification is defined
     * 
     * @param dimension
     */
    public void setD(Dimension dimension);

    /**
     * 
     * Gets the slice conditions on the dimension.
     * 
     * @return a list of slice conditions, or an empty list if none exist.
     * 
     */
    public List<ISliceSinglePosition<IDimensionQualification>> getSliceConditions();

    /**
     * 
     * Gets the configuration (summarizability wise) of the dimension
     * qualification)
     * 
     * @return
     * 
     */
    @Override
    public Configuration getConfiguration();

    /**
     * 
     * Sets the configuration of the dimension.
     * 
     * @param configuration
     *            the configuration to set.
     * 
     */
    @Override
    public void setConfiguration(Configuration configuration);

    /**
     * 
     * Gets the dices on the dimension.
     * 
     * @return a list of dices, or an empty list if none exist.
     * 
     */
    public List<IDiceSpecification> getDices();

    /**
     * 
     * Sets the dices on the dimension.
     * 
     * @param dices
     *            a list of dices.
     * 
     */
    public void setDices(List<IDiceSpecification> dices);

    /**
     * 
     * Gets the dice node of the dimension. (Can be implemented as the dice node
     * of the first dice of the dimension)
     * 
     * @return the dice node on the dimension.
     * 
     */
    public DiceNodeInAnalysisSituation getDiceNode();

    /**
     * 
     * Sets the dice node of the dimension. (Can be implemented as the dice node
     * of the first dice of the dimension)
     * 
     * @param the
     *            dice node on the dimension.
     * 
     */
    public void setDiceNode(DiceNodeInAnalysisSituation dn);

    /**
     * 
     * Gets the dice level of the dimension. (Can be implemented as the dice
     * level of the first dice of the dimension)
     * 
     * @return the dice level on the dimension.
     * 
     */
    public LevelInAnalysisSituation getDiceLevel();

    /**
     * 
     * Sets the dice level of the dimension. (Can be implemented as the dice
     * level of the first dice of the dimension)
     * 
     * @param the
     *            dice level on the dimension.
     * 
     */
    public void setDiceLevel(LevelInAnalysisSituation diceLevel);

    public IGranularitySpecification getGanularity();

    public void setGranularity(IGranularitySpecification gran);

    public List<IGranularitySpecification> getGranularities();

    public void setGranularity(List<IGranularitySpecification> grans);

    public ISliceSetDim getSliceSet();

    public void setSliceSet(ISliceSetDim sliceSetDim);

    public List<ISliceSetDim> getSliceSets();

    public void setSliceSets(List<ISliceSetDim> sliceSetDims);

    public IDimensionQualification copy();

    public IDiceSpecification getDiceSpecByLevel(Level l);

    public default IDiceSpecification getSingleDice() {
	return getDices().get(0);
    }

    public default void setSingleDice(IDiceSpecification di) {
	getDices().clear();
	getDices().add(di);
    }
}
