package com.dubatovka.app.service.impl;

import com.dubatovka.app.config.ConfigConstant;
import com.dubatovka.app.dao.BetDAO;
import com.dubatovka.app.dao.PlayerDAO;
import com.dubatovka.app.dao.TransactionDAO;
import com.dubatovka.app.dao.exception.DAOException;
import com.dubatovka.app.dao.impl.DAOProvider;
import com.dubatovka.app.entity.Bet;
import com.dubatovka.app.entity.Category;
import com.dubatovka.app.entity.Event;
import com.dubatovka.app.entity.Player;
import com.dubatovka.app.entity.Transaction;
import com.dubatovka.app.service.BetService;
import com.dubatovka.app.service.MessageService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.dubatovka.app.config.ConfigConstant.MESSAGE_ERR_SQL_OPERATION;
import static com.dubatovka.app.config.ConfigConstant.MESSAGE_ERR_SQL_TRANSACTION;

/**
 * The class provides implementation for Service layer actions with Bets.
 *
 * @author Dubatovka Vadim
 */
class BetServiceImpl extends BetService {
    private static final Logger logger = LogManager.getLogger(BetServiceImpl.class);
    
    private final BetDAO         betDAO         = daoProvider.getBetDAO();
    private final PlayerDAO      playerDAO      = daoProvider.getPlayerDAO();
    private final TransactionDAO transactionDAO = daoProvider.getTransactionDAO();
    
    /**
     * Default constructor.
     */
    BetServiceImpl() {
    }
    
    /**
     * Constructs instance using definite {@link DAOProvider} object.
     */
    BetServiceImpl(DAOProvider daoProvider) {
        super(daoProvider);
    }
    
    /**
     * Calls DAO layer to get list of {@link Bet} for given {@link Player} id from database using
     * limit and offset parameters in SQL query that define specific range of bets in database.
     *
     * @param playerId {@link Player} id
     * @param limit    parameter in SQL query
     * @param offset   parameter in SQL query
     * @return {@link List} of {@link Bet}
     */
    @Override
    public List<Bet> getBetListForPlayer(int playerId, int limit, int offset) {
        List<Bet> betList = null;
        try {
            betList = betDAO.readBetListForPlayer(playerId, limit, offset);
        } catch (DAOException e) {
            logger.log(Level.ERROR, e.getMessage());
        }
        return betList;
    }
    
    /**
     * Calls DAO layer to get {@link Set} of {@link Bet} which correspond to given parameters.
     *
     * @param eventId {@link Event} id for query
     * @param status  {@link Bet.Status} for query
     * @return {@link Set} of {@link Bet}
     */
    @Override
    public Set<Bet> getBetSetForEventAndStatus(int eventId, Bet.Status status) {
        Set<Bet> betList = null;
        try {
            betList = betDAO.readBetSetForEventAndStatus(eventId, status);
        } catch (DAOException e) {
            logger.log(Level.ERROR, e.getMessage());
        }
        return betList;
    }
    
    /**
     * Calls DAO layer to get {@link Map} with information about quantity and amount of winning bets
     * for events which correspond to given {@link Category} id.
     *
     * @param categoryId {@link Category} id for query
     * @return {@link Map} whose keys are {@link ConfigConstant#WIN_BET_INFO_KEY_COUNT} and {@link
     * ConfigConstant#WIN_BET_INFO_KEY_SUM} and values are {@link Map} whose keys are {@link Event}
     * id and values are quantity or amount of winning bets for corresponding {@link Event} id.
     */
    @Override
    public Map<String, Map<String, String>> getWinBetInfo(int categoryId) {
        Map<String, Map<String, String>> winBetInfoMap = null;
        try {
            winBetInfoMap = betDAO.readWinBetInfoMap(categoryId);
        } catch (DAOException e) {
            logger.log(Level.ERROR, e.getMessage());
        }
        return winBetInfoMap;
    }
    
    /**
     * Calls DAO layer to pay winning bets for given {@link Event} id and update balance
     * of player whose bet won.
     *
     * @param eventId        {@link Event} id
     * @param messageService {@link MessageService} that holds messages, formed during execution of
     *                       method
     */
    @Override
    public void payWinBet(int eventId, MessageService messageService) {
        Set<Bet> winBetSet = getBetSetForEventAndStatus(eventId, Bet.Status.WIN);
        if ((winBetSet != null) && !winBetSet.isEmpty()) {
            try {
                daoProvider.beginTransaction();
                boolean isTransactionOk = true;
                for (Bet bet : winBetSet) {
                    int playerId = bet.getPlayerId();
                    BigDecimal amount = bet.getAmount();
                    BigDecimal coefficient = bet.getCoefficient();
                    BigDecimal winning = amount.multiply(coefficient);
                    Transaction.TransactionType transactionType
                        = Transaction.TransactionType.REPLENISH;
                    int transactId = transactionDAO.insertTransaction(playerId, winning,
                                                                      transactionType);
                    boolean isBalanceUpd = playerDAO.updateBalance(playerId, winning,
                                                                   transactionType);
                    if (!isBalanceUpd || (transactId == 0)) {
                        isTransactionOk = false;
                    }
                }
                int betUpdCount = betDAO.updateBetStatus(eventId, Bet.Status.WIN, Bet.Status.PAID);
                if ((betUpdCount == winBetSet.size()) && isTransactionOk) {
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
    }
    
    /**
     * Calls DAO layer to place information about given {@link Bet} in database and update balance
     * of player who made bet.
     *
     * @param bet            {@link Bet} bet
     * @param messageService {@link MessageService} that holds messages, formed during execution of
     *                       method
     */
    @Override
    public void makeBet(Bet bet, MessageService messageService) {
        try {
            daoProvider.beginTransaction();
            boolean isBetIns = betDAO.insertBet(bet);
            boolean isBalUpd = playerDAO.updateBalance(bet.getPlayerId(), bet.getAmount(),
                                                       Transaction.TransactionType.WITHDRAW);
            if (isBetIns && isBalUpd) {
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
     * Calls DAO layer count bets in database for given {@link Player} id.
     *
     * @param playerId {@link Player} id
     * @return quantity
     */
    @Override
    public int countBetsForPlayer(int playerId) {
        int count = 0;
        try {
            count = betDAO.countBetForPlayer(playerId);
        } catch (DAOException e) {
            logger.log(Level.ERROR, e.getMessage());
        }
        return count;
    }
    
}
