package com.dubatovka.app.controller.impl.navigation;

import com.dubatovka.app.controller.Command;
import com.dubatovka.app.controller.PageNavigator;
import com.dubatovka.app.entity.Bet;
import com.dubatovka.app.entity.Category;
import com.dubatovka.app.entity.Event;
import com.dubatovka.app.entity.Player;
import com.dubatovka.app.entity.User;
import com.dubatovka.app.service.BetService;
import com.dubatovka.app.service.CategoryService;
import com.dubatovka.app.service.EventService;
import com.dubatovka.app.service.MessageService;
import com.dubatovka.app.service.PaginationService;
import com.dubatovka.app.service.PlayerService;
import com.dubatovka.app.service.QueryService;
import com.dubatovka.app.service.ValidationService;
import com.dubatovka.app.service.impl.ServiceFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.dubatovka.app.config.ConfigConstant.ATTR_BET_LIST;
import static com.dubatovka.app.config.ConfigConstant.ATTR_CATEGORY_MAP;
import static com.dubatovka.app.config.ConfigConstant.ATTR_EVENT_MAP;
import static com.dubatovka.app.config.ConfigConstant.ATTR_PAGINATION;
import static com.dubatovka.app.config.ConfigConstant.ATTR_PLAYER;
import static com.dubatovka.app.config.ConfigConstant.ATTR_SPORT_MAP;
import static com.dubatovka.app.config.ConfigConstant.MESSAGE_ERR_PLAYER_NOT_DEFINED;
import static com.dubatovka.app.config.ConfigConstant.PARAM_PAGE_NUMBER;
import static com.dubatovka.app.config.ConfigConstant.PLAYER;

/**
 * The class provides navigating to player state page.
 *
 * @author Dubatovka Vadim
 */
@Controller
public class GotoPlayerStateCommand implements Command {
    @GetMapping("/player_state_page")
    public String showPlayerStatePage(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        MessageService messageService = ServiceFactory.getMessageService(session);
        
        Player player = (Player) session.getAttribute(PLAYER);
        String pageNumberStr = request.getParameter(PARAM_PAGE_NUMBER);
        
        validateCommand(player, messageService);
        String navigator = "forward:/main_page";//TODO сделать на FORWARD_PREV_QUERY
//        PageNavigator navigator = PageNavigator.FORWARD_PREV_QUERY;
        if (messageService.isErrMessEmpty()) {
            PaginationService paginationService = getPaginationService(request, player,
                                                                       pageNumberStr);
            setBetInfo(request, player, paginationService);
            setPlayerInfo(session, player);
            navigator = "player_state";
        }
        QueryService.saveQueryToSession(request);
        setMessagesToRequest(messageService, request);
        
        return navigator;
    }
    
    /**
     * Limit of bets on page
     */
    private static final int PAGE_LIMIT = 5;
    
    /**
     * Method provides navigation process to player state page.<p>Takes input parameters and
     * attributes from {@link HttpServletRequest} and {@link HttpSession} and based on them adds
     * appropriate attributes with data about player and player's bets to {@link
     * HttpServletRequest}.If process passed successfully navigates to {@link
     * PageNavigator#FORWARD_PREV_QUERY}, else navigates to {@link PageNavigator#FORWARD_PAGE_REGISTER}</p>
     *
     * @param request {@link HttpServletRequest} from client
     * @return {@link PageNavigator}
     */
    @Override
    @Deprecated
    public PageNavigator execute(HttpServletRequest request) {
        HttpSession session = request.getSession();
        MessageService messageService = ServiceFactory.getMessageService(session);
        
        Player player = (Player) session.getAttribute(PLAYER);
        String pageNumberStr = request.getParameter(PARAM_PAGE_NUMBER);
        
        validateCommand(player, messageService);
        PageNavigator navigator = PageNavigator.FORWARD_PREV_QUERY;
        if (messageService.isErrMessEmpty()) {
            PaginationService paginationService = getPaginationService(request, player,
                                                                       pageNumberStr);
            setBetInfo(request, player, paginationService);
            setPlayerInfo(session, player);
            navigator = PageNavigator.FORWARD_PAGE_PLAYER_STATE;
        }
        QueryService.saveQueryToSession(request);
        setMessagesToRequest(messageService, request);
        return navigator;
    }
    
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
}
