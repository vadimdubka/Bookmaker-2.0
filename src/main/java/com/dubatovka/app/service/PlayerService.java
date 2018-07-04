package com.dubatovka.app.service;

import com.dubatovka.app.entity.Player;

import java.util.List;

/**
 * The class provides abstraction for Service layer actions with Player.
 *
 * @author Dubatovka Vadim
 */
public interface PlayerService extends AutoCloseable{
    
    /**
     * Calls DAO layer to get {@link List} of all {@link Player} from database.
     *
     * @return {@link List} of {@link Player}
     */
    List<Player> getAllPlayers();
    
    /**
     * Calls DAO layer to inserts {@link Player} information  to database.
     *
     * @param email     {@link String}
     * @param password  {@link String}
     * @param fName     {@link String}
     * @param mName     {@link String}
     * @param lName     {@link String}
     * @param birthDate {@link String}
     * @return id of inserted User
     */
    int registerPlayer(String email, String password,
                       String fName, String mName, String lName,
                       String birthDate);
    
    /**
     * Calls DAO layer to read updated information about given {@link Player} from database and sets
     * it to given {@link Player} instance.
     *
     * @param player {@link Player}
     */
    void updatePlayerInfo(Player player);
    
    @Override
    void close();
}
