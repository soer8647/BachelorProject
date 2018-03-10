package Impl.Communication.Events;

import Impl.TransactionHistory;

import java.net.InetAddress;
/*
* An event for a server to respond to a TransactionHistoryRequest.
* */
public class TransactionHistoryResponseEvent extends ProtoEvent {
    private TransactionHistory transactions;
    private int index;

    public TransactionHistoryResponseEvent(InetAddress inetAddress, int port, TransactionHistory transactionHistory, int requestedStartIndex) {
        super(port,inetAddress);
        this.transactions = transactionHistory;
        this.index = requestedStartIndex;
    }


    public TransactionHistory getTransactions() {
        return transactions;
    }

    /**
     * @return      The index from where the transaction history was requested
     */
    public int getIndex() {
        return index;
    }
}
