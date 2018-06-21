package com.dubatovka.app.service.impl;

import com.dubatovka.app.config.ConfigConstant;
import com.dubatovka.app.service.MessageService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpSession;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import static com.dubatovka.app.config.ConfigConstant.ATTR_LOCALE;
import static com.dubatovka.app.config.ConfigConstant.COUNTRY_RU;
import static com.dubatovka.app.config.ConfigConstant.COUNTRY_US;
import static com.dubatovka.app.config.ConfigConstant.EMPTY_STRING;
import static com.dubatovka.app.config.ConfigConstant.EN_US;
import static com.dubatovka.app.config.ConfigConstant.LANG_EN;
import static com.dubatovka.app.config.ConfigConstant.LANG_RU;
import static com.dubatovka.app.config.ConfigConstant.MESSAGE_SEPARATOR;
import static com.dubatovka.app.config.ConfigConstant.PATH_TO_MESSAGES_BUNDLE;
import static com.dubatovka.app.config.ConfigConstant.RU_RU;

/**
 * Implementation of Service layer class for messaging actions.
 *
 * @author Dubatovka Vadim
 */
class MessageServiceImpl implements MessageService {
    private static final Logger logger       = LogManager.getLogger(MessageServiceImpl.class);
    private static final Locale LOCALE_EN_US = new Locale(LANG_EN, COUNTRY_US);
    private static final Locale LOCALE_RU_RU = new Locale(LANG_RU, COUNTRY_RU);
    
    private final ResourceBundle bundle;
    private final StringBuilder errMess = new StringBuilder();
    private final StringBuilder infMess = new StringBuilder();
    
    /**
     * Default constructor.
     */
    MessageServiceImpl() {
        bundle = ResourceBundle.getBundle(PATH_TO_MESSAGES_BUNDLE, LOCALE_RU_RU);
    }
    
    /**
     * Constructs instance using definite locale.
     */
    MessageServiceImpl(String localeStr) {
        Locale locale;
        switch (localeStr) {
            case EN_US:
                locale = LOCALE_EN_US;
                break;
            case RU_RU:
                locale = LOCALE_RU_RU;
                break;
            default:
                locale = LOCALE_RU_RU;
        }
        bundle = ResourceBundle.getBundle(PATH_TO_MESSAGES_BUNDLE, locale);
    }
    
    /**
     * Constructs instance using definite locale and resource bundle.
     */
    MessageServiceImpl(Locale locale, String pathToBundle) {
        bundle = ResourceBundle.getBundle(pathToBundle, locale);
    }
    
    /**
     * Constructs instance using {@link HttpSession} object to define locale from client.
     */
    MessageServiceImpl(HttpSession session) {
        this((String) session.getAttribute(ATTR_LOCALE));
    }
    
    /**
     * Finds message in resource bundle for given key and append it to {@link StringBuilder} object
     * for error messages.
     *
     * @param key {@link String}
     */
    @Override
    public void appendErrMessByKey(String key) {
        errMess.append(getMessageByKey(key)).append(MESSAGE_SEPARATOR);
    }
    
    /**
     * Finds message in resource bundle for given key and append it to {@link StringBuilder} object
     * for info messages.
     *
     * @param key {@link String}
     */
    @Override
    public void appendInfMessByKey(String key) {
        infMess.append(getMessageByKey(key)).append(MESSAGE_SEPARATOR);
    }
    
    /**
     * Append given message to {@link StringBuilder} object
     * for error messages.
     *
     * @param message {@link String}
     */
    @Override
    public void appendErrMess(String message) {
        errMess.append(message).append(MESSAGE_SEPARATOR);
    }
    
    /**
     * Append given message to {@link StringBuilder} object
     * for info messages.
     *
     * @param message {@link String}
     */
    @Override
    public void appendInfMess(String message) {
        infMess.append(message).append(MESSAGE_SEPARATOR);
    }
    
    /**
     * Returns content of {@link StringBuilder} object
     * for error messages.
     *
     * @return {@link String} content of error messages.
     */
    @Override
    public String getErrMessContent() {
        return errMess.toString().trim();
    }
    
    /**
     * Returns content of {@link StringBuilder} object
     * for info messages.
     *
     * @return {@link String} content of info messages.
     */
    @Override
    public String getInfMessContent() {
        return infMess.toString().trim();
    }
    
    /**
     * Checks whether content of {@link StringBuilder} object
     * for error messages is empty.
     *
     * @return true if content is empty.
     */
    @Override
    public boolean isErrMessEmpty() {
        return errMess.toString().trim().isEmpty();
    }
    
    /**
     * Checks whether content of {@link StringBuilder} object
     * for info messages is empty.
     *
     * @return true if content is empty.
     */
    @Override
    public boolean isInfMessEmpty() {
        return infMess.toString().trim().isEmpty();
    }
    
    /**
     * Finds message in resource bundle for given key.
     *
     * @param key {@link String}
     * @return {@link String} value for given key or {@link ConfigConstant#EMPTY_STRING} if key
     * doesn't exists
     */
    @Override
    public String getMessageByKey(String key) {
        String property;
        try {
            property = bundle.getString(key);
        } catch (MissingResourceException e) {
            logger.log(Level.ERROR, e);
            property = EMPTY_STRING;
        }
        return property;
    }
    
    /**
     * Returns the locale of this MessageService object.
     *
     * @return {@link Locale} of this MessageService object.
     */
    @Override
    public Locale getLocale() {
        return bundle.getLocale();
    }
}
