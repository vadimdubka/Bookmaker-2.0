package com.dubatovka.app.entity;

import com.dubatovka.app.config.ConfigConstant;

import java.io.Serializable;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * The class represents information about player 'Verification' domain object.
 *
 * @author Dubatovka Vadim
 */
public class PlayerVerification implements Serializable {
    private static final long serialVersionUID = 3172147030959748268L;
    /**
     * Player verification verificationStatus.
     */
    private VerificationStatus verificationStatus;
    /**
     * Player country of citizenship
     */
    private String             country;
    /**
     * Player passport id number.
     */
    private String             passport;
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public String getPassport() {
        return passport;
    }
    
    public void setPassport(String passport) {
        this.passport = passport;
    }
    
    public VerificationStatus getVerificationStatus() {
        return verificationStatus;
    }
    
    public void setVerificationStatus(VerificationStatus verificationStatus) {
        this.verificationStatus = verificationStatus;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }
        
        PlayerVerification that = (PlayerVerification) o;
        
        return Objects.equals(country, that.country) &&
                   Objects.equals(passport, that.passport) &&
                   Objects.equals(verificationStatus, that.verificationStatus);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(country, passport, verificationStatus);
    }
    
    @Override
    public String toString() {
        return new StringJoiner(", ", getClass().getSimpleName() + "[", "]")
                   .add("country = " + country)
                   .add("passport = " + passport)
                   .add("verificationStatus = " + verificationStatus)
                   .toString();
    }
    
    /**
     * Enumeration of available {@link PlayerVerification#verificationStatus} value instances.
     */
    public enum VerificationStatus {
        UNVERIFIED(ConfigConstant.UNVERIFIED),
        REQUEST(ConfigConstant.REQUEST),
        VERIFIED(ConfigConstant.VERIFIED);
        
        private final String status;
        
        VerificationStatus(String status) {
            this.status = status;
        }
        
        public String getStatus() {
            return status;
        }
    }
}