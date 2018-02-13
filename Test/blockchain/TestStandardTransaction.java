package blockchain;

import Impl.StandardTransaction;
import blockchain.Stubs.TransactionStub;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class TestStandardTransaction {

    private StandardTransaction standardTransaction;
    private BigInteger signature;
    @Before
    public void setUp(){
        signature = new BigInteger("42");
        standardTransaction = new StandardTransaction(1234, 1234, 1, new TransactionStub(), signature);
    }

    @Test
    public void hasSender(){
        assertNotEquals(standardTransaction.getSender(),0);
    }

    @Test
    public void hasReceiver(){
        assertNotEquals(standardTransaction.getReceiver(),0);
    }

    @Test
    public void hasPositiveAmount(){
        assertTrue(standardTransaction.getValue()>0);
    }

    @Test
    public void hasAmountOfOne(){
        assertThat(standardTransaction.getValue(),is(1));
    }

    @Test
    public void hasProofOfAmount(){
        assertNotEquals(standardTransaction.getValueProof(),null);
    }

    @Test
    public void hasSignature(){
        assertThat(standardTransaction.getSignature(),is(signature));
    }

    @Test
    public void shouldHashWithSHA256(){
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] hash = sha256.digest(standardTransaction.toString().getBytes());
            BigInteger hashValue = new BigInteger(1,hash);
            assertEquals(hashValue,standardTransaction.transActionHash());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


}
