package Impl;

import Interfaces.Account;
import Interfaces.Transaction;

import java.math.BigInteger;

public class StandardAccount implements Account{
    @Override
    public BigInteger getAddress() {
        return new BigInteger("42");
    }

    @Override
    public BigInteger sign(Transaction transaction) {
        return null;
    }

    @Override
    public BigInteger getPrivateKey() {
        return new BigInteger("42");
    }
}
