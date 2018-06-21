package com.dubatovka.app.controller.impl;

import com.dubatovka.app.config.ConfigConstant;
import com.dubatovka.app.controller.Command;
import com.dubatovka.app.controller.PageNavigator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static com.dubatovka.app.config.ConfigConstant.ATTR_LOCALE;
import static com.dubatovka.app.config.ConfigConstant.LOCALE_DEFAULT;
import static com.dubatovka.app.config.ConfigConstant.PARAM_LOCALE;

/**
 * The class provides command for changing locale.
 *
 * @author Dubatovka Vadim
 */
public class ChangeLocaleCommand implements Command {
    /**
     * Takes {@link ConfigConstant#PARAM_LOCALE} parameter from {@link HttpServletRequest , sets it
     * as attribute to {@link HttpSession } and navigates to {@link PageNavigator#FORWARD_PREV_QUERY}.
     *
     * @param request {@link HttpServletRequest } from client
     * @return {@link PageNavigator#FORWARD_PREV_QUERY}
     */
    @Override
    public PageNavigator execute(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String      locale  = request.getParameter(PARAM_LOCALE);
        if (locale == null) {
            locale = LOCALE_DEFAULT;
        }
        session.setAttribute(ATTR_LOCALE, locale);
        return PageNavigator.FORWARD_PREV_QUERY;
    }
}