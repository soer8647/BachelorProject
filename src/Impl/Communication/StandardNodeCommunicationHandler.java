package Impl.Communication;

import Impl.Communication.Events.*;
import Interfaces.Block;
import Interfaces.Communication.NodeCommunicationHandler;
import Interfaces.Communication.Event;
import Interfaces.Communication.NodeRunner;
import Interfaces.Communication.Publisher;
import Interfaces.Transaction;

import java.util.concurrent.BlockingQueue;

public class StandardNodeCommunicationHandler implements NodeCommunicationHandler {
    private NodeRunner nodeRunner;
    private Publisher publisher;
    private boolean interrupted;

    /**
     *
     * @param nodeRunner, The mining node's runner
     * @param publisher, The publisher of the network module
     * @param eventQueue, The queue which the SCH takes events from.
     */
    public StandardNodeCommunicationHandler(NodeRunner nodeRunner, Publisher publisher, BlockingQueue<Event> eventQueue) {
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
            HandleReceivedBlock(((ReceivedBlockEvent) event));
        } else if (event instanceof TransactionEvent) {
            HandleNewTransaction(((TransactionEvent) event).getTransaction());
        } else if (event instanceof MinedBlockEvent) {
            HandleMinedBlock(((MinedBlockEvent) event).getBlock());
        } else if (event instanceof RequestEvent) {
            HandleRequest((RequestEvent) event);
        }
    }

    private void HandleRequest(RequestEvent event) {
        int number = event.getNumber();
        if (number == -1) {
            number = nodeRunner.getBlockNumber();
        }
        publisher.answerRequest(nodeRunner.getBlock(number), event.getIp(), event.getPort());
    }

    private void HandleRequested(RequestedEvent event) {
        //TODO: MAKE
    }

    @Override
    public void HandleReceivedBlock(ReceivedBlockEvent event) {
        Block block = event.getBlock();
//        System.out.println("ReceivedBlock event");
        if (block.getBlockNumber() < nodeRunner.getBlockNumber()) {
            //we dont care about old
        } else if (block.getBlockNumber() == nodeRunner.getBlockNumber()) {
            //TODO: Change (maybe) if last block was received
        } else if (block.getBlockNumber() > nodeRunner.getBlockNumber()+1) {
            //TODO: Handle other nodes being more than 1 ahead
            publisher.requestMaxBlock(event.getIp(),event.getPort());
//            System.out.println("Other blocks far ahead");
        } else {
            if (nodeRunner.validateBlock(block)) {
                nodeRunner.interruptReceivedBlock(block);
            } else {
//                System.out.println("Not valid");
            }
        }
    }

    @Override
    public void HandleNewTransaction(Transaction transaction) {

        // put into Node's queue of potential transactions
        nodeRunner.getTransactionManager().addTransaction(transaction);
    }

    @Override
    public void HandleMinedBlock(Block block) {
 //       System.out.println("MinedBlock event");
        publisher.publishBlock(block);
    }
}
