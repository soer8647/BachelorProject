package blockchain.Stubs;

import Interfaces.Block;
import Interfaces.Transactions;

import java.math.BigInteger;

public class BlockStub implements Block {
    @Override
    public Transactions getTransactions() {
        return null;
    }

    @Override
    public int getBlockNumber() {
        return 0;
    }

    @Override
    public BigInteger getNonce() {
        return null;
    }

    @Override
    public int getHardnessParameter() {
        return 0;
    }

    @Override
    public BigInteger getPreviousHash() {
        return null;
    }

    @Override
    public int getTransactionLimit() {
        return 0;
    }

    @Override
    public BigInteger hash() {
        return null;
    }
}
