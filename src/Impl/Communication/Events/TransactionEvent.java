package Impl.Communication.Events;

import Interfaces.Communication.Event;
import Interfaces.Transaction;

public class TransactionEvent implements Event {
    private Transaction transaction;

    public Transaction getTransaction() {
        return transaction;
    }

    public TransactionEvent(Transaction transaction) {
        this.transaction = transaction;
    }
}
