package Impl;

import Crypto.Impl.RSAPrivateKey;
import Crypto.Impl.RSAPublicKey;
import Crypto.Interfaces.KeyPair;
import Crypto.Interfaces.PublicKeyCryptoSystem;
import Interfaces.Account;
import Interfaces.Transaction;

import java.math.BigInteger;

public class StandardAccount implements Account{
    private PublicKeyCryptoSystem cryptoSystem;
    private RSAPrivateKey privateKey;
    private RSAPublicKey publicKey;

    public StandardAccount(PublicKeyCryptoSystem cryptoSystem) {
        this.cryptoSystem = cryptoSystem;
        KeyPair keyPair = cryptoSystem.generateNewKeys();

        privateKey = keyPair.getPrivateKey();
        publicKey = keyPair.getPublicKey();
    }

    public StandardAccount(PublicKeyCryptoSystem cryptoSystem, RSAPrivateKey privateKey, RSAPublicKey publicKey) {
        this.cryptoSystem = cryptoSystem;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    @Override
    public RSAPublicKey getAddress() {
        return publicKey;
    }

    @Override
    public BigInteger sign(Transaction transaction) {
        return null;
    }

    @Override
    public RSAPrivateKey getPrivateKey() {
        return privateKey;
    }


    @Override
    public PublicKeyCryptoSystem getCryptoSystem() {
        return cryptoSystem;
    }

    @Override
    public RSAPublicKey getPublicKey() {
        return publicKey;
    }

    @Override
    public Transaction makeTransaction() {
        return null;
    }
}
