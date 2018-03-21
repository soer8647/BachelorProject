package Impl.Communication.Events;

import Impl.Communication.UDP.UDPConnectionData;

import java.net.InetAddress;
import java.util.List;

public class JoinResponseEvent extends ProtoEvent {
    private List<UDPConnectionData> connectionsDataList;

    public JoinResponseEvent(List<UDPConnectionData> connectionsDataList, int port, InetAddress ip) {
        super(port, ip);
        this.connectionsDataList = connectionsDataList;
    }

    public List<UDPConnectionData> getConnectionsDataList() {
        return connectionsDataList;
    }
}
