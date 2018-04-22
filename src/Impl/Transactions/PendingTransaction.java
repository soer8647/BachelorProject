package Impl.Transactions;

import Interfaces.Address;
import Interfaces.Transaction;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class PendingTransaction implements Transaction {
    private Transaction transaction;
    /**
     * The time this transaction was made on the account side. Used for confirmation or discarding a transaction.
     */
    private LocalDateTime time;

    public PendingTransaction(Transaction transaction, LocalDateTime time) {
        this.transaction = transaction;
        this.time = time;
    }

    @Override
    public BigInteger transactionHash() {
        return transaction.transactionHash();
    }

    @Override
    public Address getSenderAddress() {
        return transaction.getSenderAddress();
    }

    @Override
    public Address getReceiverAddress() {
        return transaction.getSenderAddress();
    }

    @Override
    public int getValue() {
        return transaction.getValue();
    }

    @Override
    public BigInteger getValueProof() {
        return transaction.getValueProof();
    }

    @Override
    public int getBlockNumberOfValueProof() {
        return transaction.getBlockNumberOfValueProof();
    }

    @Override
    public BigInteger getSignature() {
        return transaction.getSignature();
    }

    /**
     * @return      The block number when this transaction was made.
     */
    @Override
    public int getTimestamp() {
        return transaction.getTimestamp();
    }

    public LocalDateTime getTime() {
        return time;
    }

    public Transaction getTransaction(){
        return transaction;
    }
}
