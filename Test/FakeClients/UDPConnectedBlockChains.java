package FakeClients;

import Impl.ArrayListTransactions;
import Impl.Communication.UDPPublisher;
import Impl.Communication.UDPReceiver;
import blockchain.Stubs.FauxPublisher;
import blockchain.Stubs.FauxReceiver;
import Impl.Communication.StandardCommunicationHandler;
import Impl.Communication.StandardNodeRunner;
import Impl.Hashing.SHA256;
import Impl.StandardBlock;
import Interfaces.Block;
import Interfaces.Communication.CommunicationHandler;
import Interfaces.Communication.Event;
import Interfaces.Communication.NodeRunner;
import Interfaces.Communication.Publisher;

import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class UDPConnectedBlockChains {
    public static void main(String[] args) {
        BlockingQueue<Event> queue_A = new LinkedBlockingQueue<>();
        BlockingQueue<Event> queue_B = new LinkedBlockingQueue<>();
        Block genesisBlock =  new StandardBlock(new BigInteger("42"),20, new BigInteger("42"), 8, new ArrayListTransactions(),1);
        NodeRunner nodeRunner_A = new StandardNodeRunner(genesisBlock,queue_A);
        NodeRunner nodeRunner_B = new StandardNodeRunner(genesisBlock,queue_B);
        UDPReceiver receiver_A = new UDPReceiver(queue_A,9876);
        UDPReceiver receiver_B = new UDPReceiver(queue_B,6789);
        Publisher publisher_A = new UDPPublisher(6789);
        Publisher publisher_B = new UDPPublisher(9876);
        CommunicationHandler communicationHandler_A = new StandardCommunicationHandler(nodeRunner_A,publisher_A,queue_A);
        CommunicationHandler communicationHandler_B = new StandardCommunicationHandler(nodeRunner_B,publisher_B,queue_B);
        System.out.println("begin");
    }
}
