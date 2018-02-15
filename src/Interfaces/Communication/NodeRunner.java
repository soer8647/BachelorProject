package Interfaces.Communication;

import Interfaces.Block;

public interface NodeRunner {
    boolean validateBlock(Block block);

    void interruptReceivedBlock(Block block);
}
