package com.dubatovka.app.dao.impl;

import com.dubatovka.app.dao.BetDAO;
import com.dubatovka.app.dao.CategoryDAO;
import com.dubatovka.app.dao.EventDAO;
import com.dubatovka.app.dao.OutcomeDAO;
import com.dubatovka.app.dao.PlayerDAO;
import com.dubatovka.app.dao.TransactionDAO;
import com.dubatovka.app.dao.UserDAO;
import com.dubatovka.app.dao.db.ConnectionPool;
import com.dubatovka.app.dao.db.ConnectionPoolException;
import com.dubatovka.app.dao.db.WrappedConnection;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;

/**
 * The class provides access to DAO layer classes implementations and provides methods for SQL
 * transactions management. Every instance of {@link DAOProvider} has property of {@link
 * WrappedConnection} instance that is used for initialization of every instance of DAO layer
 * classes.
 *
 * @author Dubatovka Vadim
 */
public final class DAOProvider implements AutoCloseable {
    private static final Logger            logger = LogManager.getLogger(DAOProvider.class);
    /**
     * Field used to connect to database and do queries.
     *
     * @see WrappedConnection
     */
    private              WrappedConnection connection;
    
    private BetDAO         betDAO;
    private CategoryDAO    categoryDAO;
    private EventDAO       eventDAO;
    private OutcomeDAO     outcomeDAO;
    private PlayerDAO      playerDAO;
    private TransactionDAO transactionDAO;
    private UserDAO        userDAO;
    
    /**
     * Constructs DAOProvider object by taking {@link WrappedConnection} object from {@link
     * ConnectionPool} collection.
     */
    public DAOProvider() {
        try {
            connection = ConnectionPool.getInstance().takeConnection();
        } catch (ConnectionPoolException e) {
            logger.log(Level.ERROR, "Database connection error.");
        }
    }
    
    /**
     * Constructs DAOProvider object by assigning {@link DAOProvider#connection} field definite
     * {@link WrappedConnection} object.
     *
     * @param connection {@link WrappedConnection} to assign to {@link DAOProvider#connection}
     *                   field
     */
    public DAOProvider(WrappedConnection connection) {
        this.connection = connection;
    }
    
    /**
     * Factory method to create {@link BetDAO} object.
     *
     * @return {@link BetDAO} object initialized with {@link DAOProvider#connection} field.
     */
    public BetDAO getBetDAO() {
        if (betDAO == null) {
            betDAO = new BetDAOImpl(connection);
        }
        return betDAO;
    }
    
    /**
     * Factory method to create {@link CategoryDAO} object.
     *
     * @return {@link CategoryDAO} object initialized with {@link DAOProvider#connection} field.
     */
    public CategoryDAO getCategoryDAO() {
        if (categoryDAO == null) {
            categoryDAO = new CategoryDAOImpl(connection);
        }
        return categoryDAO;
    }
    
    /**
     * Factory method to create {@link EventDAO} object.
     *
     * @return {@link EventDAO} object initialized with {@link DAOProvider#connection} field.
     */
    public EventDAO getEventDAO() {
        if (eventDAO == null) {
            eventDAO = new EventDAOImpl(connection);
        }
        return eventDAO;
    }
    
    /**
     * Factory method to create {@link OutcomeDAO} object.
     *
     * @return {@link OutcomeDAO} object initialized with {@link DAOProvider#connection} field.
     */
    public OutcomeDAO getOutcomeDAO() {
        if (outcomeDAO == null) {
            outcomeDAO = new OutcomeDAOImpl(connection);
        }
        return outcomeDAO;
    }
    
    /**
     * Factory method to create {@link PlayerDAO} object.
     *
     * @return {@link PlayerDAO} object initialized with {@link DAOProvider#connection} field.
     */
    public PlayerDAO getPlayerDAO() {
        if (playerDAO == null) {
            playerDAO = new PlayerDAOImpl(connection);
        }
        return playerDAO;
    }
    
    /**
     * Factory method to create {@link TransactionDAO} object.
     *
     * @return {@link TransactionDAO} object initialized with {@link DAOProvider#connection} field.
     */
    public TransactionDAO getTransactionDAO() {
        if (transactionDAO == null) {
            transactionDAO = new TransactionDAOImpl(connection);
        }
        return transactionDAO;
    }
    
    /**
     * Factory method to create {@link UserDAO} object.
     *
     * @return {@link UserDAO} object initialized with {@link DAOProvider#connection} field.
     */
    public UserDAO getUserDAO() {
        if (userDAO == null) {
            userDAO = new UserDAOImpl(connection);
        }
        return userDAO;
    }
    
    /**
     * Starts transaction for multiple SQL queries.
     *
     * @throws SQLException if a database access error occurs, setAutoCommit(true) is called while
     *                      participating in a distributed transaction, or this method is called on
     *                      a closed connection
     */
    public void beginTransaction() throws SQLException {
        connection.setAutoCommit(false);
    }
    
    /**
     * Commits database changes made during transaction.
     *
     * @throws SQLException if a database access error occurs, this method is called while
     *                      participating in a distributed transaction, wrapped connection is closed
     *                      or wrapped {@code Connection} object is in auto-commit mode
     */
    public void commit() throws SQLException {
        connection.commit();
    }
    
    /**
     * Rollbacks database changes made during transaction.
     *
     * @throws SQLException if a database access error occurs, this method is called while
     *                      participating in a distributed transaction, this method is called on a
     *                      closed connection or this {@code Connection} object is in auto-commit
     *                      mode
     */
    public void rollback() throws SQLException {
        connection.rollback();
        connection.setAutoCommit(true);
    }
    
    /**
     * Returns {@link #connection} to {@link ConnectionPool}.
     */
    @Override
    public void close() {
        ConnectionPool.getInstance().returnConnection(connection);
        connection = null;
    }
}