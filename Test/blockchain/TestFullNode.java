package blockchain;

import Configuration.Configuration;
import Crypto.Impl.RSA;
import Crypto.Impl.RSAPrivateKey;
import Crypto.Impl.RSAPublicKey;
import Crypto.Interfaces.KeyPair;
import Crypto.Interfaces.PublicKeyCryptoSystem;
import Impl.*;
import Impl.Transactions.StandardCoinBaseTransaction;
import Impl.Transactions.StandardTransaction;
import Interfaces.*;
import blockchain.Stubs.AddressStub;
import blockchain.Stubs.BlockStub;
import blockchain.Stubs.TransactionStub;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.*;


public class TestFullNode {
    private FullNode node;
    private static BlockChainDatabase blockChain;
    private Block block;
    private Address nodeAddress;
    private Transaction tx;
    private Transaction stx;
    private CoinBaseTransaction ct;
    private Block block2;
    private PublicKeyCryptoSystem cryptoSystem;
    private KeyPair keyPair1;
    private RSAPublicKey publicKeySender;
    private RSAPrivateKey privateKeySender;

    private KeyPair keyPair2;
    private RSAPublicKey publicKeyReceiver;
    private RSAPrivateKey privateKeyReceiver;
    private StandardCoinBaseTransaction ct1;

    @Before
    public void setUp(){
        //SETUP FROM DB
        tx = new TransactionStub();
        stx = new StandardTransaction(tx.getSenderAddress(), tx.getReceiverAddress(), tx.getValue(), tx.getSignature(), 0);
        ct = new StandardCoinBaseTransaction(stx.getSenderAddress(), 0, 0);
        ct1 = new StandardCoinBaseTransaction(stx.getSenderAddress(), 0, 1);
        block = new StandardBlock(new BigInteger("4"), 4, new BigInteger("42"), new ArrayList<>(), 0, ct);
        block2 = new StandardBlock(new BigInteger("4"), 4, new BigInteger("42"), new ArrayList<>(), 1, ct);
        //SETUP ACCOUNTS AND TRANSACTIONS
        cryptoSystem = new RSA(Configuration.getKeyBitLength());

        keyPair1 = cryptoSystem.generateNewKeys(BigInteger.valueOf(3));
        publicKeySender = keyPair1.getPublicKey();
        privateKeySender = keyPair1.getPrivateKey();

        keyPair2 = cryptoSystem.generateNewKeys(BigInteger.valueOf(3));
        privateKeyReceiver = keyPair2.getPrivateKey();
        publicKeyReceiver = keyPair2.getPublicKey();


        blockChain = new BlockChainDatabase("FULLNODETEST",block);
        nodeAddress = stx.getSenderAddress();
        node = new FullNode(blockChain, nodeAddress,new ConstantHardnessManager(), new DBTransactionManager(blockChain));
        Configuration.setHardnessParameter(10);
    }

    @Test
    public void shouldHaveAddress() {
         assertEquals(node.getAddress(),nodeAddress);
    }

    @Test
    public void shouldHaveABlockChain(){
        assertEquals(node.getBlockChain(),blockChain);
    }

    @Test
    public void shouldBeAbleToHashABlock(){
        assertNotEquals(node.hashBlock(block),null);
    }

    @Test
    public void shouldMineABlock(){
        int blocks = blockChain.getBlockNumber();
        File falcon = new File("resources/falconGenesis.jpg");
        //Make file to a int : hashCode
        int hashCode = falcon.hashCode();
        //Make int to a BigInteger
        BigInteger integer = new BigInteger(Integer.toString(hashCode));
        node.mine(Configuration.hash(integer.toString()),new ArrayList<>());
        //The genesisblock should have blocknumber 0.
        assertEquals(blocks+1,node.getBlockChain().getBlockNumber());
    }

    @Test
    public void shouldBeAbleToMineFiveBlocks(){

        File falcon = new File("../../src/resources/falconGenesis.jpg");

        //Make file to a int : hashCode
        int hashCode = falcon.hashCode();
        //Make int to a BigInteger
        BigInteger integer = new BigInteger(Integer.toString(hashCode));
        int blocks = blockChain.getBlockNumber();
        for (int i=1;i<6;i++){
            BigInteger previousHash = node.getBlockChain().getBlock(i-1).hash();
            node.mine(Configuration.hash(previousHash.toString()),new ArrayList<>());
            assertEquals(blocks+i,node.getBlockChain().getBlockNumber());
        }
    }


    @Test
    public void shouldBeAbleToVerifyTransactions() {
        // Make addresses
        Address senderAddress = new PublicKeyAddress(publicKeySender);

        Address receiverAddress = new PublicKeyAddress(publicKeyReceiver);
        // Make a transaction
        Account sender = new StandardAccount(privateKeySender, publicKeySender);
        Account receiver = new StandardAccount(privateKeyReceiver,publicKeyReceiver);

        Transaction transaction = sender.makeTransaction(receiverAddress,5, 0);
        Collection<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);


        //make block with the transaction
        Block block = new StandardBlock(new BigInteger("42"),10,new BigInteger("42"), transactions,1, ct1);
        Block genesis = new StandardBlock(new BigInteger("42"),10,new BigInteger("42"), new ArrayList<>(),0, ct);

        BlockChainDatabase blockChain = new BlockChainDatabase("FULLNODETESTV",genesis);
        blockChain.addBlock(block);
        node = new FullNode(blockChain, new AddressStub(),new ConstantHardnessManager(), new DBTransactionManager(blockChain));

        // Receiver has 5
        // Sender has 0

        //Make new transaction to verify by value
        Transaction validTransaction = receiver.makeTransaction(senderAddress,5, 0);
        Collection<Transaction> transactionsToVerify = new ArrayList<>();
        transactionsToVerify.add(validTransaction);
        assertTrue(node.validateTransactions(transactionsToVerify));

        // Receiver sends 5. Valid, Not added to blockchain though

        //Make transaction to deny by value
        Transaction invalidTransaction = receiver.makeTransaction(senderAddress,11, 0);
        Collection<Transaction> transactionsToDeny = new ArrayList<>();
        transactionsToDeny.add(invalidTransaction);

        // Receiver sends 11. invalid, has 5.

        //Remove invalid transactions
        assertFalse(node.validateTransactions(transactionsToDeny));



        //Validate transaction by signature
        assertTrue(node.verifyTransactionSignature(validTransaction));


        validTransaction = receiver.makeTransaction(senderAddress,15, 0);
        transactionsToVerify = new ArrayList<>();
        transactionsToVerify.add(validTransaction);

        CoinBaseTransaction cb = new StandardCoinBaseTransaction(receiverAddress,10,2);
        blockChain.addBlock(new StandardBlock(new BigInteger("42"),10,new BigInteger("42"), new ArrayList<>(),2,cb));

        // Receiver sends 11. Valid has 15.

        assertTrue(node.validateTransactions(transactionsToVerify));

    }

    @Test
    public void shouldBeAbleToGetTransactionHistory() {
        // Make addresses
        Address senderAddress = new PublicKeyAddress(publicKeySender);

        Address receiverAddress = new PublicKeyAddress(publicKeyReceiver);

        Account sender = new StandardAccount(privateKeySender, publicKeySender);
        Account receiver = new StandardAccount(privateKeyReceiver,publicKeyReceiver);
        System.out.println(node.getBlockChain().getBlockNumber());

        CoinBaseTransaction valueProof1 = new StandardCoinBaseTransaction(senderAddress,100,1);
        node.addBlock(new BlockStub(valueProof1, new ArrayList<>(), 1));


        CoinBaseTransaction valueProof2 = new StandardCoinBaseTransaction(senderAddress,100,2);
        node.addBlock(new BlockStub(valueProof2, new ArrayList<>(), 2));


        //Make transaction from sender
        Transaction transaction = sender.makeTransaction(receiverAddress,5, 0);
        Collection<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        //Make transaction from receiver to sender
        Transaction transaction2 = sender.makeTransaction(senderAddress,5, 0);
        Collection<Transaction> transactions2 = new ArrayList<>();
        transactions.add(transaction2);

        node.mine(new BigInteger("42"),transactions);
        assertEquals(2,node.getTransactionHistory(senderAddress).getConfirmedTransactions().size());
    }

    @After
    public void tearDown(){
        blockChain.clearTable("BLOCKCHAIN");
        System.out.println("BLOCKCHAIN table cleared");
        blockChain.clearTable("TRANSACTIONS");
        System.out.println("TRANSACTION table cleared");
        blockChain.clearTable("UNSPENT_TRANSACTIONS");
    }


    @AfterClass
    public static void tearDownLast(){
        System.out.println("Running teardown");

        blockChain.shutDown();
    }





}
