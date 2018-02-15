package Impl;

import Impl.Hashing.SHA256;
import Interfaces.*;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;

/*
* This class is meant to implement a node, that holds the whole blockchain.
* It uses the SHA256 hashing algorithm
* */
public class FullNode implements Node {
    private HashingAlgorithm hashingAlgorithm = new SHA256();
    private BlockChain blockChain;
    private boolean interrupted = false;

    public FullNode(BlockChain blockChain) {
        this.blockChain=blockChain;
    }

    @Override
    public Block mine(BigInteger previousBlockHash, Transactions transactions) {
        //Set the hardness parameter
        //TODO make hardness parameter change
        int hardness = 10;
        //Set the hardness value, by getting the bitsize of the hashing algorithm and shifting right by the hardness parameter.
        BigInteger hardValue = BigInteger.valueOf(2).pow(hashingAlgorithm.getBitSize()).shiftRight(hardness);

        //set nonce
        BigInteger nonce = new BigInteger("0");
        BigInteger hash;
        do{
            if (this.interrupted) {
                this.interrupted = false;
                return null;
            }
            hash = new BigInteger(String.valueOf(hashingAlgorithm.hash(
                    previousBlockHash.toString()
                            + transactions.hashTransactions().toString()
                            + nonce.toString())));
            nonce = nonce.add(new BigInteger("1"));
        } while(hash.compareTo(hardValue)>0);

        Block newBlock = new StandardBlock(nonce,hardness,previousBlockHash,10,new ArrayListTransactions(),blockChain.getBlockNumber()+1,hashingAlgorithm );
        blockChain.addBlock(newBlock);
        return newBlock;
    }

    @Override
    public boolean validateTransactions(Transactions<Collection<Transaction>> transactions) {
        //TODO remove invalid transaction in stead of returning false for all
        //For each transaction
        for (Transaction t: transactions.getTransactions()) {
            //TODO validate signature
            //Get the block where there should be proof of funds.
            Block block = blockChain.getBlock(t.getBlockNumberOfValueProof());
            Transaction proofTransaction=null;
            //Validate that the block holds the transaction with the given hash
            for (Transaction tx:block.getTransactions().getTransactions()){ //TODO change retarded naming.
                if (tx.transActionHash().equals(t.getValueProof())){
                    proofTransaction = tx;
                    break;
                }
            }
            if (proofTransaction==null){
                return false;
            }
            //validate that the receiver of the funds is the sender of the new transaction
            Address receiverOfFunds = proofTransaction.getReceiverAddress();
            if(!receiverOfFunds.equals(t.getSenderAddress()))return false;
            //Validate that the received funds not are less than sending funds
            if(t.getValue()>proofTransaction.getValue()) return false;

            return true;
        }
        return false;
    }

    @Override
    public Transactions getPendingTransactions() {
        return null;
    }


    @Override
    public boolean validateBlock(Block incomingBlock) {
        return false;
    }

    @Override
    public void removeTransaction(Transaction transaction) {
    }

    @Override
    public BlockChain getBlockChain() {
        return blockChain;
    }

    @Override
    public BigInteger hashBlock(Block block) {
        return hashingAlgorithm.hash(block.toString());
    }

    @Override
    public void interrupt() {
        interrupted = true;
    }

}
