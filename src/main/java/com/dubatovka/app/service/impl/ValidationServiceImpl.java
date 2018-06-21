package com.dubatovka.app.service.impl;

import com.dubatovka.app.entity.Event;
import com.dubatovka.app.entity.Outcome;
import com.dubatovka.app.service.ValidationService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of Service layer class for validation actions.
 *
 * @author Dubatovka Vadim
 */
class ValidationServiceImpl implements ValidationService {
    private static final int        MAX_EMAIL_LENGTH        = 320;
    private static final int        MAX_EMAIL_NAME_LENGTH   = 64;
    private static final int        MAX_EMAIL_DOMAIN_LENGTH = 255;
    private static final int        EMAIL_PAIR_LENGTH       = 2;
    private static final int        MIN_PLAYER_AGE          = 18;
    private static final BigDecimal MIN_OUTCOME_COEFF       = BigDecimal.valueOf(1.01);
    private static final BigDecimal MAX_OUTCOME_COEFF       = BigDecimal.valueOf(99.99);
    private static final String     EMAIL_SPLITERATOR       = "@";
    private static final String     NAME_REGEX              = "[A-Za-z]{1,70}";
    private static final String     BET_AMOUNT_REGEX        = "^[0-9]{1,3}\\.?[0-9]{0,2}$";
    private static final String     OUTCOME_COEFF_REGEX     = "^[0-9]{1,2}\\.?[0-9]{0,2}$";
    private static final String     ID_REGEX                = "[0-9]+";
    private static final String     RESULT_REGEX            = "[0-9]{1,3}";
    private static final String     PARTICIPANT_REGEX       = "^([a-zA-Z_0-9а-яА-Я]+).{0,100}";
    private static final String     EMAIL_REGEX             =
        "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
    private static final String     PASSWORD_REGEX          =
        "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])[\\w_-]{8,}$";
    
    /**
     * Validates user email.
     *
     * @param email user email
     * @return true if email valid
     */
    @Override
    public boolean isValidEmail(String email) {
        boolean isValid = false;
        if ((email != null) && (!email.isEmpty())
                && (email.length() <= MAX_EMAIL_LENGTH)
                && (isMatchPattern(email, EMAIL_REGEX))) {
            String[] emailPair = email.split(EMAIL_SPLITERATOR);
            if (emailPair.length == EMAIL_PAIR_LENGTH) {
                String name = emailPair[0];
                String domain = emailPair[1];
                isValid = (name.length() <= MAX_EMAIL_NAME_LENGTH)
                              && (domain.length() <= MAX_EMAIL_DOMAIN_LENGTH);
            }
        }
        return isValid;
    }
    
    /**
     * Validates user password.
     *
     * @param password user password
     * @return true if password valid
     */
    @Override
    public boolean isValidPassword(String password) {
        return (password != null)
                   && !password.trim().isEmpty()
                   && isMatchPattern(password, PASSWORD_REGEX);
    }
    
    /**
     * Validates user password and compares it with password entered again.
     *
     * @param password      user password
     * @param passwordAgain user password entered again
     * @return true if password valid and matches to entered again
     */
    @Override
    public boolean isValidPassword(String password, String passwordAgain) {
        boolean result = isValidPassword(password);
        return result && password.equals(passwordAgain);
    }
    
    /**
     * Validates user name.
     *
     * @param name user name
     * @return true if name is valid
     */
    @Override
    public boolean isValidName(String name) {
        return (name == null)
                   || name.trim().isEmpty()
                   || isMatchPattern(name, NAME_REGEX);
    }
    
    /**
     * Validates user birth date.
     *
     * @param birthDate user birth date
     * @return true if birth date is valid
     */
    @Override
    public boolean isValidBirthDate(String birthDate) {
        boolean result = false;
        if ((birthDate != null) && !birthDate.trim().isEmpty()) {
            try {
                LocalDate date = LocalDate.parse(birthDate);
                LocalDate now = LocalDate.now();
                result = date.plusYears(MIN_PLAYER_AGE).isBefore(now)
                             || date.plusYears(MIN_PLAYER_AGE).isEqual(now);
            } catch (DateTimeParseException e) {
                result = false;
            }
        }
        return result;
    }
    
    /**
     * Validates bet amount.
     *
     * @param betAmount bet amount
     * @return true if bet amount is valid
     */
    @Override
    public boolean isValidBetAmount(String betAmount) {
        return (betAmount != null) && isMatchPattern(betAmount, BET_AMOUNT_REGEX);
    }
    
    /**
     * Validates string representation of id int number.
     *
     * @param id string representation of id int number
     * @return true if id is valid
     */
    @Override
    public boolean isValidId(String id) {
        return (id != null) && isMatchPattern(id, ID_REGEX) && (Integer.parseInt(id) > 0);
    }
    
    /**
     * Validates time of making bet.
     *
     * @param betDateTime   {@link LocalDateTime}
     * @param eventDateTime {@link LocalDateTime}
     * @return true if time is valid
     */
    @Override
    public boolean isValidBetTime(LocalDateTime betDateTime, LocalDateTime eventDateTime) {
        return betDateTime.isBefore(eventDateTime);
    }
    
    /**
     * Validates request parameters to confirm that all received parameters are not null and
     * not empty.
     *
     * @param params {@link String} array of request parameters.
     * @return boolean result of validation.
     */
    @Override
    public boolean isValidRequestParam(String... params) {
        boolean result = true;
        for (String param : params) {
            if ((param == null) || param.isEmpty()) {
                result = false;
            }
        }
        return result;
    }
    
    /**
     * Validates date and time of event.
     *
     * @param dateTimeStr {@link String} representation of date and time
     * @return true if date and time is valid
     */
    @Override
    public boolean isValidEventDateTime(String dateTimeStr) {
        boolean result;
        try {
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr);
            LocalDateTime now = LocalDateTime.now();
            result = dateTime.isAfter(now);
        } catch (DateTimeParseException e) {
            result = false;
        }
        return result;
    }
    
    /**
     * Validates event participant name.
     *
     * @param participant participant name
     * @return true if participant name is valid
     */
    @Override
    public boolean isValidEventParticipantName(String participant) {
        return (participant != null)
                   && !participant.trim().isEmpty()
                   && isMatchPattern(participant, PARTICIPANT_REGEX);
    }
    
    /**
     * Validates string representation of event result.
     *
     * @param eventResult string representation of event result
     * @return true if event result is valid
     */
    @Override
    public boolean isValidEventResult(String eventResult) {
        boolean result = (eventResult != null)
                             && isMatchPattern(eventResult, RESULT_REGEX);
        if (result) {
            int i = Integer.parseInt(eventResult);
            result = (i >= 0) && (i < 1000);
        }
        return result;
    }
    
    /**
     * Validates string representation of outcome coefficient.
     *
     * @param outcomeCoeff string representation of outcome coefficient
     * @return true if outcome coefficient is valid
     */
    @Override
    public boolean isValidOutcomeCoeff(String outcomeCoeff) {
        boolean result = (outcomeCoeff != null)
                             && isMatchPattern(outcomeCoeff, OUTCOME_COEFF_REGEX);
        if (result) {
            BigDecimal decimal = new BigDecimal(outcomeCoeff);
            result = (decimal.compareTo(MIN_OUTCOME_COEFF) >= 0)
                         && (decimal.compareTo(MAX_OUTCOME_COEFF) <= 0);
        }
        return result;
    }
    
    /**
     * Validates outcome coefficient that was on web page when client made bet.
     *
     * @param outcomeCoeffOnPage {@link String} outcome coefficient from web page
     * @param event              {@link Event} of bet
     * @param outcomeType        {@link Outcome.Type#getType()} of bet
     * @return true if outcome coefficient is valid
     */
    @Override
    public boolean isValidOutcomeCoeffOnPage(String outcomeCoeffOnPage, Event event, String outcomeType) {
        Outcome    outcome       = event.getOutcomeByType(outcomeType);
        BigDecimal coeffOnPage   = new BigDecimal(outcomeCoeffOnPage);
        BigDecimal coeffFromDB   = outcome.getCoefficient();
        int        compareResult = coeffFromDB.compareTo(coeffOnPage);
        return compareResult == 0;
    }
    
    /**
     * Checks if given string matches regular expression pattern.
     *
     * @param str   string value
     * @param regex string regular expression
     * @return true if string matches pattern is valid
     */
    private static boolean isMatchPattern(String str, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
}