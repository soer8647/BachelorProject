package Impl.Communication.Events;

import Interfaces.Communication.Event;
import Interfaces.Transaction;

import java.net.InetAddress;
import java.util.Collection;

public class TransactionHistoryResponseEvent implements Event {
    private InetAddress inetAddress;
    private int port;
    private Collection<Transaction> transactions;
    private int index;

    public TransactionHistoryResponseEvent(InetAddress inetAddress, int port, Collection<Transaction> transactions, int requestedStartIndex) {
        this.inetAddress = inetAddress;
        this.port = port;
        this.transactions = transactions;
        this.index = requestedStartIndex;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public InetAddress getIp() {
        return inetAddress;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public Collection<Transaction> getTransactions() {
        return transactions;
    }

    /**
     * @return      The index from where the transaction history was requested
     */
    public int getIndex() {
        return index;
    }
}
