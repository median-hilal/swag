package swag.analysis_graphs.dao;

import org.apache.jena.util.iterator.ExtendedIterator;

import swag.data_handler.Constants;
import swag.data_handler.MDSchemaBuilder;
import swag.data_handler.MDSchemaBuilderQB4O;
import swag.data_handler.OWlConnection;

public class MDSchemaBuilderFactory {

  public static IMDSchemaDAO getMDMDSchemaDAO(OWlConnection owlConn) {

    ExtendedIterator<? extends org.apache.jena.ontology.OntResource> dsItr =
        owlConn.getModel().getOntClass(Constants.QB4O_DATA_STRUCTURE_DEF).listInstances();

    if (dsItr.hasNext()) {
      return new MDSchemaBuilderQB4O(owlConn);
    } else {
      return new MDSchemaBuilder(owlConn);
    }
  }
}
