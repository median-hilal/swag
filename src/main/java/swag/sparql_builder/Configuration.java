package swag.sparql_builder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.annotation.Nonnull;
import org.apache.log4j.Logger;

public class Configuration {

  private static Configuration singelton = null;
  private static String defaultPath = "";

  private static final Logger logger = Logger.getLogger(Configuration.class);
  private Properties prop;

  public static Configuration createInstance(String path) {
    synchronized (Configuration.class) {
      if (singelton == null) {
        singelton = new Configuration(path);
      }
    }
    return singelton;
  }

  public static Configuration getInstance() {
    synchronized (Configuration.class) {
      if (singelton == null) {
        singelton = new Configuration(defaultPath);
      }
    }
    return singelton;
  }

  private Configuration(String path) {
    initializeProperties(path);
  }

  private void initializeProperties(String path) {
    if (prop == null) {
      InputStream is = null;
      try {
        prop = new Properties();
        is = new FileInputStream(new File(path));
        prop.load(is);
      } catch (FileNotFoundException e) {
        logger.error(e);
      } catch (IOException e) {
        logger.error(e);
      }
    }
  }

  public boolean isReportingActive() {
    return getPropertyValue("reporting") == null ? true
        : "active".equals(getPropertyValue("reporting"));
  }

  public boolean is(String key) {
    return prop.getProperty(key).equals("true");
  }
  
  public boolean isLocal() {
	    return prop.getProperty("local").equals("true");
	  }

  public boolean is(String key, @Nonnull String value) {
    return value.equals(prop.getProperty(key));
  }

  public String getPropertyValue(String key) {
    return prop.getProperty(key);
  }

  public static void main(String a[]) {

  }
}
