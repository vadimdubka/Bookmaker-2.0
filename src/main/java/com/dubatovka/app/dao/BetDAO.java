package com.dubatovka.app.dao;

import com.dubatovka.app.config.ConfigConstant;
import com.dubatovka.app.dao.exception.DAOException;
import com.dubatovka.app.entity.Bet;
import com.dubatovka.app.entity.Category;
import com.dubatovka.app.entity.Event;
import com.dubatovka.app.entity.Outcome;
import com.dubatovka.app.entity.Player;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * DAO interface for {@link Bet} objects.
 *
 * @author Dubatovka Vadim
 */
public interface BetDAO {
    /**
     * Column names of database table 'bet'.
     */
    String PLAYER_ID    = "player_id";
    String EVENT_ID     = "event_id";
    String OUTCOME_TYPE = "type";
    String DATE         = "date";
    String COEFFICIENT  = "coefficient";
    String AMOUNT       = "amount";
    String STATUS       = "status";
    String COUNT        = "count";
    String SUM          = "sum";
    
    /**
     * Reads list of {@link Bet} for given {@link Player} id from database.
     *
     * @param playerId {@link Player} id
     * @return {@link List} of {@link Bet}
     * @throws DAOException when {@link SQLException} occurred while working with database
     */
    List<Bet> readBetListForPlayer(int playerId) throws DAOException;
    
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
    List<Bet> readBetListForPlayer(int playerId, int limit, int offset) throws DAOException;
    
    /**
     * Inserts {@link Bet} to database.
     *
     * @param bet {@link Bet} for insertion
     * @return true if operation processed successfully
     * @throws DAOException when {@link SQLException} occurred while working with database
     */
    boolean insertBet(Bet bet) throws DAOException;
    
    /**
     * Updates bet status for bets in database, which correspond to given parameters.
     *
     * @param eventId {@link Event} id for query
     * @param type    {@link Outcome.Type} type for query
     * @param status  {@link Bet.Status} new status for update
     * @throws DAOException when {@link SQLException} occurred while working with database
     */
    void updateBetStatus(int eventId, Outcome.Type type, Bet.Status status) throws DAOException;
    
    /**
     * Updates bet status for bets in database, which correspond to given parameters.
     *
     * @param eventId   {@link Event} id for query
     * @param oldStatus {@link Bet.Status} old status for query
     * @param newStatus {@link Bet.Status} new status for update
     * @return quantity of updated rows
     * @throws DAOException when {@link SQLException} occurred while working with database
     */
    int updateBetStatus(int eventId, Bet.Status oldStatus, Bet.Status newStatus) throws DAOException;
    
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
    Map<String, Map<String, String>> readWinBetInfoMap(int categoryId) throws DAOException;
    
    /**
     * Reads {@link Set} of {@link Bet} from database which correspond to given parameters.
     *
     * @param eventId {@link Event} id for query
     * @param status  {@link Bet.Status} for query
     * @return {@link Set} of {@link Bet}
     * @throws DAOException when {@link SQLException} occurred while working with database
     */
    Set<Bet> readBetSetForEventAndStatus(int eventId, Bet.Status status) throws DAOException;
    
    /**
     * Count bets in database for given {@link Player} id.
     *
     * @param playerId {@link Player} id
     * @return quantity
     * @throws DAOException when {@link SQLException} occurred while working with database
     */
    int countBetForPlayer(int playerId) throws DAOException;
}
