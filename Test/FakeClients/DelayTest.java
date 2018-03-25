package FakeClients;

import Configuration.*;
import Impl.Communication.UDP.UDPConnectionData;

import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class DelayTest {
    public static final int runTime = 40000;
    public static final int cooldownTime = 10000;

    public static void main(String[] args) {
        int low = 3;
        int high = 9;
        int[] results = new int[1+high-low];
        for (int i = low; i <= high; i++) {
            results[i-low] = doDelayTest(3,i);
        }

        try {
            FileWriter writer = new FileWriter("JavaOut.csv");

            for (int j = 0; j < results.length; j++) {
                writer.append(String.valueOf(results[j]));
                writer.append("\n");
            }
            writer.close();
        } catch (IOException e) {

        }

    }

    /**
     *
     * @param timeTarget amount of 0.1 seconds we want it to take to mine 1 block
     * @param delay      amount of 0.1 seconds delay in communication
     * @return           The amount of conflicts that occured while the clients ran
     */
    public static int doDelayTest(int timeTarget , int delay) {
        GlobalCounter.resetConflictCount();
        InetAddress IPAddress = null;
        try {
            IPAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        Configuration.setHardnessParameter(15);
        Configuration.setHardnessTimeTarget(timeTarget);

        int portA = 9876;
        int portB = 6789;

        UDPClient clientA = new UDPClient(portA,new ArrayList<>(),delay*100);
        UDPClient clientB = new UDPClient(portB, new UDPConnectionData(IPAddress,portA),delay*100);

        try {
            Thread.sleep(runTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        clientA.stop();
        clientB.stop();
        try {
            Thread.sleep(cooldownTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return GlobalCounter.getConflictCount();
    }
}
