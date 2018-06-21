package com.dubatovka.app.entity;

import java.io.Serializable;

/**
 * The class represents information about 'Admin' domain object.
 *
 * @author Dubatovka Vadim
 */
public class Admin extends User implements Serializable {
    private static final long serialVersionUID = -8023371264325607651L;
    
    /**
     * Default constructor.
     */
    public Admin() {
    }
    
    /**
     * Creates a new instance using given {@link User} object.
     *
     * @param user {@link User}
     */
    public Admin(User user) {
        setId(user.getId());
        setEmail(user.getEmail());
        setRole(UserRole.ADMIN);
        setRegistrationDate(user.getRegistrationDate());
    }
    
    /**
     * Builds {@link String} representation of this instance.
     *
     * @return built {@link String} representation of this instance
     */
    @Override
    public String toString() {
        return "Admin{} " + super.toString();
    }
}