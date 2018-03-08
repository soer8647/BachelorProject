package Impl.Communication;

import Impl.Communication.Events.ReceivedBlockEvent;
import Impl.Communication.Events.RequestEvent;
import Impl.Communication.Events.RequestedEvent;
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
    private InetAddress localAdress;
    private int localPort;

    public UDPPublisher(InetAddress localAdress, int localPort, InetAddress[] ip, int[] port) {
        this.localAdress = localAdress;
        this.localPort = localPort;
        if (ip.length != port.length) {
            throw new IllegalArgumentException();
        }
        this.ips = ip;
        this.ports = port;
        try {
            Socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void publishBlock(Block block) {
        Event event = new ReceivedBlockEvent(block, localPort, localAdress);
        try {
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

    public void sendEvent(Event event, InetAddress ip, int port) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(outputStream);
            os.writeObject(event);
            byte[] data = outputStream.toByteArray();
            DatagramPacket sendPacket = new DatagramPacket(data, data.length, ip, port);
            Socket.send(sendPacket);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void requestBlock(int number, InetAddress ip, int port) {
        Event event = new RequestEvent(number, localPort, localAdress);
        sendEvent(event,ip,port);
    }

    @Override
    public void requestMaxBlock(InetAddress ip, int port) {
        requestBlock(-1,ip,port);
    }

    @Override
    public void answerRequest(Block block, InetAddress ip, int port) {
        Event event = new RequestedEvent(block,localPort,localAdress);
        sendEvent(event,ip,port);
    }
}