package FakeClients;

import java.net.InetAddress;
import java.net.UnknownHostException;


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
        UDPClient clientA = new UDPClient(portA,new int[]{portB,portC},new InetAddress[]{IPAddress,IPAddress});
        UDPClient clientB = new UDPClient(portB,new int[]{portA,portC},new InetAddress[]{IPAddress,IPAddress});
        UDPClient clientC = new UDPClient(portC,new int[]{portB,portA},new InetAddress[]{IPAddress,IPAddress});
    }
}
