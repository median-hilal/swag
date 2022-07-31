package swag.analysis_graphs.execution_engine.analysis_situations;

import swag.analysis_graphs.execution_engine.DefinedAGConditions;
import swag.md_elements.MDElement;
import swag.predicates.LiteralCondition;

public interface ISliceSinglePosition<T extends ISignatureType> extends IASItem<T>, Variable<T> {

    public default LiteralCondition getReferencedCondition(DefinedAGConditions p) {
	return p.getConditoinByIdentifyingName(this.getURI());
    }

    public default void createAndBind(ISliceSinglePosition<?> dn, MDElement elem, String value, String uri) {

	/*
	 * if (dn instanceof SliceCondition<?>) {
	 * 
	 * SliceCondition dn1 = (SliceCondition) dn;
	 * 
	 * if (dn.isDimSlice()) { SliceCondition<IDimensionQualification> dnTemp
	 * = new SliceCondition<>(elem, value, uri); dn1.bind(dnTemp); } else {
	 * if (dn.isMultipleSlice()) { SliceCondition<AnalysisSituation> dnTemp
	 * = new SliceCondition<>(elem, value, uri); dn1.bind(dnTemp); } else {
	 * if (dn.isMsrSlice()) { SliceCondition<IMeasureToAnalysisSituation>
	 * dnTemp = new SliceCondition<>(elem, value, uri); dn1.bind(dnTemp); }
	 * } } } else { if (dn instanceof SliceConditionTyped<?>) {
	 * 
	 * SliceConditionTyped dn1 = (SliceConditionTyped) dn;
	 * 
	 * if (dn.isDimSlice()) { SliceConditionTyped<IDimensionQualification>
	 * dnTemp = new SliceConditionTyped<>(elem, value, uri);
	 * dn1.bind(dnTemp); } else { if (dn.isMultipleSlice()) {
	 * SliceCondition<AnalysisSituation> dnTemp = new SliceCondition<>(elem,
	 * value, uri); dn1.bind(dnTemp); } else { if (dn.isMsrSlice()) {
	 * SliceCondition<IMeasureToAnalysisSituation> dnTemp = new
	 * SliceCondition<>(elem, value, uri); dn1.bind(dnTemp); } } } } }
	 */
    }

    public default void BindParent() {
	/*
	 * if (this.isDimSlice()) { IDimensionQualification dim =
	 * (IDimensionQualification) this.getContainingObject();
	 * dim.getSliceSet().getSignature().setVariableState(VariableState.
	 * BOUND_VARIABLE); } else { if (this.isMultipleSlice()) {
	 * AnalysisSituation as = (AnalysisSituation)
	 * this.getContainingObject();
	 * as.getSliceSet().getSignature().setVariableState(VariableState.
	 * BOUND_VARIABLE); } else { if (this.isMsrSlice()) {
	 * 
	 * } } }
	 */
    }

    public default void unBindParent() {
	/*
	 * if (this.isDimSlice()) { IDimensionQualification dim =
	 * (IDimensionQualification) this.getContainingObject();
	 * 
	 * boolean shouldUnBind = true; if (dim.getSliceSet() != null) { for
	 * (ISliceSinglePosition<IDimensionQualification> s :
	 * dim.getSliceSet().getConditions()) { if
	 * (s.getSignature().isBoundVariable()) { shouldUnBind = false; } } } if
	 * (shouldUnBind) {
	 * dim.getSliceSet().getSignature().setVariableState(VariableState.
	 * VARIABLE); } } else { if (this.isMultipleSlice()) { AnalysisSituation
	 * as = (AnalysisSituation) this.getContainingObject();
	 * 
	 * boolean shouldUnBind = true; if (as.getSliceSet() != null) { for
	 * (ISliceMultiplePosition<AnalysisSituation> s :
	 * as.getSliceSet().getConditions()) { if
	 * (s.getSignature().isBoundVariable()) { shouldUnBind = false; } } } if
	 * (shouldUnBind) {
	 * as.getSliceSet().getSignature().setVariableState(VariableState.
	 * VARIABLE); } } else { if (this.isMsrSlice()) {
	 * 
	 * } } }
	 */
    }

    public default boolean isWrittenCondition() {
	return "".equals(this.getURI());
    }

    public default Class<T> getTypeOfContainingObject() {
	return (Class<T>) this.getSignature().getContainingObject().getClass();
    }

    public default boolean isDimSlice() {
	return (this.getContainingObject() instanceof IDimensionQualification);
    }

    public default boolean isMultipleSlice() {
	return (this.getContainingObject() instanceof AnalysisSituation);
    }

    public default boolean isMsrSlice() {
	return (this.getContainingObject() instanceof IMeasureSignatureType);
    }

    public String getType();

    public void setType(String type);

    public MDElement getPositionOfCondition();


    public void setPositionOfCondition(MDElement e);



    public String getURI();

    public void setURI(String condition);

    public String getConditoin();

    public void setCondition(String condition);

}
