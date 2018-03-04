package Impl.Communication;

import Interfaces.Block;

import java.util.*;

public class OrphanChainHolder {
    private Map<Integer,Deque<Block>> chains = new HashMap<>();


    public Block getBlock(int key) {
        Deque<Block> stack = chains.get(key);
        return stack.peekFirst();
    }

    public void addBlock(Block block, int key) {
        Deque<Block> stack = chains.get(key);
        stack.addFirst(block);
    }

    public Object getChain(int key) {
        //TODO: convert to outside format
        Object o =  chains.remove(key);
        if (chains.containsKey(key)) {
            System.out.println("Should not trigger");
        }
        return o;
    }

    /**
     *
     * @param block
     * @param key
     * @return returns True if a new chain has been added, false if there already is a chain
     */
    public boolean addChain(Block block, int key) {
        if (chains.containsKey(key)) {
            Deque<Block> stack = chains.get(key);
            if (stack.getLast().getBlockNumber() +1 == block.getBlockNumber()) {
                stack.addLast(block);
            } else {
                System.out.println("New attempted on: " + key + " , number: " + block.getBlockNumber() + ", vs " +stack.getLast().getBlockNumber() );
            }
            return false;
        }
        Deque<Block> stack = new ArrayDeque();
        stack.addFirst(block);
        chains.put(key,stack);
        return true;
    }
}
