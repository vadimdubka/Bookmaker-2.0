package com.dubatovka.app.service;

import com.dubatovka.app.entity.User;

/**
 * The class provides abstraction for Service layer actions with User.
 *
 * @author Dubatovka Vadim
 */
public interface UserService extends AutoCloseable {
    /**
     * Provides authorisation operation service for user. Calls DAO layer to init {@link User}
     * object due to given parameters.
     *
     * @param email    user e-mail
     * @param password user password
     * @return initialized {@link User} object
     */
    User authorizeUser(String email, String password);
    
    @Override
    void close();
}
