package swag.analysis_graphs.execution_engine.analysis_situations;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * A (possibly variable) set of slice conditions to be bound under a slice saet
 * specification
 * 
 * @author swag
 *
 */
public interface ISliceSetMultiple extends Variable<AnalysisSituation>, IASItem<AnalysisSituation> {

    @Override
    public ISliceSetMultiple shallowCopy();

    public String getUri();

    public String getName();

    public void setUri(String uri);

    public void setName(String name);

    public default AnalysisSituation getAS() {
	return this.getSignature().getContainingObject();
    }

    public List<ISliceMultiplePosition<AnalysisSituation>> getConditions();

    public void setConditions(List<ISliceMultiplePosition<AnalysisSituation>> conditions);

    public default void addCondition(ISliceMultiplePosition<AnalysisSituation> condition) {
	this.getConditions().add(condition);
    }

    public default void removeCondition(ISliceMultiplePosition<AnalysisSituation> condition) {
	this.getConditions().remove(condition);
    }

    public default void emptyConditions() {
	setConditions(new ArrayList<ISliceMultiplePosition<AnalysisSituation>>());
    }
}
