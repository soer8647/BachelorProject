package Impl;

import Configuration.Configuration;
import Interfaces.HardnessManager;

public class ConstantHardnessManager implements HardnessManager {
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
}
