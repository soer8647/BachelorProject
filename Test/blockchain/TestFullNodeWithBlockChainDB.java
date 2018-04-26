package blockchain;

import Impl.BlockChainDatabase;
import Impl.StandardBlock;
import Impl.Transactions.StandardCoinBaseTransaction;
import Impl.Transactions.StandardTransaction;
import Interfaces.Block;
import Interfaces.CoinBaseTransaction;
import Interfaces.Transaction;
import blockchain.Stubs.TransactionStub;
import org.junit.AfterClass;
import org.junit.Before;

import java.math.BigInteger;
import java.util.ArrayList;

public class TestFullNodeWithBlockChainDB {



    private static BlockChainDatabase bcdb;
    private Transaction tx;
    private Transaction stx;
    private Block block;
    private Block block2;
    private CoinBaseTransaction ct;


    @Before
    public void setUp() {
        bcdb = new BlockChainDatabase("TESTFULLNODEDB", block = new StandardBlock(new BigInteger("4"), 4, new BigInteger("42"), new ArrayList<>(), 0, ct));
        System.out.println("Running setup");
        tx = new TransactionStub();
        stx = new StandardTransaction(tx.getSenderAddress(), tx.getReceiverAddress(), tx.getValue(), tx.getSignature(), 1);
        ct = new StandardCoinBaseTransaction(stx.getSenderAddress(), 10, 0);
        block = new StandardBlock(new BigInteger("4"), 4, new BigInteger("42"), new ArrayList<>(), 1, ct);
        block2 = new StandardBlock(new BigInteger("4"), 4, new BigInteger("42"), new ArrayList<>(), 2, ct);
    }

    @AfterClass
    public static void tearDown(){
        System.out.println("Running teardown");

        bcdb.clearTable("BLOCKCHAIN");
        System.out.println("BLOCKCHAIN table cleared");
        bcdb.clearTable("TRANSACTIONS");
        System.out.println("TRANSACTION table cleared");

        bcdb.shutDown();
    }


}
