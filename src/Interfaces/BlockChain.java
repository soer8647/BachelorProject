package Interfaces;

import java.util.Collection;

public interface BlockChain {
    /**
     * @param blockNumber       The number of the block
     * @return                  The block that has the given blocknumber
     */
    Block getBlock(int blockNumber);


    /**
     * @return                  The current blocknumber of this blockchain.
     */
    int getBlockNumber();

    /**
     * @param block             The block to add to the blockchain.
     */
    void addBlock(Block block);


    /**
     * @return      The first block to mine on this blockchain.
     */
    Block getGenesisBlock();

    /**
     * @param address       The address involved in transactions.
     * @return              All transactions where the address is involved
     */
    Collection<Transaction> getTransactionHistory(Address address);

    Block removeBlock();
}
