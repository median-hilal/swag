package swag.analysis_graphs.execution_engine.analysis_situations;

import java.io.Serializable;

import swag.analysis_graphs.execution_engine.Signature;

/**
 * 
 * Enforcing having a signature. Represents an element that can be a variable.
 * 
 * @author swag
 *
 * @param <T>
 */
public interface IASItem<T extends ISignatureType> extends Serializable, IClonableTo<T> {

    /**
     * 
     * gets the signature
     * 
     * @return
     */
    Signature<T> getSignature();

    /**
     * 
     * Sets the signature
     * 
     * @param signature
     */
    void setSignature(Signature<T> signature);

}
