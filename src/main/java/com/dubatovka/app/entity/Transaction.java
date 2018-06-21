package com.dubatovka.app.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * The class represents information about 'Transaction' domain object.
 *
 * @author Dubatovka Vadim
 */
public class Transaction implements Serializable {
    private static final long serialVersionUID = 494519517782731780L;
    /**
     * Transaction unique id.
     */
    private int             id;
    /**
     * Player id who made this transaction.
     */
    private int             playerId;
    /**
     * Transaction date.
     */
    private LocalDateTime   date;
    /**
     * Transaction amount.
     */
    private BigDecimal      amount;
    /**
     * Transaction type.
     */
    private TransactionType type;
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getPlayerId() {
        return playerId;
    }
    
    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
    
    public LocalDateTime getDate() {
        return date;
    }
    
    public void setDate(LocalDateTime date) {
        this.date = date;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public TransactionType getType() {
        return type;
    }
    
    public void setType(TransactionType type) {
        this.type = type;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }
        
        Transaction that = (Transaction) o;
        
        return Objects.equals(amount, that.amount) &&
                   Objects.equals(date, that.date) &&
                   Objects.equals(id, that.id) &&
                   Objects.equals(playerId, that.playerId) &&
                   Objects.equals(type, that.type);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(amount, date, id, playerId, type);
    }
    
    @Override
    public String toString() {
        return new StringJoiner(", ", getClass().getSimpleName() + "[", "]")
                   .add("amount = " + amount)
                   .add("date = " + date)
                   .add("id = " + id)
                   .add("playerId = " + playerId)
                   .add("type = " + type)
                   .toString();
    }
    
    /**
     * Enumeration of available {@link Transaction#type} value instances.
     */
    public enum TransactionType {
        REPLENISH, WITHDRAW
    }
}
