package com.dubatovka.app.controller;

import com.dubatovka.app.entity.Event;
import com.dubatovka.app.service.EventService;
import com.dubatovka.app.service.MessageService;
import com.dubatovka.app.service.PreviousQueryService;
import com.dubatovka.app.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

import static com.dubatovka.app.config.ConfigConstant.MESSAGE_ERR_EVENT_CREATE;
import static com.dubatovka.app.config.ConfigConstant.MESSAGE_ERR_EVENT_DELETE;
import static com.dubatovka.app.config.ConfigConstant.MESSAGE_ERR_EVENT_UPDATE_INFO;
import static com.dubatovka.app.config.ConfigConstant.MESSAGE_ERR_INVALID_EVENT_ID;
import static com.dubatovka.app.config.ConfigConstant.MESSAGE_ERR_INVALID_EVENT_RESULT;
import static com.dubatovka.app.config.ConfigConstant.MESSAGE_INF_EVENT_CREATE;
import static com.dubatovka.app.config.ConfigConstant.MESSAGE_INF_EVENT_DELETE;
import static com.dubatovka.app.config.ConfigConstant.MESSAGE_INF_EVENT_UPDATE_INFO;
import static com.dubatovka.app.config.ConfigConstant.PARAM_CATEGORY_ID;
import static com.dubatovka.app.config.ConfigConstant.PARAM_DATE;
import static com.dubatovka.app.config.ConfigConstant.PARAM_EVENT_ID;
import static com.dubatovka.app.config.ConfigConstant.PARAM_PARTICIPANT_1;
import static com.dubatovka.app.config.ConfigConstant.PARAM_PARTICIPANT_2;
import static com.dubatovka.app.config.ConfigConstant.PARAM_RESULT_1;
import static com.dubatovka.app.config.ConfigConstant.PARAM_RESULT_2;

@Controller
public class EventController extends AbstrController {
    
    private final PreviousQueryService previousQueryService;
    
    @Autowired
    public EventController(PreviousQueryService previousQueryService) {
        this.previousQueryService = previousQueryService;
    }
    
    @PostMapping("/event_create")
    public String eventCreate(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        MessageService messageService = serviceFactory.getMessageService(session);
        
        String categoryIdStr = request.getParameter(PARAM_CATEGORY_ID);
        String dateTimeStr = request.getParameter(PARAM_DATE);
        String participant1 = request.getParameter(PARAM_PARTICIPANT_1);
        String participant2 = request.getParameter(PARAM_PARTICIPANT_2);
        
        validateRequestParams(messageService, categoryIdStr, dateTimeStr, participant1, participant2);
        validateEventInfo(messageService, categoryIdStr, dateTimeStr, participant1, participant2);
        if (messageService.isErrMessEmpty()) {
            int categoryId = Integer.parseInt(categoryIdStr);
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr);
            Event event = new Event();
            event.setCategoryId(categoryId);
            event.setDate(dateTime);
            event.setParticipant1(participant1.trim());
            event.setParticipant2(participant2.trim());
            try (EventService eventService = serviceFactory.getEventService()) {
                eventService.insertEvent(event, messageService);
            }
            if (messageService.isErrMessEmpty()) {
                messageService.appendInfMessByKey(MESSAGE_INF_EVENT_CREATE);
            } else {
                messageService.appendErrMessByKey(MESSAGE_ERR_EVENT_CREATE);
            }
        }
        setMessagesToRequest(messageService, request);
        return "redirect:" + previousQueryService.takePreviousQuery(request);
    }
    
    @PostMapping("/event_delete")
    public String eventDelete(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        MessageService messageService = serviceFactory.getMessageService(session);
        
        String eventIdStr = request.getParameter(PARAM_EVENT_ID);
        
        validateRequestParams(messageService, eventIdStr);
        validateCommand(messageService, eventIdStr);
        if (messageService.isErrMessEmpty()) {
            int eventId = Integer.parseInt(eventIdStr);
            try (EventService eventService = serviceFactory.getEventService()) {
                eventService.deleteEvent(eventId, messageService);
            }
            if (messageService.isErrMessEmpty()) {
                messageService.appendInfMessByKey(MESSAGE_INF_EVENT_DELETE);
            } else {
                messageService.appendErrMessByKey(MESSAGE_ERR_EVENT_DELETE);
            }
        }
        
        setMessagesToRequest(messageService, request);
        return "redirect:" + previousQueryService.takePreviousQuery(request);
    }
    
    @PostMapping("/event_info_update")
    public String eventInfoUpdate(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        MessageService messageService = serviceFactory.getMessageService(session);
        
        String eventIdStr = request.getParameter(PARAM_EVENT_ID);
        String dateTimeStr = request.getParameter(PARAM_DATE);
        String participant1 = request.getParameter(PARAM_PARTICIPANT_1);
        String participant2 = request.getParameter(PARAM_PARTICIPANT_2);
        Event event = new Event();
        
        validateRequestParams(messageService, eventIdStr, dateTimeStr, participant1, participant2);
        checkAndSetEventNotNull(eventIdStr, event, messageService);
        validateEventInfo(messageService, eventIdStr, dateTimeStr, participant1, participant2);
        if (messageService.isErrMessEmpty()) {
            event.setDate(LocalDateTime.parse(dateTimeStr));
            event.setParticipant1(participant1.trim());
            event.setParticipant2(participant2.trim());
            try (EventService eventService = serviceFactory.getEventService()) {
                eventService.updateEventInfo(event, messageService);
            }
            if (messageService.isErrMessEmpty()) {
                messageService.appendInfMessByKey(MESSAGE_INF_EVENT_UPDATE_INFO);
            } else {
                messageService.appendErrMessByKey(MESSAGE_ERR_EVENT_UPDATE_INFO);
            }
        }
        setMessagesToRequest(messageService, request);
        return "redirect:" + previousQueryService.takePreviousQuery(request);
    }
    
    @PostMapping("/event_result_update")
    public String eventResultUpdate(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        MessageService messageService = serviceFactory.getMessageService(session);
        
        String eventIdStr = request.getParameter(PARAM_EVENT_ID);
        String result1Str = request.getParameter(PARAM_RESULT_1);
        String result2Str = request.getParameter(PARAM_RESULT_2);
        Event event = new Event();
        
        validateRequestParams(messageService, result1Str, result2Str);
        checkAndSetEventNotNull(eventIdStr, event, messageService);
        validateCommand(messageService, result1Str, result2Str);
        if (messageService.isErrMessEmpty()) {
            event.setResult1(result1Str.trim());
            event.setResult2(result2Str.trim());
            try (EventService eventService = serviceFactory.getEventService()) {
                eventService.updateEventResult(event, messageService);
            }
            if (messageService.isErrMessEmpty()) {
                messageService.appendInfMessByKey(MESSAGE_INF_EVENT_UPDATE_INFO);
            } else {
                messageService.appendErrMessByKey(MESSAGE_ERR_EVENT_UPDATE_INFO);
            }
        }
        setMessagesToRequest(messageService, request);
        return "redirect:" + previousQueryService.takePreviousQuery(request);
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
            ValidationService validationService = serviceFactory.getValidationService();
            if (!validationService.isValidId(eventIdStr)) {
                messageService.appendErrMessByKey(MESSAGE_ERR_INVALID_EVENT_ID);
            }
        }
    }
    
    /**
     * Method validates parameters using {@link ValidationService} to confirm that all necessary
     * parameters for command execution have proper state according to requirements for application.
     *
     * @param messageService {@link MessageService} to hold message about validation result
     * @param result1Str     {@link String} parameter for validation
     * @param result2Str     {@link String} parameter for validation
     */
    private static void validateCommand(MessageService messageService,
                                        String result1Str, String result2Str) {
        if (messageService.isErrMessEmpty()) {
            ValidationService validationService = serviceFactory.getValidationService();
            if (!validationService.isValidEventResult(result1Str) ||
                    !validationService.isValidEventResult(result2Str)) {
                messageService.appendErrMessByKey(MESSAGE_ERR_INVALID_EVENT_RESULT);
            }
        }
    }
    
}
