package blockchain;

import Configuration.Configuration;
import Crypto.Impl.RSA;
import Crypto.Impl.RSAPrivateKey;
import Crypto.Impl.RSAPublicKey;
import Crypto.Interfaces.KeyPair;
import Crypto.Interfaces.PublicKeyCryptoSystem;
import Impl.Communication.NotEnoughMoneyException;
import Impl.Communication.StandardAccountRunner;
import Impl.Hashing.SHA256;
import Impl.PublicKeyAddress;
import Impl.StandardAccount;
import Impl.StandardTransaction;
import Interfaces.Account;
import Interfaces.Communication.AccountRunner;
import Interfaces.Communication.Event;
import Interfaces.Transaction;
import External.Pair;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TestStandardAccountRunner {


    private AccountRunner accountRunner;
    private PublicKeyCryptoSystem cryptoSystem;

    private KeyPair keyPair1;
    private RSAPublicKey publicKeySender;
    private PublicKeyAddress senderAddress;
    private RSAPrivateKey privateKeySender;

    private Account account;

    private KeyPair keyPair2;
    private RSAPrivateKey privateKeyReceiver;
    private RSAPublicKey publicKeyReceiver;
    private PublicKeyAddress receiverAddress;

    Collection<Pair<InetAddress,Integer>> nodeIpAndPortCollection;

    private LinkedBlockingQueue<Event> transactionQueue;

    @Before
    public void setUp() throws Exception {
        cryptoSystem = new RSA(Configuration.getKeyBitLength());

        keyPair1 = cryptoSystem.generateNewKeys(BigInteger.valueOf(3));
        publicKeySender = keyPair1.getPublicKey();
        privateKeySender = keyPair1.getPrivateKey();

        keyPair2 = cryptoSystem.generateNewKeys(BigInteger.valueOf(3));
        privateKeyReceiver = keyPair2.getPrivateKey();
        publicKeyReceiver = keyPair2.getPublicKey();

        receiverAddress = new PublicKeyAddress(publicKeyReceiver);
        senderAddress = new PublicKeyAddress(publicKeySender);
        account = new StandardAccount(cryptoSystem,privateKeySender,publicKeySender,new SHA256());
        transactionQueue = new LinkedBlockingQueue<>();
        nodeIpAndPortCollection = new ArrayList<>();
        nodeIpAndPortCollection.add(new Pair<InetAddress, Integer>(InetAddress.getLocalHost(),1));

    }

    @Test
    public void shouldHaveAccount() {
        accountRunner = new StandardAccountRunner(account, transactionQueue,nodeIpAndPortCollection,8000);
        assertNotEquals(null,accountRunner.getAccount());
    }

    @Test
    public void shouldHaveAddress() {
        assertEquals(senderAddress.toString(),account.getAddress().toString());
    }

    @Test
    public void shouldDelegateTransaction() {
        //Transaction transaction = account.makeTransaction(account.getAddress(), receiverAddress,1,new BigInteger("42"),1);
        Transaction t1 = new StandardTransaction(receiverAddress,senderAddress,4,new BigInteger("100"),new BigInteger("42"),1);
        // Account sends 3 money
        CopyOnWriteArrayList<Transaction> transactions = new CopyOnWriteArrayList<Transaction>(){{add(t1);}};
        accountRunner=new StandardAccountRunner(account,transactions,nodeIpAndPortCollection,8003);
        accountRunner.makeTransaction(receiverAddress,1);
        assertEquals(1,accountRunner.getOutGoingEventQueue().size());
    }

    @Test
    public void shouldGetBalanceFromHistory() {
        //Setup fake history for account
        CopyOnWriteArrayList<Transaction> transactions = new CopyOnWriteArrayList<>();
        // Account receives 42 money
        Transaction t1 = new StandardTransaction(receiverAddress,senderAddress,42,new BigInteger("42"),new BigInteger("42"),1);
        // Account sends 10 money
        Transaction t2 = new StandardTransaction(senderAddress,receiverAddress,10,new BigInteger("42"),new BigInteger("42"),1);
        transactions.add(t1);
        transactions.add(t2);
        accountRunner = new StandardAccountRunner(account,transactions,new ArrayList<>(),8001);
        assertEquals(32,accountRunner.getBalance());
    }

    @Test
    public void shouldGetValueProof() {
        CopyOnWriteArrayList<Transaction> transactions = new CopyOnWriteArrayList<>();
        //Account gets 4 money
        Transaction t1 = new StandardTransaction(receiverAddress,senderAddress,4,new BigInteger("100"),new BigInteger("42"),1);
        // Account sends 3 money
        Transaction t2 = new StandardTransaction(senderAddress,receiverAddress,3,new BigInteger("42"),new BigInteger("43"),2);
        //Account gets 2 money
        Transaction t3 = new StandardTransaction(receiverAddress,senderAddress,2,new BigInteger("42"),new BigInteger("44"),3);
        // Account sends 1 money
        Transaction t4 = new StandardTransaction(senderAddress,receiverAddress,1,new BigInteger("42"),new BigInteger("45"),4);
        //Account gets 2 money
        Transaction t5 = new StandardTransaction(receiverAddress,senderAddress,2,new BigInteger("42"),new BigInteger("46"),5);
        // Account sends 1 money
        Transaction t6 = new StandardTransaction(senderAddress,receiverAddress,1,new BigInteger("42"),new BigInteger("47"),6);
        //Balance of 3

        transactions.add(t1);
        transactions.add(t2);
        transactions.add(t3);
        transactions.add(t4);
        transactions.add(t5);
        transactions.add(t6);
        try {
            accountRunner = new StandardAccountRunner(account, transactions, new ArrayList<>(),8002);
            assertEquals(3, accountRunner.getBalance());
            assertEquals(new Pair<>(new BigInteger("100"), 1).toString(), accountRunner.getValueProof(1).toString());
        }catch (NotEnoughMoneyException e){
            e.printStackTrace();
        }
            }

}
