package com.dubatovka.app.entity;

import java.io.Serializable;

/**
 * The class represents information about 'Analyst' domain object.
 *
 * @author Dubatovka Vadim
 */
public class Analyst extends User implements Serializable {
    private static final long serialVersionUID = -5826755955233635473L;
    
    /**
     * Default constructor.
     */
    public Analyst() {
    }
    
    /**
     * Creates a new instance using given {@link User} object.
     *
     * @param user {@link User}
     */
    public Analyst(User user) {
        setId(user.getId());
        setEmail(user.getEmail());
        setRole(UserRole.ANALYST);
        setRegistrationDate(user.getRegistrationDate());
    }
    
    @Override
    public String toString() {
        return "Analyst{" + super.toString() + '}';
    }
    
}