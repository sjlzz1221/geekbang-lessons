package org.geektimes.configuration.microprofile.config.source;


import org.eclipse.microprofile.config.spi.ConfigSource;

import java.util.Set;


/**
 * @author: cola
 */
public abstract class BaseConfigSource implements ConfigSource {

    private int ordinal = 1000;

    @Override
    public Set<String> getPropertyNames() {
        return getProperties().keySet();
    }

    @Override
    public int getOrdinal() {
        return ordinal;
    }

    protected void initOrdinal(int defaultOrdinal) {
        ordinal = defaultOrdinal;

        String configuredOrdinalString = getValue(CONFIG_ORDINAL);

        if (configuredOrdinalString != null) {
            ordinal = Integer.parseInt(configuredOrdinalString.trim());
        }
    }

/*    private Config createConfig() {
        ServiceLoader<ConfigProviderResolver> serviceLoader = ServiceLoader.load(ConfigProviderResolver.class);
        Iterator<ConfigProviderResolver> iterator = serviceLoader.iterator();
        if (iterator.hasNext()) {
            // 获取 Config SPI 第一个实现
            return iterator.next().getConfig();
        }
        return null;
    }

    public Config getConfig() {
        return config;
    }*/
}
