package Impl.Communication.Events;

import Interfaces.Address;

import java.net.InetAddress;
/*
* A event for a client in the network to request all transactions where a given address is involved.
* */

public class TransactionHistoryRequestEvent extends ProtoEvent{

    private int index;
    private Address address;

    public TransactionHistoryRequestEvent(InetAddress ip, int port ,int index, Address address) {
        super(port,ip);
        this.index = index;
        this.address = address;
    }

    public int getIndex() {
        return index;
    }

    public Address getAddress() {
        return address;
    }
}
