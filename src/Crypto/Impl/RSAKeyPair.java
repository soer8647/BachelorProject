package Crypto.Impl;

import Crypto.Interfaces.KeyPair;

public class RSAKeyPair implements KeyPair {
    private RSAPublicKey publicKey;
    private RSAPrivateKey privateKey;

    public RSAKeyPair(RSAPublicKey publicKey, RSAPrivateKey privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    @Override
    public RSAPublicKey getPublicKey() {
        return publicKey;
    }

    @Override
    public RSAPrivateKey getPrivateKey() {
        return privateKey;
    }
}
