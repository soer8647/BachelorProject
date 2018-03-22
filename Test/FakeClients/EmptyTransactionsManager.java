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
    public boolean validateTransaction(Transaction transaction) {
        throw new ToBeImplementedException();
    }

    @Override
    public boolean validateTransactions(Transactions transactions) {
        throw new ToBeImplementedException();
    }

    @Override
    public void removeTransactions(Transactions transactions) {

    }
}
