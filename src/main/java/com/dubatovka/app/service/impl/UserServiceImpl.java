package com.dubatovka.app.service.impl;

import com.dubatovka.app.dao.UserDAO;
import com.dubatovka.app.dao.db.ConnectionPool;
import com.dubatovka.app.dao.exception.DAOException;
import com.dubatovka.app.dao.impl.DAOProvider;
import com.dubatovka.app.entity.Admin;
import com.dubatovka.app.entity.Analyst;
import com.dubatovka.app.entity.Player;
import com.dubatovka.app.entity.User;
import com.dubatovka.app.service.EncryptionService;
import com.dubatovka.app.service.UserService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The class provides implementation for Service layer actions with Users.
 *
 * @author Dubatovka Vadim
 */
class UserServiceImpl implements UserService {
    private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);
    /**
     * DAOProvider instance for this class instance use.
     */
    private final DAOProvider daoProvider;
    private final UserDAO userDAO;
    @Autowired
    private EncryptionService encryptionService;
    
    /**
     * Default instance constructor.
     */
    UserServiceImpl() {
        this.daoProvider = new DAOProvider();
        this.userDAO = daoProvider.getUserDAO();
    }
    
    /**
     * Provides authorisation operation service for user. Calls DAO layer to init {@link User}
     * object due to given parameters.
     *
     * @param email    user e-mail
     * @param password user password
     * @return initialized {@link User} object
     */
    @Override
    public User authorizeUser(String email, String password) {
        email = email.toLowerCase().trim();
        password = encryptionService.encryptMD5(password);
        User user = null;
        try {
            user = userDAO.readUser(email, password);
        } catch (DAOException e) {
            logger.log(Level.ERROR, e.getMessage());
        }
        if (user != null) {
            User.UserRole role = user.getRole();
            if (role == User.UserRole.PLAYER) {
                user = new Player(user);
            } else if (role == User.UserRole.ADMIN) {
                user = new Admin(user);
            } else {
                user = new Analyst(user);
            }
        }
        return user;
    }
    
    /**
     * Returns {@link DAOProvider#connection} to {@link ConnectionPool}.
     */
    @Override
    public void close() {
        daoProvider.close();
    }
}