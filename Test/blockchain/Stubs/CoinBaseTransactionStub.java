package blockchain.Stubs;

import Interfaces.Address;
import Interfaces.CoinBaseTransaction;

public class CoinBaseTransactionStub implements CoinBaseTransaction{
    @Override
    public int getValue() {
        return 0;
    }

    @Override
    public Address getMinerAddress() {
        return null;
    }
}
