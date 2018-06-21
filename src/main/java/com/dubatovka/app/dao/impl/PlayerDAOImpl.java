package com.dubatovka.app.dao.impl;

import com.dubatovka.app.dao.PlayerDAO;
import com.dubatovka.app.dao.db.ConnectionPool;
import com.dubatovka.app.dao.db.WrappedConnection;
import com.dubatovka.app.dao.exception.DAOException;
import com.dubatovka.app.entity.Player;
import com.dubatovka.app.entity.PlayerAccount;
import com.dubatovka.app.entity.PlayerProfile;
import com.dubatovka.app.entity.PlayerStatus;
import com.dubatovka.app.entity.PlayerVerification;
import com.dubatovka.app.entity.Transaction;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Class provides {@link PlayerDAO} implementation for MySQL database.
 *
 * @author Dubatovka Vadim
 */
class PlayerDAOImpl extends DBConnectionHolder implements PlayerDAO {
    private static final String SQL_INSERT_PLAYER =
        "INSERT INTO player (id, fname, mname, lname, birthday) " +
            "VALUES (?, ?, ?, ?, ?)";
    
    private static final String SQL_SELECT_PLAYER_BY_ID =
        "SELECT fname, mname, lname, birthday, status, balance, " +
            "bet_limit, withdrawal_limit, verification_status, passport," +
            "IFNULL((SELECT ABS(SUM(amount)) FROM transaction WHERE player.id=player_id " +
            "AND amount < 0 AND MONTH(date)=MONTH(NOW()) AND YEAR(date)=YEAR(NOW())), 0) AS month_withdrawal " +
            "FROM player " +
            "LEFT JOIN player_status ON player.player_status=player_status.status " +
            "WHERE player.id=?;";
    
    private static final String SQL_SELECT_ALL_PLAYERS =
        "SELECT fname, mname, lname, birthday, status, balance, " +
            "bet_limit, withdrawal_limit, verification_status, passport," +
            "IFNULL((SELECT ABS(SUM(amount)) FROM transaction WHERE player.id=player_id " +
            "AND amount < 0 AND MONTH(date)=MONTH(NOW()) AND YEAR(date)=YEAR(NOW())), 0) AS month_withdrawal " +
            "FROM player " +
            "LEFT JOIN player_status ON player.player_status=player_status.status " +
            "ORDER BY lname";
    
    private static final String SQL_UPDATE_ACCOUNT_BALANCE =
        "UPDATE player SET balance=balance+? WHERE id=?";
    
    /**
     * Constructs DAO object by taking {@link WrappedConnection} object from {@link ConnectionPool}.
     */
    PlayerDAOImpl() {
    }
    
    /**
     * Constructs DAO object by assigning {@link DBConnectionHolder#connection} field definite
     * {@link WrappedConnection} object.
     *
     * @param connection {@link WrappedConnection} to assign to {@link DBConnectionHolder#connection}
     *                   field
     */
    PlayerDAOImpl(WrappedConnection connection) {
        super(connection);
    }
    
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
    @Override
    public int insertPlayer(int id, String fName, String mName, String lName, String birthDate)
        throws DAOException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_INSERT_PLAYER)) {
            statement.setInt(1, id);
            statement.setString(2, fName);
            statement.setString(3, mName);
            statement.setString(4, lName);
            statement.setString(5, birthDate);
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Database connection error while inserting player. " + e);
        }
    }
    
    /**
     * Reads {@link Player} from database which correspond to given {@link
     * Player} id.
     *
     * @param id {@link Player} id
     * @return {@link Player}
     * @throws DAOException when {@link SQLException} occurred while working with database
     */
    @Override
    public Player readPlayerById(int id) throws DAOException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT_PLAYER_BY_ID)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            return buildPlayer(resultSet);
        } catch (SQLException e) {
            throw new DAOException("Database connection error while taking player profile. " + e);
        }
    }
    
    /**
     * Reads {@link List} of all {@link Player} from database.
     *
     * @return {@link List} of {@link Player}
     * @throws DAOException when {@link SQLException} occurred while working with database
     */
    @Override
    public List<Player> readAllPlayers() throws DAOException {
        List<Player> playerList;
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(SQL_SELECT_ALL_PLAYERS);
            playerList = createUserList(resultSet);
        } catch (SQLException e) {
            throw new DAOException("Database connection error while read users. " + e);
        }
        return playerList;
    }
    
    /**
     * Updates definite {@link Player} balance by adding/subtracting definite value to it.
     *
     * @param id     id of {@link Player} whose data is updating
     * @param amount amount of money to add/subtract to current balance value
     * @param type   type of balance changing
     * @return true if update proceeded successfully
     * @throws DAOException when {@link SQLException} occurred while working with database
     */
    @Override
    public boolean updateBalance(int id, BigDecimal amount, Transaction.TransactionType type)
        throws DAOException {
        if (type == Transaction.TransactionType.WITHDRAW) {
            amount = amount.negate();
        }
        try (PreparedStatement statement = connection.prepareStatement(SQL_UPDATE_ACCOUNT_BALANCE)) {
            statement.setBigDecimal(1, amount);
            statement.setInt(2, id);
            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new DAOException("Database connection error while changing balance. " + e);
        }
    }
    
    /**
     * Builds {@link List} of {@link Player} from given {@link ResultSet}.
     *
     * @param resultSet {@link ResultSet}
     * @return {@link List} of {@link Player}
     * @throws SQLException if a database access error occurs or this method is called on a closed
     *                      result set
     */
    private static List<Player> createUserList(ResultSet resultSet) throws SQLException {
        List<Player> playerList = new ArrayList<>();
        Player       player;
        do {
            player = buildPlayer(resultSet);
            if (player != null) {
                playerList.add(player);
            }
        } while (player != null);
        
        return playerList;
    }
    
    /**
     * Method builds {@link Player} from given ResultSet
     *
     * @param resultSet {@link ResultSet}
     * @return {@link Player}
     * @throws SQLException if the columnLabel is not valid; if a database access error occurs or
     *                      this method is called on a closed result set
     */
    private static Player buildPlayer(ResultSet resultSet) throws SQLException {
        Player player = null;
        if (resultSet.next()) {
            player = new Player();
            player.setProfile(buildPlayerProfile(resultSet));
            player.setAccount(buildPlayerAccount(resultSet));
            player.setVerification(buildPlayerVerification(resultSet));
        }
        return player;
    }
    
    /**
     * Method build {@link PlayerProfile} from given ResultSet
     *
     * @param resultSet {@link ResultSet}
     * @return {@link PlayerProfile}
     * @throws SQLException if the columnLabel is not valid; if a database access error occurs or
     *                      this method is called on a closed result set
     */
    private static PlayerProfile buildPlayerProfile(ResultSet resultSet) throws SQLException {
        PlayerProfile profile = new PlayerProfile();
        profile.setFirstName(resultSet.getString(FIRST_NAME));
        profile.setMiddleName(resultSet.getString(MIDDLE_NAME));
        profile.setLastName(resultSet.getString(LAST_NAME));
        profile.setBirthDate(resultSet.getDate(BIRTHDAY).toLocalDate());
        return profile;
    }
    
    /**
     * Method builds {@link PlayerAccount} from given ResultSet
     *
     * @param resultSet {@link ResultSet}
     * @return {@link PlayerAccount}
     * @throws SQLException if the columnLabel is not valid; if a database access error occurs or
     *                      this method is called on a closed result set
     */
    private static PlayerAccount buildPlayerAccount(ResultSet resultSet) throws SQLException {
        PlayerAccount account = new PlayerAccount();
        account.setBalance(resultSet.getBigDecimal(BALANCE));
        account.setThisMonthWithdrawal(resultSet.getBigDecimal(THIS_MONTH_WITHDRAWAL));
        PlayerStatus status = new PlayerStatus();
        status.setStatus(PlayerStatus.Status.valueOf(resultSet.getString(STATUS).toUpperCase()));
        status.setBetLimit(resultSet.getBigDecimal(BET_LIMIT));
        status.setWithdrawalLimit(resultSet.getBigDecimal(WITHDRAWAL_LIMIT));
        account.setStatus(status);
        return account;
    }
    
    /**
     * Method builds {@link PlayerVerification} from given ResultSet
     *
     * @param resultSet {@link ResultSet}
     * @return {@link PlayerVerification}
     * @throws SQLException if the columnLabel is not valid; if a database access error occurs or
     *                      this method is called on a closed result set
     */
    private static PlayerVerification buildPlayerVerification(ResultSet resultSet)
        throws SQLException {
        PlayerVerification verification = new PlayerVerification();
        verification.setPassport(resultSet.getString(PASSPORT));
        verification.setVerificationStatus(PlayerVerification.VerificationStatus.valueOf(
            resultSet.getString(VERIFICATION_STATUS).toUpperCase()));
        return verification;
    }
}