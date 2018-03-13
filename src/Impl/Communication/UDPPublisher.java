package Impl.Communication;

import Interfaces.Communication.Event;
import Interfaces.Communication.Publisher;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.List;

public class UDPPublisher implements Publisher{

    private final InetAddress localAddress;
    private final int localPort;
    private final List<UDPConnectionData> connectionsDataList;
    protected DatagramSocket socket;

    public UDPPublisher(InetAddress localAddress, int localPort, List<UDPConnectionData> connectionsDataList) {
        this.localAddress = localAddress;
        this.localPort = localPort;
        this.connectionsDataList = connectionsDataList;
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }


    /**
     * Send an event to a ip and port using the UDP protocol.
     *
     * @param event     The event to send
     * @param ip        The ip to send the event to
     * @param port      The port were the receiver is listening
     */
    public void sendEvent(Event event, InetAddress ip, int port) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(outputStream);
            os.writeObject(event);
            byte[] data = outputStream.toByteArray();
            DatagramPacket sendPacket = new DatagramPacket(data, data.length, ip, port);
            socket.send(sendPacket);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param event     The event that is going to be send to all known connections.
     */
    public void broadCastEvent(Event event){
        for (UDPConnectionData data : connectionsDataList){
            sendEvent(event,data.getInetAddress(), data.getPort());
        }
    }

    public InetAddress getLocalAddress() {
        return localAddress;
    }

    public int getLocalPort() {
        return localPort;
    }

    public List<UDPConnectionData> getConnectionsDataList() {
        return connectionsDataList;
    }

    public DatagramSocket getSocket() {
        return socket;
    }
}
