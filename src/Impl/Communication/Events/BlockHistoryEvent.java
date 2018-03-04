package Impl.Communication.Events;

import Impl.BlockSnippet;

import java.net.InetAddress;

public class BlockHistoryEvent extends ProtoEvent{
    private BlockSnippet blockSnippet;

    public BlockHistoryEvent(BlockSnippet blockSnippet, int port, InetAddress ip) {
        super(port, ip);
        this.blockSnippet= blockSnippet;
    }

    public BlockSnippet getBlockSnippet() {
        return blockSnippet;
    }

}
