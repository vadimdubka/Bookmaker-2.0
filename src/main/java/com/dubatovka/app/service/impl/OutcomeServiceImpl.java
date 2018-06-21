package com.dubatovka.app.service.impl;

import com.dubatovka.app.dao.OutcomeDAO;
import com.dubatovka.app.dao.exception.DAOException;
import com.dubatovka.app.dao.impl.DAOProvider;
import com.dubatovka.app.entity.Event;
import com.dubatovka.app.entity.Outcome;
import com.dubatovka.app.service.MessageService;
import com.dubatovka.app.service.OutcomeService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;

import static com.dubatovka.app.config.ConfigConstant.MESSAGE_ERR_SQL_OPERATION;

/**
 * The class provides implementation for Service layer actions with Outcomes.
 *
 * @author Dubatovka Vadim
 */
class OutcomeServiceImpl extends OutcomeService {
    private static final Logger logger = LogManager.getLogger(OutcomeServiceImpl.class);
    
    private final OutcomeDAO outcomeDAO = daoProvider.getOutcomeDAO();
    
    /**
     * Default constructor.
     */
    OutcomeServiceImpl() {
    }
    
    /**
     * Constructs instance using definite {@link DAOProvider} object.
     */
    OutcomeServiceImpl(DAOProvider daoProvider) {
        super(daoProvider);
    }
    
    /**
     * Calls DAO layer to get {@link Set} of {@link Outcome} from database which correspond to given
     * {@link Event} and assigns it to given {@link Event}.
     *
     * @param event {@link Event}
     */
    @Override
    public void setOutcomesForEvent(Event event) {
        if (event != null) {
            try {
                int          id         = event.getId();
                Set<Outcome> outcomeSet = outcomeDAO.readOutcomesByEventId(id);
                event.setOutcomeSet(outcomeSet);
            } catch (DAOException e) {
                logger.log(Level.ERROR, e.getMessage());
            }
        }
    }
    
    /**
     * Calls DAO layer to insert  {@link Set} of {@link Outcome} to database.
     *
     * @param outcomeSet     {@link Set} of {@link Outcome}
     * @param messageService {@link MessageService} that holds messages, formed during execution of
     *                       method
     */
    @Override
    public void insertOutcomeSet(Set<Outcome> outcomeSet, MessageService messageService) {
        try {
            for (Outcome outcome : outcomeSet) {
                boolean failure = !outcomeDAO.insertOutcome(outcome);
                if (failure) {
                    messageService.appendErrMessByKey(MESSAGE_ERR_SQL_OPERATION);
                }
            }
        } catch (DAOException e) {
            logger.log(Level.ERROR, e.getMessage());
            messageService.appendErrMessByKey(MESSAGE_ERR_SQL_OPERATION);
        }
    }
}
