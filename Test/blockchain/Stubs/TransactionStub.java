package blockchain.Stubs;

import Interfaces.Address;
import Interfaces.Transaction;

import java.math.BigInteger;

public class TransactionStub implements Transaction{
    @Override
    public BigInteger transActionHash() {
        return new BigInteger("404");
    }

    @Override
    public Address getSenderAddress() {
        return null;
    }

    @Override
    public Address getReceiverAddress() {
        return null;
    }


    @Override
    public int getValue() {
        return 0;
    }

    @Override
    public BigInteger getValueProof() {
        return new BigInteger("8008");
    }

    @Override
    public int getBlockNumberOfValueProof() {
        return 0;
    }

    @Override
    public BigInteger getSignature() {
        return null;
    }
}
