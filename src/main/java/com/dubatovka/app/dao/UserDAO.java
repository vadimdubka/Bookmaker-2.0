package com.dubatovka.app.dao;


import com.dubatovka.app.dao.exception.DAOException;
import com.dubatovka.app.entity.User;

import java.sql.SQLException;

/**
 * DAO interface for {@link User} objects.
 *
 * @author Dubatovka Vadim
 */
public interface UserDAO {
    
    /**
     * Column names of database table 'user'.
     */
    String ID                = "id";
    String EMAIL             = "email";
    String PASSWORD          = "password";
    String ROLE              = "role";
    String REGISTRATION_DATE = "registration_date";
    
    /**
     * Inserts {@link User} to database.
     *
     * @param email    {@link User} email
     * @param password {{@link User} password
     * @return id of inserted User
     * @throws DAOException when {@link SQLException} occurred while working with database
     */
    int insertUser(String email, String password) throws DAOException;
    
    /**
     * Reads {@link User} by its e-mail and password encrypted by MD5 encryptor.
     *
     * @param email    user e-mail
     * @param password user password encrypted by MD5 encryptor
     * @return taken {@link User}
     * @throws DAOException if {@link SQLException} occurred while working with database
     */
    User readUser(String email, String password) throws DAOException;
    
}