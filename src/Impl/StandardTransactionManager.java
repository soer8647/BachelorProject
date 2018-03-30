package Impl;

import Configuration.Configuration;
import Crypto.Interfaces.PublicKeyCryptoSystem;
import Impl.Transactions.ArrayListTransactions;
import Impl.Transactions.ConfirmedTransaction;
import Interfaces.*;

import java.util.*;
import java.util.stream.Collectors;

public class StandardTransactionManager implements TransactionManager {
    private Queue<Transaction> transactions;
    private Node node;
    private BlockChain blockChain;

    public StandardTransactionManager(BlockChain blockChain) {
        this.blockChain = blockChain;
        transactions = new LinkedList<>();
    }


    /**
     * The method where the transactions are chosen if they are valid and if no other transaction has the same value proof.
     *
     * @return Transactions that are valid and ready to be mined in a block. The size is dependent on the configured transaction limit.
     */
    @Override
    public Transactions getSomeTransactions() {
        Transactions<ArrayList<Transaction>> result = new ArrayListTransactions();
        Iterator<Transaction> iter = transactions.iterator();
        while (result.size() < Configuration.getTransactionLimit()) {
            if (!iter.hasNext()) {
                break;
            }
            Transaction tran = iter.next();
            // The transaction might have been spend
            if (validateTransaction(tran)){
                // Check if two or more transactions in same batch uses the same value proof
                ArrayList filtered = result.getTransactions().stream()
                        .filter(t-> t.getValueProof().toString().equals(tran.getValueProof().toString())).collect(Collectors.toCollection(ArrayList<Transaction>::new));
                // Only add a transaction if it no other transaction in result has the same value proof.
                if (filtered.size()==0) {
                    result.add(tran);
                }

            }
        }
        return result;
    }

    /**
     * The method is called when a transaction is to be added to the transaction manager. Validate that the transaction is valid.
     *
     * @param transaction       The transaction to add.
     */
    @Override
    public void addTransaction(Transaction transaction) {
        if (validateTransaction(transaction)){
            this.transactions.add(transaction);
        }else {
            System.out.println(" Invalid transaction received");
        }
    }

    @Override
    public void removeTransactions(Transactions<Collection<Transaction>> toBeRemovedTransactions) {
        this.transactions.removeAll(toBeRemovedTransactions.getTransactions());
    }

    @Override
    public boolean validateTransaction(Transaction transaction) {
        if(!verifyTransactionSignature(transaction)) {
            return false;
        }

        TransactionHistory transactions = blockChain.getTransactionHistory(transaction.getSenderAddress(),transaction.getBlockNumberOfValueProof());
        int counter = 0;

        ArrayList<VerifiableTransaction> verifiableTransactions = new ArrayList<>();
        verifiableTransactions.addAll(transactions.getConfirmedTransactions());
        verifiableTransactions.addAll(transactions.getCoinBaseTransactions());
        verifiableTransactions.sort(Comparator.comparing(VerifiableTransaction::getBlockNumber).reversed());

        for (VerifiableTransaction vt : verifiableTransactions){
            if (vt instanceof CoinBaseTransaction){
                counter+=vt.getValue();
                if(counter>=transaction.getValue()) return true;
            }else if (vt instanceof ConfirmedTransaction){
                ConfirmedTransaction ct = ((ConfirmedTransaction) vt);
                if(ct.getSenderAddress().toString().equals(transaction.getSenderAddress().toString())){
                    // The sender spends money
                    counter-=vt.getValue();
                }else if (ct.getReceiverAddress().toString().equals(transaction.getSenderAddress().toString())){
                    // Sender gets money
                    counter+=vt.getValue();
                    if(counter>=transaction.getValue()) return true;
                }
            }
        }

        // If we looked at all the transactions since the proof of funds transaction
        // and it does not sum to at least the transaction value, the transaction is invalid.
        return false;
    }

    @Override
    public boolean validateTransactions(Transactions<Collection<Transaction>> transactions) {
        //For each transaction
        for (Transaction t: transactions.getTransactions()) {
            if(!validateTransaction(t)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param transaction       The transaction to verify the signature on
     * @return                  True if there is a valid transaction signature.
     */
    private boolean verifyTransactionSignature(Transaction transaction) {
        PublicKeyCryptoSystem cs = Configuration.getCryptoSystem();
        return cs.verify(transaction.getSenderAddress().getPublicKey(),transaction.getSignature(),transaction.transactionHash());
    }

    public Queue<Transaction> getTransactions() {
        return transactions;
    }
}
