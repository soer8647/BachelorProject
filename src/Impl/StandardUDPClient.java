package Impl;

import Configuration.Configuration;
import Crypto.Impl.RSAPublicKey;
import Crypto.Interfaces.PublicKey;
import GUI.JFrameListDisplay;
import Impl.Communication.StandardNodeCommunicationHandler;
import Impl.Communication.StandardNodeRunner;
import Impl.Communication.UDP.UDPConnectionData;
import Impl.Communication.UDP.UDPPublisherNode;
import Impl.Communication.UDP.UDPReceiver;
import Impl.Transactions.StandardCoinBaseTransaction;
import Interfaces.*;
import Interfaces.Communication.Event;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class StandardUDPClient {

    private UDPReceiver receiver;
    private UDPPublisherNode publisher;
    private StandardNodeCommunicationHandler nodeCommunicationHandler;
    private JFrameListDisplay display;
    private StandardNodeRunner nodeRunner;

    public StandardUDPClient(Account acc, int myPort, List<UDPConnectionData> connectionsData) {
        InetAddress myIp = null;
        try {
            myIp = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        BlockingQueue<Event> queue = new LinkedBlockingQueue<>();

        BlockChainDatabase db = new BlockChainDatabase("db",Configuration.genesisblock);

        receiver = new UDPReceiver(queue,myPort);
        TransactionManager transMan = new DBTransactionManager(db);
        Node node = new FullNode(db,acc.getAddress(),new FlexibleHardnessManager(), transMan);
        display = new JFrameListDisplay(acc.getAddress().getPublicKey() + " - " + myPort);
        nodeRunner = new StandardNodeRunner(node, queue, transMan, display);

        publisher = new UDPPublisherNode(myIp,myPort,connectionsData);
        nodeCommunicationHandler = new StandardNodeCommunicationHandler(nodeRunner,publisher,queue);
    }
}
