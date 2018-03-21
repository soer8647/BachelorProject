package Impl.Communication.Events;

import java.net.InetAddress;

public class JoinEvent extends ProtoEvent {

    public JoinEvent(int port, InetAddress ip) {
        super(port, ip);
    }
}
