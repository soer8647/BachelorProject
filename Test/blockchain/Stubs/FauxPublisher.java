package blockchain.Stubs;

import Interfaces.Block;
import Interfaces.Communication.Publisher;

import java.net.InetAddress;

public class FauxPublisher implements Publisher {
    private FauxReceiver receiver;

    public FauxPublisher(FauxReceiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void publishBlock(Block block) {
        receiver.receiveBlock(block);
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
