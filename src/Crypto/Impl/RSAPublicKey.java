package Crypto.Impl;

import Crypto.Interfaces.PublicKey;
import Crypto.Interfaces.PublicKeyCryptoSystem;

import java.math.BigInteger;

public class RSAPublicKey implements PublicKey {

    private BigInteger e;
    private BigInteger n;

    public RSAPublicKey(BigInteger e, BigInteger n) {
        this.e = e;
        this.n = n;
    }

    public BigInteger getE() {
        return e;
    }

    public BigInteger getN() {
        return n;
    }

    @Override
    public String toString() {
        return "RSAPublicKey:(n:"+n+",e:"+e+")";
    }

    @Override
    public String getCryptoSystemName() {
        return "RSA";
    }
}
