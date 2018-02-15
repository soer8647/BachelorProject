package Impl;

import Crypto.Impl.RSAPublicKey;
import Crypto.Interfaces.PublicKey;
import Crypto.Interfaces.PublicKeyCryptoSystem;
import Interfaces.Address;

import java.math.BigInteger;

public class PublicKeyAddress implements Address {

    private PublicKey publicKey;
    private PublicKeyCryptoSystem cryptoSystem;

    public PublicKeyAddress(PublicKey publicKey, PublicKeyCryptoSystem cryptoSystem) {
        this.publicKey = publicKey;
        this.cryptoSystem = cryptoSystem;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    @Override
    public String toString() {
        return "PublicKeyAddress:{\n\t[" +
                "publicKey:" + publicKey+"]";
    }

    @Override
    public PublicKeyCryptoSystem getCryptoSystem() {
        return cryptoSystem;
    }
}
