package FakeClients;

import Impl.Communication.Events.ReceivedBlockEvent;
import Impl.Communication.UDPReceiver;
import Interfaces.Communication.Event;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class UDPListener {
    private boolean interrupted;
    public UDPListener(int port) {
        BlockingQueue q = new LinkedBlockingQueue();
        UDPReceiver receiver = new UDPReceiver(q,port);
        while (!interrupted) {
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
    public void interrupt() {
        interrupted = true;
    }

    public static void main(String[] args) {
        UDPListener listener = new UDPListener(9876);
    }
}
