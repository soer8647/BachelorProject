package blockchain.Stubs;

import FakeClients.EmptyTransactionsManager;
import Impl.TransactionHistory;
import Interfaces.*;
import Interfaces.Communication.NodeRunner;

import java.util.Collection;
import java.util.Deque;

public class NodeRunnerStub implements NodeRunner {
    private boolean receivedBlock;
    private int blockNumber = 0;
    private boolean gotATransaction = false;

    @Override
    public boolean validateBlock(Block block) {
        return true;
    }

    @Override
    public void interruptReceivedBlock(Block incomingBlock) {
        this.receivedBlock = true;
        this.blockNumber++;
    }

    @Override
    public int getBlockNumber() {
        return this.blockNumber;
    }

    public TransactionManager getTransactionManager() {
        return null;
    }

    @Override
    public boolean validateTransaction(Transaction transaction) {
        return true;
    }

    @Override
    public Block getBlock(int number) {
        return null;
    }

    @Override
    public void rollback(Deque<Block> chain, int blockNumber) {

    }

    @Override
    public TransactionHistory getTransactionHistory(Address address, int index) {
        return null;
    }

    @Override
    public void stop() {

    }

    @Override
    public boolean addTransaction(Transaction transaction) {
        if (gotATransaction) {
            return false;
        }
        gotATransaction = true;
        return true;
    }

    public boolean gotTransaction() {
        return this.gotATransaction;
    }
}
