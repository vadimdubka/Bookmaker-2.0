package com.dubatovka.app.service.impl;

import com.dubatovka.app.dao.PlayerDAO;
import com.dubatovka.app.dao.TransactionDAO;
import com.dubatovka.app.dao.exception.DAOException;
import com.dubatovka.app.dao.impl.DAOProvider;
import com.dubatovka.app.entity.Transaction;
import com.dubatovka.app.service.TransactionService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import static com.dubatovka.app.config.ConfigConstant.ALL;
import static com.dubatovka.app.config.ConfigConstant.EMPTY_STRING;
import static com.dubatovka.app.config.ConfigConstant.MESSAGE_ERR_SQL_TRANSACTION;
import static com.dubatovka.app.config.ConfigConstant.PERCENT;

/**
 * The class provides implementation for Service layer actions with Transactions.
 *
 * @author Dubatovka Vadim
 */
class TransactionServiceImpl extends TransactionService {
    private static final Logger logger = LogManager.getLogger(TransactionServiceImpl.class);
    
    private final TransactionDAO transactionDAO = daoProvider.getTransactionDAO();
    private final PlayerDAO      playerDAO      = daoProvider.getPlayerDAO();
    
    /**
     * Default constructor.
     */
    TransactionServiceImpl() {
    }
    
    /**
     * Constructs instance using definite {@link DAOProvider} object.
     */
    TransactionServiceImpl(DAOProvider daoProvider) {
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
    @Override
    public List<Transaction> getPlayerTransactions(int id, String month) {
        List<Transaction> transactionList = null;
        String            monthPattern    = (month != null ? month.trim() : EMPTY_STRING) + PERCENT;
        try {
            transactionList = transactionDAO.takePlayerTransactions(id, monthPattern);
        } catch (DAOException e) {
            logger.log(Level.ERROR, e.getMessage());
        }
        return transactionList;
    }
    
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
    @Override
    public List<Transaction> getTransactionList(String filterByType, String month,
                                                boolean isSortByAmount) {
        List<Transaction> transactionList = null;
        String            monthPattern    = (month != null ? month.trim() : EMPTY_STRING) + PERCENT;
        if ((filterByType == null) || filterByType.trim().isEmpty()) {
            filterByType = ALL;
        }
        try {
            transactionList = transactionDAO.takeTransactionList(monthPattern);
        } catch (DAOException e) {
            logger.log(Level.ERROR, e.getMessage());
        }
        if (!ALL.equals(filterByType.trim())) {
            filterByType(transactionList, Transaction.TransactionType
                                              .valueOf(filterByType.trim().toUpperCase()));
        }
        if (isSortByAmount) {
            sortByAmount(transactionList, false);
        }
        return transactionList;
    }
    
    /**
     * Defines max payment value from {@link List} collection of {@link Transaction} objects.
     *
     * @param transactions {@link List} collection of {@link Transaction} objects to be filtered
     * @return defined {@link BigDecimal} value
     */
    @Override
    public BigDecimal defineMaxPayment(List<Transaction> transactions) {
        BigDecimal maxPayment = BigDecimal.ZERO;
        if (transactions != null) {
            for (Transaction transaction : transactions) {
                BigDecimal                  amount = transaction.getAmount();
                Transaction.TransactionType type   = transaction.getType();
                if ((type == Transaction.TransactionType.REPLENISH)
                        && (maxPayment.compareTo(amount) < 0)) {
                    maxPayment = amount;
                }
            }
        }
        return maxPayment;
    }
    
    /**
     * Counts total payment value from {@link List} collection of {@link Transaction} objects.
     *
     * @param transactions {@link List} collection of {@link Transaction} objects to be filtered
     * @return counted {@link BigDecimal} value
     */
    @Override
    public BigDecimal countTotalPayment(List<Transaction> transactions) {
        BigDecimal totalPayment = BigDecimal.ZERO;
        if (transactions != null) {
            for (Transaction transaction : transactions) {
                BigDecimal                  amount = transaction.getAmount();
                Transaction.TransactionType type   = transaction.getType();
                if (type == Transaction.TransactionType.REPLENISH) {
                    totalPayment = totalPayment.add(amount);
                }
            }
        }
        return totalPayment;
    }
    
    /**
     * Defines max withdrawal value from {@link List} collection of {@link Transaction} objects.
     *
     * @param transactions {@link List} collection of {@link Transaction} objects to be filtered
     * @return defined {@link BigDecimal} value
     */
    @Override
    public BigDecimal defineMaxWithdrawal(List<Transaction> transactions) {
        BigDecimal maxWithdrawal = BigDecimal.ZERO;
        if (transactions != null) {
            for (Transaction transaction : transactions) {
                BigDecimal                  amount = transaction.getAmount();
                Transaction.TransactionType type   = transaction.getType();
                if ((type == Transaction.TransactionType.WITHDRAW)
                        && (maxWithdrawal.compareTo(amount) < 0)) {
                    maxWithdrawal = amount;
                }
            }
        }
        return maxWithdrawal;
    }
    
    /**
     * Counts total withdrawal value from {@link List} collection of {@link Transaction} objects.
     *
     * @param transactions {@link List} collection of {@link Transaction} objects to be filtered
     * @return counted {@link BigDecimal} value
     */
    @Override
    public BigDecimal countTotalWithdrawal(List<Transaction> transactions) {
        BigDecimal totalWithdrawal = BigDecimal.ZERO;
        if (transactions != null) {
            for (Transaction transaction : transactions) {
                BigDecimal                  amount = transaction.getAmount();
                Transaction.TransactionType type   = transaction.getType();
                if (type == Transaction.TransactionType.WITHDRAW) {
                    totalWithdrawal = totalWithdrawal.add(amount);
                }
            }
        }
        return totalWithdrawal;
    }
    
    @Override
    public int makeTransaction(int playerId, BigDecimal amount,
                               Transaction.TransactionType transactionType) {
        int result = 0;
        try {
            daoProvider.beginTransaction();
            int transactId = transactionDAO.insertTransaction(playerId, amount, transactionType);
            boolean isBalUpd = playerDAO.updateBalance(playerId, amount, transactionType);
            if ((transactId != 0) && isBalUpd) {
                daoProvider.commit();
                result = transactId;
            } else {
                daoProvider.rollback();
            }
        } catch (DAOException e) {
            logger.log(Level.ERROR, e.getMessage());
        } catch (SQLException e) {
            logger.log(Level.ERROR, MESSAGE_ERR_SQL_TRANSACTION + e);
        }
        return result;
    }
    
    /**
     * Filters {@link List} collection of {@link Transaction} objects by removing {@link
     * Transaction} objects of {@link Transaction.TransactionType} different from given.
     *
     * @param list {@link List} collection of {@link Transaction} objects to be filtered
     * @param type {@link Transaction.TransactionType} value of {@link Transaction#type} field with
     *             which {@link Transaction} objects to keep
     * @see List#removeIf(Predicate)
     */
    private static void filterByType(List<Transaction> list,
                                     Transaction.TransactionType type) {
        if ((list == null) || list.isEmpty()) {
            return;
        }
        list.removeIf(s -> ((s == null) || (s.getType() != type)));
    }
    
    /**
     * Sorts {@link List} collection of {@link Transaction} objects by {@link Transaction#amount}
     * field values.
     *
     * @param list      {@link List} collection of {@link Transaction} objects to be sorted
     * @param ascending marker of sort order
     */
    private static void sortByAmount(List<Transaction> list, boolean ascending) {
        if ((list == null) || list.isEmpty()) {
            return;
        }
        Comparator<Transaction> comparator = new AmountComparator();
        if (!ascending) {
            comparator = comparator.reversed();
        }
        Collections.sort(list, comparator);
    }
    
    private static class AmountComparator implements Comparator<Transaction>, Serializable {
        
        private static final long serialVersionUID = 5978914129661308184L;
        
        /**
         * Compares its two arguments for order.  Returns pressedKey negative integer, zero, or
         * pressedKey positive integer as the first argument is less than, equal to, or greater than
         * the second. <p>In the foregoing description, the notation <tt>sgn(</tt><i>expression</i><tt>)</tt>
         * designates the mathematical <i>signum</i> function, which is defined to return one of
         * <tt>-1</tt>, <tt>0</tt>, or <tt>1</tt> according to whether the value of
         * <i>expression</i> is negative, zero or positive. <p>The implementor must ensure that
         * <tt>sgn(compare(x, y)) == -sgn(compare(y, x))</tt> for all <tt>x</tt> and <tt>y</tt>.
         * (This implies that <tt>compare(x, y)</tt> must throw an exception if and only if
         * <tt>compare(y, x)</tt> throws an exception.) <p>The implementor must also ensure that the
         * relation is transitive: <tt>((compare(x, y)&gt;0) &amp;&amp; (compare(y, z)&gt;0))</tt>
         * implies <tt>compare(x, z)&gt;0</tt>. <p>Finally, the implementor must ensure that
         * <tt>compare(x, y)==0</tt> implies that <tt>sgn(compare(x, z))==sgn(compare(y, z))</tt>
         * for all <tt>z</tt>. <p>It is generally the case, but <i>not</i> strictly required that
         * <tt>(compare(x, y)==0) == (x.equals(y))</tt>.  Generally speaking, any comparator that
         * violates this condition should clearly indicate this fact.  The recommended language is
         * "Note: this comparator imposes orderings that are inconsistent with equals." <p>Compares
         * {@link Transaction} objects due to their {@link Transaction#amount} field values
         *
         * @param o1 the first object to be compared.
         * @param o2 the second object to be compared.
         * @return pressedKey negative integer, zero, or pressedKey positive integer as the first
         * argument is less than, equal to, or greater than the second.
         * @throws NullPointerException if an argument is null and this comparator does not permit
         *                              null arguments
         * @throws ClassCastException   if the arguments' types prevent them from being compared by
         *                              this comparator.
         */
        @Override
        public int compare(Transaction o1, Transaction o2) {
            if ((o1 == null) || (o1.getAmount() == null)) {
                return -1;
            }
            if ((o2 == null) || (o2.getAmount() == null)) {
                return 1;
            }
            return o1.getAmount().compareTo(o2.getAmount());
        }
    }
}
