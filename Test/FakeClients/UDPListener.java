package FakeClients;

import Impl.Communication.Events.ReceivedBlockEvent;
import Impl.Communication.UDPReceiver;
import Interfaces.Communication.Event;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class UDPListener {
    public static void main(String[] args) {
        BlockingQueue q = new LinkedBlockingQueue();
        UDPReceiver receiver = new UDPReceiver(q,9876);
        while (true) {
            try {
                Event event = (Event) q.take();
                if (event instanceof ReceivedBlockEvent) {
                    System.out.println( ((ReceivedBlockEvent) event).getBlock());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
