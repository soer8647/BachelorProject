package Impl.Communication.Events;

import Interfaces.Communication.Event;

import java.net.InetAddress;

public class RequestEvent extends ProtoEvent{
    private int number;

    public RequestEvent(int number, int port, InetAddress ip) {
        super(port, ip);
        this.number = number;
    }

    public int getNumber() {
        return number;
    }
}
