package blockchain.Stubs;

import Interfaces.Block;
import Interfaces.Communication.Publisher;

/**
 *  A publisher that just writes the block to the console
 */
public class ConsolePublisher implements Publisher {
    @Override
    public void publishBlock(Block block) {
        System.out.println(block);
    }
}
