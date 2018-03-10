package Impl.Communication.Events;

import External.Pair;
import Impl.Transactions.ConfirmedTransaction;
import Interfaces.CoinBaseTransaction;

import java.net.InetAddress;
import java.util.Collection;
/*
* An event for a server to respond to a TransactionHistoryRequest.
* */
public class TransactionHistoryResponseEvent extends ProtoEvent {
    private Pair<Collection<ConfirmedTransaction>, Collection<CoinBaseTransaction>> transactions;
    private int index;

    public TransactionHistoryResponseEvent(InetAddress inetAddress, int port, Pair<Collection<ConfirmedTransaction>,Collection<CoinBaseTransaction>> transactions, int requestedStartIndex) {
        super(port,inetAddress);
        this.transactions = transactions;
        this.index = requestedStartIndex;
    }


    public Pair<Collection<ConfirmedTransaction>, Collection<CoinBaseTransaction>> getTransactions() {
        return transactions;
    }

    /**
     * @return      The index from where the transaction history was requested
     */
    public int getIndex() {
        return index;
    }
}
