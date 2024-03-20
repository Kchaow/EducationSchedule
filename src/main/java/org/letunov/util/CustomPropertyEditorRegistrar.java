package org.letunov.util;

import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;

import java.time.LocalDate;

public class CustomPropertyEditorRegistrar implements PropertyEditorRegistrar
{
    @Override
    public void registerCustomEditors(PropertyEditorRegistry registry)
    {
        registry.registerCustomEditor(LocalDate.class, new LocalDateEditor());
    }
}
