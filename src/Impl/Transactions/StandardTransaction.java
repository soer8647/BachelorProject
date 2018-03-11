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
    private BigInteger valueProof;
    private BigInteger signature;
    private int blockNumberValueProof;


    /**
     * @param sender        The "address" or id of the sender of the transaction.
     * @param receiver      The "address" or id of the receiver of the transaction.
     * @param value         The value of the transaction.
     * @param valueProof    The transaction where the sender has proof of funds for this transaction.
     * @param signature     The signature of the sender on this transaction.
     * @param blockNumberValueProof     The block number where there is proof of funds.

     */
    public StandardTransaction(Address sender, Address receiver, int value, BigInteger valueProof, BigInteger signature, int blockNumberValueProof) {
        this.sender = sender;
        this.receiver = receiver;
        this.value = value;
        this.valueProof = valueProof;
        this.signature = signature;
        this.blockNumberValueProof = blockNumberValueProof;
    }

    /**
     * @return      The hash of the transaction without the signature.
     */
    @Override
    public BigInteger transActionHash() {
            return Configuration.hash(sender.toString()+receiver.toString()+value+valueProof.toString());
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

    /**
     *  @return         The hash of the transaction where there is proof of funds
     */
    public BigInteger getValueProof() {
        return valueProof;
    }

    @Override
    public int getBlockNumberOfValueProof() {
        return blockNumberValueProof;
    }

    public BigInteger getSignature() {
        return signature;
    }

    @Override
    public String toString() {
        return "Sender: "+sender+",\n"+
                "Receiver: "+receiver+",\n"+
                "Value: "+value+",\n"+
                "Hash of value proof transaction: " + valueProof+",\n"+
                "Signature: "+signature+"\n";
    }


}
