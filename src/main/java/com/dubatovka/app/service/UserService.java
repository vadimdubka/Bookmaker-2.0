package com.dubatovka.app.service;

import com.dubatovka.app.dao.impl.DAOProvider;
import com.dubatovka.app.entity.User;

/**
 * The class provides abstraction for Service layer actions with User.
 *
 * @author Dubatovka Vadim
 */
public abstract class UserService extends DAOProviderHolder {
    /**
     * Default constructor.
     */
    protected UserService() {
    }
    
    /**
     * Constructs instance using definite {@link DAOProvider} object.
     */
    protected UserService(DAOProvider daoProvider) {
        super(daoProvider);
    }
    
    /**
     * Provides authorisation operation service for user. Calls DAO layer to init {@link User}
     * object due to given parameters.
     *
     * @param email    user e-mail
     * @param password user password
     * @return initialized {@link User} object
     */
    public abstract User authorizeUser(String email, String password);
}
