package Impl.Communication.Events;

import Interfaces.Address;
import Interfaces.Communication.Event;

import java.net.InetAddress;

public class TransactionHistoryRequestEvent implements Event{

    private int index;

    public TransactionHistoryRequestEvent(int index, Address address) {
        this.index = index;
    }

    @Override
    public int getPort() {
        return 0;
    }

    @Override
    public InetAddress getIp() {
        return null;
    }

    public int getIndex() {
        return index;
    }
}
