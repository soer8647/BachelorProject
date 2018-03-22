package FakeClients;

import Configuration.*;
import Impl.Communication.UDP.UDPConnectionData;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class DelayTest {
    public static void main(String[] args) {
        InetAddress IPAddress = null;
        try {
            IPAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        Configuration.setHardnessParameter(17);
        Configuration.setHardnessTimeTarget(10);
        int delay = 1000;

        int portA = 9876;
        int portB = 6789;

        UDPClient clientA = new UDPClient(portA,new ArrayList<>(),delay);
        UDPClient clientB = new UDPClient(portB, new UDPConnectionData(IPAddress,portA),delay);

        try {
            Thread.sleep(60000/8);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Conflicts: " + GlobalCounter.getConflictCount());
        clientA.stop();
        clientB.stop();
    }
}
