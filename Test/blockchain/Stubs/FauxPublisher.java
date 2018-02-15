package blockchain.Stubs;

import Interfaces.Block;
import Interfaces.Communication.Publisher;

public class FauxPublisher implements Publisher {
    private FauxReceiver receiver;

    public FauxPublisher(FauxReceiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void publishBlock(Block block) {
        receiver.receiveBlock(block);
    }
}
