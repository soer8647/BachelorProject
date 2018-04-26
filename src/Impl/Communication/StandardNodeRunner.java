package Impl.Communication;

import GUI.SimpleListDisplay;
import Impl.Communication.Events.MinedBlockEvent;
import Impl.FullNode;
import Impl.TransactionHistory;
import Interfaces.*;
import Interfaces.Communication.Event;
import Interfaces.Communication.NodeRunner;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;

public class StandardNodeRunner implements NodeRunner {
    private final SimpleListDisplay display;
    private Node node;
    private boolean interrupted = false;
    private Block specialBlock;
    private Semaphore lock = new Semaphore(0);

    public StandardNodeRunner(Node node, BlockingQueue<Event> eventQueue) {
        this(node, eventQueue, new SimpleListDisplay() {
            @Override
            public void addToDisplay(Object o) {
            }
            @Override
            public void removeLatestFromDisplay() {
            }

            @Override
            public void stop() {

            }
        });
    }

    public StandardNodeRunner(Node node, BlockingQueue<Event> eventQueue, SimpleListDisplay display) {
        this.node = node;
        this.display = display;
        Thread thread = new Thread(new Runnable() {
            int prevBlocknr = node.getBlockChain().getBlockNumber();
            Block newBlock = node.getBlockChain().getBlock(prevBlocknr);

            @Override
            public void run() {
                while(!interrupted) {
                    Collection<Transaction> trans = node.getSomeTransactions();

                 //   System.out.println(getBlockNumber() + ",,, " + newBlock);
                    lock.release();
                    newBlock = node.mine(newBlock.hash(), trans);
                    try {
                        lock.acquire();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (newBlock==null) {
                        if (specialBlock==null) {
                            System.out.println("Chaos");
                            newBlock = node.getBlockChain().getBlock(node.getBlockChain().getBlockNumber());
                            continue;
                        }
                        node.addBlock(specialBlock);
                        newBlock = specialBlock;
                        specialBlock = null;
                    } else {
                        try {
                            eventQueue.put(new MinedBlockEvent(newBlock,0,null)); //TODO: is this proper way?
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            System.out.println("interrupted while posting new mined block :O");
                        }
                    }
                    display.addToDisplay(newBlock);
                    node.removeTransactions(newBlock.getTransactions());
                }
            }
        });
        thread.start();
    }

    public TransactionHistory getTransactionHistory(Address a, int index){
      return node.getTransactionHistory(a,index);
    }

    @Override
    public void stop() {
        this.interrupted = true;
        node.interrupt();
    }

    @Override
    public boolean addTransaction(Transaction transaction) {
        return node.addTransaction(transaction);
    }

    @Override
    public boolean validateBlock(Block block) {
        return node.validateBlock(block);
    }

    @Override
    public void interruptReceivedBlock(Block block) {
        this.specialBlock = block;
        node.interrupt();
    }

    @Override
    public int getBlockNumber() {
        return node.getBlockChain().getBlockNumber();
    }

    @Override
    public boolean validateTransaction(Transaction transaction) {
        return node.validateTransaction(transaction);
    }

    @Override
    public Block getBlock(int number) {
        return node.getBlockChain().getBlock(number);
    }

    @Override
    public void rollback(Deque<Block> newBlocks, int blockNumber) {
        //reset Chain
        try {
            lock.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Deque<Block> removedBlocks = new ArrayDeque<>(); //needed for reseting transactions, probably
        node.interrupt();

        //remove the old fork
        while (node.getBlockChain().getBlockNumber() >= blockNumber) {
            Block removedBlock = node.removeBlock();
            removedBlocks.addFirst(removedBlock);
            display.removeLatestFromDisplay();
        }

        //insert the new
        do {
            Block new_block = newBlocks.pop();
            node.addBlock(new_block);
            display.addToDisplay(new_block);
        } while (newBlocks.peekFirst() != null);
        //TODO: reset unspent transactions

        //reset the potentiel transactionspool ( in the TransactionManager)
        while (removedBlocks.peekFirst() != null) {
        Block block = removedBlocks.removeFirst();
            for (Transaction t: block.getTransactions()) {
                if (node.validateTransaction(t)) {
                    node.addTransaction(t);
                }
            }
        }
        specialBlock = null;
        lock.release();
    }
}
