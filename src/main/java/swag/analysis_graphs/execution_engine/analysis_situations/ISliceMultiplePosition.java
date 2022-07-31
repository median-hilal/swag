package swag.analysis_graphs.execution_engine.analysis_situations;

import java.util.List;

import javax.naming.OperationNotSupportedException;

import swag.md_elements.MDElement;

public interface ISliceMultiplePosition<T extends ISignatureType>
	extends IASItem<T>, Variable<T>, ISliceSinglePosition<T> {

    public List<MDElement> getPositions();

    public void setPositions(List<MDElement> positions) throws OperationNotSupportedException;

}
