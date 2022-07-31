package swag.analysis_graphs.execution_engine.analysis_situations;

public final class CloneUtils {

    public static void createVariables(AnalysisSituation as) {

	for (IDimensionQualification dimToAS : as.getDimensionsToAnalysisSituation()) {
	    if (dimToAS.getGanularity() != null) {
		dimToAS.getGanularity().addToAnalysisSituationVariables(as);
	    }
	    if (dimToAS.getDiceLevel() != null) {
		dimToAS.getDiceLevel().addToAnalysisSituationOrNavigationStepVariables(as);
	    }
	    if (dimToAS.getDiceNode() != null) {
		dimToAS.getDiceNode().addToAnalysisSituationOrNavigationStepVariables(as);
	    }
	    for (ISliceSinglePosition<IDimensionQualification> sc : dimToAS.getSliceConditions()) {
		sc.addToAnalysisSituationOrNavigationStepVariables(as);
	    }
	}

	for (PredicateInASMultiple slice : as.getMultipleSlices()) {
	    slice.addToAnalysisSituationOrNavigationStepVariables(as);
	}

	for (ISliceSinglePosition<AnalysisSituationToBaseMeasureCondition> cond : as.getResultBaseFilters()) {
	    cond.addToAnalysisSituationOrNavigationStepVariables(as);
	}

	for (ISliceSinglePosition<AnalysisSituationToResultFilters> cond : as.getResultFilters()) {
	    cond.addToAnalysisSituationOrNavigationStepVariables(as);
	}

	for (ISliceSinglePosition<AnalysisSituationToMDConditions> cond : as.getMDConditions()) {
	    cond.addToAnalysisSituationOrNavigationStepVariables(as);
	}

	as.addVariablesToInitialVariables();
    }
}
