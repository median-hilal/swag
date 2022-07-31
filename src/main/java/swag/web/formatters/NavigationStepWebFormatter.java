package swag.web.formatters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import swag.analysis_graphs.execution_engine.AnalysisGraph;
import swag.analysis_graphs.execution_engine.navigations.NavigationStep;
import swag.analysis_graphs.execution_engine.operators.Operation;
import swag.helpers.StringHelper;
import swag.web.WebConstants;

public class NavigationStepWebFormatter {

    private static final Logger logger = Logger.getLogger(NavigationStepWebFormatter.class);

    public static Map<String, String[]> fleshCurrNVs(List<NavigationStep> nvList, AnalysisGraph ag) {

	Map<String, String[]> allNVsResponseStr = new HashMap<String, String[]>();

	for (NavigationStep nv : nvList) {
	    String responseString1 = "";
	    String responseString2 = "";
	    responseString1 += "";
	    responseString1 += "<link href='css/results.css' rel='stylesheet' type='text/css'>";
	    // responseString1 += "<center> <B> <h4> Navigation Step " +
	    // nv.getAbbName() + " </h4> </B> </center>";
	    responseString1 += "<form method='POST' autocomplete='off' action='ManipulateAnalysisGraphs' id='navigationStep' name='navigationStep'> "
		    + "<input type='button' id='navigateButton' onClick='submitForm(&quot;navigationStep&quot;)' style='font-weight:bold; font-size:12px;' class='btn btn-info btn-sm' value='NAVIGATE'/>"
		    + WebConstants.I_OPEN_RIGHT + WebConstants.NAVIGATE + WebConstants.I_CLOSE
		    + "<input type='hidden' name='nodesPositions' id='nodesPositions' value='' />"
		    + "<input type='hidden' name='transform' id='transform' value='' /> ";
	    responseString1 += "<input type='hidden' name = 'labelValuePairs' id = 'labelValuePairs' value = ''>";
	    responseString1 += "<input type='hidden' name = 'labelValuePairsLabels' id = 'labelValuePairsLabels' value = ''>";
	    responseString1 += "<input type='hidden' name='formType' value='nvNav'/> ";
	    responseString1 += "<input type='hidden' name='currNVName' value='" + nv.getName() + "'/>";
	    responseString1 += "<input type='hidden' name='currNVAbbName' value='" + nv.getAbbName() + "'/>";
	    responseString1 += NavigationStepWebFormatter.toCollapsibleList1(nv);
	    responseString1 += NavigationStepWebFormatter.toCollapsibleList(nv, ag);
	    responseString1 += " </form>";
	    // responseString2 += nv.toCollapsibleList();
	    allNVsResponseStr.put(nv.getAbbName(), new String[] { responseString1, responseString2 });
	}
	return allNVsResponseStr;
    }

    /**
     * This function In case the navigation step allows variables, it's treated
     * here. Otherwise, the operator own printing function is called.
     * 
     * @param nv
     *            the navigation step at hand
     * @return
     */
    public static String toCollapsibleList(NavigationStep nv, AnalysisGraph ag) {

	NavigationStepPresentationVisitor visitor1 = new NavigationStepPresentationVisitor(nv, ag);
	NavigationStepImagePresentationVisitor visitor0 = new NavigationStepImagePresentationVisitor();

	String asString = "";
	// asString += "<div> <div> <a data-toggle='collapse'
	// href='#acollapse-1'> <h5 class='specification'> "
	// + "<span class='glyphicon glyphicon-minus'></span> Summary </h5>
	// </a>" + " </div> ";
	asString += "<div id ='acollapse-1' class='panel-collapse collapse in'>";
	asString += "<h6 class='specification'>" + nv.getSummary() + " </h6>";
	asString += "</div>" + "<div id ='collapse1' class='panel-collapse collapse'> <ul>";
	asString += "<li> <h6 class='specification'  id='factName'>"
		+ (nv.getTarget().getFact() != null ? nv.getTarget().getFact().getName() : " ") + "</h6> </li>";
	asString += "</ul> </div> ";
	;

	/*
	 * asString += " <div> " + "" +
	 * "<a data-toggle='collapse' href='#collapse-2'> <h5 class='specification'>  <span class='glyphicon glyphicon-minus'></span>  Source "
	 * + WebConstants.I_OPEN_RIGHT + WebConstants.SOURCE +
	 * WebConstants.I_CLOSE + "</h5>  </a>" + " </div>"; asString +=
	 * "<div id ='collapse-2' class='panel-collapse collapse'> <ul>";
	 * asString += "<li> <h6 class='specification'>" +
	 * (nv.getSource().getName()) + ": " + nv.getSource().getSummary() +
	 * "</h6> </li>"; asString += "</ul> </div> ";
	 * 
	 * 
	 * asString += " <div>" + "" +
	 * "<a data-toggle='collapse' href='#collapse2'> <h5 class='specification'>  <span class='glyphicon glyphicon-minus'></span> Target "
	 * + WebConstants.I_OPEN_RIGHT + WebConstants.TARGET +
	 * WebConstants.I_CLOSE + " </h5> </a>" + " </div> ";
	 * 
	 * asString +=
	 * "<div id ='collapse2' class='panel-collapse collapse'> <ul>";
	 * asString += "<li> <h6 class='specification'>" +
	 * (nv.getTarget().getName()) + ": " + nv.getTarget().getSummary() +
	 * "</h6> </li>"; asString += "</ul> </div> ";
	 */

	asString += " <div>" + ""
		+ " <a data-toggle='collapse' href='#collapse3'> <h5 class='specification'>  <span class='glyphicon glyphicon-minus'></span>  Operations "
		+ WebConstants.I_OPEN_RIGHT + WebConstants.OPERATIONS + WebConstants.I_CLOSE + " </h5> </a> "
		+ " </div> ";
	asString += "<div id='collapse3' align='left' class='panel-collapse collapse in'> <ul> " + " ";
	int dimsCounter = 4;
	for (Operation op : nv.getOperators()) {

	    try {
		op.accept(visitor0);
		op.accept(visitor1);
	    } catch (Exception e) {
		logger.error(e);
	    }

	    asString += "<div> " + "<a data-toggle='collapse' href='#collapse" + dimsCounter
		    + "'> <h5 class='specification'>" + visitor0.getGeneratedString() + "</h5> </a>" + " </h4> </div> ";

	    asString += "<div id ='collapse" + dimsCounter + "' class='panel-collapse collapse in'>  <ul>";

	    asString += visitor1.getGeneratedString();
	    asString += " </ul> </div>";

	    ++dimsCounter;
	}
	asString += " </ul> </div> </div>";
	asString = StringHelper.repairHttpLeftQuoteForHtml(asString);
	return asString;
    }

    public static String toCollapsibleList1(NavigationStep nv) {
	return "<div> <h4 class = 'specification'> Navigation Step <p style='font-size: 12px'> <font color='#FFC0C0'>"
		+ nv.getLabel() + "</font> </p> </h4>  </div>" + "<!-- div> <h6 class = 'specification'> "
		+ nv.getSummary() + "</h6-->";
    }

}
