package org.letunov.util;

import org.springframework.format.Formatter;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateFormatter implements Formatter<Date>
{

    @Override
    public Date parse(String text, Locale locale) throws ParseException
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", locale);
        return simpleDateFormat.parse(text, new ParsePosition(0));
    }

    @Override
    public String print(Date object, Locale locale)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", locale);
        return simpleDateFormat.format(object);
    }
}
