package com.dubatovka.app.dao.impl;

import com.dubatovka.app.dao.EventDAO;
import com.dubatovka.app.dao.db.ConnectionPool;
import com.dubatovka.app.dao.db.WrappedConnection;
import com.dubatovka.app.dao.exception.DAOException;
import com.dubatovka.app.entity.Category;
import com.dubatovka.app.entity.Event;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.dubatovka.app.config.ConfigConstant.EVENT_QUERY_TYPE_ACTUAL;
import static com.dubatovka.app.config.ConfigConstant.EVENT_QUERY_TYPE_CLOSED;
import static com.dubatovka.app.config.ConfigConstant.EVENT_QUERY_TYPE_FAILED;
import static com.dubatovka.app.config.ConfigConstant.EVENT_QUERY_TYPE_NEW;
import static com.dubatovka.app.config.ConfigConstant.EVENT_QUERY_TYPE_NOT_STARTED;
import static com.dubatovka.app.config.ConfigConstant.EVENT_QUERY_TYPE_STARTED;
import static com.dubatovka.app.config.ConfigConstant.EVENT_QUERY_TYPE_TO_PAY;

/**
 * Class provides {@link EventDAO} implementation for MySQL database.
 *
 * @author Dubatovka Vadim
 */
class EventDAOImpl extends DBConnectionHolder implements EventDAO {
    private static final String SQL_SELECT_EVENT_BY_EVENT_ID =
        "SELECT id, date, category_id, participant1, participant2, result1, result2 " +
            "FROM event WHERE id =?";
    
    private static final String SQL_SELECT_NEW_EVENTS_BY_CATEGORY_ID =
        "SELECT id, category_id, date, participant1, participant2, result1, result2 " +
            "FROM event WHERE category_id =? " +
            "AND id NOT IN (SELECT event_id FROM outcome GROUP BY event_id) " +
            "AND (date - CURRENT_TIMESTAMP) > (0 * '1 sec'::interval) " +
            "AND result1 IS NULL ORDER BY date";
    
    private static final String SQL_SELECT_ACTUAL_EVENTS_BY_CATEGORY_ID =
        "SELECT id, category_id, date, participant1, participant2, result1, result2 " +
            "FROM event WHERE category_id =? " +
            "AND id IN (SELECT event_id FROM outcome GROUP BY event_id) " +
            "AND (date - CURRENT_TIMESTAMP) > (0 * '1 sec'::interval) " +
            "AND result1 IS NULL ORDER BY date";
    
    private static final String SQL_SELECT_NOT_STARTED_EVENTS_BY_CATEGORY_ID =
        "SELECT id, category_id, date, participant1, participant2, result1, result2 " +
            "FROM event WHERE category_id =? " +
            "AND (date - CURRENT_TIMESTAMP) > (0 * '1 sec'::interval) " +
            "AND result1 IS NULL ORDER BY date";
    
    private static final String SQL_SELECT_STARTED_EVENTS_BY_CATEGORY_ID =
        "SELECT id, category_id, date, participant1, participant2, result1, result2 " +
            "FROM event WHERE category_id =? " +
            "AND id IN (SELECT event_id FROM outcome GROUP BY event_id) " +
            "AND (date - CURRENT_TIMESTAMP) <= (0 * '1 sec'::interval) " +
            "AND result1 IS NULL ORDER BY date";
    
    private static final String SQL_SELECT_FAILED_EVENTS_BY_CATEGORY_ID =
        "SELECT id, category_id, date, participant1, participant2, result1, result2 " +
            "FROM event " +
            "WHERE category_id =? " +
            "AND (date - CURRENT_TIMESTAMP) <= (0 * '1 sec'::interval) " +
            "AND (id NOT IN (SELECT event_id FROM outcome GROUP BY event_id) " +
            "OR id NOT IN (SELECT event_id FROM bet GROUP BY event_id)) " +
            "ORDER BY date";
    
    private static final String SQL_SELECT_CLOSED_EVENTS_BY_CATEGORY_ID =
        "SELECT id, category_id, date, participant1, participant2, result1, result2 " +
            "FROM event WHERE category_id =? " +
            "AND result1 IS NOT NULL ORDER BY date";
    
    private static final String SQL_SELECT_EVENTS_TO_PAY_BY_CATEGORY_ID =
        "SELECT id, category_id, date, participant1, participant2, result1, result2 " +
            "FROM event " +
            "WHERE category_id =? AND id IN (SELECT event_id FROM bet WHERE status='win' " +
            "GROUP BY event_id) ORDER BY date";
    
    private static final String SQL_COUNT_NEW_EVENTS_GROUP_BY_CATEGORY_ID =
        "SELECT category_id, COUNT(category_id) AS count FROM event " +
            "WHERE id NOT IN (SELECT event_id FROM outcome GROUP BY event_id) " +
            "AND (date - CURRENT_TIMESTAMP) > (0 * '1 sec'::interval) " +
            "AND result1 IS NULL " +
            "GROUP BY category_id";
    
    private static final String SQL_COUNT_ACTUAL_EVENTS_GROUP_BY_CATEGORY_ID =
        "SELECT category_id, COUNT(category_id) AS count FROM event " +
            "WHERE id IN (SELECT event_id FROM outcome GROUP BY event_id) " +
            "AND (date - CURRENT_TIMESTAMP) > (0 * '1 sec'::interval) " +
            "AND result1 IS NULL " +
            "GROUP BY category_id";
    
    private static final String SQL_COUNT_NOT_STARTED_EVENTS_GROUP_BY_CATEGORY_ID =
        "SELECT category_id, COUNT(category_id) AS count FROM event " +
            "WHERE (date - CURRENT_TIMESTAMP) > (0 * '1 sec'::interval) " +
            "AND result1 IS NULL " +
            "GROUP BY category_id";
    
    private static final String SQL_COUNT_STARTED_EVENTS_GROUP_BY_CATEGORY_ID =
        "SELECT category_id, COUNT(category_id) AS count FROM event " +
            "WHERE id IN (SELECT event_id FROM outcome GROUP BY event_id) " +
            "AND (date - CURRENT_TIMESTAMP) <= (0 * '1 sec'::interval) " +
            "AND result1 IS NULL " +
            "GROUP BY category_id";
    
    private static final String SQL_COUNT_FAILED_EVENTS_GROUP_BY_CATEGORY_ID =
        "SELECT category_id, COUNT(category_id) AS count FROM event " +
            "WHERE (date - CURRENT_TIMESTAMP) <= (0 * '1 sec'::interval) AND " +
            "(id NOT IN (SELECT event_id FROM outcome GROUP BY event_id) " +
            "OR id NOT IN (SELECT event_id FROM bet GROUP BY event_id)) " +
            "GROUP BY category_id";
    
    private static final String SQL_COUNT_CLOSED_EVENTS_GROUP_BY_CATEGORY_ID =
        "SELECT category_id, COUNT(category_id) AS count FROM event " +
            "WHERE result1 IS NOT NULL " +
            "GROUP BY category_id";
    
    private static final String SQL_COUNT_EVENTS_TO_PAY_GROUP_BY_CATEGORY_ID =
        "SELECT category_id, COUNT(category_id) AS count FROM event " +
            "WHERE id IN (SELECT event_id FROM bet WHERE status='win' GROUP BY event_id) " +
            "GROUP BY category_id";
    
    private static final String SQL_DELETE_EVENT = "DELETE FROM event WHERE id=?";
    
    private static final String SQL_INSERT_EVENT =
        "INSERT INTO event (category_id, date, participant1, participant2) VALUES (?, ?, ?, ?)";
    
    private static final String SQL_UPDATE_EVENT_INFO =
        "UPDATE event SET date=?, participant1=?, participant2=? WHERE id=?";
    
    private static final String SQL_UPDATE_EVENT_RESULT =
        "UPDATE event SET result1=?, result2=? WHERE id=?";
    
    /**
     * Constructs DAO object by taking {@link WrappedConnection} object from {@link ConnectionPool}.
     */
    EventDAOImpl() {
    }
    
    /**
     * Constructs DAO object by assigning {@link DBConnectionHolder#connection} field definite
     * {@link WrappedConnection} object.
     *
     * @param connection {@link WrappedConnection} to assign to {@link DBConnectionHolder#connection}
     *                   field
     */
    EventDAOImpl(WrappedConnection connection) {
        super(connection);
    }
    
    /**
     * Reads {@link Event} from database which correspond to given {@link
     * Event} id.
     *
     * @param eventId {@link Event} id
     * @return {@link Category}
     * @throws DAOException when {@link SQLException} occurred while working with database
     */
    @Override
    public Event readEvent(int eventId) throws DAOException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT_EVENT_BY_EVENT_ID)) {
            statement.setInt(1, eventId);
            ResultSet resultSet = statement.executeQuery();
            Event event = null;
            if (resultSet.next()) {
                event = buildEvent(resultSet);
            }
            return event;
        } catch (SQLException e) {
            throw new DAOException("Database connection error while getting sport event. " + e);
        }
    }
    
    /**
     * Reads {@link List} of {@link Event} objects for given category id and query type from
     * database.
     *
     * @param categoryId     {@link String} category id
     * @param eventQueryType {@link String} query type
     * @return {@link List} of {@link Event}
     * @throws DAOException when {@link SQLException} occurred while working with database
     */
    @Override
    public List<Event> readEvents(String categoryId, String eventQueryType) throws DAOException {
        List<Event> events;
        switch (eventQueryType) {
            case EVENT_QUERY_TYPE_NEW:
                events = readEventsByQuery(categoryId, SQL_SELECT_NEW_EVENTS_BY_CATEGORY_ID);
                break;
            case EVENT_QUERY_TYPE_ACTUAL:
                events = readEventsByQuery(categoryId, SQL_SELECT_ACTUAL_EVENTS_BY_CATEGORY_ID);
                break;
            case EVENT_QUERY_TYPE_NOT_STARTED:
                events = readEventsByQuery(categoryId, SQL_SELECT_NOT_STARTED_EVENTS_BY_CATEGORY_ID);
                break;
            case EVENT_QUERY_TYPE_STARTED:
                events = readEventsByQuery(categoryId, SQL_SELECT_STARTED_EVENTS_BY_CATEGORY_ID);
                break;
            case EVENT_QUERY_TYPE_FAILED:
                events = readEventsByQuery(categoryId, SQL_SELECT_FAILED_EVENTS_BY_CATEGORY_ID);
                break;
            case EVENT_QUERY_TYPE_CLOSED:
                events = readEventsByQuery(categoryId, SQL_SELECT_CLOSED_EVENTS_BY_CATEGORY_ID);
                break;
            case EVENT_QUERY_TYPE_TO_PAY:
                events = readEventsByQuery(categoryId, SQL_SELECT_EVENTS_TO_PAY_BY_CATEGORY_ID);
                break;
            default:
                events = new ArrayList<>();
        }
        return events;
    }
    
    /**
     * Reads {@link Map} with information about quantity of events for given query type from
     * database.
     *
     * @param eventQueryType {@link String} query type
     * @return {@link Map} whose key is {@link Category} id and value is quantity of events for
     * corresponding {@link Category} id
     * @throws DAOException when {@link SQLException} occurred while working with database
     */
    @Override
    public Map<Integer, Integer> countEvents(String eventQueryType) throws DAOException {
        Map<Integer, Integer> eventCountMap;
        switch (eventQueryType) {
            case EVENT_QUERY_TYPE_NEW:
                eventCountMap = countEventsByQuery(SQL_COUNT_NEW_EVENTS_GROUP_BY_CATEGORY_ID);
                break;
            case EVENT_QUERY_TYPE_ACTUAL:
                eventCountMap = countEventsByQuery(SQL_COUNT_ACTUAL_EVENTS_GROUP_BY_CATEGORY_ID);
                break;
            case EVENT_QUERY_TYPE_NOT_STARTED:
                eventCountMap = countEventsByQuery(SQL_COUNT_NOT_STARTED_EVENTS_GROUP_BY_CATEGORY_ID);
                break;
            case EVENT_QUERY_TYPE_STARTED:
                eventCountMap = countEventsByQuery(SQL_COUNT_STARTED_EVENTS_GROUP_BY_CATEGORY_ID);
                break;
            case EVENT_QUERY_TYPE_FAILED:
                eventCountMap = countEventsByQuery(SQL_COUNT_FAILED_EVENTS_GROUP_BY_CATEGORY_ID);
                break;
            case EVENT_QUERY_TYPE_CLOSED:
                eventCountMap = countEventsByQuery(SQL_COUNT_CLOSED_EVENTS_GROUP_BY_CATEGORY_ID);
                break;
            case EVENT_QUERY_TYPE_TO_PAY:
                eventCountMap = countEventsByQuery(SQL_COUNT_EVENTS_TO_PAY_GROUP_BY_CATEGORY_ID);
                break;
            default:
                eventCountMap = new HashMap<>(0);
        }
        return eventCountMap;
    }
    
    /**
     * Deletes {@link Event} by given event id.
     *
     * @param eventId {@link Event} id
     * @throws DAOException when {@link SQLException} occurred while working with database
     */
    @Override
    public void deleteEvent(int eventId) throws DAOException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_DELETE_EVENT)) {
            statement.setInt(1, eventId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Database connection error while deleting event. " + e);
        }
    }
    
    /**
     * Inserts {@link Event} to database.
     *
     * @param event {@link Event} for insertion
     * @throws DAOException when {@link SQLException} occurred while working with database
     */
    @Override
    public void insertEvent(Event event) throws DAOException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_INSERT_EVENT)) {
            statement.setInt(1, event.getCategoryId());
            statement.setTimestamp(2, Timestamp.valueOf(event.getDate()));
            statement.setString(3, event.getParticipant1());
            statement.setString(4, event.getParticipant2());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Database connection error while inserting event. " + e);
        }
        
    }
    
    /**
     * Updates description information for given {@link Event} in database.
     *
     * @param event {@link Event} to update.
     * @throws DAOException when {@link SQLException} occurred while working with database
     */
    @Override
    public void updateEventInfo(Event event) throws DAOException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_UPDATE_EVENT_INFO)) {
            statement.setTimestamp(1, Timestamp.valueOf(event.getDate()));
            statement.setString(2, event.getParticipant1());
            statement.setString(3, event.getParticipant2());
            statement.setInt(4, event.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Database connection error while updating event info. " + e);
        }
    }
    
    /**
     * Updates results for given {@link Event} in database.
     *
     * @param event {@link Event} to update.
     * @return true is operation processed successfully
     * @throws DAOException when {@link SQLException} occurred while working with database
     */
    @Override
    public boolean updateEventResult(Event event) throws DAOException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_UPDATE_EVENT_RESULT)) {
            statement.setString(1, event.getResult1());
            statement.setString(2, event.getResult2());
            statement.setInt(3, event.getId());
            int count = statement.executeUpdate();
            return count == 1;
        } catch (SQLException e) {
            throw new DAOException("Database connection error while updating event results. " + e);
        }
    }
    
    /**
     * Reads {@link List} of {@link Event} objects for given category id and SQL query from
     * database.
     *
     * @param categoryIdStr {@link String} category id
     * @param sqlQuery      {@link String} SQL query
     * @return {@link List< Event >} of {@link Event}
     * @throws DAOException when {@link SQLException} occurred while working with database
     */
    private List<Event> readEventsByQuery(String categoryIdStr, String sqlQuery) throws DAOException {
        try (PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            int categoryId = Integer.parseInt(categoryIdStr);
            statement.setInt(1, categoryId);
            ResultSet resultSet = statement.executeQuery();
            return buildEventList(resultSet);
        } catch (SQLException e) {
            throw new DAOException("Database connection error while getting sport event. " + e);
        }
    }
    
    /**
     * Reads {@link Map} with information about quantity of events for given SQL query from
     * database.
     *
     * @param sqlQuery {@link String} SQL query
     * @return {@link Map} whose key is {@link Category} id and value is quantity of events for
     * corresponding {@link Category} id
     * @throws DAOException when {@link SQLException} occurred while working with database
     */
    private Map<Integer, Integer> countEventsByQuery(String sqlQuery) throws DAOException {
        try (Statement statement = connection.createStatement();) {
            ResultSet resultSet = statement.executeQuery(sqlQuery);
            Map<Integer, Integer> eventCountMap = new HashMap<>();
            while (resultSet.next()) {
                int categoryId = resultSet.getInt(CATEGORY_ID);
                int eventCount = resultSet.getInt(COUNT);
                eventCountMap.put(categoryId, eventCount);
            }
            return eventCountMap;
        } catch (SQLException e) {
            throw new DAOException("Database connection error while getting sport event. " + e);
        }
    }
    
    /**
     * Builds {@link List} of {@link Event} from given {@link ResultSet}.
     *
     * @param resultSet {@link ResultSet}
     * @return {@link List} of {@link Event}
     * @throws SQLException if a database access error occurs or this method is called on a closed
     *                      result set
     */
    private static List<Event> buildEventList(ResultSet resultSet) throws SQLException {
        List<Event> events = new ArrayList<>();
        while (resultSet.next()) {
            Event event = buildEvent(resultSet);
            events.add(event);
        }
        return events;
    }
    
    /**
     * Method builds {@link Event} from given ResultSet
     *
     * @param resultSet {@link ResultSet}
     * @return {@link Event}
     * @throws SQLException if the columnLabel is not valid; if a database access error occurs or
     *                      this method is called on a closed result set
     */
    private static Event buildEvent(ResultSet resultSet) throws SQLException {
        Event event = new Event();
        event.setId(resultSet.getInt(ID));
        event.setCategoryId(resultSet.getInt(CATEGORY_ID));
        event.setDate(resultSet.getTimestamp(DATE).toLocalDateTime());
        event.setParticipant1(resultSet.getString(PARTICIPANT1));
        event.setParticipant2(resultSet.getString(PARTICIPANT2));
        event.setResult1(resultSet.getString(RESULT1));
        event.setResult2(resultSet.getString(RESULT2));
        return event;
    }
}