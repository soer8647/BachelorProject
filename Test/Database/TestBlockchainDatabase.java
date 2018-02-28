package Database;

import Impl.StandardTransaction;
import Interfaces.Block;
import Interfaces.Transaction;
import blockchain.Stubs.BlockStub;
import blockchain.Stubs.TransactionStub;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestBlockchainDatabase {

    private static BlockchainDatabase db;
    private Transaction tx;
    private Transaction stx;
    @Before
    public void setUp(){
        System.out.println("Running setup");
        db = new BlockchainDatabase("TEST");
        tx = new TransactionStub();
    }

    @Test
    public void shouldBeAbleToQueryANewBlock() {
        Block newBlock = new BlockStub();
        assertTrue(db.addBlock(newBlock));

    }

    @Test
    public void shouldBeAbleToQueryTransaction() {
        assertTrue(db.addTransaction(tx,0));
    }

    @Test
    public void shouldBeAbleToGetTransaction() {
        StandardTransaction stx = new StandardTransaction(tx.getSenderAddress(),tx.getReceiverAddress(),tx.getValue(),tx.getValueProof(),tx.getSignature(),tx.getBlockNumberOfValueProof());
        db.addTransaction(stx,0);
        assertEquals(stx.toString(),db.getTransaction(stx.transActionHash(),0).toString());
    }

    @AfterClass
    public static void tearDown(){
        System.out.println("Running teardown");

        db.dropTable("BLOCKCHAIN");
        db.dropTable("TRANSACTIONS");

        db.shutDown();
    }
}
