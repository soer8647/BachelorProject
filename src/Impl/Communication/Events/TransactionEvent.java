package Impl.Communication.Events;

import Interfaces.Communication.Event;
import Interfaces.Transaction;

import java.net.InetAddress;

public class TransactionEvent extends ProtoEvent {
    private Transaction transaction;

    public Transaction getTransaction() {
        return transaction;
    }

    public TransactionEvent(Transaction transaction, int port, InetAddress ip) {
        super(port, ip);
        this.transaction = transaction;
    }
}
