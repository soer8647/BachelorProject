package blockchain.Stubs;

import Interfaces.Block;
import Interfaces.CoinBaseTransaction;
import Interfaces.Transactions;

import java.math.BigInteger;

public class BlockStub implements Block {
    private int blockNumber;
    private CoinBaseTransaction coinbase;
    private Transactions transactions;

    public BlockStub(CoinBaseTransaction coinbase, Transactions transactions, int blockNumber) {
        this.coinbase = coinbase;
        this.transactions = transactions;
        this.blockNumber = blockNumber;
    }



    @Override
    public Transactions getTransactions() {
        return transactions;
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
