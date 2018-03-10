package Impl;

import Impl.Transactions.ConfirmedTransaction;
import Interfaces.CoinBaseTransaction;

import java.io.Serializable;
import java.util.Collection;

public class TransactionHistory implements Serializable
{
    private Collection<ConfirmedTransaction> confirmedTransactions;
    private Collection<CoinBaseTransaction> coinBaseTransactions;

    public TransactionHistory(Collection<ConfirmedTransaction> confirmedTransactions, Collection<CoinBaseTransaction> coinBaseTransactions) {
        this.confirmedTransactions = confirmedTransactions;
        this.coinBaseTransactions = coinBaseTransactions;
    }

    public Collection<ConfirmedTransaction> getConfirmedTransactions() {
        return confirmedTransactions;
    }

    public Collection<CoinBaseTransaction> getCoinBaseTransactions() {
        return coinBaseTransactions;
    }

    public int size(){
        return coinBaseTransactions.size()+coinBaseTransactions.size();
    }
}
