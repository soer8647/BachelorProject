package blockchain.Stubs;

import Impl.Communication.Events.ReceivedBlockEvent;
import Interfaces.Block;
import Interfaces.Communication.Event;

import java.util.concurrent.BlockingQueue;

public class FauxReceiver {
    private BlockingQueue<Event> queue;

    public FauxReceiver(BlockingQueue<Event> queue) {
        this.queue = queue;
    }

    public void receiveBlock(Block block) {
        System.out.println("Received a block");
        try {
            queue.put( new ReceivedBlockEvent(block));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
