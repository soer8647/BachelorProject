package Impl;

import Interfaces.Address;
import Interfaces.CoinBaseTransaction;

public class StandardCoinBaseTransaction implements CoinBaseTransaction {


    private Address minerAddres;
    private int value;

    public StandardCoinBaseTransaction(Address minerAddres, int value) {
        this.minerAddres = minerAddres;
        this.value = value;
    }

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public Address getMinerAddress() {
        return minerAddres;
    }

    @Override
    public String toString() {
        return "StandardCoinBaseTransaction:" +
                "minerAddres=" + minerAddres +
                ", value=" + value;
    }
}
