package Interfaces;

import java.math.BigInteger;
import java.util.Collection;

public interface Node {


    /**
     * This method is suppose to mine a new block to extend to the blockchain.
     * The most common way to do this is to to guess a nounce to where the hash of the previous block and the transactions
     * start with zeroes according to the current hardness parameter.
     *
     * @param previousBlockHash     The hash of the previous block
     * @param transactions          The transactions to validate and mine in this block
     * @return                      The new block, that has been mined
     */
    Block mine(BigInteger previousBlockHash, Transactions transactions);

    /**
     * @param transactions          When mining a block or validating a block one should be able to validate that the transactions are valid.
     * @return                      true is all transactions are valid, false otherwise.
     */
    boolean validateTransactions(Transactions<Collection<Transaction>> transactions);

    /**
     * @return                      All the transactions that this node has not yes incorporated in a block.
     */
    Transactions getPendingTransactions();

    /**
     * When a block is mined on another node it is propagated to the network. You have to validate this block before accepting it.
     *
     * @param incomingBlock        The block that this node has received from the network
     * @return                      true if the block is valid. false otherwise.
     */
    boolean validateBlock(Block incomingBlock);


    /**
     * A transaction have to be removed if it is mined in one of this nodes blocks or if you accept another block where it is in.
     *
     * @param transaction            The transaction to remove from the pending transaction.
     */
    void removeTransaction(Transaction transaction);

    /**
     * @return      The blockchain of this node
     */
    BlockChain getBlockChain();

    /**
     * @return The hash as a BigInteger of a block
     */
    BigInteger hashBlock(Block block);

    void interrupt();

    /**
     * @return      The address in the blockchain for this node. This is where the blockreward is transferred to.
     */
    Address getAddress();

    boolean validateTransaction(Transaction transaction);
}
