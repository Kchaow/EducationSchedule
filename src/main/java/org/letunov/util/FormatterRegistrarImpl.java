package org.letunov.util;

import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;

public class FormatterRegistrarImpl implements FormatterRegistrar
{

    @Override
    public void registerFormatters(FormatterRegistry registry)
    {
        registry.addFormatter(new DateFormatter());
    }
}
