package com.dubatovka.app.controller.impl;

import com.dubatovka.app.controller.Command;
import com.dubatovka.app.controller.PageNavigator;
import com.dubatovka.app.entity.Event;
import com.dubatovka.app.service.EventService;
import com.dubatovka.app.service.MessageService;
import com.dubatovka.app.service.impl.ServiceFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

import static com.dubatovka.app.config.ConfigConstant.MESSAGE_ERR_EVENT_CREATE;
import static com.dubatovka.app.config.ConfigConstant.MESSAGE_INF_EVENT_CREATE;
import static com.dubatovka.app.config.ConfigConstant.PARAM_CATEGORY_ID;
import static com.dubatovka.app.config.ConfigConstant.PARAM_DATE;
import static com.dubatovka.app.config.ConfigConstant.PARAM_PARTICIPANT_1;
import static com.dubatovka.app.config.ConfigConstant.PARAM_PARTICIPANT_2;

/**
 * The class provides command implementation for event creation.
 *
 * @author Dubatovka Vadim
 */
@Deprecated
public class EventCreateCommand implements Command {
    /**
     * Method provides process for event creation.<p>Takes input parameters and attributes from
     * {@link HttpServletRequest} and {@link HttpSession} and based on them create new event.</p>
     *
     * @param request {@link HttpServletRequest} from client
     * @return {@link PageNavigator#FORWARD_PREV_QUERY}
     */
    @Override
    @Deprecated
    public PageNavigator execute(HttpServletRequest request) {
        HttpSession    session        = request.getSession();
        MessageService messageService = ServiceFactory.getMessageService(session);
        
        String categoryIdStr = request.getParameter(PARAM_CATEGORY_ID);
        String dateTimeStr = request.getParameter(PARAM_DATE);
        String participant1 = request.getParameter(PARAM_PARTICIPANT_1);
        String participant2 = request.getParameter(PARAM_PARTICIPANT_2);
        
        validateRequestParams(messageService, categoryIdStr, dateTimeStr, participant1, participant2);
        validateEventInfo(messageService, categoryIdStr, dateTimeStr, participant1, participant2);
        if (messageService.isErrMessEmpty()) {
            int           categoryId = Integer.parseInt(categoryIdStr);
            LocalDateTime dateTime   = LocalDateTime.parse(dateTimeStr);
            Event         event      = new Event();
            event.setCategoryId(categoryId);
            event.setDate(dateTime);
            event.setParticipant1(participant1.trim());
            event.setParticipant2(participant2.trim());
            try (EventService eventService = ServiceFactory.getEventService()) {
                eventService.insertEvent(event, messageService);
            }
            if (messageService.isErrMessEmpty()) {
                messageService.appendInfMessByKey(MESSAGE_INF_EVENT_CREATE);
            } else {
                messageService.appendErrMessByKey(MESSAGE_ERR_EVENT_CREATE);
            }
        }
        setMessagesToRequest(messageService, request);
        return PageNavigator.FORWARD_PREV_QUERY;
    }
}