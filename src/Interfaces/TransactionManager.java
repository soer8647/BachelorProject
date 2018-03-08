package Interfaces;

import java.util.Collection;
import java.util.Deque;

public interface TransactionManager {

    Transactions getSomeTransactions();

    void addTransaction(Transaction transaction);

    /**
     * Call this function, when a block (and its transactions has been added to the chain, and should no longer be in the pool of new transactions
     * @param transactions the list of transactions to be removed
     */
    void removeTransactions(Transactions<Collection<Transaction>> transactions);

}
