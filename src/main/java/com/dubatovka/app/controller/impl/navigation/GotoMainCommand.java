package com.dubatovka.app.controller.impl.navigation;

import com.dubatovka.app.controller.Command;
import com.dubatovka.app.controller.PageNavigator;
import com.dubatovka.app.entity.Category;
import com.dubatovka.app.entity.Event;
import com.dubatovka.app.entity.Outcome;
import com.dubatovka.app.service.BetService;
import com.dubatovka.app.service.CategoryService;
import com.dubatovka.app.service.EventService;
import com.dubatovka.app.service.MessageService;
import com.dubatovka.app.service.QueryService;
import com.dubatovka.app.service.ValidationService;
import com.dubatovka.app.service.impl.ServiceFactory;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.dubatovka.app.config.ConfigConstant.ATTR_CATEGORY_ID;
import static com.dubatovka.app.config.ConfigConstant.ATTR_EVENT_COUNT_MAP;
import static com.dubatovka.app.config.ConfigConstant.ATTR_EVENT_GOTO_TYPE;
import static com.dubatovka.app.config.ConfigConstant.ATTR_EVENT_QUERY_TYPE;
import static com.dubatovka.app.config.ConfigConstant.ATTR_EVENT_SET;
import static com.dubatovka.app.config.ConfigConstant.ATTR_SPORT_SET;
import static com.dubatovka.app.config.ConfigConstant.ATTR_TYPE_1_MAP;
import static com.dubatovka.app.config.ConfigConstant.ATTR_TYPE_2_MAP;
import static com.dubatovka.app.config.ConfigConstant.ATTR_TYPE_X_MAP;
import static com.dubatovka.app.config.ConfigConstant.ATTR_WIN_BET_COUNT;
import static com.dubatovka.app.config.ConfigConstant.ATTR_WIN_BET_SUM;
import static com.dubatovka.app.config.ConfigConstant.EVENT_GOTO_MANAGE_EVENT;
import static com.dubatovka.app.config.ConfigConstant.EVENT_GOTO_MANAGE_FAILED;
import static com.dubatovka.app.config.ConfigConstant.EVENT_GOTO_MANAGE_OUTCOME;
import static com.dubatovka.app.config.ConfigConstant.EVENT_GOTO_MANAGE_RESULT;
import static com.dubatovka.app.config.ConfigConstant.EVENT_GOTO_SHOW_ACTUAL;
import static com.dubatovka.app.config.ConfigConstant.EVENT_GOTO_SHOW_RESULT;
import static com.dubatovka.app.config.ConfigConstant.EVENT_GOTO_SHOW_TO_PAY;
import static com.dubatovka.app.config.ConfigConstant.EVENT_QUERY_TYPE_ACTUAL;
import static com.dubatovka.app.config.ConfigConstant.EVENT_QUERY_TYPE_CLOSED;
import static com.dubatovka.app.config.ConfigConstant.EVENT_QUERY_TYPE_FAILED;
import static com.dubatovka.app.config.ConfigConstant.EVENT_QUERY_TYPE_NEW;
import static com.dubatovka.app.config.ConfigConstant.EVENT_QUERY_TYPE_NOT_STARTED;
import static com.dubatovka.app.config.ConfigConstant.EVENT_QUERY_TYPE_STARTED;
import static com.dubatovka.app.config.ConfigConstant.EVENT_QUERY_TYPE_TO_PAY;
import static com.dubatovka.app.config.ConfigConstant.MESSAGE_ERR_INVALID_EVENT_ID;
import static com.dubatovka.app.config.ConfigConstant.PARAM_CATEGORY_ID;
import static com.dubatovka.app.config.ConfigConstant.WIN_BET_INFO_KEY_COUNT;
import static com.dubatovka.app.config.ConfigConstant.WIN_BET_INFO_KEY_SUM;

/**
 * The class provides navigating to main page.
 *
 * @author Dubatovka Vadim
 */
public class GotoMainCommand implements Command {
    /**
     * Method provides navigation process to main page.<p>Takes input parameters and attributes from
     * {@link HttpServletRequest} and {@link HttpSession} and based on them adds appropriate
     * attributes with data about events, events categories and bets to {@link
     * HttpServletRequest}.
     *
     * @param request {@link HttpServletRequest} from client
     * @return {@link PageNavigator#FORWARD_PAGE_MAIN}
     */
    @Override
    public PageNavigator execute(HttpServletRequest request) {
        HttpSession    session        = request.getSession();
        MessageService messageService = ServiceFactory.getMessageService(session);
        
        String categoryIdStr = request.getParameter(PARAM_CATEGORY_ID);
        String eventQueryType = (String) session.getAttribute(ATTR_EVENT_QUERY_TYPE);
        String eventCommandType = (String) session.getAttribute(ATTR_EVENT_GOTO_TYPE);
        
        if ((eventQueryType == null) || (eventCommandType == null)) {
            eventQueryType = setDefaultSessionAttr(session);
        }
        setCategoryInfo(request, eventQueryType);
        if (categoryIdStr != null) {
            validateCommand(messageService, categoryIdStr);
            if (messageService.isErrMessEmpty()) {
                setEventInfo(request, categoryIdStr, eventQueryType);
                if (EVENT_GOTO_SHOW_TO_PAY.equals(eventCommandType)) {
                    setWinBetInfo(request, categoryIdStr);
                }
            }
        }
        
        QueryService.saveQueryToSession(request);
        setMessagesToRequest(messageService, request);
        return PageNavigator.FORWARD_PAGE_MAIN;
    }
    
    /**
     * Sets default session attributes for main page.
     *
     * @param session {@link HttpSession}
     * @return {@link String} default value for event query type
     */
    private static String setDefaultSessionAttr(HttpSession session) {
        session.setAttribute(ATTR_EVENT_QUERY_TYPE, EVENT_QUERY_TYPE_ACTUAL);
        session.setAttribute(ATTR_EVENT_GOTO_TYPE, EVENT_GOTO_SHOW_ACTUAL);
        return EVENT_QUERY_TYPE_ACTUAL;
    }
    
    /**
     * Set attributes with information about events categories to {@link
     * ServletRequest} based on received parameter.
     *
     * @param request        {@link ServletRequest}
     * @param eventQueryType {@link String}
     */
    private static void setCategoryInfo(ServletRequest request, String eventQueryType) {
        try (EventService eventService = ServiceFactory.getEventService();
             CategoryService categoryService = ServiceFactory.getCategoryService()) {
            Set<Category>         sportSet      = categoryService.getSportCategories();
            Map<Integer, Integer> eventCountMap = eventService.countEvents(eventQueryType);
            request.setAttribute(ATTR_SPORT_SET, sportSet);
            request.setAttribute(ATTR_EVENT_COUNT_MAP, eventCountMap);
        }
    }
    
    /**
     * Set attributes with information about events to {@link ServletRequest} based on received
     * parameters.
     *
     * @param request        {@link ServletRequest}
     * @param categoryIdStr  {@link String} representation of {@link Category} id
     * @param eventQueryType {@link String}
     */
    private static void setEventInfo(ServletRequest request, String categoryIdStr,
                                     String eventQueryType) {
        List<Event>                      events;
        Map<String, Map<String, String>> coeffColumnMaps;
        try (EventService eventService = ServiceFactory.getEventService()) {
            events = eventService.getEvents(categoryIdStr, eventQueryType);
            coeffColumnMaps = eventService.getOutcomeColumnMaps(events);
        }
        Map<String, String> type1Map = coeffColumnMaps.get(Outcome.Type.TYPE_1.getType());
        Map<String, String> typeXMap = coeffColumnMaps.get(Outcome.Type.TYPE_X.getType());
        Map<String, String> type2Map = coeffColumnMaps.get(Outcome.Type.TYPE_2.getType());
        request.setAttribute(ATTR_CATEGORY_ID, categoryIdStr);
        request.setAttribute(ATTR_EVENT_SET, events);
        request.setAttribute(ATTR_TYPE_1_MAP, type1Map);
        request.setAttribute(ATTR_TYPE_X_MAP, typeXMap);
        request.setAttribute(ATTR_TYPE_2_MAP, type2Map);
    }
    
    /**
     * Set attributes with information about winning bets to {@link ServletRequest} based on
     * received parameter.
     *
     * @param request       {@link ServletRequest}
     * @param categoryIdStr {@link String} representation of {@link Category} id.
     */
    private static void setWinBetInfo(ServletRequest request, String categoryIdStr) {
        int categoryId = Integer.parseInt(categoryIdStr);
        Map<String, Map<String, String>> winBetInfoMap;
        try (BetService betService = ServiceFactory.getBetService()) {
            winBetInfoMap = betService.getWinBetInfo(categoryId);
        }
        Map<String, String> winBetCount = winBetInfoMap.get(WIN_BET_INFO_KEY_COUNT);
        Map<String, String> winBetSum = winBetInfoMap.get(WIN_BET_INFO_KEY_SUM);
        request.setAttribute(ATTR_WIN_BET_COUNT, winBetCount);
        request.setAttribute(ATTR_WIN_BET_SUM, winBetSum);
    }
    
    /**
     * Method validates parameters using {@link ValidationService} to confirm that all necessary
     * parameters for command execution have proper state according to requirements for application.
     *
     * @param messageService {@link MessageService} to hold message about result of validation
     * @param categoryIdStr  {@link String} parameter for validation
     */
    private static void validateCommand(MessageService messageService, String categoryIdStr) {
        if (messageService.isErrMessEmpty()) {
            ValidationService validationService = ServiceFactory.getValidationService();
            if (!validationService.isValidId(categoryIdStr)) {
                messageService.appendErrMessByKey(MESSAGE_ERR_INVALID_EVENT_ID);
            }
        }
    }
    
    @GetMapping("/event_correct_outcome")
    public String showMainPageWithEventCorrectOutcome(Model model, HttpServletRequest request){
        HttpSession session = request.getSession();
        session.setAttribute(ATTR_EVENT_QUERY_TYPE, EVENT_QUERY_TYPE_ACTUAL);
        session.setAttribute(ATTR_EVENT_GOTO_TYPE, EVENT_GOTO_MANAGE_OUTCOME);
        return "main";
    }
    
    @GetMapping("/event_correct_result")
    public String showMainPageWithEventCorrectResult(Model model, HttpServletRequest request){
        HttpSession session = request.getSession();
        session.setAttribute(ATTR_EVENT_QUERY_TYPE, EVENT_QUERY_TYPE_CLOSED);
        session.setAttribute(ATTR_EVENT_GOTO_TYPE, EVENT_GOTO_MANAGE_RESULT);
        return "main";
    }
    
    @GetMapping("/event_manage")
    public String showMainPageWithEventManage(Model model, HttpServletRequest request){
        HttpSession session = request.getSession();
        session.setAttribute(ATTR_EVENT_QUERY_TYPE, EVENT_QUERY_TYPE_NOT_STARTED);
        session.setAttribute(ATTR_EVENT_GOTO_TYPE, EVENT_GOTO_MANAGE_EVENT);
        return "main";
    }
    
    @GetMapping("/event_manage_failed")
    public String showMainPageWithEventManageFailed(Model model, HttpServletRequest request){
        HttpSession session = request.getSession();
        session.setAttribute(ATTR_EVENT_QUERY_TYPE, EVENT_QUERY_TYPE_FAILED);
        session.setAttribute(ATTR_EVENT_GOTO_TYPE, EVENT_GOTO_MANAGE_FAILED);
        return "main";
    }
    
    @GetMapping("/event_set_outcome")
    public String showMainPageWithEventSetOutcome(Model model, HttpServletRequest request){
        HttpSession session = request.getSession();
        session.setAttribute(ATTR_EVENT_QUERY_TYPE, EVENT_QUERY_TYPE_NEW);
        session.setAttribute(ATTR_EVENT_GOTO_TYPE, EVENT_GOTO_MANAGE_OUTCOME);
        return "main";
    }
    
    @GetMapping("/event_set_result")
    public String showMainPageWithEventSetResult(Model model, HttpServletRequest request){
        HttpSession session = request.getSession();
        session.setAttribute(ATTR_EVENT_QUERY_TYPE, EVENT_QUERY_TYPE_STARTED);
        session.setAttribute(ATTR_EVENT_GOTO_TYPE, EVENT_GOTO_MANAGE_RESULT);
        return "main";
    }
    
    @GetMapping("/event_show_actual")
    public String showMainPageWithEventShowActual(Model model, HttpServletRequest request){
        HttpSession session = request.getSession();
        session.setAttribute(ATTR_EVENT_QUERY_TYPE, EVENT_QUERY_TYPE_ACTUAL);
        session.setAttribute(ATTR_EVENT_GOTO_TYPE, EVENT_GOTO_SHOW_ACTUAL);
        return "main";
    }
    
    @GetMapping("/event_show_result")
    public String showMainPageWithEventShowResult(Model model, HttpServletRequest request){
        HttpSession session = request.getSession();
        session.setAttribute(ATTR_EVENT_QUERY_TYPE, EVENT_QUERY_TYPE_CLOSED);
        session.setAttribute(ATTR_EVENT_GOTO_TYPE, EVENT_GOTO_SHOW_RESULT);
        return "main";
    }
    
    @GetMapping("/event_to_pay")
    public String showMainPageWithEventToPay(Model model, HttpServletRequest request){
        HttpSession session = request.getSession();
        session.setAttribute(ATTR_EVENT_QUERY_TYPE, EVENT_QUERY_TYPE_TO_PAY);
        session.setAttribute(ATTR_EVENT_GOTO_TYPE, EVENT_GOTO_SHOW_TO_PAY);
        return "main";
    }
}
