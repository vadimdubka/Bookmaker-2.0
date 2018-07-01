package com.dubatovka.app.controller;

import com.dubatovka.app.entity.Bet;
import com.dubatovka.app.entity.Category;
import com.dubatovka.app.entity.Event;
import com.dubatovka.app.entity.Outcome;
import com.dubatovka.app.entity.Player;
import com.dubatovka.app.entity.User;
import com.dubatovka.app.service.BetService;
import com.dubatovka.app.service.CategoryService;
import com.dubatovka.app.service.EventService;
import com.dubatovka.app.service.MessageService;
import com.dubatovka.app.service.PaginationService;
import com.dubatovka.app.service.PlayerService;
import com.dubatovka.app.service.PreviousQueryService;
import com.dubatovka.app.service.ValidationService;
import com.dubatovka.app.service.impl.ServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.dubatovka.app.config.ConfigConstant.*;

/**
 * The class provides navigating to main page.
 *
 * @author Dubatovka Vadim
 */
@Controller
public class NavigationController extends AbstrController {
    
    private final PreviousQueryService previousQueryService;
    
    @Autowired
    public NavigationController(PreviousQueryService previousQueryService) {
        this.previousQueryService = previousQueryService;
    }
    
    @GetMapping("/index")
    public String gotoIndex(Model model, HttpServletRequest request) {
        previousQueryService.saveQueryToSession(request);
        return "index";
    }
    
    //TODO соединить main_page and show_actual #showMainPageWithEventShowActual
    @GetMapping("/main_page")
    public String showMainPage(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
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
        
        //TODO сделать с помощью intersepters автоматическое сохранение запросов в сессию для определенных страниц.
        previousQueryService.saveQueryToSession(request);
        setMessagesToRequest(messageService, request);
        return "main";
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
            Set<Category> sportSet = categoryService.getSportCategories();
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
        List<Event> events;
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
    public String showMainPageWithEventCorrectOutcome(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setAttribute(ATTR_EVENT_QUERY_TYPE, EVENT_QUERY_TYPE_ACTUAL);
        session.setAttribute(ATTR_EVENT_GOTO_TYPE, EVENT_GOTO_MANAGE_OUTCOME);
        return "forward:/main_page";
    }
    
    @GetMapping("/event_correct_result")
    public String showMainPageWithEventCorrectResult(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setAttribute(ATTR_EVENT_QUERY_TYPE, EVENT_QUERY_TYPE_CLOSED);
        session.setAttribute(ATTR_EVENT_GOTO_TYPE, EVENT_GOTO_MANAGE_RESULT);
        return "forward:/main_page";
    }
    
    @GetMapping("/event_manage")
    public String showMainPageWithEventManage(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setAttribute(ATTR_EVENT_QUERY_TYPE, EVENT_QUERY_TYPE_NOT_STARTED);
        session.setAttribute(ATTR_EVENT_GOTO_TYPE, EVENT_GOTO_MANAGE_EVENT);
        return "forward:/main_page";
    }
    
    @GetMapping("/event_manage_failed")
    public String showMainPageWithEventManageFailed(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setAttribute(ATTR_EVENT_QUERY_TYPE, EVENT_QUERY_TYPE_FAILED);
        session.setAttribute(ATTR_EVENT_GOTO_TYPE, EVENT_GOTO_MANAGE_FAILED);
        return "forward:/main_page";
    }
    
    @GetMapping("/event_set_outcome")
    public String showMainPageWithEventSetOutcome(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setAttribute(ATTR_EVENT_QUERY_TYPE, EVENT_QUERY_TYPE_NEW);
        session.setAttribute(ATTR_EVENT_GOTO_TYPE, EVENT_GOTO_MANAGE_OUTCOME);
        return "forward:/main_page";
    }
    
    @GetMapping("/event_set_result")
    public String showMainPageWithEventSetResult(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setAttribute(ATTR_EVENT_QUERY_TYPE, EVENT_QUERY_TYPE_STARTED);
        session.setAttribute(ATTR_EVENT_GOTO_TYPE, EVENT_GOTO_MANAGE_RESULT);
        return "forward:/main_page";
    }
    
    @GetMapping("/event_show_actual")
    public String showMainPageWithEventShowActual(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setAttribute(ATTR_EVENT_QUERY_TYPE, EVENT_QUERY_TYPE_ACTUAL);
        session.setAttribute(ATTR_EVENT_GOTO_TYPE, EVENT_GOTO_SHOW_ACTUAL);
        return "forward:/main_page";
    }
    
    @GetMapping("/event_show_result")
    public String showMainPageWithEventShowResult(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setAttribute(ATTR_EVENT_QUERY_TYPE, EVENT_QUERY_TYPE_CLOSED);
        session.setAttribute(ATTR_EVENT_GOTO_TYPE, EVENT_GOTO_SHOW_RESULT);
        return "forward:/main_page";
    }
    
    @GetMapping("/event_to_pay")
    public String showMainPageWithEventToPay(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setAttribute(ATTR_EVENT_QUERY_TYPE, EVENT_QUERY_TYPE_TO_PAY);
        session.setAttribute(ATTR_EVENT_GOTO_TYPE, EVENT_GOTO_SHOW_TO_PAY);
        return "forward:/main_page";
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
    
    @GetMapping("/manage_players_page")
    public String showManagePlayerPage(Model model, HttpServletRequest request) {
        List<Player> players;
        try (PlayerService playerService = ServiceFactory.getPlayerService()) {
            players = playerService.getAllPlayers();
        }
        request.setAttribute(ATTR_PLAYERS, players);
        previousQueryService.saveQueryToSession(request);
        return "manage_players";
    }
    
    @GetMapping("/player_state_page")
    public String showPlayerStatePage(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        MessageService messageService = ServiceFactory.getMessageService(session);
        
        Player player = (Player) session.getAttribute(PLAYER);
        String pageNumberStr = request.getParameter(PARAM_PAGE_NUMBER);
        
        validateCommand(player, messageService);
        String navigator = "forward:" + previousQueryService.takePreviousQuery(request);
        if (messageService.isErrMessEmpty()) {
            PaginationService paginationService = getPaginationService(request, player,
                                                                       pageNumberStr);
            setBetInfo(request, player, paginationService);
            setPlayerInfo(session, player);
            navigator = "player_state";
        }
        previousQueryService.saveQueryToSession(request);
        setMessagesToRequest(messageService, request);
        
        return navigator;
    }
    
    /**
     * Limit of bets on page
     */
    private static final int PAGE_LIMIT = 5;
    
    /**
     * Set attributes with information about bets to {@link
     * ServletRequest} based on received parameters.
     *
     * @param request           {@link ServletRequest}
     * @param player            {@link User}
     * @param paginationService {@link PaginationService}
     */
    private static void setBetInfo(ServletRequest request, User player,
                                   PaginationService paginationService) {
        try (BetService betService = ServiceFactory.getBetService();
             CategoryService categoryService = ServiceFactory.getCategoryService();
             EventService eventService = ServiceFactory.getEventService()) {
            int limit = paginationService.getLimitOnPage();
            int offset = paginationService.getOffset();
            List<Bet> betList = betService.getBetListForPlayer(player.getId(), limit, offset);
            Map<Bet, Event> eventMap = new HashMap<>(betList.size());
            Map<Bet, Category> categoryMap = new HashMap<>(betList.size());
            Map<Bet, Category> sportMap = new HashMap<>(betList.size());
            betList.forEach(bet -> {
                Event event = eventService.getEvent(bet.getEventId());
                Category category = categoryService.getCategoryById(event.getCategoryId());
                Category parentCategory = categoryService.getCategoryById(category.getParentId());
                eventMap.put(bet, event);
                categoryMap.put(bet, category);
                sportMap.put(bet, parentCategory);
            });
            request.setAttribute(ATTR_BET_LIST, betList);
            request.setAttribute(ATTR_EVENT_MAP, eventMap);
            request.setAttribute(ATTR_CATEGORY_MAP, categoryMap);
            request.setAttribute(ATTR_SPORT_MAP, sportMap);
        }
    }
    
    /**
     * Method builds and return {@link PaginationService}
     *
     * @param request       {@link ServletRequest}
     * @param player        {@link User}
     * @param pageNumberStr {@link String}
     * @return {@link PaginationService}
     */
    private static PaginationService getPaginationService(ServletRequest request,
                                                          User player, String pageNumberStr) {
        ValidationService validationService = ServiceFactory.getValidationService();
        int pageNumber = (validationService.isValidId(pageNumberStr)) ?
                             Integer.parseInt(pageNumberStr) : 1;
        int totalEntityAmount;
        try (BetService betService = ServiceFactory.getBetService()) {
            totalEntityAmount = betService.countBetsForPlayer(player.getId());
        }
        PaginationService paginationService = ServiceFactory.getPaginationService();
        paginationService.buildService(totalEntityAmount, PAGE_LIMIT, pageNumber);
        request.setAttribute(ATTR_PAGINATION, paginationService);
        return paginationService;
    }
    
    /**
     * Updates information about player and set attribute with player to {@link
     * HttpSession}.
     *
     * @param session {@link HttpSession}
     * @param player  {@link Player}
     */
    private static void setPlayerInfo(HttpSession session, Player player) {
        try (PlayerService playerService = ServiceFactory.getPlayerService()) {
            playerService.updatePlayerInfo(player);
        }
        session.setAttribute(ATTR_PLAYER, player);
    }
    
    /**
     * Method validates parameters using {@link ValidationService} to confirm that all necessary
     * parameters for command execution have proper state according to requirements for application.
     *
     * @param player         {@link Player} parameter for validation
     * @param messageService {@link MessageService} to hold message about result of validation
     */
    private static void validateCommand(Player player, MessageService messageService) {
        if (messageService.isErrMessEmpty()) {
            if (player == null) {
                messageService.appendErrMessByKey(MESSAGE_ERR_PLAYER_NOT_DEFINED);
            }
        }
    }
    
    @GetMapping("/register_page")
    public String showRegisterPage(Model model, HttpServletRequest request) {
        previousQueryService.saveQueryToSession(request);
        return "register";
    }
}
