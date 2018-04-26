package Impl.Transactions;

import Configuration.Configuration;
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
    private BigInteger signature;
    private int timestamp; // block number


    /**
     * @param sender        The "address" or id of the sender of the transaction.
     * @param receiver      The "address" or id of the receiver of the transaction.
     * @param value         The value of the transaction.
     * @param signature     The signature of the sender on this transaction.
     * @param timestamp

     */
    public StandardTransaction(Address sender, Address receiver, int value, BigInteger signature, int timestamp) {
        this.sender = sender;
        this.receiver = receiver;
        this.value = value;
        this.signature = signature;
        this.timestamp = timestamp;
    }

    /**
     * @return      The hash of the transaction without the signature.
     */
    @Override
    public BigInteger transactionHash() {
            return Configuration.hash(sender.toString()+receiver.toString()+value+timestamp);
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

    public BigInteger getSignature() {
        return signature;
    }

    @Override
    public String toString() {
        return "Sender: "+sender+",\n"+
                "Receiver: "+receiver+",\n"+
                "Value: "+value+",\n"+
                "Timestamp: " + timestamp+",\n"+
                "Signature: "+signature+"\n";

    }


    public int getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj==this) return true;
        if (obj.getClass() != this.getClass()) return false;
        StandardTransaction t = (StandardTransaction) obj;
        return t.transactionHash().equals(this.transactionHash());
    }
}
