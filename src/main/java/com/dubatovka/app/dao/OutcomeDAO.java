package com.dubatovka.app.dao;

import com.dubatovka.app.dao.exception.DAOException;
import com.dubatovka.app.entity.Event;
import com.dubatovka.app.entity.Outcome;

import java.sql.SQLException;
import java.util.Set;

/**
 * DAO interface for {@link Outcome} objects.
 *
 * @author Dubatovka Vadim
 */
public interface OutcomeDAO {
    /**
     * Column names of database table 'outcome'.
     */
    String EVENT_ID    = "event_id";
    String TYPE        = "type";
    String COEFFICIENT = "coefficient";
    
    /**
     * Reads {@link Set} of {@link Outcome} from database which correspond to given {@link Event}
     * id.
     *
     * @param id {@link Event} id
     * @return {@link Set} of {@link Outcome}
     * @throws DAOException when {@link SQLException} occurred while working with database
     */
    Set<Outcome> readOutcomesByEventId(int id) throws DAOException;
    
    /**
     * Inserts {@link Outcome} to database.
     *
     * @param outcome {@link Outcome} for insertion
     * @return true if operation processed successfully
     * @throws DAOException when {@link SQLException} occurred while working with database
     */
    boolean insertOutcome(Outcome outcome) throws DAOException;
}
