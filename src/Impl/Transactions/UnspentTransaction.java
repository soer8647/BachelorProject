package Impl.Transactions;

import Interfaces.Address;

import java.math.BigInteger;

public class UnspentTransaction {

    private int valueLeft;
    private BigInteger unspentTransactionHash;
    private boolean isCoinBase;
    private int blockNumber;
    private Address receiver;

    public UnspentTransaction(int valueLeft, BigInteger unspentTransactionHash, boolean isCoinBase, int blockNumber, Address receiver) {
        this.valueLeft = valueLeft;
        this.unspentTransactionHash = unspentTransactionHash;
        this.isCoinBase = isCoinBase;
        this.blockNumber = blockNumber;
        this.receiver = receiver;
    }


    public int getValueLeft() {
        return valueLeft;
    }

    public BigInteger getUnspentTransactionHash() {
        return unspentTransactionHash;
    }

    public boolean isCoinBase() {
        return isCoinBase;
    }

    public int getBlockNumber() {
        return blockNumber;
    }

    public Address getReceiver() {
        return receiver;
    }
}
