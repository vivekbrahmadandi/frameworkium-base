package com.frameworkium.base.properties;

import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public enum Property {

    BUILD("build"),
    JIRA_URL("jiraURL"),
    SPIRA_URL("spiraURL"),
    RESULT_VERSION("resultVersion"),
    ZAPI_CYCLE_REGEX("zapiCycleName"),
    JQL_QUERY("jqlQuery"),
    JIRA_USERNAME("jiraUsername"),
    JIRA_PASSWORD("jiraPassword"),
    SUT_NAME("sutName"),
    SUT_VERSION("sutVersion"),
    JIRA_RESULT_FIELD_NAME("jiraResultFieldName"),
    JIRA_RESULT_TRANSITION("jiraResultTransition"),
    JIRA_PROJECT_KEY("jiraProjectKey"), // TODO: exists in core, why not here?
    JIRA_BDD_FIELD_KEY("jiraBddFieldKey"),
    PROXY("proxy"),
    MAX_RETRY_COUNT("maxRetryCount"),
    // UI specific
    BROWSER("browser"),
    BROWSER_VERSION("browserVersion"),
    PLATFORM("platform"),
    PLATFORM_VERSION("platformVersion"),
    DEVICE("device"),
    CAPTURE_URL("captureURL"),
    GRID_URL("gridURL"),
    APP_PATH("appPath"),
    APPLICATION_NAME("applicationName"),
    SAUCE("sauce"),
    BROWSER_STACK("browserStack"),
    MAXIMISE("maximise"),
    RESOLUTION("resolution"),
    VIDEO_CAPTURE_URL("videoCaptureUrl"),
    CHROME_USER_DATA_DIR("chromeUserDataDir"),
    CUSTOM_BROWSER_IMPL("customBrowserImpl"),
    REUSE_BROWSER("reuseBrowser"),
    THREADS("threads"),
    HEADLESS("headless");

    private static Map<String, Object> configMap = null;
    private String value;

    Property(String key) {
        this.value = retrieveValue(key);
    }

    /**
     * Return a property value from system properties if present otherwise return from properties if present otherwise
     * will return null
     * @param key
     * @return value of property
     */
    private String retrieveValue(String key) {
        if (System.getProperty(key) != null) {
            return System.getProperty(key);
        } else {
            return getValueFromConfigFile(key);
        }
    }

    /**
     * Retrieve property value from config file
     * @param key
     * @return value of property
     */
    private String getValueFromConfigFile(String key) {
        if (configMap == null) {
            configMap = loadConfigFile();
        }

        Object objFromFile = configMap.get(key);
        if (objFromFile != null) {
            return Objects.toString(objFromFile);
        } else {
            return null;
        }
    }

    /**
     * Load properties from a config file into a Map
     * @return map of properties in config file
     */
    private static Map<String, Object> loadConfigFile() {
        String configFileName = System.getProperty("config");
        if (StringUtils.isNotEmpty(configFileName)) {
            try (InputStream configFileStream =
                         ClassLoader.getSystemClassLoader()
                                 .getResourceAsStream(configFileName)) {
                return new Yaml().load(configFileStream);
            } catch (IOException | YAMLException e) {
                throw new IllegalArgumentException(
                        "Properties file '" + configFileName + "' not found.", e);
            }
        } else {
            return Collections.emptyMap();
        }
    }

    /**
     * Check if a property is specified.
     *
     * @return true if the property is not empty ("") and not null
     */
    public boolean isSpecified() {
        return StringUtils.isNotEmpty(value);
    }

    public String getValue() {
        return value;
    }

    /**
     * @return true if the property is set and is equal, ignoring case, to "true".
     */
    public boolean getBoolean() {
        return isSpecified() && Boolean.parseBoolean(value);
    }

    public int getIntWithDefault(int defaultValue) {
        return isSpecified()
                ? Integer.parseInt(value)
                : defaultValue;
    }
}
