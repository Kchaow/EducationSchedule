package org.letunov.util;

import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateEditor extends PropertyEditorSupport
{
    @Override
    public void setAsText(String str) //dd-MM-yyyy
    {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        setValue(LocalDate.parse(str, dateTimeFormatter));
    }
}
