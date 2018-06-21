package com.dubatovka.app.entity;

import com.dubatovka.app.config.ConfigConstant;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * The class represents information about 'Bet' domain object.
 *
 * @author Dubatovka Vadim
 */
public class Bet implements Serializable {
    private static final long serialVersionUID = -2992542772233016603L;
    /**
     * Player id who made bet
     */
    private int           playerId;
    /**
     * Event id of outcome on which  bet was done
     */
    private int           eventId;
    /**
     * Outcome type on which outcome bet was done
     */
    private String        outcomeType;
    /**
     * Date and time when bet was done
     */
    private LocalDateTime date;
    /**
     * Coefficient that selected outcome type of event had when player made bet
     */
    private BigDecimal    coefficient;
    /**
     * Amount of money that user put on bet
     */
    private BigDecimal    amount;
    /**
     * Bet status
     */
    private Status        status;
    
    /**
     * Default constructor.
     */
    public Bet() {
    }
    
    /**
     * Creates a new instance using given parameters.
     */
    public Bet(int playerId, int eventId, String outcomeType, LocalDateTime date,
               BigDecimal coefficient, BigDecimal amount, Status status) {
        this.playerId = playerId;
        this.eventId = eventId;
        this.outcomeType = outcomeType;
        this.date = date;
        this.coefficient = coefficient;
        this.amount = amount;
        this.status = status;
    }
    
    public int getPlayerId() {
        return playerId;
    }
    
    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
    
    public int getEventId() {
        return eventId;
    }
    
    public void setEventId(int eventId) {
        this.eventId = eventId;
    }
    
    public String getOutcomeType() {
        return outcomeType;
    }
    
    public void setOutcomeType(String outcomeType) {
        this.outcomeType = outcomeType;
    }
    
    public LocalDateTime getDate() {
        return date;
    }
    
    public void setDate(LocalDateTime date) {
        this.date = date;
    }
    
    public BigDecimal getCoefficient() {
        return coefficient;
    }
    
    public void setCoefficient(BigDecimal coefficient) {
        this.coefficient = coefficient;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }
        
        Bet that = (Bet) o;
        
        return Objects.equals(amount, that.amount) &&
                   Objects.equals(coefficient, that.coefficient) &&
                   Objects.equals(date, that.date) &&
                   Objects.equals(eventId, that.eventId) &&
                   Objects.equals(outcomeType, that.outcomeType) &&
                   Objects.equals(playerId, that.playerId) &&
                   Objects.equals(status, that.status);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(amount, coefficient, date, eventId, outcomeType, playerId, status);
    }
    
    @Override
    public String toString() {
        return new StringJoiner(", ", getClass().getSimpleName() + "[", "]")
                   .add("amount = " + amount)
                   .add("coefficient = " + coefficient)
                   .add("date = " + date)
                   .add("eventId = " + eventId)
                   .add("outcomeType = " + outcomeType)
                   .add("playerId = " + playerId)
                   .add("status = " + status)
                   .toString();
    }
    
    /**
     * Enumeration of available {@link Bet#status} value instances.
     */
    public enum Status {
        NEW(ConfigConstant.NEW),
        LOSING(ConfigConstant.LOSING),
        WIN(ConfigConstant.WIN),
        PAID(ConfigConstant.PAID);
        
        private final String status;
        
        Status(String status) {
            this.status = status;
        }
        
        public String getStatus() {
            return status;
        }
    }
}
