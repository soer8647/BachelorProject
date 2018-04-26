package FakeClients;

import Configuration.Configuration;
import GUI.AccountRunnerGUI;
import GUI.JFrameListDisplay;
import Impl.*;
import Impl.Communication.StandardAccountRunner;
import Impl.Communication.StandardNodeCommunicationHandler;
import Impl.Communication.StandardNodeRunner;
import Impl.Communication.UDP.UDPConnectionData;
import Impl.Communication.UDP.UDPPublisherNode;
import Impl.Communication.UDP.UDPReceiver;
import Impl.Transactions.ConfirmedTransaction;
import Impl.Transactions.StandardCoinBaseTransaction;
import Impl.Transactions.StandardTransaction;
import Interfaces.*;
import Interfaces.Communication.AccountRunner;
import Interfaces.Communication.Event;
import Interfaces.Communication.NodeCommunicationHandler;
import Interfaces.Communication.NodeRunner;
import blockchain.Stubs.TransactionStub;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class AccountRunnerGUITest {


    public static void main(String[] args) {
        Configuration.setHardnessParameter(18);
        Transaction tx = new TransactionStub();
        StandardTransaction stx = new StandardTransaction(tx.getSenderAddress(), tx.getReceiverAddress(), tx.getValue(),  tx.getSignature(), 0);
        CoinBaseTransaction  ct = new StandardCoinBaseTransaction(stx.getSenderAddress(), 0, 0);
        Block block = new StandardBlock(new BigInteger("4"), 4, new BigInteger("42"), new ArrayList<>(), 0, ct);

        Account main = new StandardAccount();
        CoinBaseTransaction c = new StandardCoinBaseTransaction(main.getAddress(),10,1);
        TransactionHistory t = new TransactionHistory(new ArrayList<ConfirmedTransaction>(),new ArrayList<CoinBaseTransaction>());

        Account second = new StandardAccount();

        try {
            UDPConnectionData nodeData = new UDPConnectionData(InetAddress.getLocalHost(),1337);


            AccountRunner runner = new StandardAccountRunner(main,t,new ArrayList<UDPConnectionData>(){{
                add(nodeData);
            }},8000);

            AccountRunnerGUI gui = new AccountRunnerGUI(runner);
            gui.addAddress(second.getAddress());
            BlockChainDatabase blockChain = new BlockChainDatabase("ACCOUNTRUNNERTEST",block);

            //Connect accountrunner to noderunner
            Node testNode = new FullNode(blockChain,main.getAddress(),new ConstantHardnessManager(),new DBTransactionManager(blockChain));

            BlockingQueue<Event> blockingQueue = new LinkedBlockingQueue<Event>();
            NodeRunner nodeRunner = new StandardNodeRunner(testNode,blockingQueue,new JFrameListDisplay("Test"));

            UDPReceiver receiver = new UDPReceiver(blockingQueue,1337);

            NodeCommunicationHandler communicationHandler = new StandardNodeCommunicationHandler(nodeRunner,
                    new UDPPublisherNode(InetAddress.getLocalHost(),1337,new ArrayList<UDPConnectionData>()),blockingQueue);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }
}
