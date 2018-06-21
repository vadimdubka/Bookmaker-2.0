package com.dubatovka.app.controller;

import com.dubatovka.app.entity.Event;
import com.dubatovka.app.service.EventService;
import com.dubatovka.app.service.MessageService;
import com.dubatovka.app.service.ValidationService;
import com.dubatovka.app.service.impl.ServiceFactory;

import javax.servlet.http.HttpServletRequest;

import static com.dubatovka.app.config.ConfigConstant.ATTR_ERROR_MESSAGE;
import static com.dubatovka.app.config.ConfigConstant.ATTR_INFO_MESSAGE;
import static com.dubatovka.app.config.ConfigConstant.MESSAGE_ERR_INVALID_DATE;
import static com.dubatovka.app.config.ConfigConstant.MESSAGE_ERR_INVALID_EVENT_ID;
import static com.dubatovka.app.config.ConfigConstant.MESSAGE_ERR_INVALID_PARTICIPANT;
import static com.dubatovka.app.config.ConfigConstant.MESSAGE_ERR_INVALID_REQUEST_PARAMETER;

/**
 * Interface for commands, which are executed by {@link FrontControllerServlet}.
 *
 * @author Dubatovka Vadim
 */
@FunctionalInterface
public interface Command {
    /**
     * Method executes manipulations with data received from request and returns appropriate {@link
     * PageNavigator} instance.
     *
     * @param request {@link HttpServletRequest} from client with parameters for processing
     * @return {@link PageNavigator} with response parameters (contains 'query' and 'response type'
     * data for {@link FrontControllerServlet})
     */
    PageNavigator execute(HttpServletRequest request);
    
    /**
     * Method validates request parameters from {@link HttpServletRequest#getParameter(String)}
     * using {@link ValidationService} to confirm that all necessary parameters for command
     * execution are not null and not empty.
     *
     * @param messageService {@link MessageService} to hold message about validation result.
     * @param params         {@link String} array of request parameters.
     */
    default void validateRequestParams(MessageService messageService, String... params) {
        ValidationService validationService = ServiceFactory.getValidationService();
        if (!validationService.isValidRequestParam(params)) {
            messageService.appendErrMessByKey(MESSAGE_ERR_INVALID_REQUEST_PARAMETER);
        }
    }
    
    /**
     * Method checks that event with given id exists in database and, if yes, sets retrieved event
     * properties to the given event that will be used in the {@link Command#execute} method.
     *
     * @param eventIdStr     {@link String} representation of event id number.
     * @param event          {@link Event} to set properties of retrieved event from database.
     * @param messageService {@link MessageService} to hold message about result of validation.
     */
    default void checkAndSetEventNotNull(String eventIdStr, Event event,
                                         MessageService messageService) {
        if (messageService.isErrMessEmpty()) {
            try (EventService eventService = ServiceFactory.getEventService()) {
                Event eventDB = eventService.getEvent(eventIdStr);
                if (eventDB != null) {
                    event.updateFrom(eventDB);
                } else {
                    messageService.appendErrMessByKey(MESSAGE_ERR_INVALID_EVENT_ID);
                }
            }
        }
    }
    
    /**
     * Method validates parameters using {@link ValidationService} to confirm that all necessary
     * parameters for command execution have proper state according to requirements for application.
     *
     * @param messageService {@link MessageService} to hold message about validation result
     * @param eventIdStr     {@link String} parameter for validation
     * @param dateTimeStr    {@link String} parameter for validation
     * @param participant1   {@link String} parameter for validation
     * @param participant2   {@link String} parameter for validation
     */
    default void validateEventInfo(MessageService messageService,
                                   String eventIdStr, String dateTimeStr,
                                   String participant1, String participant2) {
        if (messageService.isErrMessEmpty()) {
            ValidationService validationService = ServiceFactory.getValidationService();
            if (!validationService.isValidId(eventIdStr)) {
                messageService.appendErrMessByKey(MESSAGE_ERR_INVALID_EVENT_ID);
            }
            if (!validationService.isValidEventDateTime(dateTimeStr)) {
                messageService.appendErrMessByKey(MESSAGE_ERR_INVALID_DATE);
            }
            if (!validationService.isValidEventParticipantName(participant1) ||
                    !validationService.isValidEventParticipantName(participant2)) {
                messageService.appendErrMessByKey(MESSAGE_ERR_INVALID_PARTICIPANT);
            }
        }
    }
    
    /**
     * Method set messages to the {@link HttpServletRequest} request to show users notifications
     * with results of their requests to the server.
     *
     * @param messageService {@link MessageService} that holds messages, formed during execution of
     *                       {@link Command#execute} method .
     * @param request        {@link HttpServletRequest}
     */
    default void setMessagesToRequest(MessageService messageService, HttpServletRequest request) {
        String error = messageService.getErrMessContent();
        String info = messageService.getInfMessContent();
        if (!error.isEmpty()) {
            request.setAttribute(ATTR_ERROR_MESSAGE, error);
        }
        if (!info.isEmpty()) {
            request.setAttribute(ATTR_INFO_MESSAGE, info);
        }
    }
}