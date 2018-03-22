package FakeClients;

import Configuration.Configuration;
import Crypto.Interfaces.KeyPair;
import Crypto.Interfaces.PublicKeyCryptoSystem;
import GUI.Display;
import GUI.GuiApp;
import Impl.FlexibleHardnessManager;
import Impl.Communication.StandardNodeCommunicationHandler;
import Impl.Communication.StandardNodeRunner;
import Impl.Communication.UDP.UDPConnectionData;
import Impl.Communication.UDP.UDPPublisherNode;
import Impl.Communication.UDP.UDPReceiver;
import Impl.*;
import Impl.Transactions.ArrayListTransactions;
import Interfaces.*;
import Interfaces.Communication.Event;
import Interfaces.Communication.NodeCommunicationHandler;
import Interfaces.Communication.NodeRunner;
import blockchain.Stubs.CoinBaseTransactionStub;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class UDPClient{
    private final NodeRunner nodeRunner;
    private final UDPReceiver receiver;
    private final UDPPublisherNode publisher;
    private final NodeCommunicationHandler nodeCommunicationHandler;
    private final Display display;

    public UDPClient(int myPort, UDPConnectionData seed, int delay) {
        this(myPort, new ArrayList<UDPConnectionData>(), delay);
        publisher.sendJoin(seed.getInetAddress(),seed.getPort());
    }
    public UDPClient(int myPort, UDPConnectionData seed) {
        this(myPort, seed, 0);
    }

    public UDPClient(int myPort, List<UDPConnectionData> connectionsData) {
        this(myPort,connectionsData,0);
    }
    public UDPClient(int myPort, List<UDPConnectionData> connectionsData, int delay) {
        InetAddress myIp = null;
        try {
            myIp = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        BlockingQueue<Event> queue = new LinkedBlockingQueue<>();
        Block genesisBlock =  new StandardBlock(new BigInteger("42"),20, new BigInteger("42"), 8, new ArrayListTransactions(),0,new CoinBaseTransactionStub());

        PublicKeyCryptoSystem cs = Configuration.getCryptoSystem();
        KeyPair node1KeyPair = cs.generateNewKeys(BigInteger.valueOf(3));
        Address node1Address = new PublicKeyAddress(node1KeyPair.getPublicKey());

        display = new GuiApp(node1Address.getPublicKey() + " - " + myPort);


        BlockChain blockChain = new StandardBlockChain(genesisBlock);
        TransactionManager transMan = new StandardTransactionManager(blockChain);
        Node node = new FullNode(blockChain,node1Address,new FlexibleHardnessManager(), new StandardTransactionManager(blockChain));
        nodeRunner = new StandardNodeRunner(node,queue,transMan,display);
        receiver = new UDPReceiver(queue,myPort);

        publisher = new UDPPublisherNode(myIp,myPort,connectionsData,delay);
        nodeCommunicationHandler = new StandardNodeCommunicationHandler(nodeRunner,publisher,queue);
    }

    public void stop() {
        //TODO: Stop the client and maybe return/print its last state.
        nodeRunner.stop();
        receiver.stop();
        nodeCommunicationHandler.stop();
        display.stop();
    }

    public static void main(String[] args) {
        InetAddress IPAddress = null;
        try {
            IPAddress = InetAddress.getByName("192.168.1.37");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
//        startUDP(9876,6789,IPAddress);

        List<UDPConnectionData> connectionsData = new ArrayList<UDPConnectionData>();
        connectionsData.add(new UDPConnectionData(IPAddress,9876));
        UDPClient client = new UDPClient(6789, connectionsData );
    }

    public UDPPublisherNode getPublisher() {
        return publisher;
    }
}
