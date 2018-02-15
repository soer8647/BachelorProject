package blockchain;
import Impl.ArrayListTransactions;
import Impl.FullNode;
import Impl.Hashing.SHA256;
import Impl.StandardBlockChain;
import Interfaces.Block;
import Interfaces.BlockChain;
import Interfaces.HashingAlgorithm;
import Interfaces.Node;
import blockchain.Stubs.GenesisBlockStub;
import org.junit.Before;
import org.junit.Test;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.math.BigInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;


public class TestFullNode {
    private final HashingAlgorithm hashingAlgorithm = new SHA256();
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
        HashingAlgorithm hasher = new SHA256();
        File falcon = new File("resources/falconGenesis.jpg");
        //Make file to a int : hashCode
        int hashCode = falcon.hashCode();
        //Make int to a BigInteger
        BigInteger integer = new BigInteger(Integer.toString(hashCode));
        node.mine(hasher.hash(integer.toString()),new ArrayListTransactions());
        //The genesisblock should have blocknumber 0.
        assertEquals(0,node.getBlockChain().getBlockNumber());
    }

    @Test
    public void shouldBeAbleToMineFiveBlocks(){
        HashingAlgorithm hasher = new SHA256();

        File falcon = new File("../../src/resources/falconGenesis.jpg");

        //Make file to a int : hashCode
        int hashCode = falcon.hashCode();
        //Make int to a BigInteger
        BigInteger integer = new BigInteger(Integer.toString(hashCode));
        node.mine(hasher.hash(integer.toString()),new ArrayListTransactions());
        for (int i=1;i<6;i++){
            BigInteger previousHash = node.getBlockChain().getBlock(i-1).hash();
            node.mine(hasher.hash(previousHash.toString()),new ArrayListTransactions());
            assertEquals(i,node.getBlockChain().getBlockNumber());
        }


    }


}
