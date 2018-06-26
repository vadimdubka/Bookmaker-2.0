package com.dubatovka.app.controller.impl;

import com.dubatovka.app.controller.Command;
import com.dubatovka.app.controller.PageNavigator;
import com.dubatovka.app.service.EventService;
import com.dubatovka.app.service.MessageService;
import com.dubatovka.app.service.ValidationService;
import com.dubatovka.app.service.impl.ServiceFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static com.dubatovka.app.config.ConfigConstant.MESSAGE_ERR_EVENT_DELETE;
import static com.dubatovka.app.config.ConfigConstant.MESSAGE_ERR_INVALID_EVENT_ID;
import static com.dubatovka.app.config.ConfigConstant.MESSAGE_INF_EVENT_DELETE;
import static com.dubatovka.app.config.ConfigConstant.PARAM_EVENT_ID;

/**
 * The class provides command implementation for event deletion.
 *
 * @author Dubatovka Vadim
 */
@Deprecated
public class EventDeleteCommand implements Command {
    /**
     * Method provides process for event deletion.<p>Takes input parameters and attributes from
     * {@link HttpServletRequest} and {@link HttpSession} and based on them delete event.</p>
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
        
        validateRequestParams(messageService, eventIdStr);
        validateCommand(messageService, eventIdStr);
        if (messageService.isErrMessEmpty()) {
            int eventId = Integer.parseInt(eventIdStr);
            try (EventService eventService = ServiceFactory.getEventService()) {
                eventService.deleteEvent(eventId, messageService);
            }
            if (messageService.isErrMessEmpty()) {
                messageService.appendInfMessByKey(MESSAGE_INF_EVENT_DELETE);
            } else {
                messageService.appendErrMessByKey(MESSAGE_ERR_EVENT_DELETE);
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
     * @param eventIdStr     {@link String} parameter for validation
     */
    private static void validateCommand(MessageService messageService, String eventIdStr) {
        if (messageService.isErrMessEmpty()) {
            ValidationService validationService = ServiceFactory.getValidationService();
            if (!validationService.isValidId(eventIdStr)) {
                messageService.appendErrMessByKey(MESSAGE_ERR_INVALID_EVENT_ID);
            }
        }
    }
}
