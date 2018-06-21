package com.dubatovka.app.entity;

import com.dubatovka.app.config.ConfigConstant;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * The class represents information about 'User' domain object.
 *
 * @author Dubatovka Vadim
 */
public class User implements Serializable {
    private static final long serialVersionUID = 10220719251876555L;
    /**
     * Unique user id.
     */
    private int       id;
    /**
     * User email
     */
    private String    email;
    /**
     * User role
     */
    private UserRole  role;
    /**
     * User registration date
     */
    private LocalDate registrationDate;
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public UserRole getRole() {
        return role;
    }
    
    public void setRole(UserRole role) {
        this.role = role;
    }
    
    public LocalDate getRegistrationDate() {
        return registrationDate;
    }
    
    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }
        
        User that = (User) o;
        
        return Objects.equals(email, that.email) &&
                   Objects.equals(id, that.id) &&
                   Objects.equals(registrationDate, that.registrationDate) &&
                   Objects.equals(role, that.role);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(email, id, registrationDate, role);
    }
    
    @Override
    public String toString() {
        return new StringJoiner(", ", getClass().getSimpleName() + "[", "]")
                   .add("email = " + email)
                   .add("id = " + id)
                   .add("registrationDate = " + registrationDate)
                   .add("role = " + role)
                   .toString();
    }
    
    /**
     * Enumeration of available {@link User#role} value instances.
     */
    public enum UserRole {
        GUEST(ConfigConstant.GUEST),
        PLAYER(ConfigConstant.PLAYER),
        ADMIN(ConfigConstant.ADMIN),
        ANALYST(ConfigConstant.ANALYST);
        
        private final String role;
        
        UserRole(String role) {
            this.role = role;
        }
        
        public String getRole() {
            return role;
        }
    }
}
