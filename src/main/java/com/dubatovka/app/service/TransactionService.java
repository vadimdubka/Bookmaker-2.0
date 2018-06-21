package com.dubatovka.app.service;

import com.dubatovka.app.dao.impl.DAOProvider;
import com.dubatovka.app.entity.Transaction;

import java.math.BigDecimal;
import java.util.List;

/**
 * The class provides abstraction for Service layer actions with Transactions.
 *
 * @author Dubatovka Vadim
 */
public abstract class TransactionService extends DAOProviderHolder {
    /**
     * Default constructor.
     */
    protected TransactionService() {
    }
    
    /**
     * Constructs instance using definite {@link DAOProvider} object.
     */
    protected TransactionService(DAOProvider daoProvider) {
        super(daoProvider);
    }
    
    /**
     * Calls DAO layer to take {@link List} collection of definite player {@link Transaction}
     * objects which were processed on given month.
     *
     * @param id    player id
     * @param month string representation of month value in format 'yyyy-mm'
     * @return taken {@link List} collection
     */
    public abstract List<Transaction> getPlayerTransactions(int id, String month);
    
    /**
     * Calls DAO layer to take {@link List} collection of {@link Transaction} objects due to given
     * parameters.
     *
     * @param filterByType   string representation of {@link Transaction.TransactionType} value
     *                       instance or 'all'
     * @param month          string representation of month value in format 'yyyy-mm'
     * @param isSortByAmount is need to sort result collection by {@link Transaction#amount}
     * @return taken {@link List} collection
     */
    public abstract List<Transaction> getTransactionList(String filterByType,
                                                         String month, boolean isSortByAmount);
    
    /**
     * Defines max payment value from {@link List} collection of {@link Transaction} objects.
     *
     * @param transactions {@link List} collection of {@link Transaction} objects to be filtered
     * @return defined {@link BigDecimal} value
     */
    public abstract BigDecimal defineMaxPayment(List<Transaction> transactions);
    
    /**
     * Counts total payment value from {@link List} collection of {@link Transaction} objects.
     *
     * @param transactions {@link List} collection of {@link Transaction} objects to be filtered
     * @return counted {@link BigDecimal} value
     */
    public abstract BigDecimal countTotalPayment(List<Transaction> transactions);
    
    /**
     * Defines max withdrawal value from {@link List} collection of {@link Transaction} objects.
     *
     * @param transactions {@link List} collection of {@link Transaction} objects to be filtered
     * @return defined {@link BigDecimal} value
     */
    public abstract BigDecimal defineMaxWithdrawal(List<Transaction> transactions);
    
    /**
     * Counts total withdrawal value from {@link List} collection of {@link Transaction} objects.
     *
     * @param transactions {@link List} collection of {@link Transaction} objects to be filtered
     * @return counted {@link BigDecimal} value
     */
    public abstract BigDecimal countTotalWithdrawal(List<Transaction> transactions);
    
    /**
     * Calls DAO layer to make an account transaction of definite
     * {@link Transaction.TransactionType}.
     *
     * @param playerId        player's id who processes transaction
     * @param amount          amount of money player transacts
     * @param transactionType type of transaction
     * @return true if transaction proceeded successfully
     */
    public abstract int makeTransaction(int playerId, BigDecimal amount,
                                        Transaction.TransactionType transactionType);
}
