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
        UDPClient clientA = new UDPClient(portA,new ArrayList<>());
        UDPClient clientB = new UDPClient(portB, new UDPConnectionData(IPAddress,portA));
        UDPClient clientC = new UDPClient(portC, new UDPConnectionData(IPAddress,portB));
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("size of A is " + clientA.getPublisher().getConnectionsDataList().size());
        for (UDPConnectionData d : clientA.getPublisher().getConnectionsDataList()) {
            System.out.println(d.getPort());
        }
        System.out.println("size of B is " + clientB.getPublisher().getConnectionsDataList().size());

        for (UDPConnectionData d : clientB.getPublisher().getConnectionsDataList()) {
            System.out.println(d.getPort());
        }

        System.out.println("size of C is " + clientC.getPublisher().getConnectionsDataList().size());

        for (UDPConnectionData d : clientC.getPublisher().getConnectionsDataList()) {
            System.out.println(d.getPort());
        }
    }
}
