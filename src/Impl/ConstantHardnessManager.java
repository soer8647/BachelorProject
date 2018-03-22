package Impl;

import Configuration.Configuration;
import Interfaces.HardnessManager;

import java.math.BigInteger;

public class ConstantHardnessManager implements HardnessManager {
    private BigInteger hardValue = BigInteger.valueOf(2).pow(Configuration.getBitSize()).shiftRight(getHardness());

    @Override
    public void notifyOfMining() {

    }

    @Override
    public int getHardness() {
        return Configuration.getHardnessParameter();
    }

    @Override
    public void notifyOfRemoved() {

    }

    @Override
    public BigInteger getHardValue() {
        return hardValue;
    }
}
