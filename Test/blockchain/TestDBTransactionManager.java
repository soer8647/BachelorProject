package blockchain;

import Crypto.Impl.RSA;
import Crypto.Interfaces.KeyPair;
import Crypto.Interfaces.PublicKeyCryptoSystem;
import Impl.*;
import Impl.Hashing.SHA256;
import Impl.Transactions.ArrayListTransactions;
import Impl.Transactions.StandardCoinBaseTransaction;
import Interfaces.Transaction;
import Interfaces.Transactions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;

import static junit.framework.TestCase.assertEquals;

public class TestDBTransactionManager {

    PublicKeyCryptoSystem rsa;
    private KeyPair keyPairSender;
    private PublicKeyAddress sender1;
    private StandardCoinBaseTransaction coinBase0;
    private StandardCoinBaseTransaction coinBase1;
    private StandardBlock genesis;
    private BlockChainDatabase blockchain;
    private DBTransactionManager transactionManager;
    private KeyPair keyPairReceiver;
    private PublicKeyAddress receiver1;
    private StandardAccount accountSender1;
    private StandardAccount accountReceiver;

    @Before
    public void setUp(){
        rsa = new RSA(500);

        keyPairSender = rsa.generateNewKeys(new BigInteger("3"));
        sender1 = new PublicKeyAddress(keyPairSender.getPublicKey());
        accountSender1 = new StandardAccount(rsa,keyPairSender.getPrivateKey(),keyPairSender.getPublicKey(),new SHA256());

        keyPairReceiver = rsa.generateNewKeys(new BigInteger("3"));
        receiver1 = new PublicKeyAddress(keyPairReceiver.getPublicKey());
        accountReceiver = new StandardAccount(rsa,keyPairReceiver.getPrivateKey(),keyPairSender.getPublicKey(),new SHA256());

        coinBase0 = new StandardCoinBaseTransaction(sender1,10,0);

        genesis = new StandardBlock(BigInteger.ONE,10,BigInteger.ONE,10,new ArrayListTransactions(),0, coinBase0);
        blockchain = new BlockChainDatabase("TEST_DBTRANSACTIONMANAGER",genesis);

        transactionManager = new DBTransactionManager(blockchain);
    }

    @Test
    public void shouldReturnEmptyTransactions() {
        assertEquals(0,transactionManager.getSomeTransactions().size());
    }

    @Test
    public void shouldAddTransactionToQueue() {
        Transaction t1 = accountSender1.makeTransaction(receiver1,1,coinBase0.transactionHash(),0);
        transactionManager.addTransaction(t1);

        assertEquals(1,transactionManager.getTransactionQueue().size());
    }

    @Test
    public void shouldValidateATransaction() {
        Transaction t1 = accountSender1.makeTransaction(receiver1,1,coinBase0.transactionHash(),0);

        assertEquals(true,transactionManager.validateTransaction(t1));
    }

    @Test
    public void shouldValidateTransactions() {
        Transaction t1 = accountSender1.makeTransaction(receiver1,1,coinBase0.transactionHash(),0);
        Transaction t2 = accountSender1.makeTransaction(receiver1,2,coinBase0.transactionHash(),0);
        Transactions transactions = new ArrayListTransactions();
        transactions.add(t1);
        transactions.add(t2);
        assertEquals(true,transactionManager.validateTransactions(transactions));
    }

    @Test
    public void shouldNotAcceptInvalidTransactions() {
        //not enough funds
        Transaction t1 = accountSender1.makeTransaction(receiver1,1,coinBase0.transactionHash(),0);
        Transaction t2 = accountSender1.makeTransaction(receiver1,10,coinBase0.transactionHash(),0);
        Transactions transactions = new ArrayListTransactions();
        transactions.add(t1);
        transactions.add(t2);
        assertEquals(false,transactionManager.validateTransactions(transactions));
    }

    @Test
    public void shouldGetSomeTransactions() {
        //not enough funds
        Transaction t1 = accountSender1.makeTransaction(receiver1,1,coinBase0.transactionHash(),0);
        Transaction t2 = accountSender1.makeTransaction(receiver1,2,coinBase0.transactionHash(),0);
        Transaction t3 = accountSender1.makeTransaction(receiver1,4,coinBase0.transactionHash(),0);
        transactionManager.addTransaction(t1);
        transactionManager.addTransaction(t2);
        transactionManager.addTransaction(t3);
        assertEquals(3,transactionManager.getSomeTransactions().size());
    }

    @After
    public void tearDown(){
        System.out.println("Running teardown");
        blockchain.clearTable("BLOCKCHAIN");
        System.out.println("BLOCKCHAIN table cleared");
        blockchain.clearTable("TRANSACTIONS");
        System.out.println("TRANSACTION table cleared");
        blockchain.shutDown();
    }
}
