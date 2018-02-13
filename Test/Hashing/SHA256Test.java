package Hashing;

import Impl.Hashing.SHA256;
import Interfaces.HashingAlgorithm;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.assertEquals;

public class SHA256Test {
    HashingAlgorithm sha256;
    @Before
    public void setUp(){
        sha256 = new SHA256();
    }

    @Test
    public void shouldHashAStringAccordingToSHA256(){
        //Result gathered from sha256 online. The hashed value is 42.
        BigInteger hash = new BigInteger("73475cb40a568e8da8a045ced110137e159f890ac4da883b6b17dc651b3a8049",16);
        assertEquals(hash,sha256.hash("42"));
    }

    @Test
    public void shouldBeAbleToReturnHashAlgorithm(){
        try {
            assertEquals(sha256.getHashingAlgorithm().toString(), MessageDigest.getInstance("SHA-256").toString());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
