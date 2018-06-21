package com.dubatovka.app.dao.impl;

import com.dubatovka.app.dao.OutcomeDAO;
import com.dubatovka.app.dao.db.ConnectionPool;
import com.dubatovka.app.dao.db.WrappedConnection;
import com.dubatovka.app.dao.exception.DAOException;
import com.dubatovka.app.entity.Event;
import com.dubatovka.app.entity.Outcome;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import static com.dubatovka.app.config.ConfigConstant.PREFIX_FOR_OUTCOME_TYPE;

/**
 * Class provides {@link OutcomeDAO} implementation for MySQL database.
 *
 * @author Dubatovka Vadim
 */
class OutcomeDAOImpl extends DBConnectionHolder implements OutcomeDAO {
    private static final String SQL_SELECT_OUTCOMES_BY_EVENT_ID =
        "SELECT event_id, type, coefficient " +
            "FROM outcome WHERE event_id = ?";
    
    private static final String SQL_INSERT_OUTCOME =
        "INSERT INTO outcome (event_id, type, coefficient) " +
            "VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE coefficient = ?";
    
    
    /**
     * Constructs DAO object by taking {@link WrappedConnection} object from {@link ConnectionPool}.
     */
    OutcomeDAOImpl() {
    }
    
    /**
     * Constructs DAO object by assigning {@link DBConnectionHolder#connection} field definite
     * {@link WrappedConnection} object.
     *
     * @param connection {@link WrappedConnection} to assign to {@link DBConnectionHolder#connection}
     *                   field
     */
    OutcomeDAOImpl(WrappedConnection connection) {
        super(connection);
    }
    
    /**
     * Reads {@link Set} of {@link Outcome} from database which correspond to given {@link Event}
     * id.
     *
     * @param id {@link Event} id
     * @return {@link Set} of {@link Outcome}
     * @throws DAOException when {@link SQLException} occurred while working with database
     */
    @Override
    public Set<Outcome> readOutcomesByEventId(int id) throws DAOException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT_OUTCOMES_BY_EVENT_ID)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            return buildOutcomeSet(resultSet);
        } catch (SQLException e) {
            throw new DAOException("Database connection error while reading outcome. " + e);
        }
    }
    
    /**
     * Inserts {@link Outcome} to database.
     *
     * @param outcome {@link Outcome} for insertion
     * @return true if operation processed successfully
     * @throws DAOException when {@link SQLException} occurred while working with database
     */
    @Override
    public boolean insertOutcome(Outcome outcome) throws DAOException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_INSERT_OUTCOME)) {
            statement.setInt(1, outcome.getEventId());
            statement.setString(2, outcome.getType().getType());
            statement.setBigDecimal(3, outcome.getCoefficient());
            statement.setBigDecimal(4, outcome.getCoefficient());
            int rowUpd = statement.executeUpdate();
            return rowUpd > 0;
        } catch (SQLException e) {
            throw new DAOException("Database connection error while inserting outcome. " + e);
        }
    }
    
    /**
     * Builds {@link Set} of {@link Outcome} from given {@link ResultSet}.
     *
     * @param resultSet {@link ResultSet}
     * @return {@link Set} of {@link Outcome}
     * @throws SQLException if the columnLabel is not valid; if a database access error occurs or
     *                      this method is called on a closed result set
     */
    private static Set<Outcome> buildOutcomeSet(ResultSet resultSet) throws SQLException {
        Set<Outcome> outcomeSet = new HashSet<>();
        while (resultSet.next()) {
            Outcome outcome = new Outcome();
            outcome.setEventId(resultSet.getInt(EVENT_ID));
            outcome.setCoefficient(resultSet.getBigDecimal(COEFFICIENT));
            outcome.setType(Outcome.Type.valueOf(PREFIX_FOR_OUTCOME_TYPE + resultSet.getString(TYPE)));
            outcomeSet.add(outcome);
        }
        return outcomeSet;
    }
}