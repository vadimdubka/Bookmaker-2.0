package com.dubatovka.app.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * The class represents information about player 'Profile' domain object.
 *
 * @author Dubatovka Vadim
 */
public class PlayerProfile implements Serializable {
    private static final long serialVersionUID = 4607918367842467031L;
    /**
     * Player's first name
     */
    private String    firstName;
    /**
     * Player's middle name
     */
    private String    middleName;
    /**
     * Player's last name
     */
    private String    lastName;
    /**
     * Player's birth date
     */
    private LocalDate birthDate;
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getMiddleName() {
        return middleName;
    }
    
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public LocalDate getBirthDate() {
        return birthDate;
    }
    
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }
        
        PlayerProfile that = (PlayerProfile) o;
        
        return Objects.equals(birthDate, that.birthDate) &&
                   Objects.equals(firstName, that.firstName) &&
                   Objects.equals(lastName, that.lastName) &&
                   Objects.equals(middleName, that.middleName);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(birthDate, firstName, lastName, middleName);
    }
    
    @Override
    public String toString() {
        return new StringJoiner(", ", getClass().getSimpleName() + "[", "]")
                   .add("birthDate = " + birthDate)
                   .add("firstName = " + firstName)
                   .add("lastName = " + lastName)
                   .add("middleName = " + middleName)
                   .toString();
    }
}