package com.dubatovka.app.dao;

import com.dubatovka.app.dao.exception.DAOException;
import com.dubatovka.app.entity.Player;
import com.dubatovka.app.entity.Transaction;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO interface for {@link Player} objects.
 *
 * @author Dubatovka Vadim
 */
public interface PlayerDAO {
    /**
     * Column names of database table 'player'.
     */
    String ID                    = "id";
    String EMAIL                 = "email";
    String FIRST_NAME            = "fname";
    String MIDDLE_NAME           = "mname";
    String LAST_NAME             = "lname";
    String BIRTHDAY              = "birthday";
    String STATUS                = "status";
    String BALANCE               = "balance";
    String BET_LIMIT             = "bet_limit";
    String WITHDRAWAL_LIMIT      = "withdrawal_limit";
    String THIS_MONTH_WITHDRAWAL = "month_withdrawal";
    String VERIFICATION_STATUS   = "verification_status";
    String PASSPORT              = "passport";
    
    /**
     * Inserts {@link Player} data in database.
     *
     * @param id        id of {@link Player}
     * @param fName     first name of {@link Player}
     * @param mName     middle name of {@link Player}
     * @param lName     last name of {@link Player}
     * @param birthDate birthdate of {@link Player}
     * @return true if insertion proceeded successfully
     * @throws DAOException when {@link SQLException} occurred while working with database
     */
    int insertPlayer(int id, String fName, String mName, String lName, String birthDate)
        throws DAOException;
    
    /**
     * Reads {@link List} of all {@link Player} from database.
     *
     * @return {@link List} of {@link Player}
     * @throws DAOException when {@link SQLException} occurred while working with database
     */
    List<Player> readAllPlayers() throws DAOException;
    
    /**
     * Reads {@link Player} from database which correspond to given {@link
     * Player} id.
     *
     * @param id {@link Player} id
     * @return {@link Player}
     * @throws DAOException when {@link SQLException} occurred while working with database
     */
    Player readPlayerById(int id) throws DAOException;
    
    /**
     * Updates definite {@link Player} balance by adding/subtracting definite value to it.
     *
     * @param id     id of {@link Player} whose data is updating
     * @param amount amount of money to add/subtract to current balance value
     * @param type   type of balance changing
     * @return true if update proceeded successfully
     * @throws DAOException when {@link SQLException} occurred while working with database
     */
    boolean updateBalance(int id, BigDecimal amount, Transaction.TransactionType type)
        throws DAOException;
}