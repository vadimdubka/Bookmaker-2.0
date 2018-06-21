package com.dubatovka.app.dao;

import com.dubatovka.app.dao.exception.DAOException;
import com.dubatovka.app.entity.Category;
import com.dubatovka.app.entity.Event;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * DAO interface for {@link Event} objects.
 *
 * @author Dubatovka Vadim
 */
public interface EventDAO {
    /**
     * Column names of database table 'event'.
     */
    String ID           = "id";
    String CATEGORY_ID  = "category_id";
    String DATE         = "date";
    String PARTICIPANT1 = "participant1";
    String PARTICIPANT2 = "participant2";
    String RESULT1      = "result1";
    String RESULT2      = "result2";
    String COUNT        = "count";
    
    /**
     * Reads {@link Event} from database which correspond to given {@link
     * Event} id.
     *
     * @param eventId {@link Event} id
     * @return {@link Category}
     * @throws DAOException when {@link SQLException} occurred while working with database
     */
    Event readEvent(int eventId) throws DAOException;
    
    /**
     * Reads {@link List} of {@link Event} objects for given category id and query type from
     * database.
     *
     * @param categoryId     {@link String} category id
     * @param eventQueryType {@link String} query type
     * @return {@link List} of {@link Event}
     * @throws DAOException when {@link SQLException} occurred while working with database
     */
    List<Event> readEvents(String categoryId, String eventQueryType) throws DAOException;
    
    /**
     * Deletes {@link Event} by given event id.
     *
     * @param eventId {@link Event} id
     * @throws DAOException when {@link SQLException} occurred while working with database
     */
    void deleteEvent(int eventId) throws DAOException;
    
    /**
     * Inserts {@link Event} to database.
     *
     * @param event {@link Event} for insertion
     * @throws DAOException when {@link SQLException} occurred while working with database
     */
    void insertEvent(Event event) throws DAOException;
    
    /**
     * Updates description information for given {@link Event} in database.
     *
     * @param event {@link Event} to update.
     * @throws DAOException when {@link SQLException} occurred while working with database
     */
    void updateEventInfo(Event event) throws DAOException;
    
    /**
     * Updates results for given {@link Event} in database.
     *
     * @param event {@link Event} to update.
     * @return true is operation processed successfully
     * @throws DAOException when {@link SQLException} occurred while working with database
     */
    boolean updateEventResult(Event event) throws DAOException;
    
    /**
     * Reads {@link Map} with information about quantity of events for given query type from
     * database.
     *
     * @param eventQueryType {@link String} query type
     * @return {@link Map} whose key is {@link Category} id and value is quantity of events for
     * corresponding {@link Category} id
     * @throws DAOException when {@link SQLException} occurred while working with database
     */
    Map<Integer, Integer> countEvents(String eventQueryType) throws DAOException;
}
