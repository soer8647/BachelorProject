package Impl;

import Impl.Transactions.ConfirmedTransaction;
import Interfaces.CoinBaseTransaction;

import java.io.Serializable;
import java.util.List;

public class TransactionHistory implements Serializable
{
    private List<ConfirmedTransaction> confirmedTransactions;
    private List<CoinBaseTransaction> coinBaseTransactions;

    public TransactionHistory(List<ConfirmedTransaction> confirmedTransactions, List<CoinBaseTransaction> coinBaseTransactions) {
        this.confirmedTransactions = confirmedTransactions;
        this.coinBaseTransactions = coinBaseTransactions;
    }

    public List<ConfirmedTransaction> getConfirmedTransactions() {
        return confirmedTransactions;
    }

    public List<CoinBaseTransaction> getCoinBaseTransactions() {
        return coinBaseTransactions;
    }

    public int size(){
        return coinBaseTransactions.size()+confirmedTransactions.size();
    }
}
