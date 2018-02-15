package Impl;

import Crypto.Impl.PrivateKey;
import Crypto.Impl.PublicKey;
import Crypto.Interfaces.KeyPair;
import Crypto.Interfaces.PublicKeyCryptoSystem;
import Interfaces.Account;
import Interfaces.Transaction;

import java.math.BigInteger;

public class StandardAccount implements Account{
    private PublicKeyCryptoSystem cryptoSystem;
    private PrivateKey privateKey;
    private PublicKey publicKey;

    public StandardAccount(PublicKeyCryptoSystem cryptoSystem) {
        this.cryptoSystem = cryptoSystem;
        KeyPair keyPair = cryptoSystem.generateNewKeys();

        privateKey = keyPair.getPrivateKey();
        publicKey = keyPair.getPublicKey();
    }


    @Override
    public PublicKey getAddress() {
        return publicKey;
    }

    @Override
    public BigInteger sign(Transaction transaction) {
        return null;
    }

    @Override
    public PrivateKey getPrivateKey() {
        return privateKey;
    }


    @Override
    public PublicKeyCryptoSystem getCryptoSystem() {
        return cryptoSystem;
    }
}
