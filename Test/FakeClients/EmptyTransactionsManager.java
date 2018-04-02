package FakeClients;

import Impl.ToBeImplementedException;
import Interfaces.Transaction;
import Interfaces.TransactionManager;

import java.util.ArrayList;
import java.util.Collection;

public class EmptyTransactionsManager implements TransactionManager {
    @Override
    public Collection<Transaction> getSomeTransactions() {
        return new ArrayList<>();
    }

    @Override
    public void addTransaction(Transaction transaction) {
    }

    @Override
    public boolean validateTransaction(Transaction transaction) {
        throw new ToBeImplementedException();
    }

    @Override
    public boolean validateTransactions(Collection<Transaction> transactions) {
        throw new ToBeImplementedException();
    }

    @Override
    public void removeTransactions(Collection<Transaction> transactions) {

    }
}
