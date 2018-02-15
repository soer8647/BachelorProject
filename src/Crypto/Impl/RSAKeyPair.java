package Crypto.Impl;

import Crypto.Interfaces.KeyPair;

import java.math.BigInteger;

public class RSAKeyPair implements KeyPair {
    private PublicKey publicKey;
    private PrivateKey privateKey;

    public RSAKeyPair(PublicKey publicKey, PrivateKey privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    @Override
    public PublicKey getPublicKey() {
        return publicKey;
    }

    @Override
    public PrivateKey getPrivateKey() {
        return privateKey;
    }
}
