package FakeClients;

import Impl.Transactions.ArrayListTransactions;
import Interfaces.Transaction;
import Interfaces.TransactionManager;
import Interfaces.Transactions;

public class EmptyTransactionsManager implements TransactionManager {
    @Override
    public Transactions getSomeTransactions() {
        return new ArrayListTransactions();
    }

    @Override
    public void addTransaction(Transaction transaction) {
    }

    @Override
    public void removeTransactions(Transactions transactions) {

    }
}
