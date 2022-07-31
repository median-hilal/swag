package swag.analysis_graphs.execution_engine;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import swag.analysis_graphs.execution_engine.analysis_situations.PredicateInAG;
import swag.analysis_graphs.execution_engine.analysis_situations.PredicateInAS;
import swag.analysis_graphs.execution_engine.analysis_situations.PredicateVariableToMDElementMapping;
import swag.md_elements.MDElement;

/**
 * 
 * Keeps track of the swag.predicates defined in an analysis graph, apart from
 * the fact whether the predicate is used in some analysis situation or not.
 * 
 * @author swag
 *
 */
public class DefinedAGPredicates {

    private Set<PredicateInAG> predicates;

    public DefinedAGPredicates() {
	predicates = new HashSet<>();
    }

    public void addPredicateInAG(PredicateInAG pred) {
	predicates.add(pred);
    }

    public PredicateInAG getPredicateByIdentifyingName(String identifyingName) {

	return this.predicates.stream().filter(x -> x.getURI().equals(identifyingName)).collect(Collectors.toList())
		.stream().findAny().orElse(null);
    }

    public Set<PredicateInAG> getPredicatesOfMDPositoin(MDElement elem) {

	Set<PredicateInAG> preds = new HashSet<>();

	if (MDElement.isMultipleElement(elem)) {
	    return predicates;
	} else {
	    for (PredicateInAG pred : this.predicates) {
		for (PredicateVariableToMDElementMapping mapping : pred.getVarMappings()) {
		    if (mapping.getElem().equals(elem)) {
			preds.add(pred);
			break;
		    }
		}
	    }
	    return preds;
	}
    }

    public String generatePredicateQuery(PredicateInAS pred) {

	return "";
    }

}
