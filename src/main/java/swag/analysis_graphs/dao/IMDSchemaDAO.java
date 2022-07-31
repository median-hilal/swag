package swag.analysis_graphs.dao;

import swag.md_elements.MDSchema;
import swag.md_elements.QB4OHierarchy;

/**
 * 
 * This interface provides methods to read a multidimeniosnal schema from a data connection and
 * create a corresponding {@code MDSchema}.
 * 
 * @author swag
 *
 */
public interface IMDSchemaDAO {

  /**
   * 
   * Builds an MD schema.
   * 
   * @return the built multidimensional schema, null in case of an exception
   * 
   */
  public default MDSchema buildMDSchema() {
    MDSchema schema = buildSpecificMDSchema();
    schema = buildCommonMDSchema(schema);
    return schema;
  }

  /**
   * 
   * Builds sections of MDschema that may differ by implementation
   * 
   * @return the built multidimensional schema, null in case of an exception
   * 
   */
  public MDSchema buildSpecificMDSchema();

  /**
   * 
   * Builds sections of MDschema that are common to all implementations
   * 
   * @return the built multidimensional schema, null in case of an exception
   * 
   */
  public default MDSchema buildCommonMDSchema(MDSchema schema) {
    if (schema != null) {
      schema.addNode(QB4OHierarchy.getDefaultHierarchy());
      schema.generateHierarchyIndimensionElements();
    }
    return schema;
  }

}
