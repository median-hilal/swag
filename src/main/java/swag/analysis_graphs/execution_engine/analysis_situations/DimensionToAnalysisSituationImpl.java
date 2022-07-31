package swag.analysis_graphs.execution_engine.analysis_situations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import swag.md_elements.Dimension;
import swag.md_elements.Level;
import swag.md_elements.QB4OHierarchy;
import swag.sparql_builder.ASElements.configuration.Configuration;

public class DimensionToAnalysisSituationImpl implements IDimensionQualification {

    /**
     * 
     */
    private static final long serialVersionUID = -1785284946682976897L;

    private AnalysisSituation as;
    private Dimension dimension;
    private QB4OHierarchy hier;

    private List<IDiceSpecification> dices = new ArrayList<>();
    private List<IGranularitySpecification> granularities = new ArrayList<>();
    private List<ISliceSetDim> slices = new ArrayList<>();

    private Configuration configuration = new Configuration(new HashMap<>());

    public DimensionToAnalysisSituationImpl(Dimension dimension, QB4OHierarchy hier) {
	super();
	this.dimension = dimension;
	this.hier = hier;
    }

    @Override
    public LevelInAnalysisSituation getDiceLevel() {
	if (!this.dices.isEmpty()) {
	    return Optional.ofNullable(this.getSingleDice().getPosition()).orElse(null);
	}
	return null;
    }

    @Override
    public void setDiceLevel(LevelInAnalysisSituation diceLevel) {
	if (!this.dices.isEmpty()) {
	    this.getDices().get(0).setDiceLevelInAnalysisSituation(diceLevel);
	}
    }

    @Override
    public ISliceSetDim getSliceSet() {
	if (!this.slices.isEmpty()) {
	    return slices.get(0);
	} else {
	    this.setSliceSet(new SliceSet());
	    return getSliceSet();
	}
    }

    @Override
    public void setSliceSet(ISliceSetDim sliceSetDim) {
	if (!this.slices.isEmpty()) {
	    this.slices.remove(0);
	}
	this.slices.add(0, sliceSetDim);
    }

    @Override
    public void setConfiguration(Configuration configuration) {
	this.configuration = configuration;
    }

    @Override
    public AnalysisSituation getAs() {
	return as;
    }

    @Override
    public void setAs(AnalysisSituation as) {
	this.as = as;
    }

    @Override
    public Dimension getD() {
	return dimension;
    }

    @Override
    public void setD(Dimension dimension) {
	this.dimension = dimension;
    }

    @Override
    public List<ISliceSinglePosition<IDimensionQualification>> getSliceConditions() {
	if (this.getSliceSet() != null) {
	    return getSliceSet().getConditions();
	} else {
	    this.setSliceSet(new SliceSet());
	    return getSliceSet().getConditions();
	}
    }

    public void setSliceSpecificaiotn(List<ISliceSinglePosition<IDimensionQualification>> sliceSpecification) {
	this.getSliceSet().setConditions(sliceSpecification);
    }

    public QB4OHierarchy getHier() {
	return hier;
    }

    public void setHier(QB4OHierarchy hier) {
	this.hier = hier;
    }

    public DimensionToAnalysisSituationImpl() {
    }

    @Override
    public IDimensionQualification copy() {

	DimensionToAnalysisSituationImpl dimToAS = new DimensionToAnalysisSituationImpl();

	dimToAS.slices = this.getSliceSets().stream().map(x -> x.shallowCopy()).collect(Collectors.toList());
	dimToAS.dices = this.getDices().stream().map(x -> x.shallowCopy()).collect(Collectors.toList());
	dimToAS.granularities = this.getGranularities().stream().map(x -> x.shallowCopy()).collect(Collectors.toList());

	dimToAS.as = this.getAs();
	dimToAS.dimension = this.getD();
	dimToAS.hier = this.getHier();

	dimToAS.configuration = new Configuration(new HashMap<String, String>(configuration.getConfigurationsMap()));

	return dimToAS;
    }

    @Override
    public Configuration getConfiguration() {
	return this.configuration;
    }

    @Override
    public QB4OHierarchy getHierarchy() {
	return hier;
    }

    @Override
    public void setHierarchy(QB4OHierarchy hier) {
	this.hier = hier;
    }

    @Override
    public DiceNodeInAnalysisSituation getDiceNode() {
	if (!this.dices.isEmpty()) {
	    return Optional.ofNullable(this.getSingleDice()).map(IDiceSpecification::getDiceNodeInAnalysisSituation)
		    .orElse(null);
	}
	return null;
    }

    @Override
    public void setDiceNode(DiceNodeInAnalysisSituation dn) {
	if (this.getSingleDice() != null) {
	    this.getSingleDice().setDiceNodeInAnalysisSituation(dn);
	}
    }

    @Override
    public List<IDiceSpecification> getDices() {
	return this.dices;
    }

    @Override
    public void setDices(List<IDiceSpecification> dices) {
	this.dices = dices;
    }

    @Override
    public IGranularitySpecification getGanularity() {
	if (!this.granularities.isEmpty()) {
	    return this.granularities.get(0);
	}
	return null;
    }

    @Override
    public void setGranularity(IGranularitySpecification gran) {
	if (!this.granularities.isEmpty()) {
	    this.granularities.remove(0);
	}
	this.granularities.add(0, gran);
    }

    @Override
    public List<IGranularitySpecification> getGranularities() {
	return this.granularities;
    }

    @Override
    public void setGranularity(List<IGranularitySpecification> grans) {
	this.granularities = grans;
    }

    @Override
    public List<ISliceSetDim> getSliceSets() {
	return this.slices;
    }

    @Override
    public void setSliceSets(List<ISliceSetDim> sliceSetDims) {
	this.slices = sliceSetDims;
    }

    @Override
    public IDiceSpecification getDiceSpecByLevel(Level l) {
	return dices.stream().filter(x -> x.getPosition().equalsIgnoreType(l)).findFirst().orElse(null);
    }

    @Override
    public boolean comparePositoinal(ISignatureType c) {

	if (this == c) {
	    return true;
	}

	if (c instanceof DimensionToAnalysisSituationImpl) {
	    DimensionToAnalysisSituationImpl as = (DimensionToAnalysisSituationImpl) c;
	    if (this.getD().equals(as.getD()) && this.getHier().equals(as.getHier())) {
		return true;
	    }
	}
	return false;
    }

    @Override
    public int generatePositionalHashCode() {
	return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
		append(this.getD()).append(this.getHier()).toHashCode();
    }

    @Override
    public IClonableTo<ISignatureType> cloneMeTo(ISignatureType to) {
	DimensionToAnalysisSituationImpl imp = new DimensionToAnalysisSituationImpl();
	imp.setAs((AnalysisSituation) to);
	imp.setD(this.getD());
	imp.setHier(this.getHier());
	imp.setConfiguration(this.getConfiguration());
	imp.setDices(
		this.getDices().stream().map(x -> (IDiceSpecification) x.cloneMeTo(imp)).collect(Collectors.toList()));
	imp.setSliceSets(
		this.getSliceSets().stream().map(x -> (ISliceSetDim) x.cloneMeTo(imp)).collect(Collectors.toList()));
	imp.setGranularity(this.getGranularities().stream().map(x -> (IGranularitySpecification) x.cloneMeTo(imp))
		.collect(Collectors.toList()));
	return imp;
    }

}
