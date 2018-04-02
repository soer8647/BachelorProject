package Impl.Communication;

import Configuration.Configuration;
import Configuration.GlobalCounter;
import Impl.Communication.Events.*;
import Impl.Communication.UDP.UDPPublisherNode;
import Impl.TransactionHistory;
import Impl.Transactions.ConfirmedTransaction;
import Interfaces.Block;
import Interfaces.CoinBaseTransaction;
import Interfaces.Communication.Event;
import Interfaces.Communication.NodeCommunicationHandler;
import Interfaces.Communication.NodeRunner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class StandardNodeCommunicationHandler implements NodeCommunicationHandler {
    private final Thread thread;
    private NodeRunner nodeRunner;
    private UDPPublisherNode publisher;
    private boolean interrupted;
    private OrphanChainHolder orphanage;

    /**
     *
     * @param nodeRunner, The mining node's runner
     * @param publisher, The publisher of the network module
     * @param eventQueue, The queue which the SCH takes events from.
     */
    public StandardNodeCommunicationHandler(NodeRunner nodeRunner, UDPPublisherNode publisher, BlockingQueue<Event> eventQueue) {
        this.nodeRunner = nodeRunner;
        this.publisher = publisher;

        this.orphanage = new OrphanChainHolder();

        thread = new Thread(() -> {
            while(!interrupted) {
                Event event = null;
                try {
                    event = eventQueue.take();
                } catch (InterruptedException e) {
                    System.out.println("CommunicationHandler down");
                }
                    handleEvent(event);
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
            handleNewTransaction(((TransactionEvent) event));
        } else if (event instanceof MinedBlockEvent) {
            handleMinedBlock(((MinedBlockEvent) event));
        } else if (event instanceof RequestEvent) {
            handleRequest((RequestEvent) event);
        } else if (event instanceof RequestedEvent) {
            handleRequested((RequestedEvent) event);
        } else if (event instanceof TransactionHistoryRequestEvent){
            handleTransactionHistoryRequest((TransactionHistoryRequestEvent) event);
        } else if (event instanceof JoinEvent) {
            handleJoinEvent((JoinEvent) event);
        } else if (event instanceof JoinResponseEvent) {
            handleJoinResponseEvent((JoinResponseEvent) event);
        }
    }

    private void handleJoinResponseEvent(JoinResponseEvent event) {
        publisher.addConnection(event.getIp(),event.getPort());
        publisher.addConnections(event.getConnectionsDataList());
    }

    private void handleJoinEvent(JoinEvent event) {
        //publisher.broadCast(event);
        publisher.addConnection(event.getIp(),event.getPort());
        publisher.sendJoinResponse(event.getIp(),event.getPort());
    }

    private void handleTransactionHistoryRequest(TransactionHistoryRequestEvent event) {
        //Get the history
        TransactionHistory history = nodeRunner.getTransactionHistory(event.getAddress(),event.getIndex());
        List<ConfirmedTransaction> confirmedTransactions = history.getConfirmedTransactions();
        List<CoinBaseTransaction> coinBaseTransactions = history.getCoinBaseTransactions();
        //Check if it is valid size
        TransactionHistoryResponseEvent the = new TransactionHistoryResponseEvent(event.getIp(),event.getPort(),history,event.getIndex(),1,1, LocalDateTime.now());
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(outputStream);
            os.writeObject(the);
            byte[] data = outputStream.toByteArray();
            LocalDateTime time = LocalDateTime.now();
            if (data.length> Configuration.getMax_package_size()){
                //Find nr of parts
                int parts = data.length/Configuration.getMax_package_size()*2;
                // Find part size for ConfirmedTransactions
                int confirmed_size = confirmedTransactions.size()/parts;
                // Find part size for CoinBaseTransactions
                int coin_base_size = coinBaseTransactions.size()/parts;

                for (int i = 0;i<parts;i++){
                    int confirmed_end_index;
                    int coinBase_end_index;
                    if(i==parts-1){
                        confirmed_end_index=confirmedTransactions.size();
                        coinBase_end_index = coinBaseTransactions.size();
                    }else{
                        confirmed_end_index=(i+1)*confirmed_size;
                        coinBase_end_index=(i+1)*coin_base_size;
                    }
                    ArrayList<ConfirmedTransaction> confirmed_part = new ArrayList<>(confirmedTransactions.subList(i*confirmed_size,confirmed_end_index));
                    ArrayList<CoinBaseTransaction> coinBase_part = new ArrayList<>( coinBaseTransactions.subList(i*coin_base_size,coinBase_end_index));
                    publisher.sendTransactionHistoryResponse(new TransactionHistory(confirmed_part,coinBase_part),time,event.getIndex(),i+1,parts,event.getIp(),event.getPort());
                }
            }else{
                publisher.sendTransactionHistoryResponse(history, time, event.getIndex(),1,1,event.getIp(),event.getPort());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

        if (child == null) {
            if (orphanage.addChain(block,event.getPort())) {
                publisher.requestBlock(block.getBlockNumber()-1,event.getIp(),event.getPort());
            }

        } else
        if (child.getPreviousHash().equals(block.hash())) {
            if (block.getBlockNumber()<=nodeRunner.getBlockNumber() && nodeRunner.validateBlock(block)) {
                Deque<Block> chain = orphanage.popChain(key);
                if (chain.peekLast().getBlockNumber() > nodeRunner.getBlockNumber()) {
                    System.out.println("what we do here is go Back!");
                    nodeRunner.rollback(chain,chain.peekFirst().getBlockNumber());
                }
            } else {
                orphanage.addBlock(block,key);
                publisher.requestBlock(block.getBlockNumber()-1,event.getIp(),event.getPort());
            }
        } else {
            orphanage.popChain(key);
            System.out.println("child: " +child);
            System.out.println("block: " +block);
            System.out.println("Bad Request - " + block.getBlockNumber());
        }
    }

    @Override
    public void handleReceivedBlock(ReceivedBlockEvent event) {
        //TODO: maybe not necessary (find better way)
        publisher.addConnection(event.getIp(),event.getPort());

        Block block = event.getBlock();
//        System.out.println("ReceivedBlock event");
        if (block.getBlockNumber() < nodeRunner.getBlockNumber()) {
            //we dont care about old
        } else if (block.getBlockNumber() == nodeRunner.getBlockNumber()) {

            //TODO: Change (maybe) if last block was received
        } else if (block.getBlockNumber() > nodeRunner.getBlockNumber()+1) {
            GlobalCounter.reportConflict();
            if (orphanage.addChain(block,event.getPort())) {
                publisher.requestBlock(block.getBlockNumber()-1,event.getIp(),event.getPort());
            }
            publisher.broadCast(event);
        } else {
            //the block is valid
            if (nodeRunner.validateBlock(block)) {
                //the block was valid
                nodeRunner.interruptReceivedBlock(block);
                publisher.broadCast(event);
            } else {
                // The block was not valid
                System.out.println("Invalid next block");
                //TODO not ignore these? (perhaps this is counts as a conflict)
                GlobalCounter.reportConflict();
            }
        }
    }

    @Override
    public void handleNewTransaction(TransactionEvent transactionEvent) {
        // put into Node's queue of potential transactions
        //the TransactionManager should validate the transaction
        if (nodeRunner.getTransactionManager().addTransaction(transactionEvent.getTransaction())) {
            publisher.broadCast(transactionEvent);
        }
    }

    @Override
    public void handleMinedBlock(MinedBlockEvent block) {
        publisher.publishBlock(block.getBlock());
    }

    @Override
    public void stop() {
        this.interrupted = true;
        thread.interrupt();
    }
}
