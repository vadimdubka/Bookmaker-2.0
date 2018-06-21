package com.dubatovka.app.entity;

import java.io.Serializable;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * The class represents information about 'Player' domain object.
 *
 * @author Dubatovka Vadim
 */
public class Player extends User implements Serializable {
    private static final long               serialVersionUID = -3724993061188402916L;
    /**
     * Object which contains player profile data.
     */
    private              PlayerProfile      profile;
    /**
     * Object which contains player account data.
     */
    private              PlayerAccount      account;
    /**
     * Object which contains player verification data.
     */
    private              PlayerVerification verification;
    
    /**
     * Default constructor.
     */
    public Player() {
    }
    
    /**
     * Creates a new instance using given {@link User} object.
     *
     * @param user {@link User}
     */
    public Player(User user) {
        setId(user.getId());
        setEmail(user.getEmail());
        setRole(UserRole.PLAYER);
        setRegistrationDate(user.getRegistrationDate());
    }
    
    public PlayerProfile getProfile() {
        return profile;
    }
    
    public void setProfile(PlayerProfile profile) {
        this.profile = profile;
    }
    
    public PlayerAccount getAccount() {
        return account;
    }
    
    public void setAccount(PlayerAccount account) {
        this.account = account;
    }
    
    public PlayerVerification getVerification() {
        return verification;
    }
    
    public void setVerification(PlayerVerification verification) {
        this.verification = verification;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Player)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        Player player = (Player) o;
        return Objects.equals(profile, player.profile) &&
                   Objects.equals(account, player.account) &&
                   Objects.equals(verification, player.verification);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), profile, account, verification);
    }
    
    @Override
    public String toString() {
        return new StringJoiner(", ", getClass().getSimpleName() + "[", "]")
                   .add("account = " + account)
                   .add("profile = " + profile)
                   .add("verification = " + verification)
                   .toString();
    }
}