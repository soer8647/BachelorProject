package Impl.Communication.UDP;

import java.net.InetAddress;

public class UDPConnectionData {

    private InetAddress inetAddress;
    private int port;


    public UDPConnectionData(InetAddress inetAddress, int port) {
        this.inetAddress = inetAddress;
        this.port = port;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public int getPort() {
        return port;
    }
}
