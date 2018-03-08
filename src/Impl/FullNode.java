package Impl;

import Configuration.Configuration;
import Crypto.Interfaces.PublicKeyCryptoSystem;
import External.Pair;
import Interfaces.*;
import Interfaces.Communication.HardnessManager;

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
    private HardnessManager hardnessManager;


    public FullNode(BlockChain blockChain, Address address, HardnessManager hardnessManager) {
        this.blockChain=blockChain;
        this.address = address;
        this.hardnessManager = hardnessManager;
    }

    @Override
    public Block mine(BigInteger previousBlockHash, Transactions transactions) {
        //Set the hardness parameter
        int hardness = hardnessManager.getHardness();
        //Set the hardness value, by getting the bitsize of the hashing algorithm and shifting right by the hardness parameter.
        BigInteger hardValue = BigInteger.valueOf(2).pow(Configuration.getBitSize()).shiftRight(hardness);

        //set nonce
        BigInteger nonce = new BigInteger("0");
        BigInteger hash;
        StandardCoinBaseTransaction coinBase = new StandardCoinBaseTransaction(address,Configuration.getBlockReward(), 0);
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

    public boolean validateTransaction(Transaction t){
        if(!verifyTransactionSignature(t)) {
            return false;
        }
        Pair<Collection<ConfirmedTransaction>,Collection<CoinBaseTransaction>> transactions = blockChain.getTransactionHistory(t.getSenderAddress(),t.getBlockNumberOfValueProof());
        int valueToVerify = t.getValue();
        int counter = 0;

        Object[] trans = transactions.getKey().toArray();
        Object[] coinBases = transactions.getValue().toArray();
        int coinBaseElementNr = coinBases.length-1;

        for (int i=trans.length-1;i>=0;i--){
            CoinBaseTransaction cbt;
            ConfirmedTransaction tr = (ConfirmedTransaction) trans[i];
            if (coinBaseElementNr>=0){
                cbt = ((CoinBaseTransaction)coinBases[coinBaseElementNr]);
                //TODO MAKE A NEW TYPE OF TRANSACTIONS THAT ARE CONFIRMED.
                if(cbt.getBlockNumber()>=tr.getBlockNumber()){
                    counter+=cbt.getValue();
                    coinBaseElementNr--;
                }
            }
            if (tr.getReceiverAddress().toString().equals(t.getSenderAddress().toString())){
                counter+=tr.getValue();
                if (counter>=valueToVerify) return true;
            }else if(tr.getSenderAddress().toString().equals(t.getSenderAddress().toString())){
                counter -=tr.getValue();
            }
        }
        // If we looked at all the transactions since the proof of funds transaction
        // and it does not sum to at least the transaction value, the transaction is invalid.
        return false;
    }

    @Override
    public Transactions getPendingTransactions() {
        return null;
    }


    @Override
    public boolean validateBlock(Block incomingBlock) {
            return blockChain.getBlock(incomingBlock.getBlockNumber()-1).hash().equals(incomingBlock.getPreviousHash())
                    && validateTransactions(incomingBlock.getTransactions());
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
    public Pair<Collection<ConfirmedTransaction>, Collection<CoinBaseTransaction>> getTransactionHistory(Address address) {
        return blockChain.getTransactionHistory(address);
    }

    @Override
    public Block removeBlock() {
        hardnessManager.notifyOfRemoved();
        return blockChain.removeBlock();
    }

    @Override
    public void addBlock(Block block) {
        blockChain.addBlock(block);
        hardnessManager.notifyOfMining();
    }

    public Pair<Collection<ConfirmedTransaction>,Collection<CoinBaseTransaction>> getTransactionHistory(Address address,int index){
        return blockChain.getTransactionHistory(address,index);
    }
}
