package Impl.Communication;

import Impl.Communication.Events.*;
import Interfaces.Block;
import Interfaces.Communication.Event;
import Interfaces.Communication.NodeCommunicationHandler;
import Interfaces.Communication.NodeRunner;
import Interfaces.Communication.Publisher;
import Interfaces.Transaction;

import java.util.Deque;
import java.util.concurrent.BlockingQueue;

public class StandardNodeCommunicationHandler implements NodeCommunicationHandler {
    private NodeRunner nodeRunner;
    private Publisher publisher;
    private boolean interrupted;
    private OrphanChainHolder orphanage;

    /**
     *
     * @param nodeRunner, The mining node's runner
     * @param publisher, The publisher of the network module
     * @param eventQueue, The queue which the SCH takes events from.
     */
    public StandardNodeCommunicationHandler(NodeRunner nodeRunner, Publisher publisher, BlockingQueue<Event> eventQueue) {
        this.nodeRunner = nodeRunner;
        this.publisher = publisher;

        this.orphanage = new OrphanChainHolder();

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
                    handleEvent(event);
                    }
            }
        });
        thread.start();
    }

    /**
     * This function determines the type of event and delegates to the appropriate function.
     * @param event, The event to be handled
     */
    private void handleEvent(Event event) {
        if (event instanceof ReceivedBlockEvent) {
            handleReceivedBlock(((ReceivedBlockEvent) event));
        } else if (event instanceof TransactionEvent) {
            handleNewTransaction(((TransactionEvent) event).getTransaction());
        } else if (event instanceof MinedBlockEvent) {
            handleMinedBlock(((MinedBlockEvent) event).getBlock());
        } else if (event instanceof RequestEvent) {
            handleRequest((RequestEvent) event);
        } else if (event instanceof RequestedEvent) {
            handleRequested((RequestedEvent) event);
        }    }

    private void handleRequest(RequestEvent event) {
        int number = event.getNumber();
        if (number == -1) {
            number = nodeRunner.getBlockNumber();
        }
       publisher.answerRequest(nodeRunner.getBlock(number), event.getIp(), event.getPort());
    }

    private void handleRequested(RequestedEvent event) {
        Block block = event.getBlock();
        int key = event.getPort();
        Block child = orphanage.getBlock(key);

        //TODO : remove
        if (child.getBlockNumber() - block.getBlockNumber() != 1) {
            System.out.println(child.getBlockNumber() + " > " + block.getBlockNumber());
        }

        if (child.getPreviousHash().equals(block.hash())) {
            if (block.getBlockNumber()<=nodeRunner.getBlockNumber() && nodeRunner.validateBlock(block)) {
                Deque<Block> chain = orphanage.getChain(key);
                //TODO: perform rollback (if it's the best chain)
                if (chain.peekLast().getBlockNumber() > nodeRunner.getBlockNumber()) {
                    System.out.println("what we do here is go Back!");
                    nodeRunner.rollback(chain,chain.peekFirst().getBlockNumber());
                }
            } else {
                System.out.println("this happens?");
                orphanage.addBlock(block,key);
                publisher.requestBlock(block.getBlockNumber()-1,event.getIp(),event.getPort());
            }
        } else {
            System.out.println("Bad Requested");
        }
    }

    @Override
    public void handleReceivedBlock(ReceivedBlockEvent event) {
        Block block = event.getBlock();
//        System.out.println("ReceivedBlock event");
        if (block.getBlockNumber() < nodeRunner.getBlockNumber()) {
            //we dont care about old
        } else if (block.getBlockNumber() == nodeRunner.getBlockNumber()) {
            //TODO: Change (maybe) if last block was received
        } else if (block.getBlockNumber() > nodeRunner.getBlockNumber()+1) {
            //TODO: Handle other nodes being more than 1 ahead (not done)
            if (orphanage.addChain(block,event.getPort())) {
                publisher.requestBlock(block.getBlockNumber()-1,event.getIp(),event.getPort());
            }
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
    public void handleNewTransaction(Transaction transaction) {
        // put into Node's queue of potential transactions
        nodeRunner.getTransactionManager().addTransaction(transaction);
    }

    @Override
    public void handleMinedBlock(Block block) {
 //       System.out.println("MinedBlock event");
        publisher.publishBlock(block);
    }
}
