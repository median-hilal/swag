package swag.analysis_graphs.execution_engine;

import java.util.List;
import java.util.Set;

import swag.analysis_graphs.execution_engine.analysis_situations.AnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.PredicateInAG;
import swag.analysis_graphs.execution_engine.navigations.NavigationStep;
import swag.md_elements.MDElement;
import swag.md_elements.MDSchema;
import swag.predicates.LiteralCondition;

/**
 * 
 * The data structure that holds the analysis graph and its components.
 * 
 * @author swag
 *
 */
public class AnalysisGraph {

    private String ssss;
    private String name;
    private String uri;
    private String namespace;
    private List<AnalysisSituation> analysisSituations;
    private List<NavigationStep> navigationSteps;
    private DefinedAGPredicates definedAGPredicates;
    private DefinedAGConditions definedAGConditions;
    private DefinedAGConditionsTypes definedAGConditionTypes;
    private MDSchema schema;

    public AnalysisGraph(String name, String uri, String namespace, List<AnalysisSituation> analysisSituations,
	    List<NavigationStep> navigationSteps, DefinedAGPredicates definedAGPredicates,
	    DefinedAGConditions definedAGConditions, DefinedAGConditionsTypes definedAGConditionTypes,
	    MDSchema schema) {
	super();
	this.name = name;
	this.uri = uri;
	this.namespace = namespace;
	this.analysisSituations = analysisSituations;
	this.navigationSteps = navigationSteps;
	this.definedAGPredicates = definedAGPredicates;
	this.definedAGConditions = definedAGConditions;
	this.definedAGConditionTypes = definedAGConditionTypes;
	this.schema = schema;
    }

    public List<AnalysisSituation> getAnalysisSituations() {
	return analysisSituations;
    }

    public void setAnalysisSituations(List<AnalysisSituation> analysisSituations) {
	this.analysisSituations = analysisSituations;
    }

    public List<NavigationStep> getNavigationSteps() {
	return navigationSteps;
    }

    public void setNavigationSteps(List<NavigationStep> navigationSteps) {
	this.navigationSteps = navigationSteps;
    }

    public MDSchema getSchema() {
	return schema;
    }

    public void setSchema(MDSchema schema) {
	this.schema = schema;
    }

    public DefinedAGConditionsTypes getDefinedAGConditionTypes() {
	return definedAGConditionTypes;
    }

    public void setDefinedAGConditionTypes(DefinedAGConditionsTypes definedAGConditionTypes) {
	this.definedAGConditionTypes = definedAGConditionTypes;
    }

    public DefinedAGPredicates getDefinedAGPredicates() {
	return definedAGPredicates;
    }

    public void setDefinedAGPredicates(DefinedAGPredicates definedAGPredicates) {
	this.definedAGPredicates = definedAGPredicates;
    }

    public DefinedAGConditions getDefinedAGConditions() {
	return definedAGConditions;
    }

    public void setDefinedAGConditions(DefinedAGConditions definedAGConditions) {
	this.definedAGConditions = definedAGConditions;
    }

    public Set<PredicateInAG> getPredicatesOfMDPositoin(MDElement elem) {
	return definedAGPredicates.getPredicatesOfMDPositoin(elem);
    }

    public Set<LiteralCondition> getLiteralConditionsOfMDPositoin(MDElement elem) {
	return definedAGConditions.getConditionsOfMDPositoin(elem);
    }

    public Set<LiteralCondition> getLiteralConditionsOfTypeOfMDPositoin(MDElement elem, String type) {
	return definedAGConditions.getConditionsOfTypeOfMDPositoin(elem, type);
    }

    public String getNamespace() {
	return namespace;
    }

    public void setNamespace(String namespace) {
	this.namespace = namespace;
    }

    @Override
    public String toString() {
	String str = "";
	System.out.println("Situations: ");
	for (AnalysisSituation as : this.getAnalysisSituations()) {
	    System.out.println(as.getName());
	    System.out.println("Out steps: ");
	    for (NavigationStep step : as.getOutNavigations()) {
		System.out.println(step.getName());
	    }
	    System.out.println("---------");
	}

	return str;
    }

}
