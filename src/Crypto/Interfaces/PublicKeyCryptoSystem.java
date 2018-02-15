package Crypto.Interfaces;

import Crypto.Impl.RSAPrivateKey;
import Crypto.Impl.RSAPublicKey;

import java.math.BigInteger;

public interface PublicKeyCryptoSystem {
    BigInteger encrypt(RSAPublicKey key, BigInteger message);
    BigInteger decrypt(RSAPrivateKey key, BigInteger cipher);
    BigInteger sign(RSAPrivateKey key, BigInteger message);
    boolean verify(RSAPublicKey key, BigInteger signature, BigInteger message);
    KeyPair generateNewKeys();

    int getKeyBitLength();
}
