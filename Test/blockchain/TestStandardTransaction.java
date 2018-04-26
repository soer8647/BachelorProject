package blockchain;

import Crypto.Impl.RSAPublicKey;
import Impl.PublicKeyAddress;
import Impl.Transactions.StandardTransaction;
import Interfaces.Address;
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

        Address receiver = new PublicKeyAddress(new RSAPublicKey(new BigInteger("3"),new BigInteger("1234")));
        Address  sender = new PublicKeyAddress(new RSAPublicKey(new BigInteger("3"),new BigInteger("1234")));
        standardTransaction = new StandardTransaction(receiver,sender, 1, signature, 0);
    }

    @Test
    public void hasSender(){
        assertNotEquals(standardTransaction.getSenderAddress(),0);
    }

    @Test
    public void hasReceiver(){
        assertNotEquals(standardTransaction.getReceiverAddress(),0);
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
    public void hasSignature(){
        assertThat(standardTransaction.getSignature(),is(signature));
    }

    @Test
    public void hasTimestamp(){
        assertNotEquals(standardTransaction.getTimestamp(),null);
    }

    @Test
    public void shouldHashWithSHA256(){
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");

            String s =standardTransaction.getSenderAddress().toString()+standardTransaction.getReceiverAddress().toString()+standardTransaction.getValue()+standardTransaction.getTimestamp();
            byte[] hash = sha256.digest(s.getBytes());
            BigInteger hashValue = new BigInteger(1,hash);
            assertEquals(hashValue,standardTransaction.transactionHash());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldOverrideToString(){


        String should = "Sender: "+standardTransaction.getSenderAddress().toString()+",\n"+
                        "Receiver: "+standardTransaction.getReceiverAddress().toString()+
                                ",\nValue: 1,\nTimestamp: 0,\nSignature: "+ signature+"\n";

        assertEquals(should,standardTransaction.toString());
    }


}
