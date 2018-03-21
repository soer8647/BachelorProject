package Impl;

import Configuration.Configuration;
import Crypto.Interfaces.PublicKeyCryptoSystem;
import Impl.Transactions.StandardCoinBaseTransaction;
import Interfaces.*;
import Interfaces.HardnessManager;

import java.math.BigInteger;
import java.util.Collection;

/*
* This class is meant to implement a node, that holds the full block chain.
* It uses the SHA256 hashing algorithm.
* */
public class FullNode implements Node {
    private BlockChain blockChain;
    private boolean interrupted = false;
    private Address address;
    private HardnessManager hardnessManager;
    private TransactionManager transactionManager;


    public FullNode(BlockChain blockChain, Address address, HardnessManager hardnessManager, TransactionManager transactionManager) {
        this.blockChain=blockChain;
        this.address = address;
        this.hardnessManager = hardnessManager;
        this.transactionManager = transactionManager;
    }

    /**
     * The method for mining a single block and append it to the block chain.
     * If the node receives an incoming block in the meantime, the mining stops and the received block is appended to the block chain.
     *
     * @param previousBlockHash The hash of the previous block
     * @param transactions      The transactions to validate and mine in this block
     * @return                  The new block that was mined
     */
    @Override
    public Block mine(BigInteger previousBlockHash, Transactions transactions) {
        //Set the hardness parameter
        int hardness = hardnessManager.getHardness();
        //Set the hardness value, by getting the bitsize of the hashing algorithm and shifting right by the hardness parameter.
        BigInteger hardValue = BigInteger.valueOf(2).pow(Configuration.getBitSize()).shiftRight(hardness);

        //set nonce
        BigInteger nonce = new BigInteger("0");
        BigInteger hash;
        StandardCoinBaseTransaction coinBase = new StandardCoinBaseTransaction(address,Configuration.getBlockReward(), getBlockChain().getBlockNumber()+1);
        do{
            if (this.interrupted) {
                this.interrupted = false;
                return null;
            }
            hash = new BigInteger(String.valueOf(Configuration.hash(
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
                transactions,
                blockChain.getBlockNumber()+1,
                coinBase);
        blockChain.addBlock(newBlock);
        hardnessManager.notifyOfMining();
        return newBlock;
    }


    /**
     * @param transactions When mining a block or validating a block one should be able to validate that the transactions are valid.
     * @return              True if all the transactions are valid.
     */
    @Override
    public boolean validateTransactions(Transactions<Collection<Transaction>> transactions) {
        return transactionManager.validateTransactions(transactions);
    }

    public boolean validateTransaction(Transaction transaction){
        return transactionManager.validateTransaction(transaction);
    }


    @Override
    public boolean validateBlock(Block incomingBlock) {
            return blockChain.getBlock(incomingBlock.getBlockNumber()-1).hash().equals(incomingBlock.getPreviousHash())
                    && validateTransactions(incomingBlock.getTransactions());
        }

    @Override
    public BlockChain getBlockChain() {
        return blockChain;
    }

    @Override
    public BigInteger hashBlock(Block block) {
        return Configuration.hash(block.toString());
    }

    @Override
    public void interrupt() {
        interrupted = true;
    }

    @Override
    public Address getAddress() {
        return address;
    }

    /**
     * @param transaction       The transaction to verify the signature on
     * @return                  True if there is a valid transaction signature.
     */
    public boolean verifyTransactionSignature(Transaction transaction) {
        PublicKeyCryptoSystem cs = Configuration.getCryptoSystem();
        return cs.verify(transaction.getSenderAddress().getPublicKey(),transaction.getSignature(),transaction.transActionHash());
    }

    @Override
    public TransactionHistory getTransactionHistory(Address a) {
        return blockChain.getTransactionHistory(a);
    }

    @Override
    public Block removeBlock() {
        hardnessManager.notifyOfRemoved();
        return blockChain.removeBlock();
    }

    /**
     * @param block     The block to append to the block chain.
     */
    @Override
    public void addBlock(Block block) {
        blockChain.addBlock(block);
        hardnessManager.notifyOfMining();
    }

    public TransactionHistory getTransactionHistory(Address a, int index){
        return blockChain.getTransactionHistory(a,index);
    }
}
