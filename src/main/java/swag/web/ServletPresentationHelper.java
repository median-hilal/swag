package swag.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import org.apache.commons.validator.routines.UrlValidator;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

import swag.analysis_graphs.execution_engine.analysis_situations.AnalysisSituation;
import swag.helpers.AutoCompleteData;
import swag.sparql_builder.ASElements.configuration.DimensionConfigurationObject;
import swag.sparql_builder.ASElements.configuration.MeasureConfigurationObject;

public class ServletPresentationHelper {

  public static void appendToMap(Map<String, Map<String, String>> labels,
      Map<String, String[]> addedLabels) {

    for (Map.Entry<String, String[]> entry : addedLabels.entrySet()) {
      if (labels.containsKey(entry.getKey()) && labels.get(entry.getKey()) != null) {
        labels.get(entry.getKey()).put(entry.getValue()[0], entry.getValue()[1]);
      } else {
        Map<String, String> vals = new HashMap<String, String>();
        vals.put(entry.getValue()[0], entry.getValue()[1]);
        labels.put(entry.getKey(), vals);
      }
    }
  }

  public static List<List<String>> getResultsRange(List<List<String>> results, int startIndex,
      int rowsCount) {
    try {
      return results.subList(startIndex, startIndex + rowsCount);
    } catch (IndexOutOfBoundsException ex) {
      if (startIndex < results.size()) {
        return results.subList(startIndex, results.size());
      } else {
        return new ArrayList<List<String>>();
      }
    }
  }

  public static String getRangedResponse(List<List<String>> rangedResults) {

    String rangedResponse = "";
    rangedResponse += "";
    for (List<String> tmpOut : rangedResults) {
      rangedResponse += "<tr>";
      for (String tmpIn : tmpOut) {
        rangedResponse += "<td>";
        rangedResponse += tmpIn;
        rangedResponse += "</td>";
      }
      rangedResponse += "</tr>";
    }
    rangedResponse += "";
    return rangedResponse;
  }

  public static String getURIOfStringValue(String val,
      List<AutoCompleteData> alreadyLoadedSuggestions) {

    UrlValidator urlValidator = new UrlValidator();
    if (urlValidator.isValid(val)) {
      return val;
    } else {
      for (AutoCompleteData dat : alreadyLoadedSuggestions) {
        if (dat.getLabel().toUpperCase().equals(val.toUpperCase())) {
          return dat.getValue();
        }
      }
    }
    return val;
  }

  public static String trimQueryString(String str) {
    return str.trim();// .replace("\n", "<br/>");
  }

  /**
   * 
   * Escaping single quotes
   * 
   * @param str the string to escape characters from
   * 
   * @return the resulting string with escaped characters
   * 
   */
  public static String escapeSpecialCharacters(String str) {
    if (str != null)
      return str.replace("'", "\\'")/* .replaceAll("\"","\\\\\"") */;
    else
      return null;
  }

  public static String breakifyString(String str, int lineLength, String breakingChar) {

    StringBuilder sb = new StringBuilder(str);



    int i = 0;
    while ((i = sb.indexOf(" ", i + 30)) != -1) {
      sb.replace(i, i + 1, "\n");
    }

    return "";
  }


  /**
   * 
   * Changes variable names (in the results header) to the name expressing each variable in the
   * analysis situation.
   * 
   * @param results the SPARQL Query result set
   * @param as the analysis situation to get variable names from
   * 
   * @return list of new variables names
   * 
   */
  public static List<String> substituteResultHeaderVarNames(ResultSet results,
      AnalysisSituation as) {

    List<String> resultsHeader = new ArrayList<>();
    resultsHeader.addAll(results.getResultVars());
    ListIterator<String> itr = resultsHeader.listIterator();
    while (itr.hasNext()) {
      String newVarName = as.getMDElementNameByQueryVariableName(itr.next());
      itr.remove();
      itr.add(newVarName);
    }
    return resultsHeader;
  }

  /**
   * 
   * Puts results in a displayable format
   * 
   * @param results the SPARQL Query result set
   * 
   * @return results
   */
  public static List<List<String>> prepareResults(ResultSet results) {

    List<List<String>> finalResults = new ArrayList<>();
    for (; results.hasNext();) {
      List<String> varValues = new ArrayList<String>();
      QuerySolution soln = results.nextSolution();
      for (String v : results.getResultVars()) {
        varValues.add((soln.get(v) != null)
            ? ServletPresentationHelper.removeLabelSuffix(soln.get(v).toString())
            : null);
      }
      finalResults.add(varValues);
    }
    return finalResults;
  }

  /**
   * 
   * Puts results in a html form represented by a String.
   * 
   * @param finalResults the displayable results
   * @param resultsHeader the headers of the results
   * @param as the analysis situation to get names of variables from
   * 
   * @return a string representing html of the results
   * 
   */
  public static String putResultsAsHTMLString(List<List<String>> finalResults,
      List<String> resultsHeader, AnalysisSituation as) {

    StringBuilder builder = new StringBuilder();
    builder.append("<link href='css/results.css' rel='stylesheet' type='text/css'>");

    builder.append(
        "<div id='results'> <link href='css/styl1.css' rel='stylesheet' type='text/css'> <table id='results'>");
    builder.append("<tr>");
    for (String tmpIn : resultsHeader) {
      builder.append("<th>");
      builder.append(as.getMDElementNameByQueryVariableName(tmpIn));
      builder.append("</th>");
    }
    builder.append("</tr>");

    for (List<String> tmpOut : finalResults) {
      builder.append("<tr>");
      for (String tmpIn : tmpOut) {
        builder.append("<td>");
        builder.append(tmpIn);
        builder.append("</td>");
      }
      builder.append("</tr>");
    }
    builder.append("</table>");

    for (MeasureConfigurationObject msrConfig : as.getMsrConfigs()) {
      builder.append(msrConfig);
    }
    for (DimensionConfigurationObject dimConfig : as.getDimConfigs()) {
      builder.append(dimConfig);
    }

    builder.append(" </div>");
    return builder.toString();
  }

  public static String removeLabelSuffix(String oldLabel) {

    String newLabel = oldLabel;
    if (oldLabel.contains("@")) {
      newLabel = oldLabel.substring(0, oldLabel.lastIndexOf("@"));
    }
    return newLabel;
  }

}
