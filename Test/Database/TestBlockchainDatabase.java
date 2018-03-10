package Database;

import Impl.BlockchainDatabase;
import Impl.StandardBlock;
import Impl.Transactions.ArrayListTransactions;
import Impl.Transactions.StandardCoinBaseTransaction;
import Impl.Transactions.StandardTransaction;
import Interfaces.Block;
import Interfaces.CoinBaseTransaction;
import Interfaces.Transaction;
import blockchain.Stubs.TransactionStub;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

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
        tx = new TransactionStub();
        stx = new StandardTransaction(tx.getSenderAddress(),tx.getReceiverAddress(),tx.getValue(),tx.getValueProof(),tx.getSignature(),tx.getBlockNumberOfValueProof());
        ct = new StandardCoinBaseTransaction(stx.getSenderAddress(),10, 0);
        block = new StandardBlock(new BigInteger("4"),4,new BigInteger("42"),10,new ArrayListTransactions(),1,ct);
        block2 = new StandardBlock(new BigInteger("4"),4,new BigInteger("42"),10,new ArrayListTransactions(),2,ct);
        db = new BlockchainDatabase("TEST", new StandardBlock(new BigInteger("4"),4,new BigInteger("42"),10,new ArrayListTransactions(),0,ct));
    }



    @Test
    public void shouldBeAbleToQueryANewBlock() {
        int blocks = db.getBlockNumber();
        db.addBlock(block);
        assertEquals(blocks+1,db.getBlockNumber());
    }

    @Test
    public void shouldBeAbleToQueryTransaction() {
        assertEquals(0,db.getTotalNumberOfTransactions());
        db.addTransaction(tx,0);
        assertEquals(1,db.getTotalNumberOfTransactions());
    }

    @Test
    public void shouldBeAbleToGetTransaction() {
        db.addTransaction(stx,0);
        assertEquals(stx.toString(),db.getTransaction(stx.transActionHash(),0).toString());
    }

    @Test
    public void shouldBeAbleToGetBlock() {
        db.addBlock(block2);
        String b = block2.toString();
        assertEquals(b,db.getBlock(block2.getBlockNumber()).toString());
    }

    @AfterClass
    public static void tearDown(){
        System.out.println("Running teardown");

        db.clearTable("BLOCKCHAIN");
        System.out.println("BLOCKCHAIN table cleared");
        db.clearTable("TRANSACTIONS");
        System.out.println("TRANSACTION table cleared");
        db.shutDown();
    }
}
