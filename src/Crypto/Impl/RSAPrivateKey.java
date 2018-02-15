package Crypto.Impl;

import Crypto.Interfaces.PrivateKey;

import java.math.BigInteger;

public class RSAPrivateKey implements PrivateKey{

    private BigInteger n;
    private BigInteger d;

    public RSAPrivateKey(BigInteger n, BigInteger d) {
        this.n = n;
        this.d = d;
    }


    public BigInteger getN() {
        return n;
    }

    public BigInteger getD() {
        return d;
    }

    @Override
    public String getCryptoSystemName() {
        return "RSA";
    }
}
