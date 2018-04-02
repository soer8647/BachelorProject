package blockchain;

import Impl.StandardBlockChain;
import Interfaces.Block;
import Interfaces.BlockChain;
import blockchain.Stubs.BlockStub;
import blockchain.Stubs.GenesisBlockStub;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class TestStandardBlockchain {

    private Block genesisBlock;
    private BlockChain blockChain;

    @Before
    public void setUp() {
        genesisBlock = new GenesisBlockStub();
        blockChain = new StandardBlockChain(genesisBlock);
    }

    @Test
    public void shouldHaveGenesisBlock() {
        assertEquals(blockChain.getGenesisBlock(), genesisBlock);
    }

    @Test
    public void shouldBeAbleToGetBlockNumber() {
        assertEquals(blockChain.getGenesisBlock().getBlockNumber(), 0);
    }

    @Test
    public void shouldBeAbleToAddBlockToBlockChain() {
        assertEquals(blockChain.getBlockNumber(), 0);
        BlockStub newBlockStub = new BlockStub(null, new ArrayList<>(), 1);
        newBlockStub.setBlockNumber(1);
        blockChain.addBlock(newBlockStub);
        assertEquals(blockChain.getBlockNumber(), 1);
        newBlockStub = new BlockStub(null, new ArrayList<>(), 1);
        newBlockStub.setBlockNumber(2);
        blockChain.addBlock(newBlockStub);
        assertEquals(blockChain.getBlockNumber(), 2);
    }


}
