package blockchain.Stubs;

import Interfaces.Transaction;

import java.math.BigInteger;

public class TransactionStub implements Transaction{
    @Override
    public BigInteger transActionHash() {
        return null;
    }

    @Override
    public BigInteger getSenderAddress() {
        return null;
    }

    @Override
    public BigInteger getReceiverAddress() {
        return null;
    }


    @Override
    public int getValue() {
        return 0;
    }

    @Override
    public Transaction getValueProof() {
        return null;
    }
}
