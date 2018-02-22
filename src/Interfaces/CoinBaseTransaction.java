package Interfaces;

public interface CoinBaseTransaction {

    /**
     * @return      The block mining reward
     */
    int getValue();

    /**
     * @return      The address to where the reward is transfered.
     */
    Address getMinerAddress();
}
