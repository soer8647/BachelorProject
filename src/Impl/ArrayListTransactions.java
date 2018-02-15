package Impl;

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
        return Global.hash(toString());
    }

    @Override
    public int size() {
        return transactions.size();
    }

    @Override
    public void add(Transaction transaction) {
        transactions.add(transaction);
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append("Transactions:\n");
        for (Transaction t : transactions){
            builder.append("\t"+t.toString()+"\n");
        }
        return builder.toString();
    }
}
