package Impl.Communication.Events;

import Interfaces.Communication.Event;

import java.net.InetAddress;

public abstract class ProtoEvent implements Event{
    private int port;
    private InetAddress ip;

    protected ProtoEvent(int port, InetAddress ip) {
        this.port = port;
        this.ip = ip;
    }


    @Override
    public int getPort() {
        return port;
    }

    @Override
    public InetAddress getIp() {
        return ip;
    }
}
