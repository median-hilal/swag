package swag.analysis_graphs.execution_engine.analysis_situations;

/**
 * Signature type of a selection condition containing object
 * 
 * @author swag
 */
public interface ISignatureType extends IClonableTo<ISignatureType> {

    /**
     * Compares objects based on their position; i.e., the MD element(s) they
     * are based on.
     * 
     * @param c
     *            the object to compare with
     * 
     * @return true if objects are positionally equal, false otherwise
     */
    public boolean comparePositoinal(ISignatureType c);

    /**
     * Generates a hash code from the object that takes into consideration only
     * the positional details (the MD elements involved)
     * 
     * @return
     */
    public int generatePositionalHashCode();
}
