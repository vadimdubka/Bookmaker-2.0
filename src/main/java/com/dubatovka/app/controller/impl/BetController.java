package com.dubatovka.app.controller.impl;

import com.dubatovka.app.controller.Command;
import com.dubatovka.app.controller.PageNavigator;
import com.dubatovka.app.entity.Bet;
import com.dubatovka.app.entity.Event;
import com.dubatovka.app.entity.Player;
import com.dubatovka.app.entity.PlayerStatus;
import com.dubatovka.app.entity.User;
import com.dubatovka.app.service.BetService;
import com.dubatovka.app.service.MessageService;
import com.dubatovka.app.service.PlayerService;
import com.dubatovka.app.service.PreviousQueryService;
import com.dubatovka.app.service.ValidationService;
import com.dubatovka.app.service.impl.ServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.dubatovka.app.config.ConfigConstant.ATTR_PLAYER;
import static com.dubatovka.app.config.ConfigConstant.ATTR_ROLE;
import static com.dubatovka.app.config.ConfigConstant.MESSAGE_ERR_BETTING_INTERRUPTED;
import static com.dubatovka.app.config.ConfigConstant.MESSAGE_ERR_BET_AMOUNT_LESS_BALANCE;
import static com.dubatovka.app.config.ConfigConstant.MESSAGE_ERR_BET_AMOUNT_LESS_BET_LIMIT;
import static com.dubatovka.app.config.ConfigConstant.MESSAGE_ERR_BET_FOR_EMPLOYEE;
import static com.dubatovka.app.config.ConfigConstant.MESSAGE_ERR_BET_GOTO_REGISTRATION;
import static com.dubatovka.app.config.ConfigConstant.MESSAGE_ERR_BET_TIME;
import static com.dubatovka.app.config.ConfigConstant.MESSAGE_ERR_INVALID_BET_AMOUNT;
import static com.dubatovka.app.config.ConfigConstant.MESSAGE_ERR_INVALID_EVENT_ID;
import static com.dubatovka.app.config.ConfigConstant.MESSAGE_ERR_OUTCOME_COEFF_CHANGE;
import static com.dubatovka.app.config.ConfigConstant.MESSAGE_ERR_PAY_WIN_BET;
import static com.dubatovka.app.config.ConfigConstant.MESSAGE_ERR_PLAYER_STATUS_BAN;
import static com.dubatovka.app.config.ConfigConstant.MESSAGE_INF_BET_IS_DONE;
import static com.dubatovka.app.config.ConfigConstant.MESSAGE_INF_PAY_WIN_BET;
import static com.dubatovka.app.config.ConfigConstant.PARAM_BET_AMOUNT;
import static com.dubatovka.app.config.ConfigConstant.PARAM_EVENT_ID;
import static com.dubatovka.app.config.ConfigConstant.PARAM_OUTCOME_COEFFICIENT;
import static com.dubatovka.app.config.ConfigConstant.PARAM_OUTCOME_TYPE;
import static com.dubatovka.app.config.ConfigConstant.PLAYER;

@Controller
public class BetController implements Command {
    
    private final PreviousQueryService previousQueryService;
    
    @Autowired
    public BetController(PreviousQueryService previousQueryService) {
        this.previousQueryService = previousQueryService;
    }
    
    @PostMapping("/make_bet")
    public String makeBet(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        MessageService messageService = ServiceFactory.getMessageService(session);
        
        Player player = (Player) session.getAttribute(PLAYER);
        User.UserRole role = (User.UserRole) session.getAttribute(ATTR_ROLE);
        String betAmountStr = request.getParameter(PARAM_BET_AMOUNT);
        String eventIdStr = request.getParameter(PARAM_EVENT_ID);
        String outcomeType = request.getParameter(PARAM_OUTCOME_TYPE);
        String outcomeCoeffOnPage = request.getParameter(PARAM_OUTCOME_COEFFICIENT);
        Event event = new Event();
        
        validateRequestParams(messageService, betAmountStr, eventIdStr, outcomeType, outcomeCoeffOnPage);
        checkAndSetEventNotNull(eventIdStr, event, messageService);
        validateUserRole(role, messageService);
        validateCommand(player, betAmountStr, event, outcomeType, outcomeCoeffOnPage, messageService);
        String navigator = "redirect:" + previousQueryService.takePreviousQuery(request);
        if (messageService.isErrMessEmpty()) {
            try (PlayerService playerService = ServiceFactory.getPlayerService();
                 BetService betService = ServiceFactory.getBetService()) {
                BigDecimal coefficient = event.getOutcomeByType(outcomeType).getCoefficient();
                BigDecimal betAmount = new BigDecimal(betAmountStr);
                Bet bet = new Bet(player.getId(), event.getId(), outcomeType,
                                  LocalDateTime.now(), coefficient, betAmount, Bet.Status.NEW);
                betService.makeBet(bet, messageService);
                if (messageService.isErrMessEmpty()) {
                    playerService.updatePlayerInfo(player);
                    session.setAttribute(ATTR_PLAYER, player);
                    navigator = "redirect:/main_page";
                    messageService.appendInfMessByKey(MESSAGE_INF_BET_IS_DONE);
                } else {
                    messageService.appendErrMessByKey(MESSAGE_ERR_BETTING_INTERRUPTED);
                }
            }
        }
        setMessagesToRequest(messageService, request);
        return navigator;
    }
    
    @PostMapping("/pay_win_bet")
    public String payWinBet(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        MessageService messageService = ServiceFactory.getMessageService(session);
        
        String eventIdStr = request.getParameter(PARAM_EVENT_ID);
        validateRequestParams(messageService, eventIdStr);
        validateEventId(messageService, eventIdStr);
        if (messageService.isErrMessEmpty()) {
            int eventId = Integer.parseInt(eventIdStr);
            try (BetService betService = ServiceFactory.getBetService()) {
                betService.payWinBet(eventId, messageService);
            }
            if (messageService.isErrMessEmpty()) {
                messageService.appendInfMessByKey(MESSAGE_INF_PAY_WIN_BET);
            } else {
                messageService.appendErrMessByKey(MESSAGE_ERR_PAY_WIN_BET);
            }
        }
        
        setMessagesToRequest(messageService, request);
        return "redirect:" + previousQueryService.takePreviousQuery(request);
    }
    
    
    /**
     * Method validates {@link User.UserRole}.
     *
     * @param role           {@link User.UserRole}
     * @param messageService {@link MessageService} to hold message about validation result
     */
    private static void validateUserRole(User.UserRole role, MessageService messageService) {
        if (messageService.isErrMessEmpty()) {
            if (role == User.UserRole.GUEST) {
                messageService.appendErrMessByKey(MESSAGE_ERR_BET_GOTO_REGISTRATION);
            } else if ((role == User.UserRole.ADMIN) || (role == User.UserRole.ANALYST)) {
                messageService.appendErrMessByKey(MESSAGE_ERR_BET_FOR_EMPLOYEE);
            }
        }
    }
    
    /**
     * Method validates parameters using {@link ValidationService} to confirm that all necessary
     * parameters for command execution have proper state according to requirements for application.
     *
     * @param player             {@link Player} parameter for validation
     * @param betAmountStr       {@link String} parameter for validation
     * @param event              {@link Event} parameter for validation
     * @param outcomeType        {@link String} parameter for validation
     * @param outcomeCoeffOnPage {@link String} parameter for validation
     * @param messageService     {@link MessageService} to hold message about validation result
     */
    private static void validateCommand(Player player, String betAmountStr, Event event,
                                        String outcomeType, String outcomeCoeffOnPage,
                                        MessageService messageService) {
        if (messageService.isErrMessEmpty()) {
            LocalDateTime betDateTime = LocalDateTime.now();
            ValidationService validationService = ServiceFactory.getValidationService();
            if (!validationService.isValidBetTime(betDateTime, event.getDate())) {
                messageService.appendErrMessByKey(MESSAGE_ERR_BET_TIME);
            }
            if (!validationService.isValidOutcomeCoeffOnPage(outcomeCoeffOnPage, event, outcomeType)) {
                messageService.appendErrMessByKey(MESSAGE_ERR_OUTCOME_COEFF_CHANGE);
            }
            
            if (player.getAccount().getStatus().getStatus() == PlayerStatus.Status.BAN) {
                messageService.appendErrMessByKey(MESSAGE_ERR_PLAYER_STATUS_BAN);
            }
            if (validationService.isValidBetAmount(betAmountStr)) {
                BigDecimal betAmount = new BigDecimal(betAmountStr);
                BigDecimal balance = player.getAccount().getBalance();
                BigDecimal betLimit = player.getAccount().getStatus().getBetLimit();
                if (betAmount.compareTo(balance) > 0) {
                    messageService.appendErrMessByKey(MESSAGE_ERR_BET_AMOUNT_LESS_BALANCE);
                }
                if (betAmount.compareTo(betLimit) >= 0) {
                    messageService.appendErrMessByKey(MESSAGE_ERR_BET_AMOUNT_LESS_BET_LIMIT);
                }
            } else {
                messageService.appendErrMessByKey(MESSAGE_ERR_INVALID_BET_AMOUNT);
            }
        }
    }
    
    /**
     * Method validates parameters using {@link ValidationService} to confirm that all necessary
     * parameters for command execution have proper state according to requirements for application.
     *
     * @param messageService {@link MessageService} to hold message about validation result
     * @param eventIdStr     {@link String} parameter for validation
     */
    private static void validateEventId(MessageService messageService, String eventIdStr) {
        if (messageService.isErrMessEmpty()) {
            ValidationService validationService = ServiceFactory.getValidationService();
            if (!validationService.isValidId(eventIdStr)) {
                messageService.appendErrMessByKey(MESSAGE_ERR_INVALID_EVENT_ID);
            }
        }
    }
    
    @Override
    public PageNavigator execute(HttpServletRequest request) {
        return null;
    }
}
