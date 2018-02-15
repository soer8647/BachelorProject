package Impl.Communication.Events;

import Interfaces.Block;
import Interfaces.Communication.Event;

public class MinedBlockEvent implements Event {
    private Block block;

    public MinedBlockEvent(Block block) {
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }
}