package Impl.Communication;

import Interfaces.Communication.Event;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.List;

public class UDPEventPublisher {

    private DatagramSocket socket;
    private List<UDPConnectionData> connectionDataList;


    public UDPEventPublisher(List<UDPConnectionData> connectionDataList) {
        this.connectionDataList = connectionDataList;

        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param event     The event to send to a given address.
     * @param ip        The ip address of the receiver.
     * @param port      The port where the receiver is listening.
     */
    private void sendEvent(Event event, InetAddress ip, int port) {
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
    public void broadcastEvent(Event event){
        for (UDPConnectionData data : connectionDataList){
            sendEvent(event,data.getInetAddress(), data.getPort());
        }
    }
}


