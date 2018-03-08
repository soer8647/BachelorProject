package IntegrationTest;

import Configuration.Configuration;
import Crypto.Impl.RSA;
import Crypto.Impl.RSAPrivateKey;
import Crypto.Impl.RSAPublicKey;
import Crypto.Interfaces.KeyPair;
import Crypto.Interfaces.PublicKeyCryptoSystem;
import Database.BlockchainDatabase;
import Impl.*;
import Impl.Communication.StandardAccountRunner;
import Impl.Communication.StandardNodeRunner;
import Impl.Hashing.SHA256;
import Interfaces.Account;
import Interfaces.Block;
import Interfaces.CoinBaseTransaction;
import Interfaces.Communication.AccountRunner;
import Interfaces.Communication.Event;
import Interfaces.Communication.NodeRunner;
import Interfaces.Transaction;
import blockchain.Stubs.TransactionStub;
import External.Pair;

import java.math.BigInteger;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;

public class AccountAndNodeCommunication {

    private PublicKeyCryptoSystem cryptoSystem;

    private KeyPair keyPair1;
    private RSAPublicKey publicKeyAccount;
    private PublicKeyAddress accountAddress;
    private RSAPrivateKey privateKeyAccount;

    private Account account;
    private AccountRunner accountRunner;

    private KeyPair keyPair2;
    private RSAPrivateKey privateKeyNode;
    private RSAPublicKey publicKeyNode;
    private PublicKeyAddress nodeAddress;

    Pair<InetAddress,Integer> nodeConnInfo;
    private FullNode node;
    private NodeRunner nodeRunner;

    Collection<Pair<InetAddress,Integer>> nodeIpAndPortCollection;

    private LinkedBlockingQueue<Event> transactionQueue;


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
        nodeRunner = new StandardNodeRunner(node,new LinkedBlockingQueue<>(),new StandardTransactionManager());

        nodeIpAndPortCollection = new ArrayList<>();
        accountRunner = new StandardAccountRunner(account,transactionQueue,nodeIpAndPortCollection,8000);

    }
}
