package Impl.Communication;

import Interfaces.Communication.Event;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.net.DatagramSocket;
import java.util.concurrent.BlockingQueue;

public class UDPReceiver {
    private DatagramSocket socket = null;
    private BlockingQueue queue;
    private int port;

    public UDPReceiver(BlockingQueue<Event> queue, int port) {
        this.queue = queue;
        this.port = port;
        new Thread(() -> createAndListenSocket()).start();
    }

    private void createAndListenSocket() {
        try {
            socket = new DatagramSocket(this.port);
            byte[] incomingData = new byte[2048];

            while (true) {
                DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
                socket.receive(incomingPacket);
                byte[] data = incomingPacket.getData();
                ByteArrayInputStream in = new ByteArrayInputStream(data);
                ObjectInputStream is = new ObjectInputStream(in);
                try {
                    Event event = (Event) is.readObject();
                    queue.put(event);
                } catch (ClassNotFoundException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}