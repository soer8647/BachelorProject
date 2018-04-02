package Impl.Transactions;

import Configuration.Configuration;
import Interfaces.Address;
import Interfaces.Transaction;
import Interfaces.VerifiableTransaction;

import java.math.BigInteger;
/*
* A ConfirmedTransaction is a transaction that has been put on the blockchain and therefore has a block number.
* This type of transaction is for communicating a transaction that has been verified and is valid.
* */
public class ConfirmedTransaction implements Transaction, VerifiableTransaction {
    private Address sender;
    private Address receiver;
    private int value;
    private BigInteger valueProof;
    private BigInteger signature;
    private int blockNumberValueProof;
    private int blockNumber;
    private int timestamp;


    public ConfirmedTransaction(Transaction transaction,int blockNumber) {
        sender= transaction.getSenderAddress();
        receiver=transaction.getReceiverAddress();
        value=transaction.getValue();
        valueProof=transaction.getValueProof();
        signature = transaction.getSignature();
        blockNumberValueProof=transaction.getBlockNumberOfValueProof();
        timestamp = transaction.getTimestamp();
        this.blockNumber = blockNumber;
    }

    /**
     * @return      The hash of the transaction without the signature.
     */
    @Override
    public BigInteger transactionHash() {
        return Configuration.hash(sender.toString()+receiver.toString()+value+valueProof.toString()+timestamp);
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
    public int getTimestamp() {
        return this.timestamp;
    }

    @Override
    public String toString() {
        return "Sender: "+sender+",\n"+
                "Receiver: "+receiver+",\n"+
                "Value: "+value+",\n"+
                "Hash of value proof transaction: " + valueProof+",\n"+
                "Signature: "+signature+"\n";
    }

    public int getBlockNumber() {
        return blockNumber;
    }


}
