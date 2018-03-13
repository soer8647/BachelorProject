package IntegrationTest;

import Configuration.Configuration;
import Crypto.Impl.RSA;
import Crypto.Impl.RSAPrivateKey;
import Crypto.Impl.RSAPublicKey;
import Crypto.Interfaces.KeyPair;
import Crypto.Interfaces.PublicKeyCryptoSystem;
import External.Pair;
import Impl.*;
import Impl.Communication.*;
import Impl.Hashing.SHA256;
import Impl.Transactions.ArrayListTransactions;
import Impl.Transactions.StandardCoinBaseTransaction;
import Impl.Transactions.StandardTransaction;
import Interfaces.Account;
import Interfaces.Block;
import Interfaces.CoinBaseTransaction;
import Interfaces.Communication.*;
import Interfaces.Transaction;
import blockchain.Stubs.TransactionStub;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.Assert.assertEquals;

public class AccountAndNodeCommunication {

    private static boolean running = true;
    private PublicKeyCryptoSystem<RSAPublicKey, RSAPrivateKey> cryptoSystem;

    private KeyPair keyPair1;
    private RSAPublicKey publicKeyAccount;
    private static PublicKeyAddress accountAddress;
    private RSAPrivateKey privateKeyAccount;

    private static Account account;
    private static AccountRunner accountRunner;

    private static Account nodeAccount;
    private static AccountRunner nodeAccountRunner;

    private KeyPair keyPair2;
    private RSAPrivateKey privateKeyNode;
    private RSAPublicKey publicKeyNode;
    private static PublicKeyAddress nodeAddress;

    Pair<InetAddress,Integer> nodeConnInfo;
    private static FullNode node;
    private static NodeRunner nodeRunner;

    static List<UDPConnectionData> udpConnectionsData;

    private static LinkedBlockingQueue<Event> transactionQueue;


    public AccountAndNodeCommunication() {
        cryptoSystem = new RSA(Configuration.getKeyBitLength());

        keyPair1 = cryptoSystem.generateNewKeys(BigInteger.valueOf(3));
        publicKeyAccount = keyPair1.getPublicKey();
        privateKeyAccount = keyPair1.getPrivateKey();

        keyPair2 = cryptoSystem.generateNewKeys(BigInteger.valueOf(3));
        publicKeyNode = keyPair2.getPublicKey();
        privateKeyNode = keyPair2.getPrivateKey();

        accountAddress = new PublicKeyAddress(publicKeyAccount);

        nodeAddress = new PublicKeyAddress(publicKeyNode);

        account = new StandardAccount(cryptoSystem,privateKeyAccount,publicKeyAccount,new SHA256());

        nodeAccount = new StandardAccount(cryptoSystem,privateKeyNode,publicKeyNode,new SHA256());

        Transaction tx = new TransactionStub();
        Transaction stx = new StandardTransaction(tx.getSenderAddress(),tx.getReceiverAddress(),tx.getValue(),tx.getValueProof(),tx.getSignature(),tx.getBlockNumberOfValueProof());
        CoinBaseTransaction ct = new StandardCoinBaseTransaction(stx.getSenderAddress(),10, 0);
        Block genesis = new StandardBlock(new BigInteger("4"),4,new BigInteger("42"),10,new ArrayListTransactions(),0,ct);

        transactionQueue = new LinkedBlockingQueue<>();
        node = new FullNode(new BlockChainDatabase("ACCOUNTCONNTEST",genesis),nodeAddress,new ConstantHardnessManager());
        Configuration.setHardnessParameter(18);

    }

    public static void main(String[] args) {
        //Not so much an integration test, but manual testing is done here.
        new AccountAndNodeCommunication();
        BlockingQueue<Event> eventQueue = new LinkedBlockingQueue<>();
        nodeRunner = new StandardNodeRunner(node,eventQueue,new StandardTransactionManager());
        UDPReceiver receiver = new UDPReceiver(eventQueue,8008);




        try {
            NodeCommunicationHandler nodeCommunicationHandler = new StandardNodeCommunicationHandler(nodeRunner,new UDPPublisher(InetAddress.getLocalHost(),8008, new ArrayList<>()),eventQueue);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }


        udpConnectionsData = new ArrayList<UDPConnectionData>();
        try {
            udpConnectionsData.add(new UDPConnectionData(InetAddress.getLocalHost(),8008));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        nodeAccountRunner = new StandardAccountRunner(nodeAccount,new TransactionHistory(), udpConnectionsData,8042);
        accountRunner = new StandardAccountRunner(account,new TransactionHistory(), udpConnectionsData,8000);
       //Account make transaction
        //Look in print to see the transaction is sent to the node and mined.
        //Now pull history from node
        for(int i =0;i<=3;i++){
            TransactionHistory th = accountRunner.getTransactionHistory();
            System.out.println("TRANSACTIONHISTORY :Confirmed nr:" + th.getConfirmedTransactions().size()+" CoinBase " +th.getCoinBaseTransactions().size()+"\n");
            accountRunner.updateTransactionHistory();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try{
                accountRunner.makeTransaction(nodeAddress,10);
            }catch (Exception e){
                assertEquals(NotEnoughMoneyException.class,e.getClass());
            }
        }
        for (int i=0;i<=5;i++){
            nodeAccountRunner.updateTransactionHistory();
            // Give the system 5 sec to respond
            try {
                Thread.sleep(5000);
                //TODO If the last transaction has not been verified the same value proof will be used causing a duplicate transaction.
                nodeAccountRunner.makeTransaction(accountAddress,10);
            } catch (InterruptedException | NotEnoughMoneyException e) {
                e.printStackTrace();
            }
            accountRunner.updateTransactionHistory();
        }
        // Account should now have 50!
        System.out.println(accountRunner.getBalance());
    }
}
