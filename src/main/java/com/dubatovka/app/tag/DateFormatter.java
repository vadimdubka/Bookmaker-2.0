package com.dubatovka.app.tag;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * The class provides custom function to use on JSP pages which provides easier date and time
 * formatting.
 */
public final class DateFormatter {
    private static final Logger logger = LogManager.getLogger(DateFormatter.class);
    
    /**
     * Prints if given date or time parameter is null.
     */
    private static final String NOT_AVAILABLE = "-";
    
    private DateFormatter() {
    }
    
    /**
     * Formats given object due to given pattern.
     */
    public static String formatLocalDateTime(LocalDateTime localDateTime, String pattern) {
        return (localDateTime != null) ? localDateTime.format(DateTimeFormatter.ofPattern(pattern))
                                       : NOT_AVAILABLE;
    }
    
    /**
     * Formats given object due to given pattern.
     */
    public static String formatLocalDateTime(String dateTime, String pattern) {
        String result;
        try {
            DateFormat formatterFrom = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            Date date = formatterFrom.parse(dateTime);
            DateFormat formatterTo = new SimpleDateFormat(pattern);
            result = formatterTo.format(date);
        } catch (ParseException e) {
            logger.log(Level.ERROR, e.getMessage());
            result = NOT_AVAILABLE;
        }
        
        return result;
    }
    
    /**
     * Formats given object due to given pattern.
     */
    public static String formatLocalDate(LocalDate localDate, String pattern) {
        return (localDate != null) ? localDate.format(DateTimeFormatter.ofPattern(pattern))
                                   : NOT_AVAILABLE;
    }
}