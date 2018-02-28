package FakeClients;

import Configuration.Configuration;
import Crypto.Impl.RSA;
import Crypto.Interfaces.KeyPair;
import Crypto.Interfaces.PublicKeyCryptoSystem;
import Impl.ArrayListTransactions;
import Impl.Communication.StandardCommunicationHandler;
import Impl.Communication.StandardNodeRunner;
import Impl.Communication.UDPPublisher;
import Impl.Communication.UDPReceiver;
import Impl.PublicKeyAddress;
import Impl.StandardBlock;
import Interfaces.Address;
import Interfaces.Block;
import Interfaces.Communication.CommunicationHandler;
import Interfaces.Communication.Event;
import Interfaces.Communication.NodeRunner;
import Interfaces.Communication.Publisher;
import Interfaces.TransactionManager;
import blockchain.Stubs.CoinBaseTransactionStub;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class UDPConnectedBlockChains {
    public static void main(String[] args) {
        InetAddress IPAddress = null;
        try {
            IPAddress = InetAddress.getByName("localhost");
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
