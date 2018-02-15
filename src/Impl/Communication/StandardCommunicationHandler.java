package Impl.Communication;

import Impl.Communication.Events.*;
import Interfaces.Block;
import Interfaces.Communication.CommunicationHandler;
import Interfaces.Communication.Event;
import Interfaces.Communication.Publisher;
import Interfaces.Node;
import Interfaces.Transaction;

import java.util.concurrent.BlockingQueue;

public class StandardCommunicationHandler implements CommunicationHandler{
    private Node node;
    private Publisher publisher;

    /**
     *
     * @param node, The mining node
     * @param publisher, The publisher of the network module
     * @param eventQueue, The queue which the SCH takes events from.
     */
    public StandardCommunicationHandler(Node node, Publisher publisher, BlockingQueue<Event> eventQueue) {
        this.node = node;
        this.publisher = publisher;

        //TODO: maybe this should start a new thread
        while(true) {
            Event event = null;
            try {
                event = eventQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            HandleEvent(event);
        }
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
        if (node.validateBlock(block)) {
            //node.interruptReceivedBlock(block);
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
