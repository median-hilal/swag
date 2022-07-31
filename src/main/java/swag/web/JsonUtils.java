package swag.web;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import swag.analysis_graphs.execution_engine.analysis_situations.AnalysisSituation;
import swag.analysis_graphs.execution_engine.navigations.NavigationStep;

public class JsonUtils {

    /**
     * Decodes a json string from the session storage to Map<String, Map<String,
     * String>> (the string should be compatible).
     * 
     * @param jsonStr
     *            the string to decode
     * @return the decoded map
     * @throws Exception
     *             when the decoding fails, e.g. the degree of depth of the json
     *             string is more than the anticipated one in this function.
     */
    public static Map<String, Map<String, String>> decodeJsonStringToMap(String jsonStr) throws Exception {

	try {
	    JsonParser jsonParser = new JsonParser();
	    JsonElement obj = jsonParser.parse(jsonStr);
	    Map<String, Map<String, String>> map = new HashMap<>();
	    if (obj.isJsonObject()) {
		JsonObject realObj = obj.getAsJsonObject();
		for (Map.Entry<String, JsonElement> entry : realObj.entrySet()) {
		    Map<String, String> asValues = new HashMap<>();
		    for (Map.Entry<String, JsonElement> entryLoopInner : entry.getValue().getAsJsonObject()
			    .entrySet()) {
			asValues.put(entryLoopInner.getKey(), entryLoopInner.getValue().getAsString());
		    }
		    map.put(entry.getKey(), asValues);
		}
	    }
	    return map;
	} catch (Exception ex) {
	    throw ex;
	}
    }

    /**
     * Encodes a Map<String, Map<String, String>> as a json string.
     * 
     * @param map
     *            the map to encode
     * @return the encoded String
     */
    public static String encodeMapAsJsonString(Map<String, Map<String, String>> map) {

	String finalOutput = "";
	finalOutput += "{";
	int counterOuter = 1;
	for (Map.Entry<String, Map<String, String>> entry : map.entrySet()) {
	    finalOutput += "\"" + entry.getKey() + "\":";
	    if (!entry.getValue().isEmpty()) {
		finalOutput += "{";
		int counter = 1;
		for (Map.Entry<String, String> innerEntry : entry.getValue().entrySet()) {
		    finalOutput += "\"" + innerEntry.getKey() + "\":" + "\"" + innerEntry.getValue() + "\"";
		    if (counter < entry.getValue().size()) {
			finalOutput += ",";
		    }
		    counter++;
		}
		finalOutput += "}";
	    }
	    if (counterOuter < map.size()) {
		finalOutput += ",";
	    }
	    counterOuter++;
	}
	finalOutput += "}";
	return finalOutput;
    }

    /**
     * @param originalString
     * @param map
     * @return
     */
    @Deprecated
    private static String appendAddedValuesAndLabels(String originalString, Map<String, String[]> map) {

	String res = originalString;
	for (Entry<String, String[]> entry : map.entrySet()) {
	    String strToAppend = calculateStrTOAppend(entry.getKey(), entry.getValue()[0], entry.getValue()[1]);
	    res += appendSingleAddedValuesAndLabels(originalString, strToAppend);
	}
	return res;
    }

    /**
     * @param uri
     * @param key
     * @param value
     * @return
     */
    @Deprecated
    private static String calculateStrTOAppend(String uri, String key, String value) {
	return "{\"" + uri + "\":" + "{\"" + key + "\": " + "\"" + value + "\"}}";
    }

    /**
     * @param str
     * @param strToAdd
     * @return
     */
    @Deprecated
    private static String appendSingleAddedValuesAndLabels(String str, String strToAdd) {

	String newStr = str;
	if (str.length() > 2) {
	    // remove the last '}'
	    newStr = str.substring(0, str.length() - 1) + ",";
	    // return the last '}' after appending the string to add
	    newStr = newStr + strToAdd + "}";
	} else {
	    newStr = str.substring(0, str.length() - 1);
	    // return the last '}' after appending the string to add
	    newStr = newStr + strToAdd + "}";
	}
	return newStr;
    }

    public static String generateASJSON(List<AnalysisSituation> asList) {
	String json = "";
	json += "[";

	int id = 0;
	int asListSize = asList.size();
	for (AnalysisSituation as : asList) {
	    json += "{";
	    json += "\"id\": \"" + id++ + "\", ";
	    json += "\"name\": \"" + as.getName() + "\", ";
	    json += "\"uri\": \"" + as.getURI() + "\", ";
	    json += "\"summary\": \"" + fullJustify(as.getSummary(), 50, "</br>") + "\", ";
	    // json+="\"x\": \"" + id * 100 + "\", ";
	    // json+="\"y\": \"" + id * 100 + "\", ";
	    json += "\"label\": \"" + as.getLabel() + "\", ";	
	    json += "\"reflexive\": " + "\"false\"";
	    // json+="\"fixed\": " + "\"true\"";
	    json += "}";
	    json += (id < asListSize) ? ", " : "";
	}
	json += "]";
	return json.replace("'", "\\'");
    }

    public static String generateNVJson(List<NavigationStep> nvList, List<AnalysisSituation> asList) {
	String json = "";
	json += "[";

	int id = 0;
	int asListSize = nvList.size();
	for (NavigationStep nv : nvList) {
	    id++;
	    json += "{";
	    json += "\"id\": \"" + id + "\", ";
	    json += "\"source\": " + asList.indexOf(nv.getSource()) + ", ";
	    json += "\"target\": " + asList.indexOf(nv.getTarget()) + ", ";
	    json += "\"name\": \"" + nv.getAbbName() + "\", ";
	    json += "\"summary\": \"" + fullJustify(nv.getSummary(), 50, "</br>") + "\", ";
	    json += "\"label\": \"" + nv.getLabel() + "\", ";
	    json += "\"left\":" + "\"false\", ";
	    json += "\"right\": " + "\"true\"";
	    json += "}";
	    json += (id < asListSize) ? ", " : "";
	}
	json += "]";
	return json.replace("'", "\\'");
    }

    public static String getRangedResultsASJSON(List<List<String>> rangedResultsWithHeader) {
	String json = "";
	json += "{";

	int id = 0;
	int asListSize = rangedResultsWithHeader.size();
	for (List<String> outLst : rangedResultsWithHeader) {

	    if (id == 0) {
		json += "\"cols\": [";
		int colsCntr = 0;
		int nonNumColsCntr = 0;
		for (String inStr : outLst) {
		    json += "{";

		    json += "\"id\": \"" + inStr + "\", ";
		    json += "\"label\": \"" + inStr + "\", ";

		    // TODO-soonsoon
		    if (inStr.contains("sum") || inStr.contains("avg") || inStr.contains("count")) {
			json += "\"type\": \"number\" ";
		    } else {
			if (nonNumColsCntr >= 1) {
			    json += "\"role\": \"annotation\", ";
			}
			json += "\"type\": \"string\" ";
			nonNumColsCntr++;
		    }

		    json += (colsCntr++ == outLst.size() - 1 ? "}" : "},");
		}
		json += "],";

		json += "\"rows\": [";
	    } else {

		int colsCntr = 0;

		String outStr = "";

		json += "{\"c\":[";

		for (String inStr : outLst) {

		    // TODO soon this if is added for the wikidata case. To show
		    // only labels after the artificially added '__'
		    if ((null != inStr) && inStr.contains("http://") && inStr.contains("__")) {
			String[] splitted = inStr.split("__");
			json += "{\"v\": \"" + splitted[1] + "\"} ";
		    } else {
			// TODO-soonsoon
			if ((null != inStr) && inStr.contains("^^")) {
			    String newStr = inStr.split("\\^\\^")[0];
			    if (NumberUtils.isNumber(newStr)) {

				DecimalFormat df = new DecimalFormat("#.##");
				df.setRoundingMode(RoundingMode.CEILING);
				Double d = Double.parseDouble(newStr);
				String ss = df.format(d);

				json += "{\"v\": " + ss.replace(",", ".") + "} ";
			    } else {
				json += "{\"v\": \"" + newStr + "\"} ";
			    }
			} else {
			    json += "{\"v\": \"" + inStr + "\"} ";
			}
		    }

		    // last row
		    if (colsCntr++ != outLst.size() - 1) {
			json += ",";
		    }

		}

		json += "]}";
		if (id != rangedResultsWithHeader.size() - 1) {
		    json += ",";
		}
	    }
	    ++id;
	}

	json += "]}";
	json = json.replace("'", "\\'");
	return json;
    }

    public static String getRangedResultsASJSONForPlainSPARQL(List<List<String>> rangedResultsWithHeader) {
	String json = "";
	json += "{";

	int id = 0;
	int asListSize = rangedResultsWithHeader.size();
	for (List<String> outLst : rangedResultsWithHeader) {

	    if (id == 0) {
		json += "\"cols\": [";
		int colsCntr = 0;
		int nonNumColsCntr = 0;
		for (String inStr : outLst) {
		    json += "{";

		    json += "\"id\": \"" + inStr + "\", ";
		    json += "\"label\": \"" + inStr + "\", ";

		    if (nonNumColsCntr >= 1) {
			json += "\"role\": \"annotation\", ";
		    }
		    json += "\"type\": \"string\" ";
		    nonNumColsCntr++;

		    json += (colsCntr++ == outLst.size() - 1 ? "}" : "},");
		}
		json += "],";

		json += "\"rows\": [";
	    } else {

		int colsCntr = 0;

		String outStr = "";

		json += "{\"c\":[";

		for (String inStr : outLst) {

		    // TODO soon this if is added for the wikidata case. To show
		    // only labels after the artificially added '__'
		    if ((null != inStr) && inStr.contains("http://") && inStr.contains("__")) {
			String[] splitted = inStr.split("__");
			json += "{\"v\": \"" + splitted[1] + "\"} ";
		    } else {
			// TODO-soonsoon
			if ((null != inStr) && inStr.contains("^^")) {
			    String newStr = inStr.split("\\^\\^")[0];
			    if (NumberUtils.isNumber(newStr)) {
				json += "{\"v\": " + newStr + "} ";
			    } else {
				json += "{\"v\": \"" + newStr + "\"} ";
			    }
			} else {
			    json += "{\"v\": \"" + inStr + "\"} ";
			}
		    }

		    // last row
		    if (colsCntr++ != outLst.size() - 1) {
			json += ",";
		    }

		}

		json += "]}";
		if (id != rangedResultsWithHeader.size() - 1) {
		    json += ",";
		}
	    }
	    ++id;
	}

	json += "]}";
	json = json.replace("'", "\\'");
	return json;
    }

    public static String fullJustify(String str, int maxWidth, String breakingChar) {

	if (!StringUtils.isEmpty(str)) {
	    String[] words = str.split(" ");

	    List<String> result = new ArrayList<String>();

	    if (words == null || words.length == 0) {
		return "";
	    }

	    int count = 0;
	    int last = 0;
	    ArrayList<String> list = new ArrayList<String>();
	    for (int i = 0; i < words.length; i++) {
		count = count + words[i].length();

		if (count + i - last > maxWidth) {
		    int wordsLen = count - words[i].length();
		    int spaceLen = maxWidth - wordsLen;
		    int eachLen = 1;
		    int extraLen = 0;

		    if (i - last - 1 > 0) {
			eachLen = spaceLen / (i - last - 1);
			extraLen = spaceLen % (i - last - 1);
		    }

		    StringBuilder sb = new StringBuilder();

		    for (int k = last; k < i - 1; k++) {
			sb.append(words[k]);

			int ce = 0;
			while (ce < eachLen) {
			    sb.append(" ");
			    ce++;
			}

			if (extraLen > 0) {
			    sb.append(" ");
			    extraLen--;
			}
		    }

		    sb.append(words[i - 1]);// last words in the line
		    // if only one word in this line, need to fill left with
		    // space
		    while (sb.length() < maxWidth) {
			sb.append(" ");
		    }

		    result.add(sb.toString());

		    last = i;
		    count = words[i].length();
		}
	    }

	    int lastLen = 0;
	    StringBuilder sb = new StringBuilder();

	    for (int i = last; i < words.length - 1; i++) {
		count = count + words[i].length();
		sb.append(words[i] + " ");
	    }

	    sb.append(words[words.length - 1]);
	    int d = 0;
	    while (sb.length() < maxWidth) {
		sb.append(" ");
	    }
	    result.add(sb.toString());

	    // Here making the final String
	    StringBuffer returned = new StringBuffer();

	    for (String str1 : result) {
		returned.append(str1);
		returned.append(breakingChar);
	    }

	    return returned.toString();
	}
	return StringUtils.EMPTY;
    }

}
