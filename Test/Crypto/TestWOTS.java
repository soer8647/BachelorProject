package Crypto;

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
        wots = new WOTS();
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

        assertEquals(wots.verify(keys.getPublicKey(),signature,message),true);
    }

    @Test
    public void ShouldValidateSignature1() {
        BigInteger message = new BigInteger("102103201123128");
        Seed seed = new Seed();

        WOTSKeyPair keys = wots.generateNewKeys(seed,0,message.toByteArray().length);

        BigInteger[] signature = wots.sign(keys.getPrivateKey(), message);

        assertEquals(wots.verify(keys.getPublicKey(),signature,message),true);
    }


}
