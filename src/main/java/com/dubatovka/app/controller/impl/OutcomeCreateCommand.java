package com.dubatovka.app.controller.impl;

import com.dubatovka.app.controller.Command;
import com.dubatovka.app.controller.PageNavigator;
import com.dubatovka.app.entity.Event;
import com.dubatovka.app.entity.Outcome;
import com.dubatovka.app.service.MessageService;
import com.dubatovka.app.service.OutcomeService;
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
import java.util.HashSet;
import java.util.Set;

import static com.dubatovka.app.config.ConfigConstant.MESSAGE_ERR_INVALID_EVENT_OUTCOME;
import static com.dubatovka.app.config.ConfigConstant.MESSAGE_ERR_OUTCOME_UPDATE;
import static com.dubatovka.app.config.ConfigConstant.MESSAGE_INF_OUTCOME_UPDATE;
import static com.dubatovka.app.config.ConfigConstant.PARAM_EVENT_ID;
import static com.dubatovka.app.config.ConfigConstant.PARAM_OUTCOME_1;
import static com.dubatovka.app.config.ConfigConstant.PARAM_OUTCOME_2;
import static com.dubatovka.app.config.ConfigConstant.PARAM_OUTCOME_X;

/**
 * The class provides command implementation for outcome creation.
 *
 * @author Dubatovka Vadim
 */
@Controller
//TODO переименовать команду в контроллер
public class OutcomeCreateCommand implements Command {
    private final PreviousQueryService previousQueryService;
    
    @Autowired
    public OutcomeCreateCommand(PreviousQueryService previousQueryService) {
        this.previousQueryService = previousQueryService;
    }
    
    @PostMapping("/outcome_create")
    public String outcomeCreate(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        MessageService messageService = ServiceFactory.getMessageService(session);
        
        String eventIdStr = request.getParameter(PARAM_EVENT_ID);
        String outcome1Str = request.getParameter(PARAM_OUTCOME_1);
        String outcomeXStr = request.getParameter(PARAM_OUTCOME_X);
        String outcome2Str = request.getParameter(PARAM_OUTCOME_2);
        Event event = new Event();
        
        validateRequestParams(messageService, outcome1Str, outcomeXStr, outcome2Str);
        checkAndSetEventNotNull(eventIdStr, event, messageService);
        validateCommand(messageService, outcome1Str, outcomeXStr, outcome2Str);
        if (messageService.isErrMessEmpty()) {
            int eventId = event.getId();
            Outcome outcomeType1 = new Outcome(eventId, new BigDecimal(outcome1Str), Outcome.Type.TYPE_1);
            Outcome outcomeTypeX = new Outcome(eventId, new BigDecimal(outcomeXStr), Outcome.Type.TYPE_X);
            Outcome outcomeType2 = new Outcome(eventId, new BigDecimal(outcome2Str), Outcome.Type.TYPE_2);
            Set<Outcome> outcomeSet = new HashSet<>(3);
            outcomeSet.add(outcomeType1);
            outcomeSet.add(outcomeTypeX);
            outcomeSet.add(outcomeType2);
            try (OutcomeService outcomeService = ServiceFactory.getOutcomeService()) {
                outcomeService.insertOutcomeSet(outcomeSet, messageService);
            }
            if (messageService.isErrMessEmpty()) {
                messageService.appendInfMessByKey(MESSAGE_INF_OUTCOME_UPDATE);
            } else {
                messageService.appendErrMessByKey(MESSAGE_ERR_OUTCOME_UPDATE);
            }
        }
        
        setMessagesToRequest(messageService, request);
        return "redirect:" + previousQueryService.takePreviousQuery(request);
    }
    
    /**
     * Method provides process for outcome creation.<p>Takes input parameters and attributes from
     * {@link HttpServletRequest} and {@link HttpSession} and based on them create new outcomes.</p>
     *
     * @param request {@link HttpServletRequest} from client
     * @return {@link PageNavigator#FORWARD_PREV_QUERY}
     */
    @Override
    @Deprecated
    public PageNavigator execute(HttpServletRequest request) {
        HttpSession session = request.getSession();
        MessageService messageService = ServiceFactory.getMessageService(session);
        
        String eventIdStr = request.getParameter(PARAM_EVENT_ID);
        String outcome1Str = request.getParameter(PARAM_OUTCOME_1);
        String outcomeXStr = request.getParameter(PARAM_OUTCOME_X);
        String outcome2Str = request.getParameter(PARAM_OUTCOME_2);
        Event event = new Event();
        
        validateRequestParams(messageService, outcome1Str, outcomeXStr, outcome2Str);
        checkAndSetEventNotNull(eventIdStr, event, messageService);
        validateCommand(messageService, outcome1Str, outcomeXStr, outcome2Str);
        if (messageService.isErrMessEmpty()) {
            int eventId = event.getId();
            Outcome outcomeType1 = new Outcome(eventId, new BigDecimal(outcome1Str), Outcome.Type.TYPE_1);
            Outcome outcomeTypeX = new Outcome(eventId, new BigDecimal(outcomeXStr), Outcome.Type.TYPE_X);
            Outcome outcomeType2 = new Outcome(eventId, new BigDecimal(outcome2Str), Outcome.Type.TYPE_2);
            Set<Outcome> outcomeSet = new HashSet<>(3);
            outcomeSet.add(outcomeType1);
            outcomeSet.add(outcomeTypeX);
            outcomeSet.add(outcomeType2);
            try (OutcomeService outcomeService = ServiceFactory.getOutcomeService()) {
                outcomeService.insertOutcomeSet(outcomeSet, messageService);
            }
            if (messageService.isErrMessEmpty()) {
                messageService.appendInfMessByKey(MESSAGE_INF_OUTCOME_UPDATE);
            } else {
                messageService.appendErrMessByKey(MESSAGE_ERR_OUTCOME_UPDATE);
            }
        }
        
        setMessagesToRequest(messageService, request);
        return PageNavigator.FORWARD_PREV_QUERY;
    }
    
    /**
     * Method validates parameters using {@link ValidationService} to confirm that all necessary
     * parameters for command execution have proper state according to requirements for application.
     *
     * @param messageService {@link MessageService} to hold message about validation result
     * @param outcome1Str    {@link String} parameter for validation
     * @param outcomeXStr    {@link String} parameter for validation
     * @param outcome2Str    {@link String} parameter for validation
     */
    private static void validateCommand(MessageService messageService,
                                        String outcome1Str, String outcomeXStr, String outcome2Str) {
        if (messageService.isErrMessEmpty()) {
            ValidationService validationService = ServiceFactory.getValidationService();
            if (!validationService.isValidOutcomeCoeff(outcome1Str)) {
                messageService.appendErrMessByKey(MESSAGE_ERR_INVALID_EVENT_OUTCOME);
            }
            if (!validationService.isValidOutcomeCoeff(outcomeXStr)) {
                messageService.appendErrMessByKey(MESSAGE_ERR_INVALID_EVENT_OUTCOME);
            }
            if (!validationService.isValidOutcomeCoeff(outcome2Str)) {
                messageService.appendErrMessByKey(MESSAGE_ERR_INVALID_EVENT_OUTCOME);
            }
        }
    }
}
