package swag.analysis_graphs.execution_engine.operators;

import swag.analysis_graphs.execution_engine.analysis_situations.IGranularitySpecification;
import swag.md_elements.Dimension;
import swag.md_elements.QB4OHierarchy;

/**
 * Umbrella Navigation Operation for the operations that change a granularity level of an analysis
 * situation.
 * 
 * @author swag
 */
public abstract class ChangeGranularityOperator extends DimOperation {

  /**
   * 
   */
  private static final long serialVersionUID = 2112927680107007243L;
  private final static String OPERATOR_NAME = "Change granularity to";
  private IGranularitySpecification granSpec;

  public String getOperatorName() {
    return OPERATOR_NAME;
  }

  public IGranularitySpecification getGranSpec() {
    return granSpec;
  }

  public void setGranSpec(IGranularitySpecification granSpec) {
    this.granSpec = granSpec;
  }

  public ChangeGranularityOperator(String name, String abbName, IGranularitySpecification granSpec,
      Dimension dimension, QB4OHierarchy hierarchy) {
    super(name, abbName, dimension, hierarchy);
    setGranSpec(granSpec);
  }

  /**
   * 
   * Constructs a new {@code ChangeGranularityOperator} with label and comment being set.
   * 
   * @param uri uri of the operation
   * @param name local name of the operation
   * @param label the label
   * @param comment the comment
   * @param dim dimension of the operation
   * @param hier hierarchy of the operation
   * @param granSpec
   */
  public ChangeGranularityOperator(String uri, String name, String label, String comment,
      IGranularitySpecification granSpec, Dimension dim, QB4OHierarchy hier) {
    super(uri, name, label, comment, dim, hier);
    setGranSpec(granSpec);
  }

  @Override
  public void accept(IOperatorVisitor visitor) throws Exception {
    visitor.visit(this);
  }

}
