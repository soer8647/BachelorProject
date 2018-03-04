package blockchain;

import Configuration.Configuration;
import Crypto.Impl.RSA;
import Crypto.Impl.RSAPrivateKey;
import Crypto.Impl.RSAPublicKey;
import Crypto.Interfaces.KeyPair;
import Crypto.Interfaces.PublicKeyCryptoSystem;
import Impl.Communication.StandardAccountRunner;
import Impl.Hashing.SHA256;
import Impl.PublicKeyAddress;
import Impl.StandardAccount;
import Interfaces.Account;
import Interfaces.Communication.AccountRunner;
import Interfaces.Communication.Event;
import javafx.util.Pair;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

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
        Collection<Pair<InetAddress,Integer>> nodeIpAndPortCollection = new ArrayList<>();
        nodeIpAndPortCollection.add(new Pair(InetAddress.getLocalHost(),1));
        accountRunner = new StandardAccountRunner(account, transactionQueue,nodeIpAndPortCollection );
    }

    @Test
    public void shouldHaveAccount() {
        assertNotEquals(null,accountRunner.getAccount());
    }

    @Test
    public void shouldHaveAddress() {
        assertEquals(senderAddress.toString(),account.getAddress().toString());
    }

    @Test
    public void shouldDelegateTransaction() {
        //Transaction transaction = account.makeTransaction(account.getAddress(), receiverAddress,1,new BigInteger("42"),1);
        accountRunner.makeTransaction(receiverAddress,1);
        assertEquals(1,accountRunner.getEventHandler().getEventCount());
    }
}
