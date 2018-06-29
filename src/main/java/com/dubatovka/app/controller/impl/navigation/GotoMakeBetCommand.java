package com.dubatovka.app.controller.impl.navigation;

import com.dubatovka.app.controller.Command;
import com.dubatovka.app.controller.PageNavigator;
import com.dubatovka.app.entity.Category;
import com.dubatovka.app.entity.Event;
import com.dubatovka.app.entity.Outcome;
import com.dubatovka.app.service.CategoryService;
import com.dubatovka.app.service.MessageService;
import com.dubatovka.app.service.PreviousQueryService;
import com.dubatovka.app.service.QueryService;
import com.dubatovka.app.service.impl.ServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static com.dubatovka.app.config.ConfigConstant.ATTR_CATEGORY;
import static com.dubatovka.app.config.ConfigConstant.ATTR_EVENT;
import static com.dubatovka.app.config.ConfigConstant.ATTR_OUTCOME;
import static com.dubatovka.app.config.ConfigConstant.ATTR_SPORT_CATEGORY;
import static com.dubatovka.app.config.ConfigConstant.PARAM_EVENT_ID;
import static com.dubatovka.app.config.ConfigConstant.PARAM_OUTCOME_TYPE;

/**
 * The class provides navigating to page for making bet.
 *
 * @author Dubatovka Vadim
 */
@Controller
public class GotoMakeBetCommand implements Command {
    
    private final PreviousQueryService previousQueryService;
    
    @Autowired
    public GotoMakeBetCommand(PreviousQueryService previousQueryService) {
        this.previousQueryService = previousQueryService;
    }
    
    @GetMapping("/make_bet_page")
    public String showMakeBetPage(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        MessageService messageService = ServiceFactory.getMessageService(session);
        
        String eventIdStr = request.getParameter(PARAM_EVENT_ID);
        String outcomeType = request.getParameter(PARAM_OUTCOME_TYPE);
        Event event = new Event();
        
        validateRequestParams(messageService, eventIdStr, outcomeType);
        checkAndSetEventNotNull(eventIdStr, event, messageService);
        String navigator = "forward:/main_page";
        if (messageService.isErrMessEmpty()) {
            try (CategoryService categoryService = ServiceFactory.getCategoryService()) {
                Outcome outcome = event.getOutcomeByType(outcomeType);
                Category category = categoryService.getCategoryById(event.getCategoryId());
                Category parentCategory = categoryService.getCategoryById(category.getParentId());
                request.setAttribute(ATTR_CATEGORY, category);
                request.setAttribute(ATTR_SPORT_CATEGORY, parentCategory);
                request.setAttribute(ATTR_EVENT, event);
                request.setAttribute(ATTR_OUTCOME, outcome);
                navigator = "make_bet";
            }
        }
    
        previousQueryService.saveQueryToSession(request);
        setMessagesToRequest(messageService, request);
        return navigator;
    }
    
    /**
     * Method provides navigation process to page for making bet.<p>Takes input parameters and
     * attributes from {@link HttpServletRequest} and {@link HttpSession} and based on them adds
     * appropriate attributes with data about event, event category and event outcome to {@link
     * HttpServletRequest}. If process passed successfully navigates to {@link
     * PageNavigator#FORWARD_PAGE_MAKE_BET}, else if some navigates to{@link
     * PageNavigator#FORWARD_GOTO_MAIN}.</p>
     *
     * @param request {@link HttpServletRequest} from client
     * @return {@link PageNavigator}
     */
    @Deprecated
    @Override
    public PageNavigator execute(HttpServletRequest request) {
        HttpSession session = request.getSession();
        MessageService messageService = ServiceFactory.getMessageService(session);
        
        String eventIdStr = request.getParameter(PARAM_EVENT_ID);
        String outcomeType = request.getParameter(PARAM_OUTCOME_TYPE);
        Event event = new Event();
        
        validateRequestParams(messageService, eventIdStr, outcomeType);
        checkAndSetEventNotNull(eventIdStr, event, messageService);
        PageNavigator navigator = PageNavigator.FORWARD_GOTO_MAIN;
        if (messageService.isErrMessEmpty()) {
            try (CategoryService categoryService = ServiceFactory.getCategoryService()) {
                Outcome outcome = event.getOutcomeByType(outcomeType);
                Category category = categoryService.getCategoryById(event.getCategoryId());
                Category parentCategory = categoryService.getCategoryById(category.getParentId());
                request.setAttribute(ATTR_CATEGORY, category);
                request.setAttribute(ATTR_SPORT_CATEGORY, parentCategory);
                request.setAttribute(ATTR_EVENT, event);
                request.setAttribute(ATTR_OUTCOME, outcome);
                navigator = PageNavigator.FORWARD_PAGE_MAKE_BET;
            }
        }
        
        QueryService.saveQueryToSession(request);
        setMessagesToRequest(messageService, request);
        return navigator;
    }
}