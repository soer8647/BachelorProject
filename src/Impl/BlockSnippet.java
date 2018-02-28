package Impl;

import Interfaces.Block;

import java.io.Serializable;
import java.util.List;

public class BlockSnippet implements Serializable{
    private int startNumber;
    private int endNumber;
    private List<Block> blocks;


    public BlockSnippet(List<Block> blocks) {
        this.blocks = blocks;
        this.startNumber = blocks.get(0).getBlockNumber();
        this.endNumber = blocks.get(blocks.size()).getBlockNumber();
    }
}
