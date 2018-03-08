package Impl.Communication.Events;

import External.Pair;
import Impl.ConfirmedTransaction;
import Interfaces.CoinBaseTransaction;
import Interfaces.Communication.Event;

import java.net.InetAddress;
import java.util.Collection;

public class TransactionHistoryResponseEvent implements Event {
    private InetAddress inetAddress;
    private int port;
    private Pair<Collection<ConfirmedTransaction>, Collection<CoinBaseTransaction>> transactions;
    private int index;

    public TransactionHistoryResponseEvent(InetAddress inetAddress, int port, Pair<Collection<ConfirmedTransaction>,Collection<CoinBaseTransaction>> transactions, int requestedStartIndex) {
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
