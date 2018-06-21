package com.dubatovka.app.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * The class represents information about player 'Account' domain object.
 *
 * @author Dubatovka Vadim
 */
public class PlayerAccount implements Serializable {
    private static final long         serialVersionUID = -3202129806852194136L;
    /**
     * Object which contains player status data.
     */
    private              PlayerStatus status;
    /**
     * Player balance value.
     */
    private              BigDecimal   balance;
    /**
     * Player current month withdrawal value.
     */
    private              BigDecimal   thisMonthWithdrawal;
    
    public PlayerStatus getStatus() {
        return status;
    }
    
    public void setStatus(PlayerStatus status) {
        this.status = status;
    }
    
    public BigDecimal getBalance() {
        return balance;
    }
    
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
    
    public BigDecimal getThisMonthWithdrawal() {
        return thisMonthWithdrawal;
    }
    
    public void setThisMonthWithdrawal(BigDecimal thisMonthWithdrawal) {
        this.thisMonthWithdrawal = thisMonthWithdrawal;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }
        
        PlayerAccount that = (PlayerAccount) o;
        
        return Objects.equals(balance, that.balance) &&
                   Objects.equals(status, that.status) &&
                   Objects.equals(thisMonthWithdrawal, that.thisMonthWithdrawal);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(balance, status, thisMonthWithdrawal);
    }
    
    @Override
    public String toString() {
        return new StringJoiner(", ", getClass().getSimpleName() + "[", "]")
                   .add("balance = " + balance)
                   .add("status = " + status)
                   .add("thisMonthWithdrawal = " + thisMonthWithdrawal)
                   .toString();
    }
}