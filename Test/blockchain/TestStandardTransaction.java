package blockchain;

import Crypto.Impl.RSAPublicKey;
import Impl.PublicKeyAddress;
import Impl.StandardTransaction;
import Interfaces.Address;
import blockchain.Stubs.CryptoSystemStub;
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
    private BigInteger valueProof;
    @Before
    public void setUp(){
        valueProof = new TransactionStub().transActionHash();
        signature = new BigInteger("42");

        Address receiver = new PublicKeyAddress(new RSAPublicKey(new BigInteger("3"),new BigInteger("1234")),new CryptoSystemStub());
        Address  sender = new PublicKeyAddress(new RSAPublicKey(new BigInteger("3"),new BigInteger("1234")),new CryptoSystemStub());
        standardTransaction = new StandardTransaction(receiver,sender, 1, valueProof, signature, 0);
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

    @Test
    public void shouldOverrideToString(){
        BigInteger hash = valueProof;

        String should = "Sender: "+standardTransaction.getSenderAddress().toString()+",\n"+
                        "Receiver: "+standardTransaction.getReceiverAddress().toString()+
                                ",\nValue: 1,\nHash of value proof transaction: " +hash+",\nSignature: "+ signature+"\n";


        assertEquals(should,standardTransaction.toString());
    }


}
