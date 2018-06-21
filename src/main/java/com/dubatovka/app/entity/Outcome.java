package com.dubatovka.app.entity;

import com.dubatovka.app.config.ConfigConstant;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * The class represents information about 'Outcome' domain object. Every outcome belong to
 * concrete {@link Event}.
 *
 * @author Dubatovka Vadim
 */
public class Outcome implements Serializable {
    private static final long serialVersionUID = 2004092848932695202L;
    /**
     * Event id that contains this outcome instance
     */
    private int        eventId;
    /**
     * Outcome type
     */
    private Type       type;
    /**
     * Coefficient for this outcome instance
     */
    private BigDecimal coefficient;
    
    /**
     * Default constructor.
     */
    public Outcome() {
    }
    
    /**
     * Creates a new instance using given parameters.
     */
    public Outcome(int eventId, BigDecimal coefficient, Type type) {
        this.eventId = eventId;
        this.coefficient = coefficient;
        this.type = type;
    }
    
    public int getEventId() {
        return eventId;
    }
    
    public void setEventId(int eventId) {
        this.eventId = eventId;
    }
    
    public Type getType() {
        return type;
    }
    
    public void setType(Type type) {
        this.type = type;
    }
    
    public BigDecimal getCoefficient() {
        return coefficient;
    }
    
    public void setCoefficient(BigDecimal coefficient) {
        this.coefficient = coefficient;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }
        
        Outcome that = (Outcome) o;
        
        return Objects.equals(coefficient, that.coefficient) &&
                   Objects.equals(eventId, that.eventId) &&
                   Objects.equals(type, that.type);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(coefficient, eventId, type);
    }
    
    @Override
    public String toString() {
        return new StringJoiner(", ", getClass().getSimpleName() + "[", "]")
                   .add("coefficient = " + coefficient)
                   .add("eventId = " + eventId)
                   .add("type = " + type)
                   .toString();
    }
    
    /**
     * Enumeration of available {@link Outcome#type} value instances.
     */
    public enum Type {
        TYPE_1(ConfigConstant.TYPE_1), TYPE_X(ConfigConstant.TYPE_X), TYPE_2(ConfigConstant.TYPE_2);
        
        private final String type;
        
        Type(String type) {
            this.type = type;
        }
        
        public String getType() {
            return type;
        }
    }
}
