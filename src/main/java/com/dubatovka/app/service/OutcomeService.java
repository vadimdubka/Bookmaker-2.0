package com.dubatovka.app.service;

import com.dubatovka.app.dao.impl.DAOProvider;
import com.dubatovka.app.entity.Event;
import com.dubatovka.app.entity.Outcome;

import java.util.Set;

/**
 * The class provides abstraction for Service layer actions with Outcomes.
 *
 * @author Dubatovka Vadim
 */
public abstract class OutcomeService extends DAOProviderHolder {
    /**
     * Default constructor.
     */
    protected OutcomeService() {
    }
    
    /**
     * Constructs instance using definite {@link DAOProvider} object.
     */
    protected OutcomeService(DAOProvider daoProvider) {
        super(daoProvider);
    }
    
    /**
     * Calls DAO layer to get {@link Set} of {@link Outcome} from database which correspond to given
     * {@link Event} and assigns it to given {@link Event}.
     *
     * @param event {@link Event}
     */
    public abstract void setOutcomesForEvent(Event event);
    
    /**
     * Calls DAO layer to insert  {@link Set} of {@link Outcome} to database.
     *
     * @param outcomeSet     {@link Set} of {@link Outcome}
     * @param messageService {@link MessageService} that holds messages, formed during execution of
     *                       method
     */
    public abstract void insertOutcomeSet(Set<Outcome> outcomeSet,
                                          MessageService messageService);
}
