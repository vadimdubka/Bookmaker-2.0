package com.dubatovka.app.dao.db;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Class provides singleton container for {@link WrappedConnection} objects.
 */
public final class ConnectionPool {
    private static final Logger logger = LogManager.getLogger(ConnectionPool.class);
    
    /**
     * Database property-file keys.
     */
    private static final String DB_DRIVER   = "driver";
    private static final String DB_USER     = "user";
    private static final String DB_PASSWORD = "password";
    private static final String DB_URL      = "url";
    private static final String POOL_SIZE   = "poolsize";
    
    /**
     * Seconds for checking if connection is valid
     */
    private static final int VALIDATION_TIMEOUT = 1;
    
    /**
     * Marker to check if {@link #instance} is created.
     */
    private static final AtomicBoolean created = new AtomicBoolean(false);
    /**
     * Class lock for {@link #getInstance()} method.
     */
    private static final Lock          lock    = new ReentrantLock();
    
    /**
     * Class singleton instance.
     */
    private static ConnectionPool instance;
    
    /**
     * Size of {@link #connections} to initialize and destroy pool.
     */
    private int poolSize;
    
    /**
     * Database property-file values.
     */
    private String driver;
    private String url;
    private String user;
    private String password;
    private String poolSizeStr;
    
    /**
     * Collection of {@link WrappedConnection} objects.
     */
    private ArrayBlockingQueue<WrappedConnection> connections;
    
    /**
     * Registers MySQL JDBC driver while constructing {@link #instance}.
     *
     * @throws RuntimeException if {@link SQLException} occurred during registering driver
     */
    private ConnectionPool() {
        try {
            DriverManager.registerDriver(new org.postgresql.Driver());
        } catch (SQLException e) {
            logger.log(Level.FATAL, e + " DriverManager wasn't found.");
            throw new RuntimeException(e);
        }
        
        /*try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            logger.log(Level.FATAL, e + " DriverManager wasn't found.");
            throw new RuntimeException(e);
        }*/
    }
    
    /**
     * Singleton getter. Locks for first access.
     *
     * @return {@link #instance}
     */
    public static ConnectionPool getInstance() {
        if (!created.get()) {
            lock.lock();
            try {
                if (instance == null) {
                    instance = new ConnectionPool();
                    created.set(true);
                }
            } finally {
                lock.unlock();
            }
        }
        return instance;
    }
    
    /**
     * Initializes pool due to given config data.
     *
     * @return number of created connections
     * @throws ConnectionPoolException if {@link InterruptedException} occurred while putting {@link
     *                                 WrappedConnection} to {@link #connections}
     */
    public int initPool(String properties) throws ConnectionPoolException {
        ResourceBundle resourceBundle;
        try {
            resourceBundle = ResourceBundle.getBundle(properties);
        } catch (MissingResourceException e) {
            logger.log(Level.ERROR, "Invalid resource path to database *.properties file");
            throw new RuntimeException(e.getMessage());
        }
        driver = resourceBundle.getString(DB_DRIVER);
        user = resourceBundle.getString(DB_USER);
        password = resourceBundle.getString(DB_PASSWORD);
        url = resourceBundle.getString(DB_URL);
        poolSizeStr = resourceBundle.getString(POOL_SIZE);
        
        poolSize = Integer.parseInt(poolSizeStr);
        connections = new ArrayBlockingQueue<>(poolSize);
        for (int i = 0; i < poolSize; i++) {
            try {
                WrappedConnection connection = new WrappedConnection(url, user, password);
                connections.put(connection);
            } catch (InterruptedException e) {
                throw new ConnectionPoolException("ConnectionPool initializing was interrupted.", e);
            }
        }
        return connections.size();
    }
    
    /**
     * Takes {@link WrappedConnection} from {@link #connections} collection.
     *
     * @return taken {@link WrappedConnection}
     * @throws ConnectionPoolException if {@link InterruptedException} occurred while taking {@link
     *                                 WrappedConnection} from {@link #connections}
     */
    public WrappedConnection takeConnection() throws ConnectionPoolException {
        WrappedConnection connection;
        try {
            connection = connections.take();
            for (int i = 0; i < poolSize; i++) {
                if (isDamaged(connection)) {
                    replaceDamagedConnection(connection);
                    connection = connections.take();
                } else {
                    return connection;
                }
            }
            if (isDamaged(connection)) {
                returnConnection(connection);
                throw new ConnectionPoolException("No valid connections to take. Database denies access.");
            }
        } catch (InterruptedException | SQLException e) {
            throw new ConnectionPoolException("Taking-connection process was interrupted.", e);
        }
        return connection;
    }
    
    /**
     * Returns {@link WrappedConnection} to connections collection. Rollbacks any transaction and
     * sets auto-commit of connection to true.
     *
     * @param connection {@link WrappedConnection} to return
     */
    public void returnConnection(WrappedConnection connection) {
        try {
            if (connection == null) {
                logger.log(Level.WARN, "Can't return null connection reference to pool.");
                return;
            }
            if (isDamaged(connection)) {
                replaceDamagedConnection(connection);
                return;
            }
            try {
                if (!connection.getAutoCommit()) {
                    connection.rollback();
                    connection.setAutoCommit(true);
                }
                logger.log(Level.DEBUG, "Connection was returned to pool. Current pool size: "
                                            + connections.size());
                connections.put(connection);
            } catch (SQLException e) {
                logger.log(Level.ERROR, "Exception while setting autoCommit to connection. "
                                            + e.getMessage());
            }
        } catch (SQLException e) {
            logger.log(Level.ERROR, "Database access error occurred.", e);
        } catch (InterruptedException e) {
            logger.log(Level.ERROR, "Putting connection back into pool was interrupted. "
                                        + e.getMessage());
        }
    }
    
    /**
     * Destroys pool.
     *
     * @return number of closed connections
     */
    public int destroyPool() {
        int counter = 0;
        for (int i = 0; i < poolSize; i++) {
            try {
                WrappedConnection connection = connections.poll(10, TimeUnit.SECONDS);
                if (connection != null) {
                    connection.closeConnection();
                    counter++;
                }
            } catch (SQLException e) {
                logger.log(Level.ERROR, "Database access error occurred.", e);
            } catch (InterruptedException e) {
                logger.log(Level.ERROR,
                           "Taking connection from deque to close and destroy pool was interrupted.", e);
            }
        }
        try {
            Enumeration<Driver> drivers = DriverManager.getDrivers();
            while (drivers.hasMoreElements()) {
                Driver driver = drivers.nextElement();
                DriverManager.deregisterDriver(driver);
            }
        } catch (SQLException e) {
            logger.log(Level.ERROR, e + " DriverManager wasn't found.");
        } finally {
            instance = null;
            created.getAndSet(false);
        }
        return counter;
    }
    
    /**
     * Tries to replace damaged connection.
     *
     * @see WrappedConnection
     */
    private void replaceDamagedConnection(WrappedConnection connection) throws InterruptedException {
        WrappedConnection createdConnection = createWrappedConnection();
        if (createdConnection != null) {
            connections.put(createdConnection);
            logger.log(Level.ERROR,
                       "Connection was damaged and lost while returning but was replaced by a new one.");
        } else {
            connections.put(connection);
            logger.log(Level.ERROR,
                       "Connection was damaged and can't be replaced by a new one, database denies access.");
        }
    }
    
    /**
     * Tries to creates {@link WrappedConnection}.
     *
     * @return {@link WrappedConnection}
     */
    private WrappedConnection createWrappedConnection() {
        WrappedConnection createdConnection = null;
        try {
            createdConnection = new WrappedConnection(url, user, password);
        } catch (ConnectionPoolException e) {
            logger.log(Level.ERROR, e.getMessage());
        }
        return createdConnection;
    }
    
    /**
     * Validates {@link WrappedConnection}.
     *
     * @param connection {@link WrappedConnection} connection for validation
     * @return {@link true} if connection is null, closed or invalid
     * @throws SQLException if a database access error occurs; if the value supplied for timeout is
     *                      less then 0
     */
    private static boolean isDamaged(WrappedConnection connection) throws SQLException {
        return connection.isNull() || connection.isClosed()
                   || !connection.isValid(VALIDATION_TIMEOUT);
    }
}