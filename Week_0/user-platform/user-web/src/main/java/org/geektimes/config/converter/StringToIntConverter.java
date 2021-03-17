package org.geektimes.config.converter;

import org.eclipse.microprofile.config.spi.Converter;

/**
 * @author: cola
 */
public class StringToIntConverter implements Converter<Integer> {
    @Override
    public Integer convert(String value) throws IllegalArgumentException, NullPointerException {
        return Integer.parseInt(value);
    }
}
