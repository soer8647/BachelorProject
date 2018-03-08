package Impl;

import Configuration.Configuration;
import Interfaces.Block;
import Interfaces.Transaction;
import Interfaces.TransactionManager;
import Interfaces.Transactions;

import java.util.*;

public class StandardTransactionManager implements TransactionManager {
    private Queue<Transaction> transactions;

    public StandardTransactionManager() {
        transactions = new LinkedList<>();
    }

    @Override
    public Transactions getSomeTransactions() {
        Transactions result = new ArrayListTransactions();
        Iterator<Transaction> iter = transactions.iterator();
        for (int i = 0; i < Configuration.getTransactionLimit(); i++) {
            if (!iter.hasNext()) {
                break;
            }
            Transaction tran = iter.next();
            result.add(tran);
        }
        return result;
    }

    @Override
    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }

    @Override
    public void removeTransactions(Transactions<Collection<Transaction>> toBeRemovedTransactions) {
        this.transactions.removeAll(toBeRemovedTransactions.getTransactions());
    }
}
