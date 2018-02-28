package Database;

import Impl.ArrayListTransactions;
import Impl.StandardBlock;
import Impl.StandardCoinBaseTransaction;
import Impl.StandardTransaction;
import Interfaces.Block;
import Interfaces.CoinBaseTransaction;
import Interfaces.Transaction;
import blockchain.Stubs.TransactionStub;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestBlockchainDatabase {

    private static BlockchainDatabase db;
    private Transaction tx;
    private Transaction stx;
    private Block block;
    private CoinBaseTransaction ct;
    private Block block2;

    @Before
    public void setUp(){
        System.out.println("Running setup");
        db = new BlockchainDatabase("TEST");
        tx = new TransactionStub();
        stx = new StandardTransaction(tx.getSenderAddress(),tx.getReceiverAddress(),tx.getValue(),tx.getValueProof(),tx.getSignature(),tx.getBlockNumberOfValueProof());
        ct = new StandardCoinBaseTransaction(stx.getSenderAddress(),10);
        block = new StandardBlock(new BigInteger("4"),4,new BigInteger("42"),10,new ArrayListTransactions(),0,ct);
        block2 = new StandardBlock(new BigInteger("4"),4,new BigInteger("42"),10,new ArrayListTransactions(),1,ct);
    }

    @Test
    public void shouldBeAbleToQueryANewBlock() {
        assertTrue(db.addBlock(block));
    }

    @Test
    public void shouldBeAbleToQueryTransaction() {
        assertTrue(db.addTransaction(tx,0));
    }

    @Test
    public void shouldBeAbleToGetTransaction() {
        db.addTransaction(stx,0);
        assertEquals(stx.toString(),db.getTransaction(stx.transActionHash(),0).toString());
    }

    @Test
    public void shouldBeAbleToGetBlock() {
        assertTrue(db.addBlock(block2));
        String b = block2.toString();
        assertEquals(b,db.getBlock(1).toString());
    }

    @AfterClass
    public static void tearDown(){
        System.out.println("Running teardown");

        db.clearTable("BLOCKCHAIN");
        System.out.println("BLOCKCHAIN table dropped");
        db.clearTable("TRANSACTIONS");
        System.out.println("TRANSACTION table dropped");

        db.shutDown();
    }
}
