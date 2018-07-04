package com.dubatovka.app.dao.impl;

import com.dubatovka.app.dao.TransactionDAO;
import com.dubatovka.app.dao.db.ConnectionPool;
import com.dubatovka.app.dao.db.WrappedConnection;
import com.dubatovka.app.dao.exception.DAOException;
import com.dubatovka.app.entity.Transaction;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Class provides {@link TransactionDAO} implementation for MySQL database.
 *
 * @author Dubatovka Vadim
 */
class TransactionDAOImpl extends DBConnectionHolder implements TransactionDAO {
    /**
     * Selects definite player transactions and orders them by date in descending order.
     */
    private static final String SQL_SELECT_BY_PLAYER_ID =
        "SELECT id, player_id, date, amount FROM transaction " +
            "WHERE player_id=? ORDER BY date DESC";
    /**
     * Selects definite player transactions where date is like definite pattern and orders them by
     * date in descending order.
     */
    private static final String SQL_SELECT_PLAYER_LIKE_MONTH =
        "SELECT id, player_id, date, amount FROM transaction " +
            "WHERE player_id=? AND date LIKE ? ORDER BY date DESC";
    /**
     * Selects transactions where date is like definite pattern and orders them by date in
     * descending order.
     */
    private static final String SQL_SELECT_LIKE_MONTH =
        "SELECT id, player_id, date, amount FROM transaction " +
            "WHERE date LIKE ? ORDER BY date DESC";
    /**
     * Inserts transaction to database.
     */
    private static final String SQL_INSERT_TRANSACTION =
        "INSERT INTO transaction (player_id, date, amount) VALUES (?, NOW(), ?)";
    
    /**
     * Constructs DAO object by taking {@link WrappedConnection} object from {@link ConnectionPool}.
     */
    TransactionDAOImpl() {
    }
    
    
    /**
     * Constructs DAO object by assigning {@link DBConnectionHolder#connection} field definite
     * {@link WrappedConnection} object.
     *
     * @param connection {@link WrappedConnection} to assign to {@link DBConnectionHolder#connection}
     *                   field
     */
    TransactionDAOImpl(WrappedConnection connection) {
        super(connection);
    }
    
    /**
     * Takes {@link List} filled by definite player {@link Transaction} objects.
     *
     * @param playerId id of player whose transactions to take
     * @return {@link List} filled by definite player {@link Transaction} objects
     * @throws DAOException if {@link SQLException} occurred while working with database
     */
    @Override
    public List<Transaction> takePlayerTransactions(int playerId) throws DAOException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_PLAYER_ID)) {
            statement.setInt(1, playerId);
            ResultSet resultSet = statement.executeQuery();
            return buildTransactionList(resultSet);
        } catch (SQLException e) {
            throw new DAOException("Database connection error while taking transactions. " + e);
        }
    }
    
    
    /**
     * Takes {@link List} filled by definite player {@link Transaction} objects due to definite
     * transaction date pattern.
     *
     * @param playerId     id of player whose transactions to take
     * @param monthPattern pattern of transaction date conforming to <code>SQL LIKE</code> operator
     * @return {@link List} filled by definite player {@link Transaction} objects
     * @throws DAOException if {@link SQLException} occurred while working with database
     */
    @Override
    public List<Transaction> takePlayerTransactions(int playerId, String monthPattern)
        throws DAOException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT_PLAYER_LIKE_MONTH)) {
            statement.setInt(1, playerId);
            statement.setString(2, monthPattern);
            ResultSet resultSet = statement.executeQuery();
            return buildTransactionList(resultSet);
        } catch (SQLException e) {
            throw new DAOException("Database connection error while taking transactions. " + e);
        }
    }
    
    /**
     * Takes {@link List} filled by {@link Transaction} objects due to definite transaction date
     * pattern.
     *
     * @param monthPattern pattern of transaction date conforming to <code>SQL LIKE</code> operator
     * @return {@link List} filled by {@link Transaction} objects
     * @throws DAOException if {@link SQLException} occurred while working with database
     */
    @Override
    public List<Transaction> takeTransactionList(String monthPattern) throws DAOException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT_LIKE_MONTH)) {
            statement.setString(1, monthPattern);
            ResultSet resultSet = statement.executeQuery();
            return buildTransactionList(resultSet);
        } catch (SQLException e) {
            throw new DAOException("Database connection error while taking transactions. " + e);
        }
    }
    
    /**
     * Inserts {@link Transaction} to database.
     *
     * @param playerId id of player who does transaction
     * @param amount   amount of money transferred by transaction
     * @param type     {@link Transaction.TransactionType} of inserting transaction
     * @return int value of inserted transaction generated id
     * @throws DAOException if {@link SQLException} occurred while working with database
     */
    @Override
    public int insertTransaction(int playerId, BigDecimal amount, Transaction.TransactionType type)
        throws DAOException {
        if (type == Transaction.TransactionType.WITHDRAW) {
            amount = amount.negate();
        }
        try (PreparedStatement statement = connection.prepareStatement(SQL_INSERT_TRANSACTION,
                                                                       Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, playerId);
            statement.setBigDecimal(2, amount);
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            return resultSet.next() ? resultSet.getInt(1) : 0;
        } catch (SQLException e) {
            throw new DAOException("Database connection error while inserting transaction. " + e);
        }
    }
    
    /**
     * Method builds {@link Transaction} from given ResultSet
     *
     * @param resultSet {@link ResultSet}
     * @return {@link Transaction}
     * @throws SQLException if the columnLabel is not valid; if a database access error occurs or
     *                      this method is called on a closed result set
     */
    private static Transaction buildTransaction(ResultSet resultSet) throws SQLException {
        Transaction transaction = null;
        if (resultSet.next()) {
            transaction = new Transaction();
            transaction.setId(resultSet.getInt(ID));
            transaction.setPlayerId(resultSet.getInt(PLAYER_ID));
            transaction.setDate(resultSet.getTimestamp(DATE).toLocalDateTime());
            BigDecimal amount = resultSet.getBigDecimal(AMOUNT);
            Transaction.TransactionType transactionType = (amount.signum() == -1)
                                                              ? Transaction.TransactionType.WITHDRAW
                                                              : Transaction.TransactionType.REPLENISH;
            transaction.setType(transactionType);
            transaction.setAmount(amount.abs());
        }
        return transaction;
    }
    
    /**
     * Builds {@link List} of {@link Transaction} from given {@link ResultSet}.
     *
     * @param resultSet {@link ResultSet}
     * @return {@link List} of {@link Transaction}
     * @throws SQLException if a database access error occurs or this method is called on a closed
     *                      result set
     */
    private static List<Transaction> buildTransactionList(ResultSet resultSet)
        throws SQLException {
        List<Transaction> transactionList = new ArrayList<>();
        Transaction transaction;
        do {
            transaction = buildTransaction(resultSet);
            if (transaction != null) {
                transactionList.add(transaction);
            }
        } while (transaction != null);
        return transactionList.isEmpty() ? null : transactionList;
    }
}