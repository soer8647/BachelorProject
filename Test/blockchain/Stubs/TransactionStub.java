package blockchain.Stubs;

import Interfaces.Transaction;

import java.math.BigInteger;

public class TransactionStub implements Transaction{
    @Override
    public BigInteger transActionHash() {
        return null;
    }

    @Override
    public long getSender() {
        return 0;
    }

    @Override
    public long getReceiver() {
        return 0;
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
