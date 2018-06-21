package com.dubatovka.app.controller.impl.authorization;

import com.dubatovka.app.controller.Command;
import com.dubatovka.app.controller.PageNavigator;

import javax.servlet.http.HttpServletRequest;

/**
 * The class provides logout command implementation.
 *
 * @author Dubatovka Vadim
 */
public class LogoutCommand implements Command {
    /**
     * Method provides logout process for users.
     *
     * @param request {@link HttpServletRequest} to get session for invalidation
     * @return {@link PageNavigator#REDIRECT_GOTO_INDEX}
     */
    @Override
    public PageNavigator execute(HttpServletRequest request) {
        request.getSession().invalidate();
        return PageNavigator.REDIRECT_GOTO_INDEX;
    }
}
