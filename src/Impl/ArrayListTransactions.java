package Impl;

import Impl.Hashing.SHA256;
import Interfaces.HashingAlgorithm;
import Interfaces.Transaction;
import Interfaces.Transactions;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
/*
* This class is to hold multiple transactions.
* It uses the SHA-256 hashing algorithm.
* */
public class ArrayListTransactions implements Transactions {
    private final HashingAlgorithm hashingAlgorithm = new SHA256();
    private ArrayList<Transaction> transactions;

    public ArrayListTransactions() {
        transactions = new ArrayList<>();
    }

    @Override
    public Collection getTransactions() {
        return transactions;
    }

    @Override
    public BigInteger hashTransactions() {
        return hashingAlgorithm.hash(toString());
    }

    @Override
    public int size() {
        return transactions.size();
    }

    @Override
    public void add(Transaction transaction) {
        transactions.add(transaction);
    }
}
