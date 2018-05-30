package Crypto;

import Configuration.Configuration;
import Crypto.Impl.Seed;
import Crypto.Impl.WOTS;
import Crypto.Impl.WOTSKeyPair;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Arrays;

import static junit.framework.TestCase.assertEquals;

public class TestWOTS {

    private WOTS wots;

    @Before
    public void setUp(){
        wots = new WOTS(Configuration.getHashingAlgorithm());
    }


    @Test
    public void PublicKeyShouldBePrivateKeyHashed255() {
        Seed seed = new Seed();

        WOTSKeyPair keys = wots.generateNewKeys(seed,0,1);
        assertEquals(keys.getPublicKey().get(0),wots.hash(keys.getPrivateKey().get(0),255));
    }



    @Test
    public void ShouldValidateSignature0() {
        BigInteger message = new BigInteger("-128");
        byte[] bytes = message.toByteArray();
        Seed seed = new Seed();

        WOTSKeyPair keys = wots.generateNewKeys(seed,0,message.toByteArray().length);

        BigInteger[] signature = wots.sign(keys.getPrivateKey(), message);

        assertEquals(true,wots.verify(keys.getPublicKey(),signature,message));
    }

    @Test
    public void ShouldValidateSignature1() {
        BigInteger message = new BigInteger("102103201123128");
        Seed seed = new Seed();

        WOTSKeyPair keys = wots.generateNewKeys(seed,0,message.toByteArray().length);

        BigInteger[] signature = wots.sign(keys.getPrivateKey(), message);

        assertEquals(true,wots.verify(keys.getPublicKey(),signature,message));
    }

    @Test
    public void ShouldNotValidateTamperedMessage0() {
        BigInteger message = new BigInteger("102103201123128");
        Seed seed = new Seed();

        WOTSKeyPair keys = wots.generateNewKeys(seed,0,message.toByteArray().length);

        BigInteger[] signature = wots.sign(keys.getPrivateKey(), message);
        message = message.add(BigInteger.ONE);

        assertEquals(false,wots.verify(keys.getPublicKey(),signature,message));
    }

    @Test
    public void ShouldNotValidateTamperedSignature0() {
        BigInteger message = new BigInteger("102103201123128");
        Seed seed = new Seed();

        WOTSKeyPair keys = wots.generateNewKeys(seed,0,message.toByteArray().length);

        BigInteger[] signature = wots.sign(keys.getPrivateKey(), message);
        signature[0] = signature[0].add(BigInteger.ONE);

        assertEquals(false,wots.verify(keys.getPublicKey(),signature,message));
    }

    @Test
    public void ShouldNotValidateSketchedAttack() {
        BigInteger message = new BigInteger("102103201123128");
        Seed seed = new Seed();

        WOTSKeyPair keys = wots.generateNewKeys(seed,0,message.toByteArray().length);

        BigInteger[] signature = wots.sign(keys.getPrivateKey(), message);
        byte[] messageBytes = message.toByteArray();
        messageBytes[0]++;
        message = new BigInteger(messageBytes);

        signature[0] = wots.hash(signature[0],1);

        assertEquals(false,wots.verify(keys.getPublicKey(),signature,message));
    }

    @Test
    public void ShouldValidateAntiNormalization() {
        BigInteger message = new BigInteger("102103201123128");
        Seed seed = new Seed();

        WOTSKeyPair keys = wots.generateNewKeys(seed,0,message.toByteArray().length);

        BigInteger[] signature = wots.sign(keys.getPrivateKey(), message);
        byte[] messageBytes = message.toByteArray();
        messageBytes[0]++;
        message = new BigInteger(messageBytes);

        assertEquals(true,wots.verify(keys.getPublicKey(),signature,message));
    }

    @Test
    public void ShouldNotValidateSketched2() {
        BigInteger message = new BigInteger("102103201123128");
        Seed seed = new Seed();

        WOTSKeyPair keys = wots.generateNewKeys(seed,0,message.toByteArray().length);

        BigInteger[] signature = wots.sign(keys.getPrivateKey(), message);
        byte[] messageBytes = message.toByteArray();
        System.out.println(Arrays.toString(messageBytes));
        messageBytes[4]++;
        message = new BigInteger(messageBytes);

        assertEquals(false,wots.verify(keys.getPublicKey(),signature,message));
    }

    @Test
    public void ShouldNormalize() {
        BigInteger message = new BigInteger("102103201123128");
        // sum is -60
        byte[] normalized = wots.normalize(message.toByteArray());
        assertEquals(0,wots.sumArray(normalized));

        message = new BigInteger("1021032011404028");
        // sum is -206
        normalized = wots.normalize(message.toByteArray());
        assertEquals(0,wots.sumArray(normalized));

        message = new BigInteger("998989879");
        // sum is 93
        normalized = wots.normalize(message.toByteArray());
        assertEquals(0,wots.sumArray(normalized));

    }

    @Test
    public void ShouldSum() {
        byte[] bytes = new byte[5];
        bytes[0] = 5;
        bytes[1] = 7;
        bytes[2] = 0;
        bytes[3] = 4;
        bytes[4] = 10;
        assertEquals(26,wots.sumArray(bytes));
    }

    @Test
    public void canConvertByteArrayBigInt() {
        BigInteger message = new BigInteger("102103201123128");
        byte[] messageBytes = message.toByteArray();
        assertEquals(message,new BigInteger(messageBytes));
    }

}
