package blockchain;

import Configuration.Configuration;
import Crypto.Impl.RSA;
import Crypto.Impl.RSAPrivateKey;
import Crypto.Impl.RSAPublicKey;
import Crypto.Interfaces.KeyPair;
import Crypto.Interfaces.PublicKeyCryptoSystem;
import External.Pair;
import Impl.Communication.NotEnoughMoneyException;
import Impl.Communication.StandardAccountRunner;
import Impl.ConfirmedTransaction;
import Impl.Hashing.SHA256;
import Impl.PublicKeyAddress;
import Impl.StandardAccount;
import Impl.StandardTransaction;
import Interfaces.Account;
import Interfaces.CoinBaseTransaction;
import Interfaces.Communication.AccountRunner;
import Interfaces.Communication.Event;
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
        ConfirmedTransaction t1 = new ConfirmedTransaction(new StandardTransaction(receiverAddress,senderAddress,4,new BigInteger("100"),new BigInteger("42"),1),2);
        // Account sends 3 money
        Pair transactions = new Pair<Collection<ConfirmedTransaction>,Collection<CoinBaseTransaction>>(new CopyOnWriteArrayList<ConfirmedTransaction>(){{add(t1);}},new CopyOnWriteArrayList<>());
        accountRunner=new StandardAccountRunner(account,transactions,nodeIpAndPortCollection,8003);
        accountRunner.makeTransaction(receiverAddress,1);
        assertEquals(1,accountRunner.getEventQueue().size());
    }

    @Test
    public void shouldGetBalanceFromHistory() {
        //Setup fake history for account

        // Account receives 42 money
        ConfirmedTransaction t1 = new ConfirmedTransaction(new StandardTransaction(receiverAddress,senderAddress,42,new BigInteger("100"),new BigInteger("42"),1),2);
        // Account sends 10 money
        ConfirmedTransaction t2 = new ConfirmedTransaction(new StandardTransaction(senderAddress,receiverAddress,10,new BigInteger("100"),new BigInteger("42"),1),2);
        Pair transactions = new Pair<Collection<ConfirmedTransaction>,Collection<CoinBaseTransaction>>(new CopyOnWriteArrayList<ConfirmedTransaction>(){{add(t1);add(t2);}},new CopyOnWriteArrayList<>());
        accountRunner = new StandardAccountRunner(account,transactions,new ArrayList<>(),8001);
        assertEquals(32,accountRunner.getBalance());
    }

    @Test
    public void shouldGetValueProof() {

        //Account gets 4 money
        ConfirmedTransaction t1 = new ConfirmedTransaction(new StandardTransaction(receiverAddress,senderAddress,4,new BigInteger("100"),new BigInteger("42"),1),2);
        // Account sends 3 money
        ConfirmedTransaction t2 = new ConfirmedTransaction(new StandardTransaction(senderAddress,receiverAddress,3,new BigInteger("42"),new BigInteger("43"),2),3);
        //Account gets 2 money
        ConfirmedTransaction t3 = new ConfirmedTransaction(new StandardTransaction(receiverAddress,senderAddress,2,new BigInteger("42"),new BigInteger("44"),3),4);
        // Account sends 1 money
        ConfirmedTransaction t4 = new ConfirmedTransaction(new StandardTransaction(senderAddress,receiverAddress,1,new BigInteger("42"),new BigInteger("45"),4),5);
        //Account gets 2 money
        ConfirmedTransaction t5 = new ConfirmedTransaction(new StandardTransaction(receiverAddress,senderAddress,2,new BigInteger("42"),new BigInteger("46"),5),6);
        // Account sends 1 money
        ConfirmedTransaction t6 = new ConfirmedTransaction(new StandardTransaction(senderAddress,receiverAddress,1,new BigInteger("42"),new BigInteger("47"),6),7);
        //Balance of 3
        Pair transactions = new Pair<Collection<ConfirmedTransaction>,Collection<CoinBaseTransaction>>(new CopyOnWriteArrayList<ConfirmedTransaction>(){{add(t1);add(t2);add(t3);add(t4);add(t5);add(t6);}},new CopyOnWriteArrayList<>());
        try {
            accountRunner = new StandardAccountRunner(account, transactions, new ArrayList<>(),8002);
            assertEquals(3, accountRunner.getBalance());
            assertEquals(new Pair<>(t1.transActionHash(), 2).toString(), accountRunner.getValueProof(1).toString());
        }catch (NotEnoughMoneyException e){
            e.printStackTrace();
        }
            }

}
