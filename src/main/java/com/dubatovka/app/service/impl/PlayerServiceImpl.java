package com.dubatovka.app.service.impl;

import com.dubatovka.app.dao.PlayerDAO;
import com.dubatovka.app.dao.UserDAO;
import com.dubatovka.app.dao.exception.DAOException;
import com.dubatovka.app.dao.impl.DAOProvider;
import com.dubatovka.app.entity.Player;
import com.dubatovka.app.service.EncryptionService;
import com.dubatovka.app.service.PlayerService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * The class provides implementation for Service layer actions with Players.
 *
 * @author Dubatovka Vadim
 */
class PlayerServiceImpl extends PlayerService {
    private static final Logger logger = LogManager.getLogger(PlayerServiceImpl.class);
    
    private final UserDAO   userDAO   = daoProvider.getUserDAO();
    private final PlayerDAO playerDAO = daoProvider.getPlayerDAO();
    
    /**
     * Default constructor.
     */
    PlayerServiceImpl() {
    }
    
    /**
     * Constructs instance using definite {@link DAOProvider} object.
     */
    PlayerServiceImpl(DAOProvider daoProvider) {
        super(daoProvider);
    }
    
    /**
     * Calls DAO layer to get {@link List} of all {@link Player} from database.
     *
     * @return {@link List} of {@link Player}
     */
    @Override
    public List<Player> getAllPlayers() {
        List<Player> playerList = null;
        try {
            playerList = playerDAO.readAllPlayers();
        } catch (DAOException e) {
            logger.log(Level.ERROR, e.getMessage());
        }
        
        return playerList;
    }
    
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
    @Override
    public int registerPlayer(String email, String password, String fName,
                              String mName, String lName, String birthDate) {
        email = email.trim().toLowerCase();
        password = EncryptionService.encryptMD5(password);
        if (fName != null) {
            fName = fName.trim().toUpperCase();
        }
        if (mName != null) {
            mName = mName.trim().toUpperCase();
        }
        if (lName != null) {
            lName = lName.trim().toUpperCase();
        }
        int result = 0;
        try {
            int id = userDAO.insertUser(email, password);
            int insertedRows = playerDAO.insertPlayer(id, fName, mName, lName, birthDate);
            if ((id != 0) && (insertedRows == 1)) {
                result = id;
            }
        } catch (DAOException e) {
            logger.log(Level.ERROR, e.getMessage());
        }
        return result;
    }
    
    /**
     * Calls DAO layer to read updated information about given {@link Player} from database and sets
     * it to given {@link Player} instance.
     *
     * @param player {@link Player}
     */
    @Override
    public void updatePlayerInfo(Player player) {
        int id = player.getId();
        try {
            Player playerWithLastInfo = playerDAO.readPlayerById(id);
            player.setProfile(playerWithLastInfo.getProfile());
            player.setAccount(playerWithLastInfo.getAccount());
            player.setVerification(playerWithLastInfo.getVerification());
        } catch (DAOException e) {
            logger.log(Level.ERROR, e.getMessage());
        }
    }
}