package com.dubatovka.app.service.impl;

import com.dubatovka.app.dao.BetDAO;
import com.dubatovka.app.dao.EventDAO;
import com.dubatovka.app.dao.exception.DAOException;
import com.dubatovka.app.dao.impl.DAOProvider;
import com.dubatovka.app.entity.Bet;
import com.dubatovka.app.entity.Category;
import com.dubatovka.app.entity.Event;
import com.dubatovka.app.entity.Outcome;
import com.dubatovka.app.service.EventService;
import com.dubatovka.app.service.MessageService;
import com.dubatovka.app.service.OutcomeService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.dubatovka.app.config.ConfigConstant.MESSAGE_ERR_SQL_OPERATION;
import static com.dubatovka.app.config.ConfigConstant.MESSAGE_ERR_SQL_TRANSACTION;
import static com.dubatovka.app.config.ConfigConstant.OUTCOME_TYPE_KEY_NAME;

/**
 * The class provides implementation for Service layer actions with Events.
 *
 * @author Dubatovka Vadim
 */
class EventServiceImpl extends EventService {
    private static final Logger logger = LogManager.getLogger(EventServiceImpl.class);
    
    private final EventDAO eventDAO = daoProvider.getEventDAO();
    private final BetDAO   betDAO   = daoProvider.getBetDAO();
    
    private final Map<String, Map<String, String>> coeffColumnMaps = new HashMap<>();
    private final Map<String, String>              type1           = new HashMap<>();
    private final Map<String, String>              typeX           = new HashMap<>();
    private final Map<String, String>              type2           = new HashMap<>();
    
    {
        coeffColumnMaps.put(Outcome.Type.TYPE_1.getType(), type1);
        coeffColumnMaps.put(Outcome.Type.TYPE_X.getType(), typeX);
        coeffColumnMaps.put(Outcome.Type.TYPE_2.getType(), type2);
        
        type1.put(OUTCOME_TYPE_KEY_NAME, Outcome.Type.TYPE_1.getType());
        typeX.put(OUTCOME_TYPE_KEY_NAME, Outcome.Type.TYPE_X.getType());
        type2.put(OUTCOME_TYPE_KEY_NAME, Outcome.Type.TYPE_2.getType());
    }
    
    /**
     * Default constructor.
     */
    EventServiceImpl() {
        
    }
    
    /**
     * Constructs instance using definite {@link DAOProvider} object.
     */
    EventServiceImpl(DAOProvider daoProvider) {
        super(daoProvider);
    }
    
    /**
     * Calls DAO layer to get {@link Event} from database which correspond to given {@link
     * Event} id.
     *
     * @param eventId {@link Event} id
     * @return {@link Category}
     */
    @Override
    public Event getEvent(int eventId) {
        Event event = null;
        try {
            event = eventDAO.readEvent(eventId);
        } catch (DAOException e) {
            logger.log(Level.ERROR, e.getMessage());
        }
        OutcomeService outcomeService = ServiceFactory.getOutcomeService(daoProvider);
        outcomeService.setOutcomesForEvent(event);
        return event;
    }
    
    /**
     * Calls DAO layer to get {@link Event} from database which correspond to given {@link
     * Event} id.
     *
     * @param eventIdStr {@link Event} id of type {@link String}
     * @return {@link Event}
     */
    @Override
    public Event getEvent(String eventIdStr) {
        Event event = null;
        try {
            int eventId = Integer.parseInt(eventIdStr);
            event = getEvent(eventId);
        } catch (NumberFormatException e) {
            logger.log(Level.ERROR, String.format("%s %s %s",
                                                  e.getClass(), e.getMessage(), eventIdStr));
        }
        return event;
    }
    
    /**
     * Calls DAO layer to get {@link List} of {@link Event} objects for given category id and query
     * type from database.
     *
     * @param categoryId     {@link String} category id
     * @param eventQueryType {@link String} query type
     * @return {@link List} of {@link Event}
     */
    @Override
    public List<Event> getEvents(String categoryId, String eventQueryType) {
        List<Event> events = null;
        try {
            if (eventQueryType != null) {
                events = eventDAO.readEvents(categoryId, eventQueryType);
                setOutcomesForEvents(events);
            }
        } catch (DAOException e) {
            logger.log(Level.ERROR, e.getMessage());
        }
        return events;
    }
    
    /**
     * Calls DAO layer to get {@link Map} with information about quantity of events for given query
     * type from database.
     *
     * @param eventQueryType {@link String} query type
     * @return {@link Map} whose key is {@link Category} id and value is quantity of events for
     * corresponding {@link Category} id
     */
    @Override
    public Map<Integer, Integer> countEvents(String eventQueryType) {
        Map<Integer, Integer> eventCountMap = null;
        try {
            if (eventQueryType != null) {
                eventCountMap = eventDAO.countEvents(eventQueryType);
            }
        } catch (DAOException e) {
            logger.log(Level.ERROR, e.getMessage());
        }
        return eventCountMap;
    }
    
    /**
     * Calls DAO layer to get {@link Map} with information about coefficients for certain outcome
     * type and event.
     *
     * @param events {@link List} of {@link Event} for which info about coefficient will be gotten
     * @return {@link Map} whose key is {@link Outcome.Type#getType} and value is {@link Map} whose
     * key is {@link Event} id and values are coefficients for corresponding {@link Event} id.
     */
    @Override
    public Map<String, Map<String, String>> getOutcomeColumnMaps(List<Event> events) {
        if (events != null) {
            events.forEach(event -> {
                int evetId = event.getId();
                fillOutcomeColumnMaps(evetId, event.getOutcomeSet());
            });
        }
        return coeffColumnMaps;
    }
    
    /**
     * Calls DAO layer to delete {@link Event} by given event id.
     *
     * @param eventId        {@link Event} id
     * @param messageService {@link MessageService} that holds messages, formed during execution of
     *                       method
     */
    @Override
    public void deleteEvent(int eventId, MessageService messageService) {
        try {
            eventDAO.deleteEvent(eventId);
        } catch (DAOException e) {
            logger.log(Level.ERROR, e.getMessage());
            messageService.appendErrMessByKey(MESSAGE_ERR_SQL_OPERATION);
        }
    }
    
    /**
     * Calls DAO layer to insert {@link Event} to database.
     *
     * @param event          {@link Event} for insertion
     * @param messageService {@link MessageService} that holds messages, formed during execution of
     *                       method
     */
    @Override
    public void insertEvent(Event event, MessageService messageService) {
        try {
            eventDAO.insertEvent(event);
        } catch (DAOException e) {
            logger.log(Level.ERROR, e.getMessage());
            messageService.appendErrMessByKey(MESSAGE_ERR_SQL_OPERATION);
        }
    }
    
    /**
     * Calls DAO layer to update description information for given {@link Event} in database.
     *
     * @param event          {@link Event} to update
     * @param messageService {@link MessageService} that holds messages, formed during execution of
     *                       method
     */
    @Override
    public void updateEventInfo(Event event, MessageService messageService) {
        try {
            eventDAO.updateEventInfo(event);
        } catch (DAOException e) {
            logger.log(Level.ERROR, e.getMessage());
            messageService.appendErrMessByKey(MESSAGE_ERR_SQL_OPERATION);
        }
    }
    
    /**
     * Calls DAO layer to update results for given {@link Event} and update bet status of bets in
     * database based on event results.
     *
     * @param event          {@link Event} to update.
     * @param messageService {@link MessageService} that holds messages, formed during execution of
     *                       method
     */
    @Override
    public void updateEventResult(Event event, MessageService messageService) {
        int eventId = event.getId();
        int result1 = Integer.parseInt(event.getResult1());
        int result2 = Integer.parseInt(event.getResult2());
        
        Map<Outcome.Type, Bet.Status> betStatusMap
            = getBetStatusMapForEventResult(result1, result2);
        try {
            daoProvider.beginTransaction();
            boolean isUpdEvent = eventDAO.updateEventResult(event);
            for (Map.Entry<Outcome.Type, Bet.Status> entrySet : betStatusMap.entrySet()) {
                betDAO.updateBetStatus(eventId, entrySet.getKey(), entrySet.getValue());
            }
            if (isUpdEvent) {
                daoProvider.commit();
            } else {
                daoProvider.rollback();
            }
        } catch (DAOException e) {
            logger.log(Level.ERROR, e.getMessage());
            messageService.appendErrMessByKey(MESSAGE_ERR_SQL_OPERATION);
        } catch (SQLException e) {
            logger.log(Level.ERROR, MESSAGE_ERR_SQL_TRANSACTION + e);
            messageService.appendErrMessByKey(MESSAGE_ERR_SQL_TRANSACTION);
        }
    }
    
    /**
     * Maps definite {@link Outcome.Type} to corresponding {@link Bet.Status} based on event
     * results.
     *
     * @param result1 {@link int} result of first event participant
     * @param result2 {@link int} result of second event participant
     */
    private static Map<Outcome.Type, Bet.Status> getBetStatusMapForEventResult(int result1,
                                                                               int result2) {
        Map<Outcome.Type, Bet.Status> betStatusMap = new EnumMap<>(Outcome.Type.class);
        betStatusMap.put(Outcome.Type.TYPE_1, Bet.Status.LOSING);
        betStatusMap.put(Outcome.Type.TYPE_X, Bet.Status.LOSING);
        betStatusMap.put(Outcome.Type.TYPE_2, Bet.Status.LOSING);
        if (result1 > result2) {
            betStatusMap.put(Outcome.Type.TYPE_1, Bet.Status.WIN);
        } else if (result1 < result2) {
            betStatusMap.put(Outcome.Type.TYPE_2, Bet.Status.WIN);
        } else {
            betStatusMap.put(Outcome.Type.TYPE_X, Bet.Status.WIN);
        }
        return betStatusMap;
    }
    
    private void setOutcomesForEvents(Iterable<Event> eventSet) {
        eventSet.forEach(ServiceFactory.getOutcomeService(daoProvider)::setOutcomesForEvent);
    }
    
    private void fillOutcomeColumnMaps(int eventId, Iterable<Outcome> outcomeSet) {
        for (Outcome outcome : outcomeSet) {
            String type = outcome.getType().getType();
            Map<String, String> typeMap = coeffColumnMaps.get(type);
            if (typeMap != null) {
                typeMap.put(String.valueOf(eventId), String.valueOf(outcome.getCoefficient()));
            }
        }
    }
}
