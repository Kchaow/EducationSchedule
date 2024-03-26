package org.letunov.util;

import org.springframework.format.Formatter;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class LocalDateFormatter implements Formatter<LocalDate>
{
    private final DateTimeFormatter dateTimeFormatter;
    public LocalDateFormatter()
    {
        dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    }

    @Override
    public LocalDate parse(String text, Locale locale) throws ParseException
    {
        return LocalDate.parse(text, dateTimeFormatter);
    }

    @Override
    public String print(LocalDate object, Locale locale)
    {
        return dateTimeFormatter.format(object);
    }
}
