package com.dubatovka.app.controller.impl.navigation;

import com.dubatovka.app.controller.Command;
import com.dubatovka.app.controller.PageNavigator;
import com.dubatovka.app.service.QueryService;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;

/**
 * The class provides navigating to index page.
 *
 * @author Dubatovka Vadim
 */
@Deprecated
@Controller
public class GotoIndexCommand implements Command {
    
    
    
    /**
     * Method provide navigation process to index page.
     *
     * @param request {@link HttpServletRequest} from client.
     * @return {@link PageNavigator#FORWARD_PAGE_INDEX}.
     */
    
    @Override
    public PageNavigator execute(HttpServletRequest request) {
        QueryService.saveQueryToSession(request);
        return PageNavigator.FORWARD_PAGE_INDEX;
    }
}
