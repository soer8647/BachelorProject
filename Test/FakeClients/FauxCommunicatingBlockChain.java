package FakeClients;

import Configuration.Configuration;
import Crypto.Interfaces.KeyPair;
import Crypto.Interfaces.PublicKeyCryptoSystem;
import Impl.Communication.StandardNodeCommunicationHandler;
import Impl.Communication.StandardNodeRunner;
import Impl.Communication.UDP.UDPPublisherNode;
import Impl.*;
import Impl.Transactions.ArrayListTransactions;
import Interfaces.*;
import Impl.ConstantHardnessManager;
import Interfaces.Communication.Event;
import Interfaces.Communication.NodeCommunicationHandler;
import Interfaces.Communication.NodeRunner;
import blockchain.Stubs.CoinBaseTransactionStub;
import blockchain.Stubs.FauxPublisher;
import blockchain.Stubs.FauxReceiver;

import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class FauxCommunicatingBlockChain {
    public static void main(String[] args) {
        BlockingQueue<Event> queue_A = new LinkedBlockingQueue<>();
        BlockingQueue<Event> queue_B = new LinkedBlockingQueue<>();

        Block genesisBlock =  new StandardBlock(new BigInteger("42"),20, new BigInteger("42"), 8, new ArrayListTransactions(),1,new CoinBaseTransactionStub());

        TransactionManager transMan = new EmptyTransactionsManager();

        PublicKeyCryptoSystem cs = Configuration.getCryptoSystem();
        KeyPair node1KeyPair = cs.generateNewKeys(BigInteger.valueOf(3));
        Address node1Address = new PublicKeyAddress(node1KeyPair.getPublicKey());

        KeyPair node2KeyPair = cs.generateNewKeys(BigInteger.valueOf(3));
        Address node2Address = new PublicKeyAddress(node2KeyPair.getPublicKey());
        BlockChain blockChain1 = new StandardBlockChain(genesisBlock);
        BlockChain blockChain2 = new StandardBlockChain(genesisBlock);
        Node node1 = new FullNode(blockChain1,node1Address,new ConstantHardnessManager(), new StandardTransactionManager(blockChain1));
        Node node2 = new FullNode(blockChain2,node2Address,new ConstantHardnessManager(),new StandardTransactionManager(blockChain2) );
        NodeRunner nodeRunner_A = new StandardNodeRunner(node1,queue_A,transMan);
        NodeRunner nodeRunner_B = new StandardNodeRunner(node2,queue_B,transMan);
        FauxReceiver receiver_A = new FauxReceiver(queue_A);
        FauxReceiver receiver_B = new FauxReceiver(queue_B);
        UDPPublisherNode publisher_A = new FauxPublisher(receiver_B);
        UDPPublisherNode publisher_B = new FauxPublisher(receiver_A);
        NodeCommunicationHandler nodeCommunicationHandler_A = new StandardNodeCommunicationHandler(nodeRunner_A,publisher_A,queue_A);
        NodeCommunicationHandler nodeCommunicationHandler_B = new StandardNodeCommunicationHandler(nodeRunner_B,publisher_B,queue_B);
        System.out.println("begin");
    }
}
