package com.dubatovka.app.dao.impl;

import com.dubatovka.app.config.ConfigConstant;
import com.dubatovka.app.dao.BetDAO;
import com.dubatovka.app.dao.db.ConnectionPool;
import com.dubatovka.app.dao.db.WrappedConnection;
import com.dubatovka.app.dao.exception.DAOException;
import com.dubatovka.app.entity.Bet;
import com.dubatovka.app.entity.Category;
import com.dubatovka.app.entity.Event;
import com.dubatovka.app.entity.Outcome;
import com.dubatovka.app.entity.Player;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.dubatovka.app.config.ConfigConstant.WIN_BET_INFO_KEY_COUNT;
import static com.dubatovka.app.config.ConfigConstant.WIN_BET_INFO_KEY_SUM;

/**
 * Class provides {@link BetDAO} implementation for MySQL database.
 *
 * @author Dubatovka Vadim
 */
class BetDAOImpl extends DBConnectionHolder implements BetDAO {
    private static final String SQL_INSERT_BET =
        "INSERT INTO bet (player_id, event_id, type, date, coefficient, amount, status) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?::bet_status)";
    
    private static final String SQL_UPDATE_BET_STATUS_BY_TYPE =
        "UPDATE bet SET status=?::bet_status WHERE event_id=? AND type=? AND status <>'paid'";
    
    private static final String SQL_UPDATE_BET_STATUS_BY_STATUS =
        "UPDATE bet SET status=?::bet_status WHERE event_id=? AND status= ?::bet_status";
    
    private static final String SQL_SELECT_BET_BY_EVENT_ID_AND_STATUS =
        "SELECT player_id, event_id, type, date, coefficient, amount, status " +
            "FROM bet WHERE event_id = ? and status = ?::bet_status";
    
    private static final String SQL_SELECT_BET_BY_PLAYER_ID =
        "SELECT player_id, event_id, type, date, coefficient, amount, status " +
            "FROM bet WHERE player_id = ? ORDER BY date DESC";
    
    private static final String SQL_COUNT_BET_BY_PLAYER_ID =
        "SELECT COUNT(player_id) AS count FROM bet WHERE player_id = ?";
    
    private static final String SQL_SELECT_BET_BY_PLAYER_ID_LIMIT =
        "SELECT player_id, event_id, type, date, coefficient, amount, status " +
            "FROM bet WHERE player_id = ? ORDER BY date DESC LIMIT ? OFFSET ?";
    
    private static final String SQL_SELECT_BET_INFO_BY_STATUS_GROUP_BY_EVENT_ID =
        "SELECT event_id, COUNT(event_id) AS count, SUM(amount) AS sum " +
            "FROM bet WHERE status=?::bet_status and event_id IN (SELECT id FROM event " +
            "WHERE category_id=?) GROUP BY event_id;";
    
    /**
     * Constructs DAO object by taking {@link WrappedConnection} object from {@link ConnectionPool}.
     */
    BetDAOImpl() {
    }
    
    /**
     * Constructs DAO object by assigning {@link DBConnectionHolder#connection} field definite
     * {@link WrappedConnection} object.
     *
     * @param connection {@link WrappedConnection} to assign to {@link DBConnectionHolder#connection}
     *                   field
     */
    BetDAOImpl(WrappedConnection connection) {
        super(connection);
    }
    
    /**
     * Inserts {@link Bet} to database.
     *
     * @param bet {@link Bet} for insertion
     * @return true if operation processed successfully
     * @throws DAOException when {@link SQLException} occurred while working with database
     */
    @Override
    public boolean insertBet(Bet bet) throws DAOException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_INSERT_BET)) {
            statement.setInt(1, bet.getPlayerId());
            statement.setInt(2, bet.getEventId());
            statement.setString(3, bet.getOutcomeType());
            statement.setTimestamp(4, Timestamp.valueOf(bet.getDate()));
            statement.setBigDecimal(5, bet.getCoefficient());
            statement.setBigDecimal(6, bet.getAmount());
            statement.setString(7, bet.getStatus().getStatus());
            int rowUpd = statement.executeUpdate();
            return rowUpd == 1;
        } catch (SQLException e) {
            throw new DAOException("Database connection error while inserting bet. " + e);
        }
    }
    
    /**
     * Reads {@link List} of {@link Bet} for given {@link Player} id from database.
     *
     * @param playerId {@link Player} id
     * @return {@link List} of {@link Bet}
     * @throws DAOException when {@link SQLException} occurred while working with database
     */
    @Override
    public List<Bet> readBetListForPlayer(int playerId) throws DAOException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BET_BY_PLAYER_ID)) {
            statement.setInt(1, playerId);
            ResultSet resultSet = statement.executeQuery();
            return buildBetList(resultSet);
        } catch (SQLException e) {
            throw new DAOException("Database connection error while reading bet. " + e);
        }
    }
    
    /**
     * Reads list of {@link Bet} for given {@link Player} id from database using limit and offset
     * parameters in SQL query that define specific range of bets in database.
     *
     * @param playerId {@link Player} id
     * @param limit    parameter in SQL query
     * @param offset   parameter in SQL query
     * @return {@link List} of {@link Bet}
     * @throws DAOException when {@link SQLException} occurred while working with database
     */
    @Override
    public List<Bet> readBetListForPlayer(int playerId, int limit, int offset) throws DAOException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BET_BY_PLAYER_ID_LIMIT)) {
            statement.setInt(1, playerId);
            statement.setInt(2, limit);
            statement.setInt(3, offset);
            ResultSet resultSet = statement.executeQuery();
            return buildBetList(resultSet);
        } catch (SQLException e) {
            throw new DAOException("Database connection error while reading bet. " + e);
        }
    }
    
    /**
     * Reads {@link Set} of {@link Bet} from database which correspond to given parameters.
     *
     * @param eventId {@link Event} id for query
     * @param status  {@link Bet.Status} for query
     * @return {@link Set} of {@link Bet}
     * @throws DAOException when {@link SQLException} occurred while working with database
     */
    @Override
    public Set<Bet> readBetSetForEventAndStatus(int eventId, Bet.Status status) throws DAOException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BET_BY_EVENT_ID_AND_STATUS)) {
            statement.setInt(1, eventId);
            statement.setString(2, status.getStatus());
            ResultSet resultSet = statement.executeQuery();
            return buildBetSet(resultSet);
        } catch (SQLException e) {
            throw new DAOException("Database connection error while reading bet. " + e);
        }
    }
    
    /**
     * Count bets in database for given {@link Player} id.
     *
     * @param playerId {@link Player} id
     * @return quantity
     * @throws DAOException when {@link SQLException} occurred while working with database
     */
    @Override
    public int countBetForPlayer(int playerId) throws DAOException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_COUNT_BET_BY_PLAYER_ID);) {
            statement.setInt(1, playerId);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt(COUNT);
        } catch (SQLException e) {
            throw new DAOException("Database connection error while count bet. " + e);
        }
    }
    
    /**
     * Updates bet status for bets in database, which correspond to given parameters.
     *
     * @param eventId {@link Event} id for query
     * @param type    {@link Outcome.Type} type for query
     * @param status  {@link Bet.Status} new status for update
     * @throws DAOException when {@link SQLException} occurred while working with database
     */
    @Override
    public void updateBetStatus(int eventId, Outcome.Type type, Bet.Status status)
        throws DAOException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_UPDATE_BET_STATUS_BY_TYPE)) {
            statement.setString(1, status.getStatus());
            statement.setInt(2, eventId);
            statement.setString(3, type.getType());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Database connection error while updating bet. " + e);
        }
    }
    
    /**
     * Updates bet status for bets in database, which correspond to given parameters.
     *
     * @param eventId   {@link Event} id for query
     * @param oldStatus {@link Bet.Status} old status for query
     * @param newStatus {@link Bet.Status} new status for update
     * @return quantity of updated rows
     * @throws DAOException when {@link SQLException} occurred while working with database
     */
    @Override
    public int updateBetStatus(int eventId, Bet.Status oldStatus, Bet.Status newStatus)
        throws DAOException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_UPDATE_BET_STATUS_BY_STATUS)) {
            statement.setString(1, newStatus.getStatus());
            statement.setInt(2, eventId);
            statement.setString(3, oldStatus.getStatus());
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Database connection error while updating bet. " + e);
        }
    }
    
    /**
     * Reads {@link Map} with information about quantity and amount of winning bets for events which
     * correspond to given {@link Category} id.
     *
     * @param categoryId {@link Category} id for query
     * @return {@link Map} whose keys are {@link ConfigConstant#WIN_BET_INFO_KEY_COUNT} and {@link
     * ConfigConstant#WIN_BET_INFO_KEY_SUM} and values are {@link Map} whose keys are {@link Event}
     * id and values are quantity or amount of winning bets for corresponding {@link Event} id.
     * @throws DAOException when {@link SQLException} occurred while working with database
     */
    @Override
    public Map<String, Map<String, String>> readWinBetInfoMap(int categoryId) throws DAOException {
        try (PreparedStatement statement =
                 connection.prepareStatement(SQL_SELECT_BET_INFO_BY_STATUS_GROUP_BY_EVENT_ID)) {
            statement.setString(1, Bet.Status.WIN.getStatus());
            statement.setInt(2, categoryId);
            ResultSet resultSet = statement.executeQuery();
            
            Map<String, String> winBetCount = new HashMap<>();
            Map<String, String> winBetSum = new HashMap<>();
            
            while (resultSet.next()) {
                int eventId = resultSet.getInt(EVENT_ID);
                int count = resultSet.getInt(COUNT);
                BigDecimal sum = resultSet.getBigDecimal(SUM);
                winBetCount.put(String.valueOf(eventId), String.valueOf(count));
                winBetSum.put(String.valueOf(eventId), String.valueOf(sum));
            }
            Map<String, Map<String, String>> winBetInfoMap = new HashMap<>();
            winBetInfoMap.put(WIN_BET_INFO_KEY_COUNT, winBetCount);
            winBetInfoMap.put(WIN_BET_INFO_KEY_SUM, winBetSum);
            return winBetInfoMap;
        } catch (SQLException e) {
            throw new DAOException("Database connection error while reading bet. " + e);
        }
    }
    
    /**
     * Builds {@link List} of {@link Bet} from given {@link ResultSet}.
     *
     * @param resultSet {@link ResultSet}
     * @return {@link List} of {@link Bet}
     * @throws SQLException if a database access error occurs or this method is called on a closed
     *                      result set
     */
    private static List<Bet> buildBetList(ResultSet resultSet) throws SQLException {
        List<Bet> betList = new ArrayList<>();
        while (resultSet.next()) {
            Bet bet = buildBet(resultSet);
            betList.add(bet);
        }
        return betList;
    }
    
    /**
     * Builds {@link Set} of {@link Bet} from given {@link ResultSet}.
     *
     * @param resultSet {@link ResultSet}
     * @return {@link Set} of {@link Bet}
     * @throws SQLException if the columnLabel is not valid; if a database access error occurs or
     *                      this method is called on a closed result set
     */
    private static Set<Bet> buildBetSet(ResultSet resultSet) throws SQLException {
        Set<Bet> betSet = new HashSet<>();
        while (resultSet.next()) {
            Bet bet = buildBet(resultSet);
            betSet.add(bet);
        }
        return betSet;
    }
    
    /**
     * Method builds {@link Bet} from given ResultSet
     *
     * @param resultSet {@link ResultSet}
     * @return {@link Bet}
     * @throws SQLException if the columnLabel is not valid; if a database access error occurs or
     *                      this method is called on a closed result set
     */
    private static Bet buildBet(ResultSet resultSet) throws SQLException {
        int playerId = resultSet.getInt(PLAYER_ID);
        int eventId = resultSet.getInt(EVENT_ID);
        String outcomeType = resultSet.getString(OUTCOME_TYPE);
        LocalDateTime date = resultSet.getTimestamp(DATE).toLocalDateTime();
        BigDecimal coefficient = resultSet.getBigDecimal(COEFFICIENT);
        BigDecimal amount = resultSet.getBigDecimal(AMOUNT);
        Bet.Status status = Bet.Status.valueOf(resultSet.getString(STATUS).toUpperCase());
        Bet bet = new Bet(playerId, eventId, outcomeType, date, coefficient, amount, status);
        return bet;
    }
}