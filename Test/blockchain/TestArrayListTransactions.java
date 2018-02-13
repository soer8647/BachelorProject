package blockchain;

import Impl.ArrayListTransactions;
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
    Transactions transactions;
    @Before
    public void setUp(){
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
}
