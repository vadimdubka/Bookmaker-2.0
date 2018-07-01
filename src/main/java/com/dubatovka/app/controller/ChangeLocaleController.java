package com.dubatovka.app.controller;

import com.dubatovka.app.service.PreviousQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
@Controller
public class ChangeLocaleController extends AbstrController{
    private final PreviousQueryService previousQueryService;
    
    @Autowired
    public ChangeLocaleController(PreviousQueryService previousQueryService) {
        this.previousQueryService = previousQueryService;
    }
    
    @GetMapping("/change_locale")
    public String showMainPage(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        String locale = request.getParameter(PARAM_LOCALE);
        if (locale == null) {
            locale = LOCALE_DEFAULT;
        }
        session.setAttribute(ATTR_LOCALE, locale);
        return "forward:" + previousQueryService.takePreviousQuery(request);
    }
}