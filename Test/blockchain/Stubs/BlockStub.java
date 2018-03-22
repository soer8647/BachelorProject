package blockchain.Stubs;

import Impl.Transactions.ArrayListTransactions;
import Interfaces.Block;
import Interfaces.CoinBaseTransaction;
import Interfaces.Transactions;

import java.math.BigInteger;

public class BlockStub implements Block {
    private int blockNumber;
    private CoinBaseTransaction coinbase;

    public BlockStub(CoinBaseTransaction coinbase) {
        this.coinbase = coinbase;
    }

    @Override
    public Transactions getTransactions() {
        return new ArrayListTransactions();
    }

    @Override
    public int getBlockNumber() {
        return blockNumber;
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
        return coinbase;
    }

    public void setBlockNumber(int blockNumber) {
        this.blockNumber = blockNumber;
    }
}
