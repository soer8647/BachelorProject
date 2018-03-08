package blockchain;

import Configuration.Configuration;
import Crypto.Impl.RSA;
import Crypto.Impl.RSAPrivateKey;
import Crypto.Impl.RSAPublicKey;
import Crypto.Interfaces.KeyPair;
import Crypto.Interfaces.PublicKeyCryptoSystem;
import Database.BlockchainDatabase;
import Impl.*;
import Impl.Hashing.SHA256;
import Interfaces.*;
import Interfaces.Communication.ConstantHardnessManager;
import blockchain.Stubs.AddressStub;
import blockchain.Stubs.TransactionStub;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.math.BigInteger;
import java.util.Collection;

import static org.junit.Assert.*;


public class TestFullNode {
    private FullNode node;
    private static BlockchainDatabase blockChain;
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

    @Before
    public void setUp(){
        //SETUP FROM DB
        tx = new TransactionStub();
        stx = new StandardTransaction(tx.getSenderAddress(), tx.getReceiverAddress(), tx.getValue(), tx.getValueProof(), tx.getSignature(), tx.getBlockNumberOfValueProof());
        ct = new StandardCoinBaseTransaction(stx.getSenderAddress(), 0, 0);
        block = new StandardBlock(new BigInteger("4"), 4, new BigInteger("42"), 10, new ArrayListTransactions(), 0, ct);
        block2 = new StandardBlock(new BigInteger("4"), 4, new BigInteger("42"), 10, new ArrayListTransactions(), 1, ct);
        //SETUP ACCOUNTS AND TRANSACTIONS
        cryptoSystem = new RSA(Configuration.getKeyBitLength());

        keyPair1 = cryptoSystem.generateNewKeys(BigInteger.valueOf(3));
        publicKeySender = keyPair1.getPublicKey();
        privateKeySender = keyPair1.getPrivateKey();

        keyPair2 = cryptoSystem.generateNewKeys(BigInteger.valueOf(3));
        privateKeyReceiver = keyPair2.getPrivateKey();
        publicKeyReceiver = keyPair2.getPublicKey();


        blockChain = new BlockchainDatabase("FULLNODETEST",block);
        nodeAddress = stx.getSenderAddress();
        node = new FullNode(blockChain, nodeAddress,new ConstantHardnessManager());
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
        node.mine(Configuration.hash(integer.toString()),new ArrayListTransactions());
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
            node.mine(Configuration.hash(previousHash.toString()),new ArrayListTransactions());
            assertEquals(blocks+i,node.getBlockChain().getBlockNumber());
        }
    }


    @Test
    public void shouldBeAbleToVerifyTransactions() {
        // Make addresses
        Address senderAddress = new PublicKeyAddress(publicKeySender);

        Address receiverAddress = new PublicKeyAddress(publicKeyReceiver);
        // Make a transaction
        BigInteger valueProofFake = new TransactionStub().transActionHash();

        Account sender = new StandardAccount(cryptoSystem, privateKeySender, publicKeySender, new SHA256());
        Account receiver = new StandardAccount(cryptoSystem, privateKeyReceiver,publicKeyReceiver,new SHA256());

        Transaction transaction = sender.makeTransaction(senderAddress,receiverAddress,5,valueProofFake, 0);
        Transactions transactions = new ArrayListTransactions();
        transactions.add(transaction);


        //make block with the transaction
        Block block = new StandardBlock(new BigInteger("42"),10,new BigInteger("42"),10,transactions,1, ct);
        Block genesis = new StandardBlock(new BigInteger("42"),10,new BigInteger("42"),10,new ArrayListTransactions(),0, ct);

        BlockChain blockChain = new StandardBlockChain(genesis);
        blockChain.addBlock(block);
        node = new FullNode(blockChain, new AddressStub(),new ConstantHardnessManager());

        // Receiver has 5
        // Sender has 0

        //Make new transaction to verify by value
        Transaction validTransaction = receiver.makeTransaction(receiverAddress,senderAddress,5,transaction.transActionHash(), 1);
        Transactions<Collection<Transaction>> transactionsToVerify = new ArrayListTransactions();
        transactionsToVerify.add(validTransaction);
        assertTrue(node.validateTransactions(transactionsToVerify));

        // Receiver sends 5. Valid, Not added to blockchain though

        //Make transaction to deny by value
        Transaction invalidTransaction = receiver.makeTransaction(receiverAddress,senderAddress,11,transaction.transActionHash(), 1);
        Transactions transactionsToDeny = new ArrayListTransactions();
        transactionsToDeny.add(invalidTransaction);

        // Receiver sends 11. invalid, has 5.

        //Remove invalid transactions
        assertFalse(node.validateTransactions(transactionsToDeny));



        //Validate transaction by signature
        assertTrue(node.verifyTransactionSignature(validTransaction));


        validTransaction = receiver.makeTransaction(receiverAddress,senderAddress,15,transaction.transActionHash(), 1);
        transactionsToVerify = new ArrayListTransactions();
        transactionsToVerify.add(validTransaction);

        CoinBaseTransaction cb = new StandardCoinBaseTransaction(receiverAddress,10,2);
        blockChain.addBlock(new StandardBlock(new BigInteger("42"),10,new BigInteger("42"),10,new ArrayListTransactions(),2,cb));

        // Receiver sends 11. Valid has 15.

        assertTrue(node.validateTransactions(transactionsToVerify));

    }

    @Test
    public void shouldBeAbleToGetTransactionHistory() {
        // Make addresses
        Address senderAddress = new PublicKeyAddress(publicKeySender);

        Address receiverAddress = new PublicKeyAddress(publicKeyReceiver);
        // Make a transaction
        BigInteger valueProofFake = new TransactionStub().transActionHash();

        Account sender = new StandardAccount(cryptoSystem, privateKeySender, publicKeySender, new SHA256());
        Account receiver = new StandardAccount(cryptoSystem, privateKeyReceiver,publicKeyReceiver,new SHA256());


        //Make transaction from sender
        Transaction transaction = sender.makeTransaction(senderAddress,receiverAddress,5,valueProofFake, 0);
        Transactions transactions = new ArrayListTransactions();
        transactions.add(transaction);

        //Make transaction from receiver to sender
        Transaction transaction2 = sender.makeTransaction(receiverAddress,senderAddress,5,valueProofFake, 0);
        Transactions transactions2 = new ArrayListTransactions();
        transactions.add(transaction2);

        node.mine(valueProofFake,transactions);
        assertEquals(2,node.getTransactionHistory(senderAddress).getKey().size());
    }

    @AfterClass
    public static void tearDown(){
        System.out.println("Running teardown");

        blockChain.clearTable("BLOCKCHAIN");
        System.out.println("BLOCKCHAIN table cleared");
        blockChain.clearTable("TRANSACTIONS");
        System.out.println("TRANSACTION table cleared");

        blockChain.shutDown();
    }





}
