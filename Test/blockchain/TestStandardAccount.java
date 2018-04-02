package blockchain;

import Configuration.Configuration;
import Crypto.Impl.RSA;
import Crypto.Impl.RSAPrivateKey;
import Crypto.Impl.RSAPublicKey;
import Crypto.Interfaces.KeyPair;
import Crypto.Interfaces.PublicKeyCryptoSystem;
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

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class TestStandardAccount {
    PublicKeyCryptoSystem cryptoSystem;

    private Account account;
    @Before
    public void setUp(){
        cryptoSystem = new RSA(1000);
        account = new StandardAccount();
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
    public void shouldHavePublicKey() {
        assertNotEquals(account.getPublicKey(),null);
    }

    @Test
    public void shouldBeAbleToMakeAccountFromKeyPair() {
        PublicKeyCryptoSystem cryptoSystem = new RSA(1000);
        KeyPair rsaKeyPair = cryptoSystem.generateNewKeys(BigInteger.valueOf(3));
        RSAPublicKey publicKey = rsaKeyPair.getPublicKey();
        RSAPrivateKey privateKey = rsaKeyPair.getPrivateKey();
        assertNotEquals(new StandardAccount(privateKey,publicKey),null);
    }

    @Test
    public void shouldBeAbleToMakeTransaction() {
        BigInteger valueProof = new TransactionStub().transactionHash();
        Address sender = new PublicKeyAddress(account.getPublicKey());
        Address receiver = new AddressStub();

        assertNotEquals(account.makeTransaction(receiver,1,valueProof, 0,0),null);
    }

    @Test
    public void shouldBeVerifiableSignatureOnTransaction() {
        BigInteger valueProof = new TransactionStub().transactionHash();
        Address sender = new PublicKeyAddress(account.getPublicKey());
        Address receiver = new AddressStub();
        RSAPublicKey publicKey = account.getPublicKey();

        Transaction transaction = account.makeTransaction(receiver,1,valueProof, 0,0);

        String tx = sender.toString()+receiver.toString()+1+valueProof.toString()+0;
        BigInteger hash = Configuration.hash(tx);

        assertTrue(cryptoSystem.verify(publicKey,transaction.getSignature(),hash));
    }
}
