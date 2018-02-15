package Impl.Communication;

import Impl.Communication.Events.*;
import Interfaces.Block;
import Interfaces.Communication.CommunicationHandler;
import Interfaces.Communication.Event;
import Interfaces.Communication.NodeRunner;
import Interfaces.Communication.Publisher;
import Interfaces.Node;
import Interfaces.Transaction;

import java.util.concurrent.BlockingQueue;

public class StandardCommunicationHandler implements CommunicationHandler{
    private NodeRunner nodeRunner;
    private Publisher publisher;
    private boolean interrupted;

    /**
     *
     * @param nodeRunner, The mining node's runner
     * @param publisher, The publisher of the network module
     * @param eventQueue, The queue which the SCH takes events from.
     */
    public StandardCommunicationHandler(NodeRunner nodeRunner, Publisher publisher, BlockingQueue<Event> eventQueue) {
        this.nodeRunner = nodeRunner;
        this.publisher = publisher;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(!interrupted) {
                    Event event = null;
                    try {
                        event = eventQueue.take();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    HandleEvent(event);
                }
            }
        });
        thread.start();
    }

    /**
     * This function determines the type of event and delegates to the appropriate function.
     * @param event, The event to be handled
     */
    private void HandleEvent(Event event) {
        if (event instanceof ReceivedBlockEvent) {
            HandleReceivedBlock(((ReceivedBlockEvent) event).getBlock());
        } else if (event instanceof TransactionEvent) {
            HandleNewTransaction(((TransactionEvent) event).getTransaction());
        } else if (event instanceof MinedBlockEvent) {
            HandleMinedBlock(((MinedBlockEvent) event).getBlock());
        }
    }

    @Override
    public void HandleReceivedBlock(Block block) {
        if (nodeRunner.validateBlock(block)) {
            nodeRunner.interruptReceivedBlock(block);
        }
    }

    @Override
    public void HandleNewTransaction(Transaction transaction) {
        // put into Node's queue of potential transactions
    }

    @Override
    public void HandleMinedBlock(Block block) {
        publisher.publishBlock(block);
    }
}
