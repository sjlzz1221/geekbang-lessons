package org.geektimes.configuration.microprofile.config.source;


import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author: cola
 */
public class SystemConfigSource extends BaseConfigSource {

    private final Map<String, String> properties;

    public SystemConfigSource(){
        Map systemProperties = System.getProperties();
        this.properties = new HashMap<>(systemProperties);
        super.initOrdinal(300);
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
    public String getValue(String key) {
        return this.properties.get(key);
    }

}
