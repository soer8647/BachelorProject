package FakeClients;

import Impl.Transactions.ArrayListTransactions;
import Interfaces.Transaction;
import Interfaces.TransactionManager;
import Interfaces.Transactions;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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
        throw new NotImplementedException();
    }

    @Override
    public boolean validateTransactions(Transactions transactions) {
        throw new NotImplementedException();
    }

    @Override
    public void removeTransactions(Transactions transactions) {

    }
}
