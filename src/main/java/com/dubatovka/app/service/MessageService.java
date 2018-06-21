package com.dubatovka.app.service;

import com.dubatovka.app.config.ConfigConstant;

import java.util.Locale;

/**
 * Interface for Service layer actions for messaging.
 *
 * @author Dubatovka Vadim
 */
public interface MessageService {
    /**
     * Finds message in resource bundle for given key and append it to {@link StringBuilder} object
     * for error messages.
     *
     * @param key {@link String}
     */
    void appendErrMessByKey(String key);
    
    /**
     * Finds message in resource bundle for given key and append it to {@link StringBuilder} object
     * for info messages.
     *
     * @param key {@link String}
     */
    void appendInfMessByKey(String key);
    
    /**
     * Append given message to {@link StringBuilder} object
     * for error messages.
     *
     * @param message {@link String}
     */
    void appendErrMess(String message);
    
    /**
     * Append given message to {@link StringBuilder} object
     * for info messages.
     *
     * @param message {@link String}
     */
    void appendInfMess(String message);
    
    /**
     * Returns content of {@link StringBuilder} object
     * for error messages.
     *
     * @return {@link String} content of error messages.
     */
    String getErrMessContent();
    
    /**
     * Returns content of {@link StringBuilder} object
     * for info messages.
     *
     * @return {@link String} content of info messages.
     */
    String getInfMessContent();
    
    /**
     * Checks whether content of {@link StringBuilder} object
     * for error messages is empty.
     *
     * @return true if content is empty.
     */
    boolean isErrMessEmpty();
    
    /**
     * Checks whether content of {@link StringBuilder} object
     * for info messages is empty.
     *
     * @return true if content is empty.
     */
    boolean isInfMessEmpty();
    
    /**
     * Finds message in resource bundle for given key.
     *
     * @param key {@link String}
     * @return {@link String} value for given key or {@link ConfigConstant#EMPTY_STRING} if key
     * doesn't exists
     */
    String getMessageByKey(String key);
    
    /**
     * Returns the locale of this MessageService object.
     *
     * @return {@link Locale} of this MessageService object.
     */
    Locale getLocale();
}
