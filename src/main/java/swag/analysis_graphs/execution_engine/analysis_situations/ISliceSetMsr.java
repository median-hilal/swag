package swag.analysis_graphs.execution_engine.analysis_situations;

import java.util.ArrayList;
import java.util.List;

import swag.md_elements.Dimension;
import swag.md_elements.MDElement;
import swag.md_elements.QB4OHierarchy;

/**
 * 
 * A (possibly variable) set of slice conditions to be bound under a slice saet
 * specification
 * 
 * @author swag
 *
 */
public interface ISliceSetMsr extends Variable<IDimensionQualification>, IASItem<IDimensionQualification> {

    @Override
    public ISliceSetMsr shallowCopy();

    public String getUri();

    public String getName();

    public void setUri(String uri);

    public void setName(String name);

    public default Dimension getDimension() {
	return this.getSignature().getContainingObject().getD();
    }

    public default QB4OHierarchy getHierarchy() {
	return this.getSignature().getContainingObject().getHierarchy();
    }

    public MDElement getPositoinOfSliceSet();

    public void setPositionOfMDElement(MDElement elem);

    public List<ISliceSinglePosition<IDimensionQualification>> getConditions();

    public void setConditions(List<ISliceSinglePosition<IDimensionQualification>> conditions);

    public default void addCondition(ISliceSinglePosition<IDimensionQualification> condition) {
	this.getConditions().add(condition);
    }

    public default void removeCondition(ISliceSinglePosition<IDimensionQualification> condition) {
	this.getConditions().remove(condition);
    }

    public default void emptyConditions() {
	setConditions(new ArrayList<ISliceSinglePosition<IDimensionQualification>>());
    }
}
