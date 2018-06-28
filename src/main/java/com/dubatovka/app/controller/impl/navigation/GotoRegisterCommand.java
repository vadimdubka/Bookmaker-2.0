package com.dubatovka.app.controller.impl.navigation;

import com.dubatovka.app.controller.Command;
import com.dubatovka.app.controller.PageNavigator;
import com.dubatovka.app.service.PreviousQueryService;
import com.dubatovka.app.service.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * The class provides navigating to register page.
 *
 * @author Dubatovka Vadim
 */
@Controller
public class GotoRegisterCommand implements Command {
    private final PreviousQueryService previousQueryService;
    
    @Autowired
    public GotoRegisterCommand(PreviousQueryService previousQueryService) {
        this.previousQueryService = previousQueryService;
    }
    
    @GetMapping("/register_page")
    public String showRegisterPage(Model model, HttpServletRequest request) {
        previousQueryService.saveQueryToSession(request);
        return "register";
    }
    
    /**
     * Saves current query to session and navigates to register page.
     */
    @Override
    @Deprecated
    public PageNavigator execute(HttpServletRequest request) {
        QueryService.saveQueryToSession(request);
        return PageNavigator.FORWARD_PAGE_REGISTER;
    }
}