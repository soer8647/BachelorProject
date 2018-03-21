package Impl.Communication.Events;

import java.net.InetAddress;

public class JoinEvent extends ProtoEvent {

    protected JoinEvent(int port, InetAddress ip) {
        super(port, ip);
    }
}
