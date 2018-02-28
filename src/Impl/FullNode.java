package Impl;

import Configuration.Configuration;
import Crypto.Interfaces.PublicKeyCryptoSystem;
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
    private Address address;


    public FullNode(BlockChain blockChain, Address address) {
        this.blockChain=blockChain;
        this.address = address;

    }

    @Override
    public Block mine(BigInteger previousBlockHash, Transactions transactions) {
        //Set the hardness parameter
        //TODO make hardness parameter change
        int hardness = Configuration.getHardnessParameter();
        //Set the hardness value, by getting the bitsize of the hashing algorithm and shifting right by the hardness parameter.
        BigInteger hardValue = BigInteger.valueOf(2).pow(Global.getBitSize()).shiftRight(hardness);

        //set nonce
        BigInteger nonce = new BigInteger("0");
        BigInteger hash;
        StandardCoinBaseTransaction coinBase = new StandardCoinBaseTransaction(address,Configuration.getBlockReward());
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
            if(!validateTransaction(t)) {
                return false;
            }
        }
        return true;
    }

    private boolean validateTransaction(Transaction t){
        if(!verifyTransactionSignature(t)) {
            return false;
        }
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
        if(!receiverOfFunds.equals(t.getSenderAddress())){
            return false;}
        //Validate that the received funds not are less than sending funds
        return t.getValue()<=proofTransaction.getValue();
    }

    @Override
    public Transactions getPendingTransactions() {
        return null;
    }


    @Override
    public boolean validateBlock(Block incomingBlock) {
        //TODO: DO
        return true;
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
}
