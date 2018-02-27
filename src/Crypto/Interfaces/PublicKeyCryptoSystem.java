package Crypto.Interfaces;

import Crypto.Impl.RSAKeyPair;
import Crypto.Impl.RSAPrivateKey;
import Crypto.Impl.RSAPublicKey;

import java.math.BigInteger;

public interface PublicKeyCryptoSystem<T extends PublicKey, G extends PrivateKey> {
    BigInteger encrypt(T key, BigInteger message);
    BigInteger decrypt(G key, BigInteger cipher);
    BigInteger sign(G key, BigInteger message);
    boolean verify(T key, BigInteger signature, BigInteger message);

    KeyPair generateNewKeys(BigInteger e);

    int getKeyBitLength();
}
