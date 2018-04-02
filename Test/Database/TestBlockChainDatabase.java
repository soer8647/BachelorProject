package Database;

import Impl.BlockChainDatabase;
import Impl.StandardBlock;
import Impl.Transactions.ArrayListTransactions;
import Impl.Transactions.StandardCoinBaseTransaction;
import Impl.Transactions.StandardTransaction;
import Interfaces.Block;
import Interfaces.CoinBaseTransaction;
import Interfaces.Transaction;
import blockchain.Stubs.TransactionStub;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

public class TestBlockChainDatabase {

    private static BlockChainDatabase db;
    private Transaction tx;
    private Transaction stx;
    private Block block1;
    private CoinBaseTransaction ct0;
    private Block block2;
    private StandardCoinBaseTransaction ct1;
    private StandardCoinBaseTransaction ct2;

    @Before
    public void setUp(){
        System.out.println("Running setup");
        tx = new TransactionStub();

        stx = new StandardTransaction(tx.getSenderAddress(),tx.getReceiverAddress(),tx.getValue(),tx.getValueProof(),tx.getSignature(),tx.getBlockNumberOfValueProof(), 5);
        ct0 = new StandardCoinBaseTransaction(stx.getSenderAddress(),10, 0);
        ct1 = new StandardCoinBaseTransaction(stx.getSenderAddress(),10, 1);
        ct2 = new StandardCoinBaseTransaction(stx.getSenderAddress(),10, 2);
        block1 = new StandardBlock(new BigInteger("4"),4,new BigInteger("42"),10,new ArrayListTransactions(),1,ct1);
        block2 = new StandardBlock(new BigInteger("4"),4,new BigInteger("42"),10,new ArrayListTransactions(),2,ct2);
        db = new BlockChainDatabase("TEST", new StandardBlock(new BigInteger("4"),4,new BigInteger("42"),10,new ArrayListTransactions(),0, ct0));
    }



    @Test
    public void shouldBeAbleToQueryANewBlock() {
        int blocks = db.getBlockNumber();
        db.addBlock(block1);
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
        assertEquals(stx.toString(),db.getTransaction(stx.transactionHash(),0).toString());
    }

    @Test
    public void shouldBeAbleToGetBlock() {
        db.addBlock(block2);
        String b = block2.toString();
        assertEquals(b,db.getBlock(block2.getBlockNumber()).toString());
    }

    @Test
    public void shouldRememberTimestamp() {
        db.addTransaction(stx,0);
        assertEquals(5,db.getTransaction(stx.transactionHash(),0).getTimestamp());
    }

    @Test
    public void shouldHaveUnspentTransactionWithStartValue() {
        CoinBaseTransaction c = block1.getCoinBase();
        db.addBlock(block1);
        int value = db.getUnspentTransactionValue(ct0.transactionHash());
        assertEquals(ct0.getValue(),value);
    }

    @Test
    public void shouldUpdateTransactionValue() {
        CoinBaseTransaction c = block1.getCoinBase();
        db.addBlock(block1);
        // Must be a valid transaction
        StandardTransaction t = new StandardTransaction(tx.getSenderAddress(),tx.getReceiverAddress(),5,c.transactionHash(),tx.getSignature(), block1.getBlockNumber(), 0);
        ArrayListTransactions transactions = new ArrayListTransactions();
        transactions.add(t);
        Block newBlock = new StandardBlock(new BigInteger("1"),1,new BigInteger("42"),10,transactions, block1.getBlockNumber()+1,new StandardCoinBaseTransaction(tx.getReceiverAddress(),10, block1.getBlockNumber()+1));
        db.addBlock(newBlock);
        assertEquals(ct0.getValue()-5,db.getUnspentTransactionValue(c.transactionHash()));
    }

    @Test
    public void shouldRemoveATransActionThatIsSpent() {

        // Must be a valid transaction
        StandardTransaction t = new StandardTransaction(tx.getSenderAddress(),tx.getReceiverAddress(),10,ct0.transactionHash(),tx.getSignature(), 0, 0);
        ArrayListTransactions transactions = new ArrayListTransactions();
        transactions.add(t);
        CoinBaseTransaction c2 = new StandardCoinBaseTransaction(tx.getReceiverAddress(),10, block1.getBlockNumber()+1);
        Block newBlock = new StandardBlock(new BigInteger("1"),1,new BigInteger("42"),10,transactions, block1.getBlockNumber()+1,c2);
        db.addBlock(newBlock);
        // The transactions should be c2 and t. ct0 was removed.
        assertEquals(2,db.countDataEntries("UNSPENT_TRANSACTIONS"));
    }


    @Test
    public void shouldRemoveAllTransActionThatIsSpent() {
        db.addBlock(block1);

        // Must be a valid transaction: Sender has 10 from ct0 and 10 from ct1.
        StandardTransaction t = new StandardTransaction(tx.getSenderAddress(),tx.getReceiverAddress(),15,ct0.transactionHash(),tx.getSignature(), 0, 0);
        ArrayListTransactions transactions = new ArrayListTransactions();
        transactions.add(t);

        Block newBlock = new StandardBlock(new BigInteger("1"),1,new BigInteger("42"),10,transactions, block1.getBlockNumber()+1,ct2);
        db.addBlock(newBlock);
        //Sender spends his first 15 and gets ct1 updated to 5
        assertEquals(ct1.getValue()-5,db.getUnspentTransactionValue(ct1.transactionHash()));
    }



    @Test
    public void shouldRemoveAllTransActionThatIsSpent2() {
        db.addBlock(block1);

        // Must be a valid transaction: Sender has 10 from ct0 and 10 from ct1.
        StandardTransaction t = new StandardTransaction(tx.getSenderAddress(),tx.getReceiverAddress(),19,ct0.transactionHash(),tx.getSignature(), 0,0);
        ArrayListTransactions transactions = new ArrayListTransactions();
        transactions.add(t);

        Block newBlock = new StandardBlock(new BigInteger("1"),1,new BigInteger("42"),10,transactions, block1.getBlockNumber()+1,ct2);
        db.addBlock(newBlock);
        //Sender spends his first 19 and gets ct1 updated to 1
        assertEquals(ct1.getValue()-9,db.getUnspentTransactionValue(ct1.transactionHash()));
    }

    @Test
    public void shouldBeAbleToTellIfATransactionExistsInBlockChain() {
        db.addTransaction(stx,6);
        assertEquals(true,db.doesTransactionExist(stx));
    }

    @After
    public void tearDown() throws Exception {
        db.clearTable("BLOCKCHAIN");
        System.out.println("BLOCKCHAIN table cleared");
        db.clearTable("TRANSACTIONS");
        System.out.println("TRANSACTION table cleared");
        db.clearTable("UNSPENT_TRANSACTIONS");
    }

    @AfterClass
    public static void tearDownLast(){
        System.out.println("Running teardown");
        db.shutDown();
    }
}
