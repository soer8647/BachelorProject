package Impl;

import Configuration.Configuration;
import Interfaces.*;

import java.math.BigInteger;
import java.util.Collection;

/*
* This class is meant to implement a node, that holds the whole blockchain.
* It uses the SHA256 hashing algorithm
* */
public class FullNode implements Node {
    private BlockChain blockChain;
    private boolean interrupted = false;
    private Address minerAddress;

    public FullNode(BlockChain blockChain) {
        this.blockChain=blockChain;
        this.minerAddress = null; //TODO FIX
    }

    @Override
    public Block mine(BigInteger previousBlockHash, Transactions transactions) {
        //Set the hardness parameter
        //TODO make hardness parameter change
        int hardness = 10;
        //Set the hardness value, by getting the bitsize of the hashing algorithm and shifting right by the hardness parameter.
        BigInteger hardValue = BigInteger.valueOf(2).pow(Global.getBitSize()).shiftRight(hardness);

        //set nonce
        BigInteger nonce = new BigInteger("0");
        BigInteger hash;
        StandardCoinBaseTransaction coinBase = new StandardCoinBaseTransaction(minerAddress,Configuration.getBlockReward());
        do{
            if (this.interrupted) {
                this.interrupted = false;
                return null;
            }
            hash = new BigInteger(String.valueOf(Global.hash(
                    previousBlockHash.toString()
                            + transactions.hashTransactions().toString()
                            + nonce.toString()
                            +coinBase.toString())));
            nonce = nonce.add(new BigInteger("1"));
        } while(hash.compareTo(hardValue)>0);

        Block newBlock = new StandardBlock(
                nonce,
                hardness,
                previousBlockHash,
                10,
                new ArrayListTransactions(),
                blockChain.getBlockNumber()+1,
                coinBase);
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
        return Global.hash(block.toString());
    }

    @Override
    public void interrupt() {
        interrupted = true;
    }

}
