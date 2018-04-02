package Impl;

import Configuration.Configuration;
import Crypto.Interfaces.PublicKeyCryptoSystem;
import Impl.Transactions.UnspentTransaction;
import Interfaces.Address;
import Interfaces.Transaction;
import Interfaces.TransactionManager;

import java.util.*;

public class DBTransactionManager implements TransactionManager{
    private BlockChainDatabase blockChainDatabase;
    private Queue<Transaction> transactionQueue;

    public DBTransactionManager(BlockChainDatabase blockChainDatabase) {
        this.blockChainDatabase = blockChainDatabase;
        transactionQueue = new LinkedList<>();
    }

    @Override
    public Collection<Transaction> getSomeTransactions() {
        Collection<Transaction> result = new ArrayList<>();
        Iterator<Transaction> iter = transactionQueue.iterator();
        HashMap<Address,Integer> senderMap = new HashMap<>();
        while (iter.hasNext() && result.size()<Configuration.getTransactionLimit()){
            Transaction t = iter.next();
            //Check if the transaction exists in the block chain
            if (blockChainDatabase.doesTransactionExist(t)){
                iter.remove();
                continue;
            }
            if (!senderMap.keySet().contains(t.getSenderAddress())){
                //Put sender and balance - t_value in map
                int balance = blockChainDatabase.getBalance(t.getSenderAddress());
                if (balance>=t.getValue()) {
                    result.add(t);
                    senderMap.put(t.getSenderAddress(),balance-t.getValue());
                }
                //TODO MAYBE REMOVE THEM AT SOME POINT
            }else {
                int balance = senderMap.get(t.getSenderAddress());
                if (balance>=t.getValue()) {
                    senderMap.replace(t.getSenderAddress(),balance-t.getValue());
                    result.add(t);
                }
            }

        }
        return result;
    }

    @Override
    public boolean addTransaction(Transaction transaction) {
        if (validateTransaction(transaction)) {
            transactionQueue.add(transaction);
            return true;
        }
        return false;
    }

    @Override
    public void removeTransactions(Collection<Transaction> transactions) {
        throw new ToBeImplementedException();
    }

    /**
     * @param transaction       The transaction to validate
     * @return                  True if the transaction is valid, false otherwise.
     */
    @Override
    public boolean validateTransaction(Transaction transaction) {

        if(!verifyTransactionSignature(transaction) || blockChainDatabase.doesTransactionExist(transaction)) {
            return false;
        }
        if (blockChainDatabase.getUnspentTransactionValue(transaction.getValueProof())>transaction.getValue()){
            return true;
        }else {
            //Look in every unspent transaction since
            ArrayList<UnspentTransaction> unspent = blockChainDatabase.getUnspentTransactions(transaction.getSenderAddress());

            int counter = 0;
            for(UnspentTransaction ut : unspent){
                counter+=ut.getValueLeft();
                if(counter>=transaction.getValue()) return true;
            }
        }
        return false;
    }

    /**
     * This method looks at the signatures of the transactions and if it is valid it moves on to retrieve
     * the balance for each address to check if it has funds for all the transactions in this collection.
     *
     * @param transactions      The collection of transactions that you want to validate.
     * @return                  True if the transactions are valid, false otherwise.
     */
    @Override
    public boolean validateTransactions(Collection<Transaction> transactions) {
        // Create a map from sender to transactions.
        Map<Address,ArrayList<Transaction>> sendermap = new HashMap<>();
        for (Transaction t: transactions){
            // Check the signature of the transaction and check if the transaction exists in the block chain.
            if(!verifyTransactionSignature(t) || blockChainDatabase.doesTransactionExist(t)) {
                return false;
            }
            if (sendermap.keySet().contains(t.getSenderAddress())){
                sendermap.get(t.getSenderAddress()).add(t);
            }   else {
                sendermap.put(t.getSenderAddress(),new ArrayList<Transaction>(){{add(t);}});
            }
        }
        // Look up the balance for every address that sends a transaction.
        for (Address address:sendermap.keySet()){
            int funds = blockChainDatabase.getBalance(address);
            int requestedFunds=0;
            for (Transaction t:sendermap.get(address)){
                requestedFunds+=t.getValue();
            }
            if (requestedFunds>funds) return false;
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

    public Queue<Transaction> getTransactionQueue() {
        return transactionQueue;
    }
}
