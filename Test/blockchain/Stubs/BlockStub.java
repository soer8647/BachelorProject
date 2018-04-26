package blockchain.Stubs;

import Interfaces.Block;
import Interfaces.CoinBaseTransaction;
import Interfaces.Transaction;

import java.math.BigInteger;
import java.util.Collection;

public class BlockStub implements Block {
    private int blockNumber;
    private CoinBaseTransaction coinbase;
    private Collection<Transaction> transactions;

    public BlockStub(CoinBaseTransaction coinbase, Collection<Transaction> transactions, int blockNumber) {
        this.coinbase = coinbase;
        this.transactions = transactions;
        this.blockNumber = blockNumber;
    }



    @Override
    public Collection<Transaction> getTransactions() {
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
