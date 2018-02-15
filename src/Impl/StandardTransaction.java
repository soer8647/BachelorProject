package Impl;

import Interfaces.Address;
import Interfaces.Transaction;

import java.math.BigInteger;
/*
* This class is an implementation of a standard transaction of money from one user to another.
 * It uses the SHA-256 hashing function.
* */
public class StandardTransaction implements Transaction {
    private Address sender;
    private Address receiver;
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
    public StandardTransaction(Address sender, Address receiver, int value, Transaction valueProof, BigInteger signature) {
        this.sender = sender;
        this.receiver = receiver;
        this.value = value;
        this.valueProof = valueProof;
        this.signature = signature;
    }

    @Override
    public BigInteger transActionHash() {
        return Global.hash(toString());
    }

    @Override
    public Address getSenderAddress() {
        return sender;
    }

    @Override
    public Address getReceiverAddress() {
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
        return "Sender: "+sender+",\n"+
                "Receiver: "+receiver+",\n"+
                "Value: "+value+",\n"+
                "Hash of value proof transaction: " + valueProof.transActionHash()+",\n"+
                "Signature: "+signature+"\n";
    }
}
