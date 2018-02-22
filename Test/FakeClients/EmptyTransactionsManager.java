package FakeClients;

import Impl.ArrayListTransactions;
import Interfaces.TransactionManager;
import Interfaces.Transactions;

public class EmptyTransactionsManager implements TransactionManager {
    @Override
    public Transactions getSomeTransactions() {
        return new ArrayListTransactions();
    }
}
