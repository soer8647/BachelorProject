package Impl;

import Impl.Hashing.SHA256;
import Interfaces.HashingAlgorithm;
import Interfaces.Transaction;

import java.math.BigInteger;
/*
* This class is an implementation of a standard transaction of money from one user to another.
 * It uses the SHA-256 hashing function.
* */
public class StandardTransaction implements Transaction {
    private final HashingAlgorithm hashingAlgorithm = new SHA256();
    private long sender;
    private long receiver;
    private int value;
    private Transaction valueProof;
    private BigInteger signature;

    /**
     * @param sender        The "address" or id of the sender of the transaction.
     * @param receiver      The "address" or id of the receiver of the transaction.
     * @param value         The value of the transaction.
     * @param valueProof    The transaction where the sender has proof of funds for this transaction.
     * @param signature     The signature of the sender on this transaction.
     */
    public StandardTransaction(long sender, long receiver, int value, Transaction valueProof, BigInteger signature) {
        this.sender = sender;
        this.receiver = receiver;
        this.value = value;
        this.valueProof = valueProof;
        this.signature = signature;
    }

    @Override
    public BigInteger transActionHash() {
        return hashingAlgorithm.hash(toString());
    }

    public long getSender() {
        return sender;
    }

    public long getReceiver() {
        return receiver;
    }

    public int getValue() {
        return value;
    }

    public Transaction getValueProof() {
        return valueProof;
    }

    public BigInteger getSignature() {
        return signature;
    }

    @Override
    public String toString() {
        return "Sender: "+sender+
                ", receiver: "+receiver+
                ", value: "+value+
                ", hash of value proof transaction: " + valueProof.transActionHash()+
                ", signature: "+signature;
    }
}
