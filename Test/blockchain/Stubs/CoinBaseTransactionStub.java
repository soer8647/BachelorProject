package blockchain.Stubs;

import Impl.ToBeImplementedException;
import Interfaces.Address;
import Interfaces.CoinBaseTransaction;

import java.math.BigInteger;

public class CoinBaseTransactionStub implements CoinBaseTransaction{
    @Override
    public int getValue() {
        return 0;
    }

    @Override
    public Address getMinerAddress() {
        return null;
    }

    @Override
    public int getBlockNumber() {
        return 0;
    }

    @Override
    public BigInteger transactionHash() {
        throw new ToBeImplementedException();
    }

    @Override
    public String toString() {
        return "CoinBaseTransactionStub{}";
    }
}
