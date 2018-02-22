package blockchain;
import Crypto.Impl.RSA;
import Crypto.Impl.RSAPrivateKey;
import Crypto.Impl.RSAPublicKey;
import Crypto.Interfaces.KeyPair;
import Crypto.Interfaces.PublicKeyCryptoSystem;
import Impl.*;
import Impl.Hashing.SHA256;
import Interfaces.*;
import Impl.ArrayListTransactions;
import Impl.FullNode;
import Impl.StandardBlockChain;
import Interfaces.Block;
import Interfaces.BlockChain;
import Impl.Global;
import Interfaces.Node;
import blockchain.Stubs.CoinBaseTransactionStub;
import blockchain.Stubs.GenesisBlockStub;
import blockchain.Stubs.TransactionStub;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.math.BigInteger;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;


public class TestFullNode {
    private Node node;
    private BlockChain blockChain;
    private Block block;
    @Before
    public void setUp(){
        block = new GenesisBlockStub();
        blockChain = new StandardBlockChain(block);
        node = new FullNode(blockChain);
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
    public void shouldMineGenesisBlock(){
        File falcon = new File("resources/falconGenesis.jpg");
        //Make file to a int : hashCode
        int hashCode = falcon.hashCode();
        //Make int to a BigInteger
        BigInteger integer = new BigInteger(Integer.toString(hashCode));
        node.mine(Global.hash(integer.toString()),new ArrayListTransactions());
        //The genesisblock should have blocknumber 0.
        assertEquals(0,node.getBlockChain().getBlockNumber());
    }

    @Test
    public void shouldBeAbleToMineFiveBlocks(){

        File falcon = new File("../../src/resources/falconGenesis.jpg");

        //Make file to a int : hashCode
        int hashCode = falcon.hashCode();
        //Make int to a BigInteger
        BigInteger integer = new BigInteger(Integer.toString(hashCode));
        node.mine(Global.hash(integer.toString()),new ArrayListTransactions());
        for (int i=1;i<6;i++){
            BigInteger previousHash = node.getBlockChain().getBlock(i-1).hash();
            node.mine(Global.hash(previousHash.toString()),new ArrayListTransactions());
            assertEquals(i,node.getBlockChain().getBlockNumber());
        }
    }


    @Test
    public void shouldBeAbleToVerifyTransactionsBySignature() {
        // Make addresses
        PublicKeyCryptoSystem cryptoSystem = new RSA(1000, new BigInteger("3"));
        KeyPair keyPair = cryptoSystem.generateNewKeys();

        RSAPublicKey publicKeySender = keyPair.getPublicKey();
        RSAPrivateKey privateKeySender = keyPair.getPrivateKey();
        Address senderAddress = new PublicKeyAddress(publicKeySender,cryptoSystem);

        keyPair = cryptoSystem.generateNewKeys();
        RSAPublicKey publicKeyReceiver = keyPair.getPublicKey();
        RSAPrivateKey privateKeyReceiver = keyPair.getPrivateKey();
        Address receiverAddress = new PublicKeyAddress(publicKeyReceiver,cryptoSystem);

        // Make a transaction
        BigInteger valueProofFake = new TransactionStub().transActionHash();

        Account sender = new StandardAccount(cryptoSystem, privateKeySender, publicKeySender, new SHA256());
        Account receiver = new StandardAccount(cryptoSystem, privateKeyReceiver,publicKeyReceiver,new SHA256());

        Transaction transaction = sender.makeTransaction(senderAddress,receiverAddress,5,valueProofFake, 0);
        Transactions transactions = new ArrayListTransactions();
        transactions.add(transaction);


        //make block with the transaction
        Block block = new StandardBlock(new BigInteger("42"),10,new BigInteger("42"),10,transactions,1, new CoinBaseTransactionStub());
        Block genesis = new GenesisBlockStub();

        BlockChain blockChain = new StandardBlockChain(genesis);
        blockChain.addBlock(genesis);
        blockChain.addBlock(block);
        node = new FullNode(blockChain);
        //Make new transaction to verify
        Transaction newTransaction = receiver.makeTransaction(receiverAddress,senderAddress,5,transaction.transActionHash(), 1);
        Transactions transactionsToVerify = new ArrayListTransactions();
        transactionsToVerify.add(newTransaction);
        assertTrue(node.validateTransactions(transactionsToVerify));

    }
}
