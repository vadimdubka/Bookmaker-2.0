package com.dubatovka.app.service;

import com.dubatovka.app.config.ConfigConstant;
import com.dubatovka.app.entity.Bet;
import com.dubatovka.app.entity.Category;
import com.dubatovka.app.entity.Event;
import com.dubatovka.app.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The class provides abstraction for Service layer actions with Bets.
 *
 * @author Dubatovka Vadim
 */
public interface BetService extends AutoCloseable {
    
    /**
     * Calls DAO layer to get list of {@link Bet} for given {@link Player} id from database using
     * limit and offset parameters in SQL query that define specific range of bets in database.
     *
     * @param playerId {@link Player} id
     * @param limit    parameter in SQL query
     * @param offset   parameter in SQL query
     * @return {@link List} of {@link Bet}
     */
    List<Bet> getBetListForPlayer(int playerId, int limit, int offset);
    
    /**
     * Calls DAO layer to get {@link Set} of {@link Bet} which correspond to given parameters.
     *
     * @param eventId {@link Event} id for query
     * @param status  {@link Bet.Status} for query
     * @return {@link Set} of {@link Bet}
     */
    Set<Bet> getBetSetForEventAndStatus(int eventId, Bet.Status status);
    
    /**
     * Calls DAO layer to get {@link Map} with information about quantity and amount of winning bets
     * for events which correspond to given {@link Category} id.
     *
     * @param categoryId {@link Category} id for query
     * @return {@link Map} whose keys are {@link ConfigConstant#WIN_BET_INFO_KEY_COUNT} and {@link
     * ConfigConstant#WIN_BET_INFO_KEY_SUM} and values are {@link Map} whose keys are {@link Event}
     * id and values are quantity or amount of winning bets for corresponding {@link Event} id.
     */
    Map<String, Map<String, String>> getWinBetInfo(int categoryId);
    
    /**
     * Calls DAO layer to pay winning bets for given {@link Event} id and update balance
     * of player whose bet won.
     *
     * @param eventId        {@link Event} id
     * @param messageService {@link MessageService} that holds messages, formed during execution of
     *                       method
     */
    void payWinBet(int eventId, MessageService messageService);
    
    /**
     * Calls DAO layer to place information about given {@link Bet} in database and update balance
     * of player who made bet.
     *
     * @param bet            {@link Bet} bet
     * @param messageService {@link MessageService} that holds messages, formed during execution of
     *                       method
     */
    void makeBet(Bet bet, MessageService messageService);
    
    /**
     * Calls DAO layer count bets in database for given {@link Player} id.
     *
     * @param playerId {@link Player} id
     * @return quantity
     */
    int countBetsForPlayer(int playerId);
    
    @Override
    void close();
}
