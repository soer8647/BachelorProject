package blockchain;

import Impl.ArrayListTransactions;
import Impl.StandardTransaction;
import Interfaces.Transaction;
import Interfaces.Transactions;
import blockchain.Stubs.TransactionStub;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class TestArrayListTransactions {
    private Transactions transactions;
    private Transaction valueProof;
    private Transaction t1;
    private Transaction t2;
    @Before
    public void setUp(){
        valueProof = new TransactionStub();
        t1 = new StandardTransaction(new BigInteger("1234"), new BigInteger("4321"), 1, valueProof, new BigInteger("42"));
        t2 = new StandardTransaction(new BigInteger("1234"),new BigInteger("4321"), 1, valueProof, new BigInteger("24"));
        transactions = new ArrayListTransactions();
    }

    @Test
    public void shouldHaveSizeZeroAtFirst(){
        assertThat(transactions.size(),is(0));
    }

    @Test
    public void shouldHaveSizeOneWithOneTransaction(){
        transactions.add(new TransactionStub());
        assertThat(transactions.size(),is(1));
    }

    @Test
    public void shouldBeAbleToGetTransactions(){
        TransactionStub stub = new TransactionStub();
        ArrayList<Transaction> tx = new ArrayList<>();
        tx.add(stub);
        transactions.add(stub);
        assertEquals(transactions.getTransactions(),tx);
    }

    @Test
    public void shouldHashWithSHA256(){
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] hash = sha256.digest(transactions.toString().getBytes());



            BigInteger hashValue = new BigInteger(1,hash);

            assertEquals(hashValue,transactions.hashTransactions());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldOverrideToString(){
        transactions.add(t1);
        transactions.add(t2);
        String should = "Transactions:\n" +
                        "\t"+t1.toString()+"\n"+
                        "\t"+t2.toString()+"\n";
        assertEquals(should,transactions.toString());
    }
}
