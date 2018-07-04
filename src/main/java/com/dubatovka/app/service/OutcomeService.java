package com.dubatovka.app.service;

import com.dubatovka.app.entity.Event;
import com.dubatovka.app.entity.Outcome;

import java.util.Set;

/**
 * The class provides abstraction for Service layer actions with Outcomes.
 *
 * @author Dubatovka Vadim
 */
public interface OutcomeService extends AutoCloseable {
    
    /**
     * Calls DAO layer to get {@link Set} of {@link Outcome} from database which correspond to given
     * {@link Event} and assigns it to given {@link Event}.
     *
     * @param event {@link Event}
     */
    void setOutcomesForEvent(Event event);
    
    /**
     * Calls DAO layer to insert  {@link Set} of {@link Outcome} to database.
     *
     * @param outcomeSet     {@link Set} of {@link Outcome}
     * @param messageService {@link MessageService} that holds messages, formed during execution of
     *                       method
     */
    void insertOutcomeSet(Set<Outcome> outcomeSet,
                          MessageService messageService);
    
    @Override
    void close();
}
