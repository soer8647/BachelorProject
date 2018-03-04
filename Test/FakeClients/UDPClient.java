package FakeClients;

import Configuration.Configuration;
import Crypto.Impl.RSA;
import Crypto.Interfaces.KeyPair;
import Crypto.Interfaces.PublicKeyCryptoSystem;
import GUI.GuiApp;
import Impl.ArrayListTransactions;
import Impl.Communication.UDPPublisher;
import Impl.Communication.UDPReceiver;
import Impl.Communication.StandardCommunicationHandler;
import Impl.Communication.StandardNodeRunner;
import Impl.PublicKeyAddress;
import Impl.StandardBlock;
import Impl.StandardTransactionManager;
import Interfaces.Address;
import Interfaces.Block;
import Interfaces.Communication.CommunicationHandler;
import Interfaces.Communication.Event;
import Interfaces.Communication.NodeRunner;
import Interfaces.Communication.Publisher;
import blockchain.Stubs.CoinBaseTransactionStub;
import Interfaces.TransactionManager;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class UDPClient{
    private final NodeRunner nodeRunner;
    private final UDPReceiver receiver;
    private final UDPPublisher publisher;
    private final CommunicationHandler communicationHandler;

    public UDPClient(int myPort, int[] otherPorts, InetAddress[] ips) {
        InetAddress myIp = null;
        try {
            myIp = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        BlockingQueue<Event> queue = new LinkedBlockingQueue<>();
        Block genesisBlock =  new StandardBlock(new BigInteger("42"),20, new BigInteger("42"), 8, new ArrayListTransactions(),1,new CoinBaseTransactionStub());
        TransactionManager transMan = new StandardTransactionManager();

        PublicKeyCryptoSystem cs = Configuration.getCryptoSystem();
        KeyPair node1KeyPair = cs.generateNewKeys(BigInteger.valueOf(3));
        Address node1Address = new PublicKeyAddress(node1KeyPair.getPublicKey());

        GuiApp display = new GuiApp(node1Address.getPublicKey());
        nodeRunner = new StandardNodeRunner(genesisBlock,queue,transMan,node1Address,display);
        receiver = new UDPReceiver(queue,myPort);
        publisher = new UDPPublisher(myIp,myPort,ips,otherPorts);
        communicationHandler = new StandardCommunicationHandler(nodeRunner,publisher,queue);
    }

    public void stop() {
        //TODO: Stop the client and maybe return/print its last state.
    }

    public static void main(String[] args) {
        InetAddress IPAddress = null;
        try {
            IPAddress = InetAddress.getByName("192.168.1.37");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
//        startUDP(9876,6789,IPAddress);
        UDPClient client = new UDPClient(6789, new int[]{9876}, new InetAddress[]{IPAddress});
    }
}
