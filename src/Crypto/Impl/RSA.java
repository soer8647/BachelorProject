package Crypto.Impl;

import Crypto.Interfaces.PublicKey;
import Crypto.Interfaces.PublicKeyCryptoSystem;

import java.math.BigInteger;
import java.util.Random;

public class RSA implements PublicKeyCryptoSystem<RSAPublicKey,RSAPrivateKey> {

private int keyBitLength;

    public RSA(int keyBitLength) {
        this.keyBitLength = keyBitLength;
    }



    @Override
    public BigInteger encrypt(RSAPublicKey key, BigInteger message) {
        return message.modPow(key.getE(), key.getN());
    }

    @Override
    public BigInteger decrypt(RSAPrivateKey key, BigInteger cipher) {
        return cipher.modPow(key.getD(), key.getN());
    }

    @Override
    public BigInteger sign(RSAPrivateKey key, BigInteger message) {
        return message.modPow(key.getD(), key.getN());
    }

    @Override
    public boolean verify(RSAPublicKey key, BigInteger signature, BigInteger message) {
        BigInteger candidate = signature.modPow(key.getE(), key.getN());
        return candidate.equals(message);
    }

    @Override
    public RSAKeyPair generateNewKeys(BigInteger e) {
        Random r = new Random();

        BigInteger p = new BigInteger(keyBitLength / 2, 1, r);
        BigInteger q = new BigInteger(keyBitLength / 2, 1, r);

        while (p.multiply(q).bitLength() != keyBitLength
                || !p.subtract(BigInteger.ONE).gcd(e).equals(BigInteger.ONE)
                || !q.subtract(BigInteger.ONE).gcd(e).equals(BigInteger.ONE)) {
            q = new BigInteger((keyBitLength / 2), 1, r);
            p = new BigInteger(keyBitLength / 2, 1, r);
        }

        BigInteger n = p.multiply(q);
        BigInteger d = e.modInverse(p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE)));

        return new RSAKeyPair(new RSAPublicKey(e,n),new RSAPrivateKey(n,d));

    }

    @Override
    public int getKeyBitLength() {
        return keyBitLength;
    }
}
