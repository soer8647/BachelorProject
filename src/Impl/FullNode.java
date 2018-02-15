package Impl;

import Interfaces.*;


import java.math.BigInteger;
/*
* This class is meant to implement a node, that holds the whole blockchain.
* It uses the SHA256 hashing algorithm
* */
public class FullNode implements Node {
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
        BigInteger hardValue = BigInteger.valueOf(2).pow(Global.getBitSize()).shiftRight(hardness);

        //set nonce
        BigInteger nonce = new BigInteger("0");
        BigInteger hash;
        do{
            if (this.interrupted) {
                this.interrupted = false;
                return null;
            }
            hash = new BigInteger(String.valueOf(Global.hash(
                    previousBlockHash.toString()
                            + transactions.hashTransactions().toString()
                            + nonce.toString())));
            nonce = nonce.add(new BigInteger("1"));
        } while(hash.compareTo(hardValue)>0);

        Block newBlock = new StandardBlock(nonce,hardness,previousBlockHash,10,new ArrayListTransactions(),blockChain.getBlockNumber()+1);
        blockChain.addBlock(newBlock);
        return newBlock;
    }

    @Override
    public boolean validateTransactions(Transactions transactions) {
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
