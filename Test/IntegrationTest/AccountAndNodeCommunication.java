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
import Impl.Transactions.ConfirmedTransaction;
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
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class AccountAndNodeCommunication {

    private static boolean running = true;
    private PublicKeyCryptoSystem<RSAPublicKey, RSAPrivateKey> cryptoSystem;

    private KeyPair keyPair1;
    private RSAPublicKey publicKeyAccount;
    private static PublicKeyAddress accountAddress;
    private RSAPrivateKey privateKeyAccount;

    private static Account account;
    private static AccountRunner accountRunner;

    private KeyPair keyPair2;
    private RSAPrivateKey privateKeyNode;
    private RSAPublicKey publicKeyNode;
    private static PublicKeyAddress nodeAddress;

    Pair<InetAddress,Integer> nodeConnInfo;
    private static FullNode node;
    private static NodeRunner nodeRunner;

    static Collection<Pair<InetAddress,Integer>> nodeIpAndPortCollection;

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
        Transaction tx = new TransactionStub();
        Transaction stx = new StandardTransaction(tx.getSenderAddress(),tx.getReceiverAddress(),tx.getValue(),tx.getValueProof(),tx.getSignature(),tx.getBlockNumberOfValueProof());
        CoinBaseTransaction ct = new StandardCoinBaseTransaction(stx.getSenderAddress(),10, 0);
        Block genesis = new StandardBlock(new BigInteger("4"),4,new BigInteger("42"),10,new ArrayListTransactions(),0,ct);

        transactionQueue = new LinkedBlockingQueue<>();
        node = new FullNode(new BlockChainDatabase("ACCOUNTCONNTEST",genesis),nodeAddress,new ConstantHardnessManager());
        Configuration.setHardnessParameter(17);

    }

    public static void main(String[] args) {
        //Not so much an integrationtest, but manual testing is done here.
        new AccountAndNodeCommunication();

        BlockingQueue<Event> eventQueue = new LinkedBlockingQueue<>();
        nodeRunner = new StandardNodeRunner(node,eventQueue,new StandardTransactionManager());
        UDPReceiver receiver = new UDPReceiver(eventQueue,8008);
        try {
            NodeCommunicationHandler nodeCommunicationHandler = new StandardNodeCommunicationHandler(nodeRunner,new UDPPublisher(InetAddress.getLocalHost(),8008, new InetAddress[0], new int[0]),eventQueue);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }


        nodeIpAndPortCollection = new ArrayList<>();
        try {
            nodeIpAndPortCollection.add(new Pair<>(InetAddress.getLocalHost(),8008));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        // Accounts first transaction to have proof of funds
        ConfirmedTransaction ct = new ConfirmedTransaction(new StandardTransaction(nodeAddress,accountAddress,10,new BigInteger("100"),new BigInteger("42"),1),1);
        TransactionHistory transactionHistory = new TransactionHistory(new CopyOnWriteArrayList<ConfirmedTransaction>(){{add(ct);}},new CopyOnWriteArrayList<>());
        accountRunner = new StandardAccountRunner(account,transactionHistory,nodeIpAndPortCollection,8000);
       //Account make transaction
        accountRunner.makeTransaction(nodeAddress,10);
        //Look in print to see the transaction is sent to the node and mined.

        System.out.println("TRANSACTIONHISTORY " + accountRunner.getTransactionHistory());
        //Now pull history from node
        accountRunner.updateTransactionHistory();

        while(running){
            TransactionHistory th = accountRunner.getTransactionHistory();
            System.out.println("TRANSACTIONHISTORY :Confirmed nr:" + th.getConfirmedTransactions().size()+" CoinBase " +th.getCoinBaseTransactions().size()+"\n");
            accountRunner.updateTransactionHistory();
            System.out.println("BALANCE: " +accountRunner.getBalance());
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
