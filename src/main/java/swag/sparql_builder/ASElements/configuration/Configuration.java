package swag.sparql_builder.ASElements.configuration;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * Simple class to represent summarizability options
 * 
 * @author swag
 *
 */
public class Configuration implements Serializable {

  private static final Map<String, String> explanations;
  static {
    Map<String, String> aMap = new HashMap<>();
    aMap.put("",
        "The finest granularity level on this dimension is mandatory. The selected fact instances must have values for the finest granularity level on this dimension.");
    aMap.put("",
        "All the multidimensional elements on this dimension are mandatory. The selected fact instances must have values for all these elements.");
    aMap.put("",
        "The finest granularity level on this dimension is mandatory. The selected fact instances must have values for the finest granularity level on this dimension.");
    aMap.put("", "two");
    explanations = Collections.unmodifiableMap(aMap);
  }

  /**
   * 
   */
  private static final long serialVersionUID = 7361072274090545138L;

  private Map<String, String> configurationsMap = new HashMap<String, String>() {
    {
      // put("aggregationfunction", "sum");
      // put("optional", "true");
      // put("nonstrict", "split");
    }
  };

  public void resetConfiguration() {
    this.configurationsMap = new HashMap<>();
  }

  public Map<String, String> getConfigurationsMap() {
    return configurationsMap;
  }

  public void setConfigurationsMap(Map<String, String> configurationsMap) {
    this.configurationsMap = configurationsMap;
  }


  public Configuration() {
    super();
  }

  public Configuration(Map<String, String> configurations) {
    super();
    this.configurationsMap = configurations;
  }

  public String getConfiguration(String confName) {
    return configurationsMap.get(confName);
  }

  public boolean is(String option) {
    return "true".equalsIgnoreCase(getConfiguration(option));
  }

  public boolean is(String option, String value) {
    return value.equalsIgnoreCase(getConfiguration(option));
  }

  public String get(String option) {
    return getConfiguration(option);
  }

  public String explain() {

    return "";
  }

}
