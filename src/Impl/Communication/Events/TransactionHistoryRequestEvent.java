package Impl.Communication.Events;

import Interfaces.Address;
import Interfaces.Communication.Event;

import java.net.InetAddress;

public class TransactionHistoryRequestEvent implements Event{

    private InetAddress ip;
    private int port;
    private int index;
    private Address address;

    public TransactionHistoryRequestEvent(InetAddress ip, int port ,int index, Address address) {
        this.ip = ip;
        this.port = port;
        this.index = index;
        this.address = address;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public InetAddress getIp() {
        return ip;
    }

    public int getIndex() {
        return index;
    }

    public Address getAddress() {
        return address;
    }
}
