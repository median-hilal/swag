package swag.web.formatters;

import swag.analysis_graphs.execution_engine.AnalysisGraph;
import swag.analysis_graphs.execution_engine.analysis_situations.AnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.AnalysisSituationToBaseMeasureCondition;
import swag.analysis_graphs.execution_engine.analysis_situations.AnalysisSituationToResultFilters;
import swag.analysis_graphs.execution_engine.analysis_situations.DiceNodeInAnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.IDiceSpecification;
import swag.analysis_graphs.execution_engine.analysis_situations.IDimensionQualification;
import swag.analysis_graphs.execution_engine.analysis_situations.IGranularitySpecification;
import swag.analysis_graphs.execution_engine.analysis_situations.IMeasureInAS;
import swag.analysis_graphs.execution_engine.analysis_situations.ISliceSetDim;
import swag.analysis_graphs.execution_engine.analysis_situations.ISliceSinglePosition;
import swag.analysis_graphs.execution_engine.analysis_situations.LevelInAnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.PredicateInASMultiple;
import swag.analysis_graphs.execution_engine.navigations.NavigationStep;
import swag.sparql_builder.Configuration;
import swag.web.ServletsHelper;
import swag.web.WebConstants;

public class AnalysisSituationWebPresenter implements IAnalysisSituationWebPresenter {

    public AnalysisSituationWebPresenter(AnalysisGraph graph) {
	super();
	this.graph = graph;
    }

    private AnalysisGraph graph;

    @Override
    public String presentHeader(AnalysisSituation as, AnalysisGraph ag) {

	String asString = "";
	asString += "<div class='asDiv'>" + "<h4 class = 'specification'>" + " Analysis Situation "
		+ "<p style='font-size: 12px'>" + "<font color='#FFC0C0'>" + as.getLabel() + "</font> " + "</p> </h4>";

	/*
	 * asString += "<div> " + " <div>" + "" +
	 * "<a data-toggle='collapse' href='#acollapse-2'> <h5 class='specification'>  <span class='glyphicon glyphicon-minus'></span> Summary </h5> </a>"
	 * + " </div> ";
	 */
	asString += "<div id ='acollapse-2' class='panel-collapse collapse in'>" + "<h6 class='specification'>"
		+ as.getSummary() + " </h6>" + "</div>";

	return asString;
    }

    @Override
    public String presentNavigationSteps(AnalysisSituation as, boolean forResults, AnalysisGraph ag) {

	String asString = "<div> ";
	asString += " <div>" + ""
		+ "<a data-toggle='collapse' href='#acollapse-3'> <h5 class='specification'>  <span class='glyphicon "
		+ ((forResults) ? "glyphicon-minus" : "glyphicon-plus") + "'></span> Further Steps"
		+ WebConstants.I_OPEN_RIGHT + WebConstants.FURTHER_ACTIONS + WebConstants.I_CLOSE + "</h5> </a>"
		+ "</div> ";
	asString += "<div id ='acollapse-3' class='panel-collapse collapse" + ((forResults) ? " in" : "") + "'>"
		+ "  <ul>";

	for (NavigationStep nv : as.getOutNavigations()) {
	    asString += "<li class='nonVariableListItem'> " + "<h6 class='specification'>"
		    + "<a class='toVisitLink1' href='#' onclick='" + " triggerNVSelectionFromAS(&quot;"
		    + nv.getAbbName() + "&quot;)'>" + nv.getLabel() + "</a>" + " </h6>" + "</li>";
	}
	asString += "</ul> " + "</div>";
	return asString;
    }

    @Override
    public String presentMeasures(AnalysisSituation as, AnalysisGraph ag) {

	String asString = " <div>" + "" + "<a data-toggle='collapse' href='#acollapse2'> <h5 class='specification'> "
		+ " <img src='img/ico-measure.png'  width='30' height='30' />"
		+ " <span class='glyphicon glyphicon-minus'></span> Measures" + WebConstants.I_OPEN_RIGHT
		+ WebConstants.MEAURES_STRING + WebConstants.I_CLOSE + " </h5> </a>" + " </div> ";
	asString += "<div id ='acollapse2' class='panel-collapse collapse in'>  " + "<ul> ";

	for (IMeasureInAS m : as.getResultMeasures()) {
	    if (m.getSignature().isVariable()) {
		asString += "<li class='variableListItem'>";
	    } else {
		asString += "<li class='nonVariableListItem'>";
	    }
	    asString += " <h6 class='specification'>" + " <img src='img/ico-measure.png'  width='20' height='20' /> "
		    + "<p class='specification'> " + (m.getSignature().isVariableOrBoundVariable()
			    ? ServletsHelper.getVariableString(m, as, ag) : m.getLabel())
		    + "</h6> </li>";
	}
	asString += "</ul> </div>";
	return asString;
    }

    @Override
    public String presentBaseMeasureConditions(AnalysisSituation as, AnalysisGraph ag) {

	String asString = " <div>" + "" + "<a data-toggle='collapse' href='#acollapse-5'> <h5 class='specification'> "
		+ " <img src='img/ico-filter.png'  width='30' height='30' />" + " <span class='glyphicon glyphicon-"
		+ ((as.getResultBaseFilters().isEmpty()) ? "plus" : "minus") + "'></span> Base Measures Conditoins"
		+ WebConstants.I_OPEN_RIGHT + WebConstants.BASE_MEAURES_COND_STRING + WebConstants.I_CLOSE
		+ "</h5> </a>" + " </div> ";
	asString += "<div id ='acollapse-5' class='panel-collapse collapse"
		+ ((!as.getResultBaseFilters().isEmpty()) ? " in" : "") + "'>  " + "<ul> ";
	for (ISliceSinglePosition<AnalysisSituationToBaseMeasureCondition> m : as.getResultBaseFilters()) {

	    if (m.getSignature().isVariable()) {
		asString += "<li class='variableListItem'>";
	    } else {
		asString += "<li class='nonVariableListItem'>";
	    }

	    asString += " <h6 class='specification'> " + " <img src='img/ico-filter.png'  width='20' height='20' />"
		    + WebConstants.I_OPEN_RIGHT + WebConstants.SLICE_COND_STRING + WebConstants.I_CLOSE
		    + "<br/> <p class='specification'> " + (m.getSignature().isVariableOrBoundVariable()
			    ? "" + ServletsHelper.getVariableString(m, as, ag) : getConditoinDisplayString(m))
		    + "</p> " + "</h6> </li>";
	}
	asString += "</ul> </div>";
	return asString;
    }

    @Override
    public String presentResultFilters(AnalysisSituation as, AnalysisGraph ag) {

	String asString = " <div>" + "" + "<a data-toggle='collapse' href='#acollapse-6'> <h5 class='specification'>  "
		+ " <img src='img/ico-filter.png'  width='30' height='30' />" + "<span class='glyphicon glyphicon-"
		+ ((false) ? "plus" : "minus") + "'></span> Result Filters " + WebConstants.I_OPEN_RIGHT
		+ WebConstants.RESULT_FILTERS_STRING + WebConstants.I_CLOSE + "</h5> </a>" + " </div> ";
	asString += "<div id ='acollapse-6' class='panel-collapse collapse" + ((true) ? " in" : "") + "' >  " + "<ul> ";

	for (ISliceSinglePosition<AnalysisSituationToResultFilters> m : as.getResultFilters()) {

	    if (m.getSignature().isVariable()) {
		asString += "<li class='variableListItem'>";
	    } else {
		asString += "<li class='nonVariableListItem'>";
	    }
	    asString += " <h6 class='specification'> " + " <img src='img/ico-filter.png'  width='20' height='20' />"
		    + WebConstants.I_OPEN_RIGHT + WebConstants.SLICE_COND_STRING + WebConstants.I_CLOSE
		    + "<br/> <p class='specification'> " + (m.getSignature().isVariableOrBoundVariable()
			    ? "" + ServletsHelper.getVariableString(m, as, ag) : getConditoinDisplayString(m))
		    + "</p> " + "</h6> </li>";

	}
	asString += "</ul> </div>";
	return asString;
    }

    @Override
    public String presentMDConditions(AnalysisSituation as, AnalysisGraph ag) {

	String asString = " <div>" + "" + "<a data-toggle='collapse' href='#acollapse-4'> <h5 class='specification'> "
		+ " <img src='img/ico-filter.png'  width='30' height='30' />"
		+ " <span class='glyphicon glyphicon-minus'></span> Multidimensional Predicates"
		+ WebConstants.I_OPEN_RIGHT + WebConstants.SLICE_MULTIPLE_PRED_STRING + WebConstants.I_CLOSE + "<br/> "
		+ "</h5> </a>" + " </div> ";
	asString += "<div id ='acollapse-4' class='panel-collapse collapse in'>  " + "<ul> ";

	for (PredicateInASMultiple sc : as.getMultipleSlices()) {
	    PredicateInASMultiple predInAS = sc;
	    asString += "<li class='variableListItem'>";
	    asString += " <img src='img/ico-filter.png'  width='20' height='20' />"
		    + " <h6 class='specification'> <p class='specification'> "
		    + (predInAS.getSignature().isVariableOrBoundVariable()
			    ? "" + ServletsHelper.getVariableString(predInAS, as, ag)
			    : predInAS.getPredicateInstance().getName() + " -- "
				    + predInAS.getPredicateInstance().getDescription())
		    + "</p> " + "</h6> </li>";
	}
	asString += "</ul> </div>";
	return asString;
    }

    @Override
    public String presentDimension(AnalysisSituation as, AnalysisGraph ag) {

	String asString = "";
	asString += " <div>" + "" + " <a data-toggle='collapse' href='#acollapse3'> <h5 class='specification'> "
		+ " <img src='img/ico-hierarchy.png'  width='30' height='30' />"
		+ " <span class='glyphicon glyphicon-minus'></span>  Dimensions </h5> </a> " + " </div> ";
	asString += "<div id='acollapse3' class='panel-collapse collapse in'> <ul> " + " ";

	int dimsCounter = 4;
	for (IDimensionQualification dimToAS : as.getDimensionsToAnalysisSituation()) {

	    if (dimToAS.getGanularity() != null || dimToAS.getDiceLevel() != null || dimToAS.getDiceNode() != null
		    || dimToAS.getSliceConditions().size() > 0) {

		String hierString = (Configuration.getInstance().is("showHierarchies"))
			? "/" + (dimToAS.getHierarchy().getLabel()) : "";

		asString += "<div> " + "<h4 class='panel-title'>" + "<a data-toggle='collapse' href='#acollapse"
			+ dimsCounter + "'> <h5 class='specification'>"
			+ " <img src='img/ico-hierarchy.png'  width='20' height='20' />"
			+ " <span class='glyphicon glyphicon-minus'></span> " + dimToAS.getD().getLabel() + hierString
			+ "</h5> </a>" + " </h4> </div> ";

		asString += "<div id ='acollapse" + dimsCounter + "' class='panel-collapse collapse in'>  <ul>";

		asString += this.presentGranularity(as, ag, dimToAS);
		asString += this.presentDice(as, ag, dimToAS);
		asString += this.presentConditions(as, ag, dimToAS);

		asString += " </ul></div> ";
		dimsCounter++;
	    }

	}
	asString += " </ul> </div> ";
	return asString;
    }

    @Override
    public String presentGranularity(AnalysisSituation as, AnalysisGraph ag, IDimensionQualification dimToAS) {

	String asString = "";

	for (IGranularitySpecification gr : dimToAS.getGranularities()) {

	    LevelInAnalysisSituation lvl = gr.getPosition();
	    if (lvl != null) {
		if (lvl.getSignature().isVariableOrBoundVariable()) {
		    asString += "<li class='variableListItem'>";
		} else {
		    asString += "<li class='nonVariableListItem'>";
		}

		asString += "<h6 class='specification'>"
			+ " <img src='img/ico-granularity.png'  width='20' height='20' />"
			+ "  <p class='specification'> "

			+ (lvl.getSignature().isVariableOrBoundVariable()
				? /*
				   * "" + lvl.getSignature().getVriableName() +
				   */ "<br/>" + ServletsHelper.getVariableString(lvl, as, ag) : lvl.getLabel())
			+ "</p> " + WebConstants.I_OPEN_RIGHT + WebConstants.GRAN_LEVEL_STRING + WebConstants.I_CLOSE
			// +renderSet(lvl.getParentSpecificaiotn().getSet(), as)
			+ "</h6> </li>";
	    }
	}
	return asString;
    }

    @Override
    public String presentDice(AnalysisSituation as, AnalysisGraph ag, IDimensionQualification dimToAS) {

	String asString = "";

	for (IDiceSpecification dice : dimToAS.getDices()) {

	    LevelInAnalysisSituation lvl1 = dice.getPosition();
	    DiceNodeInAnalysisSituation node = dice.getDiceNodeInAnalysisSituation();

	    if (lvl1 != null) {

		if (lvl1 != null && lvl1.getSignature().isVariableOrBoundVariable()
			|| node != null && node.getSignature().isVariableOrBoundVariable()) {
		    asString += "<li class='variableListItem'>";
		} else {
		    asString += "<li class='nonVariableListItem'>";
		}

		asString += " <h6 class='specification'>  "
			+ " <img src='img/ico-dice-black.png'  width='20' height='20' />" + "<p class='specification'> "
			+ (lvl1.getSignature().isVariableOrBoundVariable()
				? /*
				   * " " + lvl.getSignature().getVriableName() +
				   */ "" + ServletsHelper.getVariableString(lvl1, as, ag) : lvl1.getLabel())
			+ "</p> " + WebConstants.I_OPEN_RIGHT + WebConstants.DICE_NODE_STRING + WebConstants.I_CLOSE
			// + renderSet(lvl.getParentSpecificaiotn().getSet(),
			// as)
			+ "<br/> <p class='specification'> "
			+ (node.getSignature().isVariableOrBoundVariable()
				? /*
				   * " " + node.getSignature().getVriableName()
				   * +
				   */ "" + ServletsHelper.getVariableString(node, as, ag) : node.getNodeValue())
			+ "</p>  " + "</h6> </li>";
	    }
	}

	return asString;
    }

    @Override
    public String presentConditions(AnalysisSituation as, AnalysisGraph ag, IDimensionQualification dimToAS) {

	String asString = "";

	for (ISliceSetDim set : dimToAS.getSliceSets()) {
	    for (ISliceSinglePosition<IDimensionQualification> sc : set.getConditions()) {

		ISliceSinglePosition<IDimensionQualification> cond = sc;

		/*
		 * if (sc instanceof PredicateInASSimple) {
		 * 
		 * SliceSpecificationPredicateImpl pred =
		 * (SliceSpecificationPredicateImpl) sc; PredicateInAS predInAS
		 * = pred.getPredicate();
		 * 
		 * 
		 * if (pos != null) { if
		 * (pos.getSignature().isVariableOrBoundVariable() ||
		 * predInAS.getSignature().isVariableOrBoundVariable()) {
		 * asString += "<li class='variableListItem'>"; } else {
		 * asString += "<li class='nonVariableListItem'>"; }
		 * 
		 * 
		 * asString +=
		 * 
		 * " <h6 class='specification'> " /*
		 * "<p class='specification'> " +
		 * (pos.getSignature().isVariableOrBoundVariable() ? // " "+
		 * pos.getSignature().getVriableName() + "" /* +
		 * ServletsHelper.getVariableString(pos, as) :
		 * pos.getSlicePosition().getName())
		 * 
		 * 
		 * "</p>"
		 */
		// + WebConstants.I_OPEN_RIGHT +
		// WebConstants.SLICE_PRED_POS_STRING +
		// WebConstants.I_CLOSE

		/*
		 * + WebConstants.I_OPEN_RIGHT + WebConstants.SLICE_PRED_STRING
		 * + WebConstants.I_CLOSE +
		 * renderSet(pos.getParentSpecificaiotn().getSet(), as) // +
		 * 
		 * + "<br/> <p class='specification'> " +
		 * (predInAS.getSignature().isVariableOrBoundVariable() ? //
		 * " "+ cond.getSignature().getVriableName() + "" +
		 * ServletsHelper.getVariableString(predInAS, as) :
		 * predInAS.getPredicateInstance().getName() + " -- " +
		 * predInAS.getPredicateInstance().getDescription()) + "</p> " +
		 * "</h6> </li>"; } } else
		 */

		{

		    if (cond.getSignature().isVariableOrBoundVariable()) {
			asString += "<li class='variableListItem'>";
		    } else {
			asString += "<li class='nonVariableListItem'>";
		    }

		    asString += " <h6 class='specification'> "
			    + " <img src='img/ico-filter.png'  width='20' height='20' />"
			    /*
			     * + "<p class='specification'> " +
			     * (pos.getSignature().isVariableOrBoundVariable() ?
			     * // " "+ pos.getSignature().getVriableName() + +
			     * ServletsHelper.getVariableString(pos, as) :
			     * pos.getSlicePosition().getName())
			     * 
			     * + "</p>" +
			     */
			    + WebConstants.I_OPEN_RIGHT + WebConstants.SLICE_COND_STRING + WebConstants.I_CLOSE
			    // +
			    // renderSet(pos.getParentSpecificaiotn().getSet(),
			    // as)
			    + "<br/> <p class='specification'> "
			    + (cond.getSignature().isVariableOrBoundVariable()
				    ? /*
				       * " "+
				       * cond.getSignature().getVriableName() +
				       */ "" + ServletsHelper.getVariableString(cond, as, ag)
				    : getConditoinDisplayString(cond))
			    + "</p> " + "</h6> </li>";

		}
	    }
	}

	return asString;
    }

    /**
     * 
     * Get condition label, or the actual expression if the label is not present
     * 
     * @param cond
     *            the condition
     * @return condition label or expression
     */
    private String getConditoinDisplayString(ISliceSinglePosition cond) {
	return PresentationUtils.getConditoinDisplayString(cond, graph);
    }
}
