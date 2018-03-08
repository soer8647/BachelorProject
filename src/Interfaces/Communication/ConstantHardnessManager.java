package Interfaces.Communication;

import Configuration.Configuration;

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
