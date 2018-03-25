package blockchain;

import Crypto.Impl.RSA;
import Crypto.Interfaces.KeyPair;
import Crypto.Interfaces.PublicKeyCryptoSystem;
import Impl.*;
import Impl.Hashing.SHA256;
import Impl.Transactions.ArrayListTransactions;
import Impl.Transactions.StandardCoinBaseTransaction;
import Interfaces.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;

import static junit.framework.TestCase.assertEquals;

public class TestStandardTransactionManager {
    PublicKeyCryptoSystem rsa;
    private KeyPair keyPairSender;
    private PublicKeyAddress sender1;
    private StandardCoinBaseTransaction coinBase0;
    private StandardCoinBaseTransaction coinBase1;
    private StandardBlock genesis;
    private BlockChainDatabase blockchain;
    private StandardTransactionManager transactionManager;
    private KeyPair keyPairReceiver;
    private PublicKeyAddress receiver1;
    private StandardAccount accountSender1;

    @Before
    public void setUp() throws Exception {
        rsa = new RSA(500);

        keyPairSender = rsa.generateNewKeys(new BigInteger("3"));
        sender1 = new PublicKeyAddress(keyPairSender.getPublicKey());
        accountSender1 = new StandardAccount(new RSA(500),keyPairSender.getPrivateKey(),keyPairSender.getPublicKey(),new SHA256());
        keyPairReceiver = rsa.generateNewKeys(new BigInteger("3"));
        receiver1 = new PublicKeyAddress(keyPairReceiver.getPublicKey());

        coinBase0 = new StandardCoinBaseTransaction(sender1,10,0);
        genesis = new StandardBlock(BigInteger.ONE,10,BigInteger.ONE,10,new ArrayListTransactions(),0, coinBase0);
        blockchain = new BlockChainDatabase("TEST_TRANSACTIONMANAGER",genesis);

        transactionManager = new StandardTransactionManager(blockchain);
        //Add some transactions for sender1
        //coinBase1 = new StandardCoinBaseTransaction(sender1,10,1);
        //Block block1 = new BlockStub(coinBase1,new ArrayListTransactions());
        //blockchain.addBlock(block1);

        }

    @Test
    public void shouldAcceptValidTransactionWhenAdded() {
        // Give transactionmanager some transactions
        Transaction t1 = accountSender1.makeTransaction(receiver1,1,coinBase0.transactionHash(),0);

        transactionManager.addTransaction(t1);

        assertEquals(1,transactionManager.getTransactions().size());
    }

    @Test
    public void shouldRejectInvalidTransactionsWhenAdded() {
        // Give transactionmanager some transactions
        Transaction t1 = accountSender1.makeTransaction(receiver1,11,coinBase0.transactionHash(),0);

        transactionManager.addTransaction(t1);

        assertEquals(0,transactionManager.getTransactions().size());
    }

    @Test
    public void shouldOnlyReturnTransactionsWithUniqueValueProof() {
        // Give transactionmanager some transactions
        Transaction t1 = accountSender1.makeTransaction(receiver1,1,coinBase0.transactionHash(),0);

        transactionManager.addTransaction(t1);
        transactionManager.addTransaction(t1);

        assertEquals(1,transactionManager.getSomeTransactions().getTransactions().size());
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
