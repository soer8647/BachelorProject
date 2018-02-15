package blockchain;

import Crypto.Impl.RSAPrivateKey;
import Crypto.Impl.RSAPublicKey;
import Crypto.Impl.RSA;
import Crypto.Interfaces.KeyPair;
import Crypto.Interfaces.PrivateKey;
import Crypto.Interfaces.PublicKey;
import Crypto.Interfaces.PublicKeyCryptoSystem;
import Impl.Hashing.SHA256;
import Impl.PublicKeyAddress;
import Impl.StandardAccount;
import Interfaces.Account;
import Interfaces.Address;
import Interfaces.Transaction;
import blockchain.Stubs.AddressStub;
import blockchain.Stubs.TransactionStub;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class TestStandardAccount {
    PublicKeyCryptoSystem cryptoSystem;

    private Account account;
    @Before
    public void setUp(){
        cryptoSystem = new RSA(1000,new BigInteger("3"));
        account = new StandardAccount(cryptoSystem, new SHA256());
    }

    @Test
    public void shouldHaveAnAddress(){
        assertNotEquals(account.getAddress(),null);
    }

    @Test
    public void shouldHavePrivateKey(){
        assertNotEquals(account.getPrivateKey(),null);
    }

    @Test
    public void shouldUseRsa() {
        assertEquals(account.getCryptoSystem().getClass(), RSA.class);
    }

    @Test
    public void shouldHavePublicKey() {
        assertNotEquals(account.getPublicKey(),null);
    }

    @Test
    public void shouldBeAbleToMakeAccountFromKeyPair() {
        PublicKeyCryptoSystem cryptoSystem = new RSA(1000,new BigInteger("3"));
        KeyPair rsaKeyPair = cryptoSystem.generateNewKeys();
        RSAPublicKey publicKey = rsaKeyPair.getPublicKey();
        RSAPrivateKey privateKey = rsaKeyPair.getPrivateKey();
        assertNotEquals(new StandardAccount(cryptoSystem,privateKey,publicKey, new SHA256()),null);
    }

    @Test
    public void shouldBeAbleToMakeTransaction() {
        Transaction valueProof = new TransactionStub();
        Address sender = new PublicKeyAddress(account.getPublicKey(),cryptoSystem);
        Address receiver = new AddressStub();

        assertNotEquals(account.makeTransaction(sender,receiver,1,valueProof),null);
    }

    @Test
    public void shouldBeVerifiableSignatureOnTransaction() {
        Transaction valueProof = new TransactionStub();
        Address sender = new PublicKeyAddress(account.getPublicKey(),cryptoSystem);
        Address receiver = new AddressStub();
        RSAPublicKey publicKey = account.getPublicKey();

        Transaction transaction = account.makeTransaction(sender,receiver,1,valueProof);

        String tx = sender.toString()+receiver.toString()+1+valueProof.toString();
        BigInteger hash = account.getHashingAlgorithm().hash(tx);

        assertTrue(cryptoSystem.verify(publicKey,transaction.getSignature(),hash));
    }
}
