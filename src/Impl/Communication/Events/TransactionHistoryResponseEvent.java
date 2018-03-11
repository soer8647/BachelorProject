package Impl.Communication.Events;

import Impl.TransactionHistory;

import java.net.InetAddress;
import java.time.LocalDateTime;

/*
* An event for a server to respond to a TransactionHistoryRequest.
* */
public class TransactionHistoryResponseEvent extends ProtoEvent {
    private TransactionHistory transactions;
    private int index;
    private int part;
    private int parts;
    private LocalDateTime time;

    public TransactionHistoryResponseEvent(InetAddress inetAddress, int port, TransactionHistory transactionHistory, int requestedStartIndex, int part, int parts, LocalDateTime time) {
        super(port,inetAddress);
        this.transactions = transactionHistory;
        this.index = requestedStartIndex;
        this.part = part;
        this.parts = parts;
        this.time = time;
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

    public int getPart() {
        return part;
    }

    public int getParts() {
        return parts;
    }

    public LocalDateTime getTime() {
        return time;
    }
}

