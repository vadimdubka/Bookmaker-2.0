package com.dubatovka.app.controller.impl.navigation.events;

import com.dubatovka.app.config.ConfigConstant;
import com.dubatovka.app.controller.Command;
import com.dubatovka.app.controller.PageNavigator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static com.dubatovka.app.config.ConfigConstant.ATTR_EVENT_GOTO_TYPE;
import static com.dubatovka.app.config.ConfigConstant.ATTR_EVENT_QUERY_TYPE;
import static com.dubatovka.app.config.ConfigConstant.EVENT_GOTO_SHOW_ACTUAL;
import static com.dubatovka.app.config.ConfigConstant.EVENT_QUERY_TYPE_ACTUAL;

/**
 * The class provides navigation to page with actual events.
 *
 * @author Dubatovka Vadim
 */
public class GotoEventShowActualCommand implements Command {
    /**
     * Method provide navigation process to page with actual events by adding {@link
     * ConfigConstant#ATTR_EVENT_QUERY_TYPE} and {@link ConfigConstant#ATTR_EVENT_GOTO_TYPE}
     * attributes to {@link HttpSession}.
     *
     * @param request {@link HttpServletRequest} from client for accessing {@link HttpSession}.
     * @return {@link PageNavigator#FORWARD_GOTO_MAIN}.
     */
    @Override
    public PageNavigator execute(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setAttribute(ATTR_EVENT_QUERY_TYPE, EVENT_QUERY_TYPE_ACTUAL);
        session.setAttribute(ATTR_EVENT_GOTO_TYPE, EVENT_GOTO_SHOW_ACTUAL);
        return PageNavigator.FORWARD_GOTO_MAIN;
    }
}
