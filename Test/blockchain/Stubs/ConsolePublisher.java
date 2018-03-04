package blockchain.Stubs;

import Interfaces.Block;
import Interfaces.Communication.Publisher;

import java.net.InetAddress;

/**
 *  A publisher that just writes the block to the console
 */
public class ConsolePublisher implements Publisher {
    @Override
    public void publishBlock(Block block) {
        System.out.println(block);
    }

    @Override
    public void requestBlock(int number, InetAddress ip, int port) {

    }

    @Override
    public void requestMaxBlock(InetAddress ip, int port) {

    }

    @Override
    public void answerRequest(Block block, InetAddress ip, int port) {

    }
}
