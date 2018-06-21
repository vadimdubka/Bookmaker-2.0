package com.dubatovka.app.service;

import com.dubatovka.app.dao.db.ConnectionPool;
import com.dubatovka.app.dao.impl.DAOProvider;

/**
 * The class provides root abstraction for Service layer classes which use {@link DAOProvider} to
 * communicate with DAO layer.
 *
 * @author Dubatovka Vadim
 */
abstract class DAOProviderHolder implements AutoCloseable {
    
    /**
     * DAOProvider instance for this class instance use.
     */
    protected DAOProvider daoProvider;
    
    /**
     * Default constructor.
     */
    DAOProviderHolder() {
        daoProvider = new DAOProvider();
    }
    
    /**
     * Constructs instance using definite {@link DAOProvider} object.
     */
    DAOProviderHolder(DAOProvider daoProvider) {
        this.daoProvider = daoProvider;
    }
    
    /**
     * Returns {@link DAOProvider#connection} to {@link ConnectionPool}.
     */
    @Override
    public void close() {
        daoProvider.close();
    }
}