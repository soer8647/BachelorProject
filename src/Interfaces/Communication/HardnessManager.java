package Interfaces.Communication;

public interface HardnessManager {
    void notifyOfMining();

    int getHardness();

    void notifyOfRemoved();
}
