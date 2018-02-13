package Interfaces;

import java.math.BigInteger;
import java.util.Collection;

/*
* This interface is suppose to be a datastructure for holding transactions. The implemented class decides on how to hash the transactions.
* */
public interface Transactions<T extends Collection> {

    /**
     * @return      All the transactions stored in this datastructure.
     */
    T getTransactions();

    /**
     * @return      The hash of all transactions
     */
    BigInteger hashTransactions();

    int size();

    void add(Transaction transaction);
}
