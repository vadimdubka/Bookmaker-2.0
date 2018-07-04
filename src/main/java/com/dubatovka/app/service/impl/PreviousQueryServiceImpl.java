package com.dubatovka.app.service.impl;

import com.dubatovka.app.config.ConfigConstant;
import com.dubatovka.app.service.PreviousQueryService;
import org.springframework.stereotype.Service;

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

@Service
public class PreviousQueryServiceImpl implements PreviousQueryService {
    private static final String STUB = "********";
    
    /**
     * Saves query to {@link HttpSession} as {@link ConfigConstant#ATTR_PREV_QUERY} attribute.
     */
    @Override
    public void saveQueryToSession(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String query = buildQueryString(request);
        session.setAttribute(ATTR_PREV_QUERY, query);
    }
    
    /**
     * Takes saved to {@link HttpSession} previous query.
     */
    @Override
    public String takePreviousQuery(HttpServletRequest req) {
        String prevQuery = (String) req.getSession().getAttribute(ATTR_PREV_QUERY);
        if (prevQuery == null) {
            prevQuery = "/main"; // TODO убрать хардкод
        }
        return prevQuery;
    }
    
    /**
     * Builds query by parsing request parameters.
     */
    private String buildQueryString(HttpServletRequest request) {
        // /bookmaker/main_page
        // String requestURI = request.getRequestURI();
        // /bookmaker
        // String contextPath = request.getContextPath();
        // /main_page
        String servletPath = request.getServletPath();
        StringBuffer query = new StringBuffer();
        Enumeration<String> params = request.getParameterNames();
        while (params.hasMoreElements()) {
            String key = params.nextElement();
            String value = request.getParameter(key);
            if (key.equalsIgnoreCase(PARAM_PASSWORD) ||
                    key.equalsIgnoreCase(PARAM_PASSWORD_AGAIN) ||
                    key.equalsIgnoreCase(PARAM_PASSWORD_OLD)) {
                value = STUB;
            }
            query = query.append(PARAMETER_SEPARATOR).append(key)
                        .append(VALUE_SEPARATOR).append(value);
        }
        
        String result = servletPath;
        if (query.length() > 0) {
            query.deleteCharAt(0);
            result = result + QUERY_START_SEPARATOR + query;
        }
        return result;
    }
}
