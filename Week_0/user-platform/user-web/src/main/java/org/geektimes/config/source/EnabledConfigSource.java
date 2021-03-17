package org.geektimes.config.source;

import org.eclipse.microprofile.config.Config;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * @author: cola
 */
public abstract class EnabledConfigSource extends BaseConfigSource {

    private static final String ENABLED = ".enabled";

    protected abstract Map<String, String> getPropertiesIfEnabled();

    @Override
    public Map<String, String> getProperties() {
        return isEnabled() ? getPropertiesIfEnabled() : Collections.emptyMap();
    }

    protected boolean isEnabled() {
        // Config config = getConfig();
        // return config.getOptionalValue(getInstanceEnableKey(), Boolean.class).orElse(true);
        return true;
    }

    private String getInstanceEnableKey() {
        return getName() + ENABLED;
    }

}
