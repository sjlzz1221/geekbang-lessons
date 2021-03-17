package org.geektimes.config.converter;

import org.eclipse.microprofile.config.spi.Converter;

/**
 * @author: cola
 */
public class StringToLongConverter implements Converter<Long> {
    @Override
    public Long convert(String value) throws IllegalArgumentException, NullPointerException {
        return Long.parseLong(value);
    }
}
