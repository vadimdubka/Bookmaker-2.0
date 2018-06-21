package com.dubatovka.app.dao;

import com.dubatovka.app.dao.exception.DAOException;
import com.dubatovka.app.entity.Transaction;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO interface for {@link Transaction} objects.
 *
 * @author Dubatovka Vadim
 */
public interface TransactionDAO {
    /**
     * Column names of database table 'transaction'.
     */
    String ID        = "id";
    String PLAYER_ID = "player_id";
    String DATE      = "date";
    String AMOUNT    = "amount";
    
    /**
     * Takes {@link List} filled by definite player {@link Transaction} objects.
     *
     * @param playerId id of player whose transactions to take
     * @return {@link List} filled by definite player {@link Transaction} objects
     * @throws DAOException if {@link SQLException} occurred while working with database
     */
    List<Transaction> takePlayerTransactions(int playerId) throws DAOException;
    
    /**
     * Takes {@link List} filled by definite player {@link Transaction} objects due to definite
     * transaction date pattern.
     *
     * @param playerId     id of player whose transactions to take
     * @param monthPattern pattern of transaction date conforming to <code>SQL LIKE</code> operator
     * @return {@link List} filled by definite player {@link Transaction} objects
     * @throws DAOException if {@link SQLException} occurred while working with database
     */
    List<Transaction> takePlayerTransactions(int playerId, String monthPattern)
        throws DAOException;
    
    /**
     * Takes {@link List} filled by {@link Transaction} objects due to definite transaction date
     * pattern.
     *
     * @param monthPattern pattern of transaction date conforming to <code>SQL LIKE</code> operator
     * @return {@link List} filled by {@link Transaction} objects
     * @throws DAOException if {@link SQLException} occurred while working with database
     */
    List<Transaction> takeTransactionList(String monthPattern) throws DAOException;
    
    /**
     * Inserts {@link Transaction} to database.
     *
     * @param playerId id of player who does transaction
     * @param amount   amount of money transferred by transaction
     * @param type     {@link Transaction.TransactionType} of inserting transaction
     * @return int value of inserted transaction generated id
     * @throws DAOException if {@link SQLException} occurred while working with database
     */
    int insertTransaction(int playerId, BigDecimal amount, Transaction.TransactionType type)
        throws DAOException;
    
}
