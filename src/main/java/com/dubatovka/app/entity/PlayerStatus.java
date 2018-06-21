package com.dubatovka.app.entity;

import com.dubatovka.app.config.ConfigConstant;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * The class represents information about player 'Status' domain object.
 *
 * @author Dubatovka Vadim
 */
public class PlayerStatus implements Serializable {
    private static final long serialVersionUID = 539544012880393907L;
    /**
     * Player account status value.
     */
    private Status     status;
    /**
     * Player limit for 1 bet.
     */
    private BigDecimal betLimit;
    /**
     * Player withdrawal limit for 1 month.
     */
    private BigDecimal withdrawalLimit;
    
    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
    }
    
    public BigDecimal getBetLimit() {
        return betLimit;
    }
    
    public void setBetLimit(BigDecimal betLimit) {
        this.betLimit = betLimit;
    }
    
    public BigDecimal getWithdrawalLimit() {
        return withdrawalLimit;
    }
    
    public void setWithdrawalLimit(BigDecimal withdrawalLimit) {
        this.withdrawalLimit = withdrawalLimit;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }
        
        PlayerStatus that = (PlayerStatus) o;
        
        return Objects.equals(betLimit, that.betLimit) &&
                   Objects.equals(status, that.status) &&
                   Objects.equals(withdrawalLimit, that.withdrawalLimit);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(betLimit, status, withdrawalLimit);
    }
    
    @Override
    public String toString() {
        return new StringJoiner(", ", getClass().getSimpleName() + "[", "]")
                   .add("betLimit = " + betLimit)
                   .add("status = " + status)
                   .add("withdrawalLimit = " + withdrawalLimit)
                   .toString();
    }
    
    /**
     * Enumeration of available {@link PlayerStatus#status} value instances.
     */
    public enum Status {
        UNVERIFIED(ConfigConstant.UNVERIFIED),
        BASIC(ConfigConstant.BASIC),
        VIP(ConfigConstant.VIP),
        BAN(ConfigConstant.BAN);
        
        private final String status;
        
        Status(String status) {
            this.status = status;
        }
        
        public String getStatus() {
            return status;
        }
    }
}
