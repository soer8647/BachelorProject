package Impl.Communication.Events;

import Interfaces.Block;
import Interfaces.Communication.Event;

public class ReceivedBlockEvent implements Event{
    private Block block;

    public ReceivedBlockEvent(Block block) {
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }
}
