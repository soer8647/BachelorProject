package Impl.Communication;

import Impl.Communication.Events.MinedBlockEvent;
import Impl.FullNode;
import Impl.StandardBlockChain;
import Interfaces.*;
import Interfaces.Communication.Event;
import Interfaces.Communication.NodeRunner;

import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;

public class StandardNodeRunner implements NodeRunner {
    private Node node;
    private boolean interrupted = false;
    private Block specialBlock;

    public StandardNodeRunner(Block genesisBlock, BlockingQueue<Event> eventQueue, TransactionManager transactionManager, Address address) {
        this(genesisBlock, eventQueue, transactionManager, address, new Display() {
            @Override
            public void addToDisplay(Object o) {
            }
        });
    }

    public StandardNodeRunner(Block genesisBlock, BlockingQueue<Event> eventQueue, TransactionManager transactionManager, Address address,Display display) {
        this.node = new FullNode(new StandardBlockChain(genesisBlock), address);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Block newBlock = genesisBlock;
                while(!interrupted) {
                    Transactions trans = transactionManager.getSomeTransactions();

                    //TODO: Ensure that the block hasnt been added to the chain before method returns
                    newBlock = node.mine(newBlock.hash(), trans);
                    if (newBlock==null && specialBlock!=null) {
                        node.getBlockChain().addBlock(specialBlock);
                        display.addToDisplay(specialBlock);
                        newBlock = specialBlock;
                        specialBlock = null;
                    } else {
                        display.addToDisplay(newBlock);
                        try {
                            eventQueue.put(new MinedBlockEvent(newBlock));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            System.out.println("interrupted while posting new mined block :O");
                        }
                    }
                }
            }
        });
        thread.start();
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
}
