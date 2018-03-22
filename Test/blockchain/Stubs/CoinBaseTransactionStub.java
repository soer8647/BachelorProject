package blockchain.Stubs;

import Interfaces.Address;
import Interfaces.CoinBaseTransaction;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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
        throw new NotImplementedException();
    }

    @Override
    public String toString() {
        return "CoinBaseTransactionStub{}";
    }
}
