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
    private InetAddress[] ips;
    private DatagramSocket Socket;
    private int[] ports;

    public UDPPublisher(InetAddress[] ip, int[] port) {
        if (ip.length != port.length) {
            throw new IllegalArgumentException();
        }
        this.ips = ip;
        this.ports = port;
    }

    @Override
    public void publishBlock(Block block) {
        Event event = new ReceivedBlockEvent(block);
        try {
            Socket = new DatagramSocket();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(outputStream);
            os.writeObject(event);
            byte[] data = outputStream.toByteArray();
            for (int i = 0; i < ips.length; i++) {
                InetAddress ip = this.ips[i];
                int port = this.ports[i];
                DatagramPacket sendPacket = new DatagramPacket(data, data.length, ip, port);
                Socket.send(sendPacket);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}