package com.dubatovka.app.service;

import com.dubatovka.app.entity.Event;
import com.dubatovka.app.entity.Outcome;

import java.time.LocalDateTime;

/**
 * Interface for Service layer actions for validation.
 *
 * @author Dubatovka Vadim
 */
public interface ValidationService {
    /**
     * Validates user email.
     *
     * @param email user email
     * @return true if email valid
     */
    boolean isValidEmail(String email);
    
    /**
     * Validates user password.
     *
     * @param password user password
     * @return true if password valid
     */
    boolean isValidPassword(String password);
    
    /**
     * Validates user password and compares it with password entered again.
     *
     * @param password      user password
     * @param passwordAgain user password entered again
     * @return true if password valid and matches to entered again
     */
    boolean isValidPassword(String password, String passwordAgain);
    
    /**
     * Validates user name.
     *
     * @param name user name
     * @return true if name is valid
     */
    boolean isValidName(String name);
    
    /**
     * Validates user birth date.
     *
     * @param birthDate user birth date
     * @return true if birth date is valid
     */
    boolean isValidBirthDate(String birthDate);
    
    /**
     * Validates bet amount.
     *
     * @param betAmount bet amount
     * @return true if bet amount is valid
     */
    boolean isValidBetAmount(String betAmount);
    
    /**
     * Validates string representation of id int number.
     *
     * @param id string representation of id int number
     * @return true if id is valid
     */
    boolean isValidId(String id);
    
    /**
     * Validates outcome coefficient that was on web page when client made bet.
     *
     * @param outcomeCoeffOnPage {@link String} outcome coefficient from web page
     * @param event              {@link Event} of bet
     * @param outcomeType        {@link Outcome.Type#getType()} of bet
     * @return true if outcome coefficient is valid
     */
    boolean isValidOutcomeCoeffOnPage(String outcomeCoeffOnPage, Event event, String outcomeType);
    
    /**
     * Validates time of making bet.
     *
     * @param betDateTime   {@link LocalDateTime}
     * @param eventDateTime {@link LocalDateTime}
     * @return true if time is valid
     */
    boolean isValidBetTime(LocalDateTime betDateTime, LocalDateTime eventDateTime);
    
    /**
     * Validates request parameters to confirm that all received parameters are not null and
     * not empty.
     *
     * @param params {@link String} array of request parameters.
     * @return boolean result of validation.
     */
    boolean isValidRequestParam(String... params);
    
    /**
     * Validates date and time of event.
     *
     * @param dateTimeStr {@link String} representation of date and time
     * @return true if date and time is valid
     */
    boolean isValidEventDateTime(String dateTimeStr);
    
    /**
     * Validates event participant name.
     *
     * @param participant participant name
     * @return true if participant name is valid
     */
    boolean isValidEventParticipantName(String participant);
    
    /**
     * Validates string representation of event result.
     *
     * @param eventResult string representation of event result
     * @return true if event result is valid
     */
    boolean isValidEventResult(String eventResult);
    
    /**
     * Validates string representation of outcome coefficient.
     *
     * @param outcomeCoeff string representation of outcome coefficient
     * @return true if outcome coefficient is valid
     */
    boolean isValidOutcomeCoeff(String outcomeCoeff);
}
