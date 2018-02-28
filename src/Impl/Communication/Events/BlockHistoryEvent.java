package Impl.Communication.Events;

import Impl.BlockSnippet;
import Interfaces.Communication.Event;

public class BlockHistoryEvent implements Event {
    private BlockSnippet blockSnippet;

    public BlockHistoryEvent(BlockSnippet blockSnippet) {
        this.blockSnippet= blockSnippet;
    }

    public BlockSnippet getBlockSnippet() {
        return blockSnippet;
    }

}
