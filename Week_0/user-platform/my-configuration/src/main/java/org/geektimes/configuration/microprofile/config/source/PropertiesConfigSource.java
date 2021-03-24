package org.geektimes.configuration.microprofile.config.source;


import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author: cola
 */
public class PropertiesConfigSource extends EnabledConfigSource {
    private final Map<String, String> properties = new HashMap<>();

    private static final String CONFIG = "config";
    private static final String APPLICATION = "application";
    private static final String FILEEXTENSION = "properties";
    private static final String DOT = ".";
    private static final String URL = "url";

    private String path;

    public PropertiesConfigSource(){
        init();
    }

    private void init() {
        this.path = getDefaultPath();
        loadResource(path);

        super.initOrdinal(500);
    }

    private void loadResource(String path) {
        InputStream in = this.getClass().getResourceAsStream(path);
        Properties props = new Properties();
        try {
            props.load(in);
            properties.putAll((Map) props);
        } catch (IOException e) {
        }

    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    protected Map<String, String> getPropertiesIfEnabled() {
        return properties;
    }

    @Override
    public String getValue(String key) {
        return super.isEnabled() ? this.properties.get(key) : null;
    }

    /*private String loadResourcePath() {
        return getConfig().getOptionalValue(CONFIG + DOT + URL, String.class).orElse(getDefaultPath());
    }*/

    private String getDefaultPath() {
        return "/" + APPLICATION + DOT + FILEEXTENSION;
    }

}
