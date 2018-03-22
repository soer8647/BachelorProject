package Impl.Communication.UDP;

import Configuration.Configuration;
import Interfaces.Communication.Event;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.rmi.activation.UnknownObjectException;
import java.util.concurrent.BlockingQueue;

public class UDPReceiver {
    private boolean notDone;
    private DatagramSocket socket = null;
    private BlockingQueue<Event> queue;
    private int port;

    public UDPReceiver(BlockingQueue<Event> queue, int port) {
        this.queue = queue;
        this.port = port;
        notDone = true;
        new Thread(this::createAndListenSocket).start();
        while(notDone) {
            //Busy wait
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void createAndListenSocket() {
        try {
            socket = new DatagramSocket(this.port);
            byte[] incomingData = new byte[Configuration.getMax_package_size()];

            System.out.println("LISTENING ON: "+ port);
            notDone = false;
            while (true) {
                DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
                socket.receive(incomingPacket);
                byte[] data = incomingPacket.getData();
                ByteArrayInputStream in = new ByteArrayInputStream(data);
                ObjectInputStream is = new ObjectInputStream(in);
                try {
                    Object o = is.readObject();
                    if (o instanceof Event) {
                        Event event = (Event) o;
                        queue.put(event);
                    }else{
                        throw new UnknownObjectException("Unknown object received");
                    }
                } catch (InterruptedException | UnknownObjectException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            socket.close();
        }
    }
}