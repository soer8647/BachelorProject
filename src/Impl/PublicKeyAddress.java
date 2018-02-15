package Impl;

import Crypto.Impl.RSAPublicKey;
import Crypto.Interfaces.PublicKey;
import Interfaces.Address;

public class PublicKeyAddress implements Address {

    private PublicKey publicKey;

    public PublicKeyAddress(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    @Override
    public String toString() {
        return "PublicKeyAddress:{\n\t[" +
                "publicKey:" + publicKey+"]";
    }
}
