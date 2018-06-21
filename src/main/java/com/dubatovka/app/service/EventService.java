package com.dubatovka.app.service;

import com.dubatovka.app.dao.impl.DAOProvider;
import com.dubatovka.app.entity.Category;
import com.dubatovka.app.entity.Event;
import com.dubatovka.app.entity.Outcome;

import java.util.List;
import java.util.Map;

/**
 * The class provides abstraction for Service layer actions with Events.
 *
 * @author Dubatovka Vadim
 */
public abstract class EventService extends DAOProviderHolder {
    
    /**
     * Default constructor.
     */
    protected EventService() {
    }
    
    /**
     * Constructs instance using definite {@link DAOProvider} object.
     */
    protected EventService(DAOProvider daoProvider) {
        super(daoProvider);
    }
    
    /**
     * Calls DAO layer to get {@link Event} from database which correspond to given {@link
     * Event} id.
     *
     * @param eventId {@link Event} id
     * @return {@link Category}
     */
    public abstract Event getEvent(int eventId);
    
    /**
     * Calls DAO layer to get {@link Event} from database which correspond to given {@link
     * Event} id.
     *
     * @param eventIdStr {@link Event} id of type {@link String}
     * @return {@link Event}
     */
    public abstract Event getEvent(String eventIdStr);
    
    /**
     * Calls DAO layer to get {@link List} of {@link Event} objects for given category id and query
     * type from database.
     *
     * @param categoryId     {@link String} category id
     * @param eventQueryType {@link String} query type
     * @return {@link List} of {@link Event}
     */
    public abstract List<Event> getEvents(String categoryId, String eventQueryType);
    
    /**
     * Calls DAO layer to get {@link Map} with information about quantity of events for given query
     * type from database.
     *
     * @param eventQueryType {@link String} query type
     * @return {@link Map} whose key is {@link Category} id and value is quantity of events for
     * corresponding {@link Category} id
     */
    public abstract Map<Integer, Integer> countEvents(String eventQueryType);
    
    /**
     * Calls DAO layer to get {@link Map} with information about coefficients for certain outcome
     * type and event.
     *
     * @param events {@link List} of {@link Event} for which info about coefficient will be gotten
     * @return {@link Map} whose key is {@link Outcome.Type#getType} and value is {@link Map} whose
     * key is {@link Event} id and values are coefficients for corresponding {@link Event} id.
     */
    public abstract Map<String, Map<String, String>> getOutcomeColumnMaps(List<Event> events);
    
    /**
     * Calls DAO layer to delete {@link Event} by given event id.
     *
     * @param eventId        {@link Event} id
     * @param messageService {@link MessageService} that holds messages, formed during execution of
     *                       method
     */
    public abstract void deleteEvent(int eventId, MessageService messageService);
    
    /**
     * Calls DAO layer to insert {@link Event} to database.
     *
     * @param event          {@link Event} for insertion
     * @param messageService {@link MessageService} that holds messages, formed during execution of
     *                       method
     */
    public abstract void insertEvent(Event event, MessageService messageService);
    
    /**
     * Calls DAO layer to update description information for given {@link Event} in database.
     *
     * @param event          {@link Event} to update
     * @param messageService {@link MessageService} that holds messages, formed during execution of
     *                       method
     */
    public abstract void updateEventInfo(Event event, MessageService messageService);
    
    /**
     * Calls DAO layer to update results for given {@link Event} and update bet status of bets in
     * database based on event results.
     *
     * @param event          {@link Event} to update.
     * @param messageService {@link MessageService} that holds messages, formed during execution of
     *                       method
     */
    public abstract void updateEventResult(Event event, MessageService messageService);
}
