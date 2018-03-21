package Impl.Communication.UDP;

import Interfaces.Communication.Publisher;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.List;

public class UDPPublisher implements Publisher{

    private final InetAddress localAddress;
    private final int localPort;
    private final List<UDPConnectionData> connectionsDataList;
    private final UDPConnectionData Localdata;
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
        Localdata = new UDPConnectionData(localAddress,localPort);
    }


    /**
     * Send an event to a ip and port using the UDP protocol.
     *
     * @param object    The object to send
     * @param ip        The ip to send the event to
     * @param port      The port were the receiver is listening
     */
    public void send(Object object, InetAddress ip, int port) {
        try {
            System.out.println("from UDPPublisher.send(): " + object.getClass());
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(outputStream);
            os.writeObject(object);
            byte[] data = outputStream.toByteArray();
            DatagramPacket sendPacket = new DatagramPacket(data, data.length, ip, port);
            socket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param object     The object that is going to be send to all known connections.
     */
    public void broadCast(Object object){
        for (UDPConnectionData data : connectionsDataList){
            send(object,data.getInetAddress(), data.getPort());
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

    public void addConnections(List<UDPConnectionData> connectionsDataList) {
        // remove already known peers from adding list TODO: maybe not efficient, change?
        System.out.println(localPort + " got size " + connectionsDataList.size());
        for (UDPConnectionData d: connectionsDataList) {
            if (!this.connectionsDataList.contains(d) && !d.equals(Localdata)) {
                this.connectionsDataList.add(d);
                System.out.println("added: " + d.getPort());
            }
        }
    }

    public void addConnection(InetAddress ip, int port) {
        //System.out.println("adding someone");
        UDPConnectionData newPeer = new UDPConnectionData(ip,port);
        if (!connectionsDataList.contains(newPeer) && !newPeer.equals(Localdata)) {
            System.out.println("they got added");
            connectionsDataList.add(newPeer);
        }
    }
}
