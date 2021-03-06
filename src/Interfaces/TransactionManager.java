package Interfaces;

import java.util.Collection;

public interface TransactionManager {

    Collection<Transaction> getSomeTransactions();

    boolean addTransaction(Transaction transaction);

    /**
     * Call this function, when a block (and its transactions has been added to the chain, and should no longer be in the pool of new transactions
     * @param transactions the list of transactions to be removed
     */
    void removeTransactions(Collection<Transaction> transactions);

    boolean validateTransaction(Transaction transaction);

    boolean validateTransactions(Collection<Transaction> transactions);

}
