package swag.analysis_graphs.execution_engine;

import java.util.List;

import org.apache.log4j.Logger;

import swag.analysis_graphs.dao.IAnalysisGraphDAO;
import swag.analysis_graphs.dao.IDataDAO;
import swag.analysis_graphs.dao.IMDSchemaDAO;
import swag.analysis_graphs.execution_engine.analysis_situations.AnalysisSituation;
import swag.analysis_graphs.execution_engine.navigations.NavigationStep;
import swag.analysis_graphs.execution_engine.navigations.NavigationVisitorDynamic;
import swag.analysis_graphs.execution_engine.operators.Operation;
import swag.md_elements.MDSchema;

/**
 * 
 * 
 * 
 * @author swag
 *
 */
public class NavigationStrategyPerformNavigation implements NavigationStrategy {

    private static final Logger logger = Logger.getLogger(NavigationStrategyPerformNavigation.class);

    AnalysisGraph graph;

    public NavigationStrategyPerformNavigation(AnalysisGraph graph) {
	super();
	this.graph = graph;
    }

    private AnalysisSituation generatedTrget;

    @Override
    public void doNavigate(NavigationStep nv, MDSchema schema, IMDSchemaDAO mdDao, IAnalysisGraphDAO agDao,
	    IDataDAO dataDao) {

	List<NavigationStep> outSteps = nv.getTarget().getOutNavigations();
	graph.getAnalysisSituations().remove(nv.getTarget());

	logger.info("Navigating from " + nv.getSource().getName() + " to " + nv.getTarget().getName());

	NavigationVisitorDynamic mv = new NavigationVisitorDynamic(nv.getSource(), nv.getSource().cloneMe(), schema, mdDao, agDao,
		dataDao);

	for (Operation op : nv.getOperators()) {
	    try {
		logger.info("Performing navigation operation " + op.getName() + " of type " + op.getOperatorName());
		op.accept(mv);
	    } catch (Exception e) {
		System.err.println("Navigation exception..." + e.getMessage());
		e.printStackTrace();
	    }
	}
	nv.setTarget(mv.getDes());
	this.setGeneratedTrget(nv.getTarget());

	nv.getTarget().getInNavigations().add(nv);

	nv.getTarget().setOutNavigations(outSteps);
	outSteps.stream().forEach(x -> x.setSource(nv.getTarget()));
	graph.getAnalysisSituations().add(nv.getTarget());

    }

    public AnalysisSituation getGeneratedTrget() {
	return generatedTrget;
    }

    public void setGeneratedTrget(AnalysisSituation generatedTrget) {
	this.generatedTrget = generatedTrget;
    }
}
