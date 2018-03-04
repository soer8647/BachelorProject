package Impl.Communication.Events;

import Interfaces.Block;
import Interfaces.Communication.Event;

import java.net.InetAddress;

public class RequestedEvent extends ProtoEvent {
    private Block block;

    public RequestedEvent(Block block, int port, InetAddress ip) {
        super(port, ip);
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }
}
