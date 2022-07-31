package swag.helpers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public class StringHelper {

  public static final String xmlDataTypePref = "http://www.w3.org/2001/XMLSchema";

  public static String removeDataTypes(String str) {
    str = str.replace("^^", "");
    str = str.replaceAll("<" + xmlDataTypePref + "#.*?>", "");
    return str;
  }

  public static String repairHttpLeftQuoteForHtml(String str) {
    str = str.replace("<http", "&lt;http");
    return str;
  }

  public static String removeFilterIgnoreCase(String str) {
    if (!StringUtils.isEmpty(str)) {
      str = str.replaceAll("(?i)FILTER", "FILTER");
      String regexString = Pattern.quote("FILTER (") + "(.*?)" + Pattern.quote(")");
      Pattern pattern = Pattern.compile(regexString);
      // text contains the full text that you want to extract data
      Matcher matcher = pattern.matcher(str);
      String textInBetween = str;
      while (matcher.find()) {
        textInBetween = matcher.group(1);
      }
      return textInBetween;
    }
    return "";
  }

}
