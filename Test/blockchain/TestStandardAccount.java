package blockchain;

import Crypto.Impl.RSAPrivateKey;
import Crypto.Impl.RSAPublicKey;
import Crypto.Impl.RSA;
import Crypto.Interfaces.KeyPair;
import Crypto.Interfaces.PublicKeyCryptoSystem;
import Impl.StandardAccount;
import Interfaces.Account;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TestStandardAccount {

    public Account account;
    @Before
    public void setUp(){
        account = new StandardAccount(new RSA(1000, new BigInteger("3")));
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
        assertNotEquals(new StandardAccount(cryptoSystem,privateKey,publicKey),null);
    }


}
