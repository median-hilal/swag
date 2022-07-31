package swag.data_handler;

import java.util.HashSet;
import java.util.Set;
import org.apache.jena.ontology.Individual;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.apache.log4j.Logger;

import swag.analysis_graphs.execution_engine.analysis_situations.PredicateInAG;
import swag.analysis_graphs.execution_engine.analysis_situations.PredicateVariableToMDElementMapping;
import swag.md_elements.MDElement;
import swag.md_elements.MDSchema;
import swag.predicates.IPredicateGraph;
import swag.predicates.PredicateInstance;
import swag.predicates.PredicateVar;

public class PredicateRep implements IPredicateRep {

  private static final Logger logger = Logger.getLogger(PredicateRep.class);

  private OWlConnection owlConnection;
  private MDSchema mdSchema;
  private IPredicateGraph predGraph;

  public PredicateRep(OWlConnection owlConnection, MDSchema mdSchema, IPredicateGraph predGraph) {
    super();
    this.owlConnection = owlConnection;
    this.mdSchema = mdSchema;
    this.predGraph = predGraph;
  }

  @Override
  public PredicateInAG buildPredicate(Individual ind) {


    try {
      RDFNode basePredicateNode = this.owlConnection.getPropertyValueEnc(ind,
          this.owlConnection.getModel().getObjectProperty(
              OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.ON_PREDICATE));

      if (basePredicateNode != null && basePredicateNode.as(Individual.class).hasProperty(RDF.type,
          owlConnection.getClassByName("http://www.amcis2021.com/swag/pr#PredicateInstance"))) {

        Individual predicateInd = owlConnection.getPropertyValueEncAsIndividual(ind,
            this.owlConnection.getModel().getProperty(
                OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.ON_PREDICATE));

        if (predicateInd == null) {
          throw new Exception("Cannot find predicate instance " + ind);
        }

        PredicateInstance inst = (PredicateInstance) predGraph.getNode(predicateInd.getURI());
        if (inst == null) {
          throw new Exception("Cannot find predicate instance " + predicateInd);
        }

        org.apache.jena.rdf.model.StmtIterator mappingsItr = ind.listProperties(this.owlConnection
            .getModel().getProperty(OWLConnectionFactory.getAGNamespace(owlConnection)
                + Constants.HAS_VAR_TO_ELEMENT_MAPPING));

        Set<PredicateVariableToMDElementMapping> mappings = new HashSet<>();

        while (mappingsItr.hasNext()) {

          Statement mappingStmt = mappingsItr.nextStatement();
          RDFNode varToElemNode = mappingStmt.getObject();

          if (varToElemNode != null) {

            Individual varToElemInd = varToElemNode.as(Individual.class);

            Individual mdElemInd = owlConnection.getPropertyValueEncAsIndividual(varToElemInd,
                this.owlConnection.getModel().getProperty(
                    OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.HAS_MD_ELEM));

            if (mdElemInd == null) {
              throw new Exception("Cannot find MD Element " + varToElemInd);
            }



            Individual dim = owlConnection.getPropertyValueEncAsIndividual(mdElemInd,
                this.owlConnection.getModel()
                    .getProperty(OWLConnectionFactory.getAGNamespace(owlConnection)
                        + Constants.POS_DIMENSION));

            Individual hier = owlConnection.getPropertyValueEncAsIndividual(mdElemInd,
                this.owlConnection.getModel()
                    .getProperty(OWLConnectionFactory.getAGNamespace(owlConnection)
                        + Constants.POS_HIERARCHY));

            Individual posPos = owlConnection.getPropertyValueEncAsIndividual(mdElemInd,
                this.owlConnection.getModel().getProperty(
                    OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.POS_POS));


            if (dim == null || hier == null || posPos == null) {
              throw new Exception(
                  "Cannot find criteria for complex position: " + mdElemInd.getURI());
            }

            MDElement elem = mdSchema.getNode(mdSchema.getIdentifyingNameFromUriAndDimensionAndHier(
                posPos.getURI(), dim.getURI(), hier.getURI()));


            Individual varInd = owlConnection.getPropertyValueEncAsIndividual(varToElemInd,
                this.owlConnection.getModel().getProperty(
                    OWLConnectionFactory.getAGNamespace(owlConnection) + Constants.HAS_VAR));

            String connectOver = owlConnection.getPropertyValueEncAsString(varToElemInd,
                this.owlConnection.getModel()
                    .getProperty(OWLConnectionFactory.getAGNamespace(owlConnection)
                        + Constants.HAS_CONNECT_OVER));

            if (elem == null || varInd == null) {
              throw new Exception("Cannot find md element or variable " + varToElemInd.getURI());
            }

            PredicateVar var = inst.getInstanceOf().getVariableByURI(varInd.getURI());
            if (elem != null && var != null) {
              mappings.add(new PredicateVariableToMDElementMapping(var, elem,
                  connectOver != null ? QueryFactory.create(connectOver) : null));
            }
          }
        }

        PredicateInAG pred = new PredicateInAG(mappings, inst.getUri(), inst);
        return pred;
      }
    } catch (Exception ex) {
      logger.warn("Cannot get predicate " + ind.getURI() + " because of: " + ex.getMessage(), ex);
    }
    return null;
  }
}
