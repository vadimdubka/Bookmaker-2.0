package com.dubatovka.app.service;

import com.dubatovka.app.config.ConfigConstant;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;

import static com.dubatovka.app.config.ConfigConstant.ATTR_PREV_QUERY;
import static com.dubatovka.app.config.ConfigConstant.PARAMETER_SEPARATOR;
import static com.dubatovka.app.config.ConfigConstant.PARAM_PASSWORD;
import static com.dubatovka.app.config.ConfigConstant.PARAM_PASSWORD_AGAIN;
import static com.dubatovka.app.config.ConfigConstant.PARAM_PASSWORD_OLD;
import static com.dubatovka.app.config.ConfigConstant.QUERY_START_SEPARATOR;
import static com.dubatovka.app.config.ConfigConstant.VALUE_SEPARATOR;

/**
 * The class provides Service layer actions for managing queries from clients.
 */
public final class QueryService {
    private static final String STUB = "********";
    
    /**
     * Private constructor to forbid create {@link QueryService} instances.
     */
    private QueryService() {
    }
    
    /**
     * Saves query to {@link HttpSession} as {@link ConfigConstant#ATTR_PREV_QUERY} attribute.
     */
    public static void saveQueryToSession(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String      query   = buildQueryString(request);
        session.setAttribute(ATTR_PREV_QUERY, query);
    }
    
    /**
     * Builds query by parsing request parameters.
     */
    private static String buildQueryString(HttpServletRequest request) {
        String              uri    = request.getRequestURI();
        StringBuffer        query  = new StringBuffer();
        Enumeration<String> params = request.getParameterNames();
        while (params.hasMoreElements()) {
            String key   = params.nextElement();
            String value = request.getParameter(key);
            if (key.equalsIgnoreCase(PARAM_PASSWORD) ||
                    key.equalsIgnoreCase(PARAM_PASSWORD_AGAIN) ||
                    key.equalsIgnoreCase(PARAM_PASSWORD_OLD)) {
                value = STUB;
            }
            query = query.append(PARAMETER_SEPARATOR).append(key)
                        .append(VALUE_SEPARATOR).append(value);
        }
        
        String result = uri;
        if (query.length() > 0) {
            query.deleteCharAt(0);
            result = result + QUERY_START_SEPARATOR + query;
        }
        return result;
    }
}