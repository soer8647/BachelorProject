package Crypto;

import Configuration.Configuration;
import Crypto.Impl.FragmentArray;
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
    private final int wotsParam = 8;

    @Before
    public void setUp(){
        wots = new WOTS(Configuration.getHashingAlgorithm(),wotsParam);
    }


    @Test
    public void PublicKeyShouldBePrivateKeyHashedWotsParamTimes() {
        Seed seed = new Seed();

        WOTSKeyPair keys = wots.generateNewKeys(seed,0,1);
        assertEquals(keys.getPublicKey().get(0),wots.hash(keys.getPrivateKey().get(0),(int) Math.pow(2,wotsParam)-1));
    }



    @Test
    public void ShouldValidateSignature0() {
        BigInteger message = new BigInteger("-128");
        Seed seed = new Seed();

        FragmentArray fragmentArray = new FragmentArray(message,wotsParam);
        WOTSKeyPair keys = wots.generateNewKeys(seed,0,fragmentArray.getLength());

        BigInteger[] signature = wots.sign(keys.getPrivateKey(), message);

        assertEquals(true,wots.verify(keys.getPublicKey(),signature,message));
    }

    @Test
    public void ShouldValidateSignature1() {
        BigInteger messageSeed = new BigInteger("102103201123128");
        BigInteger message = wots.hash(messageSeed,1);

        Seed seed = new Seed();

        FragmentArray fragmentArray = new FragmentArray(message,wotsParam);
        WOTSKeyPair keys = wots.generateNewKeys(seed,0,fragmentArray.getLength());

        BigInteger[] signature = wots.sign(keys.getPrivateKey(), message);

        assertEquals(true,wots.verify(keys.getPublicKey(),signature,message));
    }

    @Test
    public void ShouldNotValidateTamperedMessage0() {
        BigInteger messageSeed = new BigInteger("102103201123128");
        BigInteger message = wots.hash(messageSeed,1);
        Seed seed = new Seed();

        FragmentArray fragmentArray = new FragmentArray(message,wotsParam);
        WOTSKeyPair keys = wots.generateNewKeys(seed,0,fragmentArray.getLength());


        BigInteger[] signature = wots.sign(keys.getPrivateKey(), message);
        message = message.add(BigInteger.ONE);

        assertEquals(false,wots.verify(keys.getPublicKey(),signature,message));
    }

    @Test
    public void ShouldNotValidateTamperedSignature0() {
        BigInteger messageSeed = new BigInteger("102103201123128");
        BigInteger message = wots.hash(messageSeed,1);
        Seed seed = new Seed();

        FragmentArray fragmentArray = new FragmentArray(message,wotsParam);
        WOTSKeyPair keys = wots.generateNewKeys(seed,0,fragmentArray.getLength());

        BigInteger[] signature = wots.sign(keys.getPrivateKey(), message);
        signature[0] = signature[0].add(BigInteger.ONE);

        assertEquals(false,wots.verify(keys.getPublicKey(),signature,message));
    }

    @Test
    public void ShouldNotValidateSketchedAttack() {
        BigInteger messageSeed = new BigInteger("102103201123128");
        BigInteger message = wots.hash(messageSeed,1);
        Seed seed = new Seed();

        FragmentArray fragmentArray = new FragmentArray(message,wotsParam);
        WOTSKeyPair keys = wots.generateNewKeys(seed,0,fragmentArray.getLength());

        BigInteger[] signature = wots.sign(keys.getPrivateKey(), message);
        byte[] messageBytes = message.toByteArray();
        messageBytes[0]++;
        message = new BigInteger(messageBytes);

        signature[0] = wots.hash(signature[0],1);

        assertEquals(false,wots.verify(keys.getPublicKey(),signature,message));
    }

    @Test
    public void ShouldValidateAntiNormalization() {
        BigInteger messageSeed = new BigInteger("102103201123128");
        BigInteger message = wots.hash(messageSeed,1);
        Seed seed = new Seed();

        FragmentArray fragmentArray = new FragmentArray(message,wotsParam);
        WOTSKeyPair keys = wots.generateNewKeys(seed,0,fragmentArray.getLength());

        BigInteger[] signature = wots.sign(keys.getPrivateKey(), message);
        byte[] messageBytes = message.toByteArray();
        messageBytes[0]++;
        message = new BigInteger(messageBytes);

        assertEquals(true,wots.verify(keys.getPublicKey(),signature,message));
    }

    @Test
    public void ShouldNotValidateSketched2() {
        BigInteger messageSeed = new BigInteger("102103201123128");
        BigInteger message = wots.hash(messageSeed,1);
        Seed seed = new Seed();

        FragmentArray fragmentArray = new FragmentArray(message,wotsParam);
        WOTSKeyPair keys = wots.generateNewKeys(seed,0,fragmentArray.getLength());

        BigInteger[] signature = wots.sign(keys.getPrivateKey(), message);
        byte[] messageBytes = message.toByteArray();
        messageBytes[10]++;
        message = new BigInteger(messageBytes);

        assertEquals(false,wots.verify(keys.getPublicKey(),signature,message));
    }

    @Test
    public void ShouldNormalize() {
        BigInteger message = new BigInteger("0");
        // sum is -60
        FragmentArray frags = new FragmentArray(message,wotsParam);
        assertEquals(0,wots.sumArray(frags.getFragmentsAsArray()));

        message = wots.hash(new BigInteger("102103201123128"), 1);
        // sum is -206
        frags = new FragmentArray(message,wotsParam);
        wots.normalize(frags);
        assertEquals( (int) (((Math.pow(2,wotsParam)- 1) / 2) * frags.getLength()),wots.sumArray(frags.getFragmentsAsArray()));


        message =  wots.hash(new BigInteger("1021032011404028"),1);
        // sum is -206
        frags = new FragmentArray(message,wotsParam);
        wots.normalize(frags);
        assertEquals( (int) (((Math.pow(2,wotsParam)- 1) / 2) * frags.getLength()),wots.sumArray(frags.getFragmentsAsArray()));


        message =  wots.hash(new BigInteger("54989898797"),1);
        // sum is 93
        frags = new FragmentArray(message,wotsParam);
        wots.normalize(frags);
        assertEquals( (int) (((Math.pow(2,wotsParam)- 1) / 2) * frags.getLength()),wots.sumArray(frags.getFragmentsAsArray()));


    }

    @Test
    public void ShouldSum() {
        int[] ints = new int[5];
        ints[0] = 5;
        ints[1] = 7;
        ints[2] = 0;
        ints[3] = 4;
        ints[4] = 10;
        assertEquals(26,wots.sumArray(ints));
    }

    @Test
    public void canConvertByteArrayBigInt() {
        BigInteger message = new BigInteger("102103201123128");
        byte[] messageBytes = message.toByteArray();
        assertEquals(message,new BigInteger(messageBytes));
    }

}
