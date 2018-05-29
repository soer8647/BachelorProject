package Crypto.Impl;

import java.math.BigInteger;

public class WotsPublicKey {
    private BigInteger[] publicKeyParts;

    public WotsPublicKey(BigInteger[] publicKeyParts) {
        this.publicKeyParts = publicKeyParts;
    }

    public BigInteger get(int i) {
        return publicKeyParts[i];
    }
}
