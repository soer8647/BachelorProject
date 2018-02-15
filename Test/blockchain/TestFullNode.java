package blockchain;
import Impl.ArrayListTransactions;
import Impl.FullNode;
import Impl.StandardBlockChain;
import Interfaces.Block;
import Interfaces.BlockChain;
import Impl.Global;
import Interfaces.Node;
import blockchain.Stubs.GenesisBlockStub;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.math.BigInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;


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



}
