package blockchain;

import Database.BlockchainDatabase;
import Impl.ArrayListTransactions;
import Impl.StandardBlock;
import Impl.StandardCoinBaseTransaction;
import Impl.StandardTransaction;
import Interfaces.Block;
import Interfaces.BlockChain;
import Interfaces.CoinBaseTransaction;
import Interfaces.Transaction;
import blockchain.Stubs.TransactionStub;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

public class TestFullNodeWithBlockChainDB {



    private static BlockchainDatabase bcdb;
    private Transaction tx;
    private Transaction stx;
    private Block block;
    private Block block2;
    private CoinBaseTransaction ct;


    @Before
    public void setUp() {
        bcdb = new BlockchainDatabase("TESTFULLNODEDB", block = new StandardBlock(new BigInteger("4"), 4, new BigInteger("42"), 10, new ArrayListTransactions(), 0, ct));
        System.out.println("Running setup");
        tx = new TransactionStub();
        stx = new StandardTransaction(tx.getSenderAddress(), tx.getReceiverAddress(), tx.getValue(), tx.getValueProof(), tx.getSignature(), tx.getBlockNumberOfValueProof());
        ct = new StandardCoinBaseTransaction(stx.getSenderAddress(), 10);
        block = new StandardBlock(new BigInteger("4"), 4, new BigInteger("42"), 10, new ArrayListTransactions(), 1, ct);
        block2 = new StandardBlock(new BigInteger("4"), 4, new BigInteger("42"), 10, new ArrayListTransactions(), 2, ct);
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