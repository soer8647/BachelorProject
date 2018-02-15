package FakeClients;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastServerThread extends Thread {
    private DatagramSocket socket;

    public MulticastServerThread() {
        try {
            socket = new MulticastSocket(4445);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        int i = 0;
        while (true) {
            i++;
            try {
                byte[] buf = new byte[256];
                // don't wait for request...just send a quote

                String dString = "lol" + i;
                buf = dString.getBytes();

                InetAddress group = InetAddress.getByName("239.255.255.255");
                DatagramPacket packet;
                packet = new DatagramPacket(buf, buf.length, group, 4446);
                socket.send(packet);

                try {
                    sleep((long) (Math.random() * 5000));
                }
                catch (InterruptedException e) { }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
//        socket.close();
    }
}
