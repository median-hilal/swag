package swag.analysis_graphs.execution_engine.analysis_situations;

public interface IClonableTo<T extends ISignatureType> {

    public IClonableTo<T> cloneMeTo(T to);
}
