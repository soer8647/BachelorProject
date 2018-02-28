package blockchain.Stubs;

import Interfaces.Block;
import Interfaces.CoinBaseTransaction;
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
        return new BigInteger("1");
    }

    @Override
    public int getHardnessParameter() {
        return 0;
    }

    @Override
    public BigInteger getPreviousHash() {
        return new BigInteger("42");
    }

    @Override
    public int getTransactionLimit() {
        return 0;
    }

    @Override
    public BigInteger hash() {
        return new BigInteger("1337");
    }

    @Override
    public CoinBaseTransaction getCoinBase() {
        return null;
    }
}
