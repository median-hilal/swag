package swag.data_handler;

import org.apache.jena.ontology.Individual;

import swag.analysis_graphs.execution_engine.analysis_situations.PredicateInAG;

public interface IPredicateRep {

  public PredicateInAG buildPredicate(Individual predIndiv);

}
