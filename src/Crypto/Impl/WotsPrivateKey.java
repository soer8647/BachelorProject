package Crypto.Impl;

import java.math.BigInteger;

public class WotsPrivateKey {

    private BigInteger[] parts;

    public WotsPrivateKey(BigInteger[] parts) {
        this.parts = parts;
    }

    public int getLength() {
        return this.parts.length;
    }

    public BigInteger get(int i) {
        return parts[i];
    }
}
