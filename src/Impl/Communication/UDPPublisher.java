package Impl.Communication;

import Impl.Communication.Events.ReceivedBlockEvent;
import Interfaces.Block;
import Interfaces.Communication.Event;
import Interfaces.Communication.Publisher;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;

public class UDPPublisher implements Publisher{
    private final InetAddress ip;
    DatagramSocket Socket;
    private int port;

    public UDPPublisher(InetAddress ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public void publishBlock(Block block) {
        Event event = new ReceivedBlockEvent(block);
        try {
            Socket = new DatagramSocket();
            byte[] incomingData = new byte[1024];
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(outputStream);
            os.writeObject(event);
            byte[] data = outputStream.toByteArray();
            DatagramPacket sendPacket = new DatagramPacket(data, data.length, ip, this.port);
            Socket.send(sendPacket);
            System.out.println("send");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}