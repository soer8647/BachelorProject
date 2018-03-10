package Interfaces;

import Impl.TransactionHistory;

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
    TransactionHistory getTransactionHistory(Address address);


    /**
     * @param address       The address involved in transactions.
     * @param blockNumber   The block number from where you want to get the history, inclusive.
     * @return              A collection of all the transactions the address has been involved in since a given block.
     */
    TransactionHistory getTransactionHistory(Address address, int blockNumber);
    Block removeBlock();
}
