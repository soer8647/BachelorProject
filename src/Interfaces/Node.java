package Interfaces;

import Impl.TransactionHistory;

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
    Block mine(BigInteger previousBlockHash, Collection<Transaction> transactions);

    /**
     * When mining a block or validating a block one should be able to validate that the transactions are valid.
     *
     * @param transactions          The transactions to verify.
     * @return                      True is all transactions are valid, false otherwise.
     */
    boolean validateTransactions(Collection<Transaction> transactions);


    /**
     * When a block is mined on another node it is propagated to the network. You have to validate this block before accepting it.
     *
     * @param incomingBlock        The block that this node has received from the network
     * @return                      true if the block is valid. false otherwise.
     */
    boolean validateBlock(Block incomingBlock);

    /**
     * @return      The block chain of this node
     */
    BlockChain getBlockChain();

    /**
     * @return The hash as a BigInteger of a block
     */
    BigInteger hashBlock(Block block);

    void interrupt();

    /**
     * @return      The address in the block chain for this node. This is where the block reward is transferred to.
     */
    Address getAddress();

    /**
     * Checks the signature and the proof of funds of a given transaction to see if it it valid.
     *
     * @param transaction       The transaction to be validated.
     * @return                  True if the transaction os valid and false otherwise.
     */
    boolean validateTransaction(Transaction transaction);

    /**
     * @param address       The address involved in transactions.
     * @return              A collection of all transactions where this address is involved.
     */
    TransactionHistory getTransactionHistory(Address address);

    Block removeBlock();

    void addBlock(Block block);

    TransactionHistory getTransactionHistory(Address address, int index);
}
