package blockchain;

import Impl.Communication.Events.MinedBlockEvent;
import Impl.Communication.Events.TransactionEvent;
import Impl.Communication.StandardNodeCommunicationHandler;
import Impl.Communication.UDP.UDPConnectionData;
import Impl.Communication.UDP.UDPPublisherNode;
import Impl.Communication.UDP.UDPReceiver;
import Interfaces.Communication.Event;
import Interfaces.Communication.NodeRunner;
import blockchain.Stubs.BlockStub;
import blockchain.Stubs.CoinBaseTransactionStub;
import blockchain.Stubs.NodeRunnerStub;
import blockchain.Stubs.TransactionStub;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static junit.framework.TestCase.assertEquals;

public class TestNodeCommunication {

    private NodeRunnerStub nodeRunnerC;
    private LinkedBlockingQueue<Event> queueA;
    private InetAddress address;
    private List<UDPReceiver>  receivers;

    @Before
    public void setup() {
        receivers = new ArrayList<>();
        address = null;
        try {
            address = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        int portA = 9876;
        int portB = 6789;
        int portC = 7001;
        List<UDPConnectionData> aList = new ArrayList<UDPConnectionData>();
        aList.add(new UDPConnectionData(address,portB));
        List<UDPConnectionData> bList = new ArrayList<UDPConnectionData>();
        bList.add(new UDPConnectionData(address,portA));
        bList.add(new UDPConnectionData(address,portC));
        List<UDPConnectionData> cList = new ArrayList<UDPConnectionData>();
        cList.add(new UDPConnectionData(address,portB));
        NodeRunner nodeRunnerA = new NodeRunnerStub();
        NodeRunner nodeRunnerB = new NodeRunnerStub();
        nodeRunnerC = new NodeRunnerStub();
        queueA = new LinkedBlockingQueue<>();
        BlockingQueue<Event> queueB = new LinkedBlockingQueue<>();
        BlockingQueue<Event> queueC = new LinkedBlockingQueue<>();
        startCommunicationHandler(nodeRunnerA, queueA,portA,aList);
        startCommunicationHandler(nodeRunnerB, queueB,portB,bList);
        startCommunicationHandler(nodeRunnerC, queueC,portC,cList);
    }

    public void startCommunicationHandler(NodeRunner runner, BlockingQueue<Event> queue, int port, List<UDPConnectionData> connections) {
        UDPPublisherNode publisher = null;
        try {
            publisher = new UDPPublisherNode(InetAddress.getLocalHost(), port, connections);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        UDPReceiver receiver = new UDPReceiver(queue,port);
        receivers.add(receiver);
        new StandardNodeCommunicationHandler(runner,publisher,queue);
    }

    @Test
    public void shouldSendBlockAcrossNetwork() {
        assertEquals(0,nodeRunnerC.getBlockNumber());
        queueA.add(new MinedBlockEvent(new BlockStub(new CoinBaseTransactionStub(),new ArrayList<>(),1), 9876, address));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(1,nodeRunnerC.getBlockNumber());
    }

    @Test
    public void shouldSendTransactionAcrossNetwork() {
        queueA.add(new TransactionEvent(new TransactionStub(), 9876, address));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(true,nodeRunnerC.gotTransaction());
    }

    @After
    public void after() {
        for (UDPReceiver receiver: receivers) {
            receiver.stop();
        }
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
