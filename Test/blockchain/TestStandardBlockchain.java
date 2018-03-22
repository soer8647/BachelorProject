package blockchain;

import Impl.StandardBlockChain;
import Interfaces.Block;
import Interfaces.BlockChain;
import blockchain.Stubs.BlockStub;
import blockchain.Stubs.GenesisBlockStub;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestStandardBlockchain {

    Block genesisBlock;
    BlockChain blockChain;

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
        BlockStub newBlockStub = new BlockStub(null);
        newBlockStub.setBlockNumber(1);
        blockChain.addBlock(newBlockStub);
        assertEquals(blockChain.getBlockNumber(), 1);
        newBlockStub = new BlockStub(null);
        newBlockStub.setBlockNumber(2);
        blockChain.addBlock(newBlockStub);
        assertEquals(blockChain.getBlockNumber(), 2);
    }


}
