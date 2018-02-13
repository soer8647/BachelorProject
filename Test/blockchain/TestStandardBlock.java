package blockchain;

import Impl.ArrayListTransactions;
import Impl.Hashing.SHA256;
import Impl.StandardBlock;
import Interfaces.Block;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;

public class TestStandardBlock {
    private Block block;

    @Before
    public void setup(){
        block = new StandardBlock(new BigInteger("42"),20, new BigInteger("42"), 8, new ArrayListTransactions(),1, new SHA256());
    }

    @Test
    public void shouldHaveANonce(){
        assertNotEquals(block.getNonce(),null);
    }
    @Test
    public void shouldHaveHardnessParameter(){
        assertNotEquals(block.getHardnessParameter(),null);
    }

    @Test
    public void shouldHaveHardnessParameterOf20(){
        assertEquals(block.getHardnessParameter(),20);
    }
    @Test
    public void shouldHavePreviousHash(){
        assertNotEquals(block.getPreviousHash(),null);
    }
    @Test
    public void shouldHaveTransactionLimit(){
        assertNotEquals(block.getTransactionLimit(),null);
    }
    @Test
    public void shouldHaveTransactionLimitOf8(){
        assertEquals(block.getTransactionLimit(),8);
    }

    @Test
    public void shouldHoldTransactions(){
        assertNotEquals(block.getTransactions(),null);
    }

    @Test
    public void shouldHaveBlockNumber(){
        assertNotEquals(block.getBlockNumber(),null);
    }

    @Test
    public void shouldHaveBlockNumberOfOne(){
        assertEquals(block.getBlockNumber(),1);
    }

    @Test
    public void shouldBeAbleToHashBlock(){

    }

}
