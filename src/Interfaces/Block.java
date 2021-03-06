package Interfaces;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;

/*
* This interface is intended to represent a single block of the blockchain.
* A block should contain:
* 1. Nounce
* 2. Transactions
* 3. Blocknumber
* 4. Hash of previous block
* */
public interface Block extends Serializable{
    /**
     * @return The transactions inside this block.
     */
    Collection<Transaction> getTransactions();

    /**
     * @return The number of the block
     */
    int getBlockNumber();

    /**
     * @return The nonce that was chosen when this block was mined.
     */
    BigInteger getNonce();

    /**
     * The hardnessParameter is how many zeros the hashed block has to start with to be mined.
     *
     * @return The hardnessParameter when this block was mined.
     */
    int getHardnessParameter();

    /**
     * @return The hash of the previous block in the blockchain
     */
    BigInteger getPreviousHash();

    /**
     * @return      The hash of the nonce, transactions and previous block hash.
     */
    BigInteger hash();

     CoinBaseTransaction getCoinBase();
}
