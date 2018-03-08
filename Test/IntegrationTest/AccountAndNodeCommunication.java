package IntegrationTest;

import Configuration.Configuration;
import Crypto.Impl.RSA;
import Crypto.Impl.RSAPrivateKey;
import Crypto.Impl.RSAPublicKey;
import Crypto.Interfaces.KeyPair;
import Crypto.Interfaces.PublicKeyCryptoSystem;
import Database.BlockchainDatabase;
import External.Pair;
import Impl.*;
import Impl.Communication.StandardAccountRunner;
import Impl.Communication.StandardNodeCommunicationHandler;
import Impl.Communication.StandardNodeRunner;
import Impl.Communication.UDPReceiver;
import Impl.Hashing.SHA256;
import Interfaces.Account;
import Interfaces.Block;
import Interfaces.CoinBaseTransaction;
import Interfaces.Communication.AccountRunner;
import Interfaces.Communication.Event;
import Interfaces.Communication.NodeCommunicationHandler;
import Interfaces.Communication.NodeRunner;
import Interfaces.Transaction;
import blockchain.Stubs.ConsolePublisher;
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

    private PublicKeyCryptoSystem cryptoSystem;

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
        CoinBaseTransaction ct = new StandardCoinBaseTransaction(stx.getSenderAddress(),10);
        Block genesis = new StandardBlock(new BigInteger("4"),4,new BigInteger("42"),10,new ArrayListTransactions(),0,ct);

        transactionQueue = new LinkedBlockingQueue<>();
        node = new FullNode(new BlockchainDatabase("ACCOUNTCONNTEST",genesis),nodeAddress);
        Configuration.setHardnessParameter(17);

    }

    public static void main(String[] args) {
        new AccountAndNodeCommunication();

        BlockingQueue<Event> incoming = new LinkedBlockingQueue<>();
        nodeRunner = new StandardNodeRunner(node,incoming,new StandardTransactionManager());
        UDPReceiver receiver = new UDPReceiver(incoming,8008);
        NodeCommunicationHandler nodeCommunicationHandler = new StandardNodeCommunicationHandler(nodeRunner,new ConsolePublisher(),incoming);



        nodeIpAndPortCollection = new ArrayList<>();
        try {
            nodeIpAndPortCollection.add(new Pair<>(InetAddress.getLocalHost(),8008));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        CopyOnWriteArrayList transactionHistory = new CopyOnWriteArrayList<>();
        transactionHistory.add(new StandardTransaction(nodeAddress,accountAddress,10,new BigInteger("100"),new BigInteger("42"),1));
        accountRunner = new StandardAccountRunner(account,transactionHistory,nodeIpAndPortCollection,8000);
        accountRunner.makeTransaction(nodeAddress,10);

    }
}
