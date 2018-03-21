package Impl;

import Configuration.Configuration;
import Crypto.Interfaces.PublicKeyCryptoSystem;
import Impl.Transactions.ArrayListTransactions;
import Impl.Transactions.ConfirmedTransaction;
import Interfaces.*;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class StandardTransactionManager implements TransactionManager {
    private Queue<Transaction> transactions;
    private Node node;
    private BlockChain blockChain;

    public StandardTransactionManager(BlockChain blockChain) {
        this.blockChain = blockChain;
        transactions = new LinkedList<>();
    }

    @Override
    public Transactions getSomeTransactions() {
        //TODO VERIFY TRANSACTION. incomming blocks could have invalidated some transactions
        Transactions result = new ArrayListTransactions();
        Iterator<Transaction> iter = transactions.iterator();
        for (int i = 0; i < Configuration.getTransactionLimit(); i++) {
            if (!iter.hasNext()) {
                break;
            }
            Transaction tran = iter.next();
            result.add(tran);
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
        //TODO VERIFY TRANSACTION validate
        this.transactions.add(transaction);
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
        int valueToVerify = transaction.getValue();
        int counter = 0;

        Object[] trans = transactions.getConfirmedTransactions().toArray();
        Object[] coinBases = transactions.getCoinBaseTransactions().toArray();
        int coinBaseElementNr = coinBases.length-1;

        for (int i=trans.length-1;i>=0;i--){
            CoinBaseTransaction cbt;
            ConfirmedTransaction tr = (ConfirmedTransaction) trans[i];
            if (coinBaseElementNr>=0){
                cbt = ((CoinBaseTransaction)coinBases[coinBaseElementNr]);
                if(cbt.getBlockNumber()>=tr.getBlockNumber()){
                    counter+=cbt.getValue();
                    coinBaseElementNr--;
                }
            }
            if (tr.getReceiverAddress().toString().equals(transaction.getSenderAddress().toString())){
                counter+=tr.getValue();
                if (counter>=valueToVerify) return true;
            }else if(tr.getSenderAddress().toString().equals(transaction.getSenderAddress().toString())){
                counter -=tr.getValue();
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
        return cs.verify(transaction.getSenderAddress().getPublicKey(),transaction.getSignature(),transaction.transActionHash());
    }
}
