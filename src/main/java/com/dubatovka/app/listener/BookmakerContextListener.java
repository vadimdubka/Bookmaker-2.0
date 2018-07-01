package com.dubatovka.app.listener;

import com.dubatovka.app.dao.db.ConnectionPool;
import com.dubatovka.app.dao.db.ConnectionPoolException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * The class provides listener of servlet container initialization and destruction.
 */
@WebListener
public class BookmakerContextListener implements ServletContextListener {
    private static final Logger logger = LogManager.getLogger(BookmakerContextListener.class);
    
    /**
     * Connection pool instance for managing.
     */
    private ConnectionPool pool;
    
    /**
     * Initializes {@link ConnectionPool}.
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            pool = ConnectionPool.getInstance();
            int createdConnectionsNumber = pool.initPool();
            logger.log(Level.INFO, "ConnectionPool was initialized with "
                                       + createdConnectionsNumber + " connections.");
        } catch (ConnectionPoolException e) {
            logger.log(Level.FATAL, e);
            throw new RuntimeException(e);
        }
    }
    
    /**
     * <p>Receives notification that the ServletContext is about to be
     * shut down.
     * <p>All servlets and filters will have been destroyed before any
     * ServletContextListeners are notified of context
     * destruction.
     * <p>Destroys {@link ConnectionPool} and calls {@link LogManager#shutdown()}.
     *
     * @param event the ServletContextEvent containing the ServletContext that is being destroyed
     */
    @Override
    public void contextDestroyed(ServletContextEvent event) {
        int closedConnectionsNumber = pool.destroyPool();
        logger.log(Level.INFO, "ConnectionPool was destroyed. "
                                   + closedConnectionsNumber + " connections was closed.");
        LogManager.shutdown();
    }
}