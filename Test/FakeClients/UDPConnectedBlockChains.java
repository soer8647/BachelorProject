package FakeClients;

import Impl.Communication.UDP.UDPConnectionData;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;


public class UDPConnectedBlockChains {
    public static void main(String[] args) {
        InetAddress IPAddress = null;
        try {
            IPAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        int portA = 9876;
        int portB = 6789;
        int portC = 7002;
        int portD = 8003;
        UDPClient clientA = new UDPClient(portA,new ArrayList<>());
        UDPClient clientB = new UDPClient(portB, new UDPConnectionData(IPAddress,portA));
        UDPClient clientC = new UDPClient(portC, new UDPConnectionData(IPAddress,portB));
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        UDPClient clientD = new UDPClient(portD, new UDPConnectionData(IPAddress,portB));
    }
}
