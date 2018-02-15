package Crypto.Interfaces;

import Crypto.Impl.PrivateKey;
import Crypto.Impl.PublicKey;

import java.math.BigInteger;

public interface PublicKeyCryptoSystem {
    BigInteger encrypt(PublicKey key,BigInteger message);
    BigInteger decrypt(PrivateKey key,BigInteger cipher);
    BigInteger sign(PrivateKey key,BigInteger message);
    boolean verify(PublicKey key, BigInteger signature, BigInteger message);
    KeyPair generateNewKeys();

    int getKeyBitLength();
}
