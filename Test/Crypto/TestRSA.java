package Crypto;

import Crypto.Impl.RSA;
import Crypto.Impl.RSAKeyPair;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class TestRSA {

    private RSA rsa;


    @Before
    public void setUp(){
        rsa = new RSA(1000);
    }

    @Test
    public void shouldHaveKeyLength(){
        assertNotEquals(rsa.getKeyBitLength(),null);
    }

    @Test
    public void shouldHaveKeyBitLengthOf2000(){
        assertEquals(rsa.getKeyBitLength(),1000);
    }

    @Test
    public void shouldGenerateKeys(){
        assertNotEquals(rsa.generateNewKeys(BigInteger.valueOf(3)),null);
    }

    @Test
    public void shouldGenerateRandomKeys(){
        assertNotEquals(rsa.generateNewKeys(BigInteger.valueOf(3)),rsa.generateNewKeys(BigInteger.valueOf(3)));
    }

    @Test
    public void shouldEncryptAndDecrypt() {
        RSAKeyPair keyPair = rsa.generateNewKeys(BigInteger.valueOf(3));

        BigInteger message = new BigInteger("42");
        BigInteger cipher = rsa.encrypt(keyPair.getPublicKey(),message);
        BigInteger decrypted = rsa.decrypt(keyPair.getPrivateKey(),cipher);
        assertEquals(message,decrypted);
    }


    @Test
    public void shouldBeAbleToSignMessageAndVerify() {
        RSAKeyPair keyPair = rsa.generateNewKeys(BigInteger.valueOf(3));

        BigInteger message = new BigInteger("42");

        BigInteger signature = rsa.sign(keyPair.getPrivateKey(),message);
        assertTrue(rsa.verify(keyPair.getPublicKey(),signature, message));
    }
}
