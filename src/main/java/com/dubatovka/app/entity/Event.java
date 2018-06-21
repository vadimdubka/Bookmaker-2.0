package com.dubatovka.app.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;

/**
 * The class represents information about 'Event' domain object. Every event belong to concrete
 * {@link Category}.
 *
 * @author Dubatovka Vadim
 */
public class Event implements Serializable {
    private static final long serialVersionUID = -3491120153605965764L;
    /**
     * Unique event id
     */
    private int               id;
    /**
     * Id of category that contains this instance of event
     */
    private int               categoryId;
    /**
     * Date when event is going to start
     */
    private LocalDateTime     date;
    /**
     * Name of first or single event participant
     */
    private String            participant1;
    /**
     * Name of second event participant
     */
    private String            participant2;
    /**
     * Result of first participant
     */
    private String            result1;
    /**
     * Result of second participant
     */
    private String            result2;
    /**
     * Set of outcomes for this event instance
     */
    private Set<Outcome>      outcomeSet;
    
    /**
     * Retrieves {@link Outcome} for given outcome type from {@link #outcomeSet}
     *
     * @param outcomeType {@link String} outcome type
     * @return {@link Outcome}
     */
    public Outcome getOutcomeByType(String outcomeType) {
        Outcome result = null;
        for (Outcome outcome : outcomeSet) {
            if (outcome.getType().getType().equalsIgnoreCase(outcomeType)) {
                result = outcome;
            }
        }
        return result;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
    
    public LocalDateTime getDate() {
        return date;
    }
    
    public void setDate(LocalDateTime date) {
        this.date = date;
    }
    
    public String getParticipant1() {
        return participant1;
    }
    
    public void setParticipant1(String participant1) {
        this.participant1 = participant1;
    }
    
    public String getParticipant2() {
        return participant2;
    }
    
    public void setParticipant2(String participant2) {
        this.participant2 = participant2;
    }
    
    public String getResult1() {
        return result1;
    }
    
    public void setResult1(String result1) {
        this.result1 = result1;
    }
    
    public String getResult2() {
        return result2;
    }
    
    public void setResult2(String result2) {
        this.result2 = result2;
    }
    
    public Set<Outcome> getOutcomeSet() {
        return outcomeSet;
    }
    
    public void setOutcomeSet(Set<Outcome> outcomeSet) {
        this.outcomeSet = outcomeSet;
    }
    
    /**
     * Updates this instance fields from given event.
     *
     * @param eventSource {@link Event} to update from
     */
    public void updateFrom(Event eventSource) {
        this.id = eventSource.id;
        this.categoryId = eventSource.categoryId;
        this.date = eventSource.date;
        this.participant1 = eventSource.participant1;
        this.participant2 = eventSource.participant2;
        this.result1 = eventSource.result1;
        this.result2 = eventSource.result2;
        this.outcomeSet = eventSource.outcomeSet;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }
        
        Event that = (Event) o;
        
        return Objects.equals(categoryId, that.categoryId) &&
                   Objects.equals(date, that.date) &&
                   Objects.equals(id, that.id) &&
                   Objects.equals(outcomeSet, that.outcomeSet) &&
                   Objects.equals(participant1, that.participant1) &&
                   Objects.equals(participant2, that.participant2) &&
                   Objects.equals(result1, that.result1) &&
                   Objects.equals(result2, that.result2);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(categoryId, date, id, outcomeSet, participant1, participant2,
                            result1, result2);
    }
    
    @Override
    public String toString() {
        return new StringJoiner(", ", getClass().getSimpleName() + "[", "]")
                   .add("categoryId = " + categoryId)
                   .add("date = " + date)
                   .add("id = " + id)
                   .add("outcomeSet = " + outcomeSet)
                   .add("participant1 = " + participant1)
                   .add("participant2 = " + participant2)
                   .add("result1 = " + result1)
                   .add("result2 = " + result2)
                   .toString();
    }
}
