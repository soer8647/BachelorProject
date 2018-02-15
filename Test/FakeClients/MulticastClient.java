package FakeClients;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class MulticastClient extends Thread {
    public void run() {
       try {
           MulticastSocket socket = new MulticastSocket(4446);
           InetAddress group = InetAddress.getByName("239.255.255.255");
           socket.joinGroup(group);

           DatagramPacket packet;
           for (int i = 0; i < 5; i++) {
               byte[] buf = new byte[256];
               packet = new DatagramPacket(buf, buf.length);
               socket.receive(packet);

               String received = new String(packet.getData());
               System.out.println("Quote of the Moment: " + received);
           }

           socket.leaveGroup(group);
           socket.close();
       } catch (IOException e) {
           e.printStackTrace();
       }
    }
}
