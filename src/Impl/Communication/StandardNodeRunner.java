package Impl.Communication;

import Impl.ArrayListTransactions;
import Impl.Communication.Events.MinedBlockEvent;
import Impl.FullNode;
import Impl.StandardBlock;
import Impl.StandardBlockChain;
import Interfaces.Block;
import Interfaces.Communication.Event;
import Interfaces.Communication.NodeRunner;
import Interfaces.Node;

import java.util.concurrent.BlockingQueue;

public class StandardNodeRunner implements NodeRunner {
    private Node node;
    private boolean interrupted = false;
    private Block specialBlock;

    public StandardNodeRunner(Block genesisBlock, BlockingQueue<Event> eventQueue) {
        this.node = new FullNode(new StandardBlockChain(genesisBlock));

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Block newBlock = genesisBlock;
                while(!interrupted) {
                    newBlock = node.mine(newBlock.hash(), new ArrayListTransactions());
                    if (newBlock==null && specialBlock!=null) {
                        node.getBlockChain().addBlock(specialBlock);
                        specialBlock = null;
                    } else {
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
