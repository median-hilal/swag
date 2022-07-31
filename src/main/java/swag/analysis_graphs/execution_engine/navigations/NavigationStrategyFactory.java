package swag.analysis_graphs.execution_engine.navigations;


import swag.analysis_graphs.execution_engine.AnalysisGraph;
import swag.analysis_graphs.execution_engine.ConsiderNavigationVariablesStrategy;
import swag.analysis_graphs.execution_engine.NavigationStrategy;
import swag.analysis_graphs.execution_engine.NavigationStrategyPerformNavigation;
import swag.sparql_builder.Configuration;

public class NavigationStrategyFactory {

	public static NavigationStrategy getNavigationStrategy (AnalysisGraph graph){
		
		if (Configuration.getInstance().is("dynamicGenerationOfTarget")) {
			return new NavigationStrategyPerformNavigation(graph);
		}else{
			return new ConsiderNavigationVariablesStrategy();
		}
	}
}
