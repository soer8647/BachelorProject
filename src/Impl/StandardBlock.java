package Impl;

import Configuration.Configuration;
import Interfaces.Block;
import Interfaces.CoinBaseTransaction;
import Interfaces.Transaction;

import java.math.BigInteger;
import java.util.Collection;

public class StandardBlock implements Block {


    private BigInteger nonce;
    private int hardnessParameter;
    private BigInteger previousHash;
    private int transactionLimit;
    private Collection<Transaction> transactions;
    private int blockNumber;
    private CoinBaseTransaction coinBaseTransaction;

    public StandardBlock(BigInteger nonce,
                         int hardnessParameter,
                         BigInteger previousHash,
                         int transactionLimit,
                         Collection<Transaction> transactions,
                         int blockNumber,
                         CoinBaseTransaction coinbase) {
        this.nonce = nonce;
        this.hardnessParameter = hardnessParameter;
        this.previousHash = previousHash;
        this.transactionLimit = transactionLimit;
        this.transactions = transactions;
        this.blockNumber=blockNumber;
        this.coinBaseTransaction = coinbase;
    }

    @Override
    public Collection<Transaction> getTransactions() {
        return transactions;
    }

    @Override
    public int getBlockNumber() {
        return blockNumber;
    }

    @Override
    public BigInteger getNonce() {
        return nonce;
    }

    @Override
    public int getHardnessParameter() {
        return hardnessParameter;
    }

    @Override
    public BigInteger getPreviousHash() {
        return previousHash;
    }

    @Override
    public int getTransactionLimit() {
        return transactionLimit;
    }

    @Override
    public BigInteger hash() {
        StringBuilder sb = new StringBuilder();
        for (Transaction t: transactions){
            sb.append(t.transactionHash());
        }


        return new BigInteger(String.valueOf(Configuration.hash(
                previousHash.toString()
                        + sb.toString()
                        + nonce.toString()
                        +coinBaseTransaction.toString())));
    }

    @Override
    public CoinBaseTransaction getCoinBase() {
        return coinBaseTransaction;
    }

    @Override
    public String toString() {
        return "Block "+getBlockNumber()+"\n"
                + "Previous Hash: " + previousHash + "\n"
                + "CoinBase: " + coinBaseTransaction
                + transactions + "\n"
                + "My Hash: " + hash();
    }
}
