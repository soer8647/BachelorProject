package Interfaces;

import java.io.Serializable;
import java.math.BigInteger;

public
interface CoinBaseTransaction  extends Serializable,VerifiableTransaction{

    /**
     * @return      The block mining reward
     */
    int getValue();

    /**
     * @return      The address to where the reward is transfered.
     */
    Address getMinerAddress();

    int getBlockNumber();

    /**
     * @return      The hash of this transaction.
     */
    BigInteger transactionHash();
}
