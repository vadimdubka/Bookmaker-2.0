package com.dubatovka.app.dao.impl;

import com.dubatovka.app.dao.UserDAO;
import com.dubatovka.app.dao.db.ConnectionPool;
import com.dubatovka.app.dao.db.WrappedConnection;
import com.dubatovka.app.dao.exception.DAOException;
import com.dubatovka.app.entity.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

/**
 * Class provides {@link UserDAO} implementation for MySQL database.
 *
 * @author Dubatovka Vadim
 */
class UserDAOImpl extends DBConnectionHolder implements UserDAO {
    private static final String SQL_INSERT_USER =
        "INSERT INTO user (email, password, role, registration_date) " +
            "VALUES (?, ?, 'player', NOW())";
    
    private static final String SQL_AUTH =
        "SELECT id, email, role, registration_date  FROM user " +
            "WHERE email=? AND password=?";
    
    /**
     * Constructs DAO object by taking {@link WrappedConnection} object from {@link ConnectionPool}.
     */
    UserDAOImpl() {
    }
    
    /**
     * Constructs DAO object by assigning {@link DBConnectionHolder#connection} field definite
     * {@link WrappedConnection} object.
     *
     * @param connection {@link WrappedConnection} to assign to {@link DBConnectionHolder#connection}
     *                   field
     */
    UserDAOImpl(WrappedConnection connection) {
        super(connection);
    }
    
    /**
     * Inserts {@link User} to database.
     *
     * @param email    {@link User} email
     * @param password {{@link User} password
     * @return id of inserted User
     * @throws DAOException when {@link SQLException} occurred while working with database
     */
    @Override
    public int insertUser(String email, String password) throws DAOException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_INSERT_USER,
                                                                       Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, email);
            statement.setString(2, password);
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            return resultSet.next() ? resultSet.getInt(1) : 0;
        } catch (SQLException e) {
            throw new DAOException("Database connection error while inserting user player. " + e);
        }
    }
    
    /**
     * Reads {@link User} by its e-mail and password encrypted by MD5 encryptor.
     *
     * @param email    user e-mail
     * @param password user password encrypted by MD5 encryptor
     * @return taken {@link User}
     * @throws DAOException if {@link SQLException} occurred while working with database
     */
    @Override
    public User readUser(String email, String password) throws DAOException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_AUTH)) {
            statement.setString(1, email);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            return buildUser(resultSet);
        } catch (SQLException e) {
            throw new DAOException("Database connection error while authorizing user. " + e);
        }
    }
    
    /**
     * Method builds {@link User} from given ResultSet
     *
     * @param resultSet {@link ResultSet}
     * @return {@link User}
     * @throws SQLException if the columnLabel is not valid; if a database access error occurs or
     *                      this method is called on a closed result set
     */
    private static User buildUser(ResultSet resultSet) throws SQLException {
        User user = null;
        if (resultSet.next()) {
            user = new User();
            user.setId(resultSet.getInt(ID));
            user.setEmail(resultSet.getString(EMAIL));
            String role = resultSet.getString(ROLE).toUpperCase();
            user.setRole(User.UserRole.valueOf(role));
            LocalDate registrationDate = resultSet.getDate(REGISTRATION_DATE).toLocalDate();
            user.setRegistrationDate(registrationDate);
        }
        return user;
    }
}