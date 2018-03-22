package Interfaces;

import java.math.BigInteger;

public interface HardnessManager {
    void notifyOfMining();

    int getHardness();

    void notifyOfRemoved();

    BigInteger getHardValue();
}
