package Impl;

import Interfaces.Address;
import Interfaces.CoinBaseTransaction;

public class StandardCoinBaseTransaction implements CoinBaseTransaction {


    private Address minerAddress;
    private int value;

    public StandardCoinBaseTransaction(Address minerAddress, int value) {
        this.minerAddress = minerAddress;
        this.value = value;
    }

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public Address getMinerAddress() {
        return minerAddress;
    }

    @Override
    public String toString() {
        return "StandardCoinBaseTransaction:" +
                "minerAddres=" + minerAddress +
                ", value=" + value;
    }
}
