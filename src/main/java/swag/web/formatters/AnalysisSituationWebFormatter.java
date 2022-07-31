package swag.web.formatters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import swag.analysis_graphs.execution_engine.AnalysisGraph;
import swag.analysis_graphs.execution_engine.analysis_situations.AnalysisSituation;
import swag.web.WebConstants;

public class AnalysisSituationWebFormatter {

    /**
     * @param asList
     * @param nodesPositions
     * @return
     */
    public static Map<String, String[]> fleshCurrASs(List<AnalysisSituation> asList, String nodesPositions,
	    AnalysisSituation nextAs, AnalysisGraph graph) {

	Map<String, String[]> allASsResponseStr = new HashMap<String, String[]>();
	for (AnalysisSituation as : asList) {

	    String responseString1 = "";
	    String responseString2 = "";

	    responseString1 = "<link href='css/results.css' rel='styl/esheet' type='text/css'>";
	    responseString1 += "<form method='POST' autocomplete='off' class='oneHundredPercentHeight' action='ManipulateAnalysisGraphs' id='analysisSituation' name='analysisSituation'> "
		    + "<input type='hidden' name='nodesPositions' id='nodesPositions' value='' />"
		    + "<input type='hidden' name='transform' id='transform' value='' /> ";

	    responseString1 += "<div> "
		    + " <!--input style='display:none;' class='button' id='refreshResults' type='button' onclick='doAjaxSubmit(&quot;this&quot;)' value='REFRESH RESULTS' -->";
	    responseString1 += "<input type='hidden' name = 'labelValuePairs' id = 'labelValuePairs' value = ''>";
	    responseString1 += "<input type='hidden' name = 'labelValuePairsLabels' id = 'labelValuePairsLabels' value = ''>";

	    responseString1 += "<input type='button' onClick='submitForm(&quot;analysisSituation&quot;)' "
		    + "style='font-weight:bold; font-size:12px; "
		    /* + "display: flex; justify-content: center;" */
		    + "' " + "class='btn btn-info btn-sm' value='RESULTS'/> " + WebConstants.I_OPEN_RIGHT
		    + WebConstants.RESULTS + WebConstants.I_CLOSE + "<!--br/-->" + "<!--input "
		    + "class='btn btn-info btn-sm' "
		    + "id='showHideNonVarsChck' type='checkbox' onChange='showOrHideNonVariables();'> <font style='font-weight:bold; font-size:12px;'> Show only variables </font--> "
		    + "&ensp;&ensp;&ensp;&ensp; " + "</div> " + "<div class='autoOverflow'>";

	    responseString1 += "<input type='hidden' name='formType' value='asParams'/> ";
	    responseString1 += "<input type='hidden' id='navigateOrNot' name='navigateOrNot' value=''/>";
	    responseString1 += AnalysisSituationWebFormatter.toCollapsibleListWithVariables(as,
		    (as.equals(nextAs)) ? true : false, graph);
	    responseString1 += "<input type='hidden' name='currASName' value='" + as.getURI() + "'/>";
	    // this hidden input specifies the name of the current analysis
	    // situation from
	    // abbreviated name
	    responseString1 += "<input type='hidden' name='currASAbbName' value='" + as.getName() + "'/>"; // this
	    responseString1 += "</div>" + "</form> ";
	    responseString2 += "";// AnalysisSituationWebFormatter.toCollapsibleList(as);

	    allASsResponseStr.put(as.getName(), new String[] { responseString1, responseString2 });
	}
	return allASsResponseStr;
    }

    public static String toCollapsibleListWithVariables(AnalysisSituation as, boolean forResults, AnalysisGraph graph) {

	IAnalysisSituationWebPresenter presenter = new AnalysisSituationWebPresenter(graph);
	String asString = "";
	asString += presenter.presentHeader(as, graph);
	asString += presenter.presentNavigationSteps(as, forResults, graph);
	asString += presenter.presentMeasures(as, graph);
	// asString += presenter.presentBaseMeasureConditions(as, graph);
	asString += presenter.presentResultFilters(as, graph);
	asString += presenter.presentDimension(as, graph);
	return asString.replace("<http", "&lt;http");
    }

}
