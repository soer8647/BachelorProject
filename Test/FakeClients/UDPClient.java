package FakeClients;

import Impl.ArrayListTransactions;
import Impl.Communication.UDPPublisher;
import Impl.Communication.UDPReceiver;
import Impl.Communication.StandardCommunicationHandler;
import Impl.Communication.StandardNodeRunner;
import Impl.StandardBlock;
import Interfaces.Block;
import Interfaces.Communication.CommunicationHandler;
import Interfaces.Communication.Event;
import Interfaces.Communication.NodeRunner;
import Interfaces.Communication.Publisher;
import blockchain.Stubs.CoinBaseTransactionStub;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class UDPClient{
    public static void main(String[] args) {
        InetAddress IPAddress = null;
        try {
            IPAddress = InetAddress.getByName("192.168.1.37");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
//        startUDP(9876,6789,IPAddress);
        startUDP(6789,9876,IPAddress);
    }

    public static void startUDP(int myPort, int otherPort, InetAddress ip) {
        BlockingQueue<Event> queue = new LinkedBlockingQueue<>();
        Block genesisBlock =  new StandardBlock(new BigInteger("42"),20, new BigInteger("42"), 8, new ArrayListTransactions(),1, new CoinBaseTransactionStub());
        NodeRunner nodeRunner = new StandardNodeRunner(genesisBlock,queue);
        UDPReceiver receiver = new UDPReceiver(queue,myPort);
        Publisher publisher = new UDPPublisher(ip,otherPort);
        CommunicationHandler communicationHandler_B = new StandardCommunicationHandler(nodeRunner,publisher,queue);
    }
}
