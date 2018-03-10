package Interfaces.Communication;

import Impl.TransactionHistory;
import Interfaces.Address;
import Interfaces.Block;
import Interfaces.Transaction;
import Interfaces.TransactionManager;

import java.util.Deque;

public interface NodeRunner {
    boolean validateBlock(Block block);

    /**
     * This method interrupts the inner node in it's mining process, and if no block has been found, puts the incomingBlock on the chain (and it is now the latest block)
     * When a new block is received from the outside, this method should be called.
     *
     * @param incomingBlock     The block that was received.
     */
    void interruptReceivedBlock(Block incomingBlock);

    /**
     * This should give the blocknumber of the inner node
     * @return the blocknumber of the inner node
     */
    int getBlockNumber();

    TransactionManager getTransactionManager();

    boolean validateTransaction(Transaction transaction);

    Block getBlock(int number);

    void rollback(Deque<Block> chain, int blockNumber);

    TransactionHistory getTransactionHistory(Address address, int index);
}
