package Interfaces;

import java.io.Serializable;

public
interface CoinBaseTransaction extends Serializable{

    /**
     * @return      The block mining reward
     */
    int getValue();

    /**
     * @return      The address to where the reward is transfered.
     */
    Address getMinerAddress();

    int getBlockNumber();
}
