package Impl;

import Impl.Transactions.ConfirmedTransaction;
import Interfaces.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class StandardBlockChain implements BlockChain{
    private Collection<Block> blocks;
    private Block genesisBlock;
    public StandardBlockChain(Block genesisBlock)
    {
        this.blocks = new ArrayList<>();
        this.genesisBlock = genesisBlock;
        blocks.add(genesisBlock);
    }

    @Override
    public Block getBlock(int blockNumber) {
        Iterator<Block> iter = blocks.iterator();
        int index =0;
        Block iterBlock;
        while(iter.hasNext()&&index<=blockNumber){
            iterBlock =  iter.next();
            if (blockNumber==index){
                return iterBlock;
            }
            index++;
        }
        //TODO maybe throw exception instead. null is bad.
        return null;
    }

    @Override
    public int getBlockNumber() {
        return blocks.size()-1;
    }

    @Override
    public void addBlock(Block block) {
        blocks.add(block);
    }

    @Override
    public Block getGenesisBlock() {
        return genesisBlock;
    }

    @Override
    public TransactionHistory getTransactionHistory(Address address) {
        return getTransactionHistory(address,0);
    }

    @Override
    public TransactionHistory getTransactionHistory(Address address, int blockNumber) {
        ArrayList<ConfirmedTransaction> transactions = new ArrayList<>();
        ArrayList<CoinBaseTransaction> coinBaseTransactions = new ArrayList<>();
        for (Block b : blocks){
            if(b.getBlockNumber()>=blockNumber) {
                for (Transaction t : b.getTransactions().getTransactions()) {
                    if (t.getSenderAddress().toString().equals(address.toString()) || t.getReceiverAddress().toString().equals(address.toString())) {
                        transactions.add(new ConfirmedTransaction(t,b.getBlockNumber()));
                    }
                }
            }
            if(b.getCoinBase().getMinerAddress().toString().equals(address.toString())){
                coinBaseTransactions.add(b.getCoinBase());
            }
        }

        return new TransactionHistory(transactions,coinBaseTransactions);
    }

    @Override
    public Block removeBlock() {
        Block block = getBlock(getBlockNumber());
        blocks.remove(block);
        return block;
    }

}
