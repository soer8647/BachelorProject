package FakeClients;

import Configuration.Configuration;
import Crypto.Interfaces.KeyPair;
import Crypto.Interfaces.PublicKeyCryptoSystem;
import GUI.GuiApp;
import Impl.*;
import Impl.Communication.*;
import Impl.ArrayListTransactions;
import Impl.Communication.StandardNodeCommunicationHandler;
import Impl.Communication.StandardNodeRunner;
import Impl.Communication.UDPPublisher;
import Impl.Communication.UDPReceiver;
import Impl.PublicKeyAddress;
import Impl.StandardBlock;
import Impl.StandardTransactionManager;
import Interfaces.Address;
import Interfaces.Block;
import Interfaces.Communication.ConstantHardnessManager;
import Interfaces.Communication.NodeCommunicationHandler;
import Interfaces.Communication.Event;
import Interfaces.Communication.NodeCommunicationHandler;
import Interfaces.Communication.NodeRunner;
import Interfaces.Node;
import Interfaces.TransactionManager;
import blockchain.Stubs.CoinBaseTransactionStub;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class UDPClient{
    private final NodeRunner nodeRunner;
    private final UDPReceiver receiver;
    private final UDPPublisher publisher;
    private final NodeCommunicationHandler nodeCommunicationHandler;

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
        Node node = new FullNode(new StandardBlockChain(genesisBlock),node1Address,new FlexibleHardnessManager());
        nodeRunner = new StandardNodeRunner(node,queue,transMan,display);
        receiver = new UDPReceiver(queue,myPort);
        publisher = new UDPPublisher(myIp,myPort,ips,otherPorts);
        nodeCommunicationHandler = new StandardNodeCommunicationHandler(nodeRunner,publisher,queue);
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
