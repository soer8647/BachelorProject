package blockchain.Stubs;

import Crypto.Impl.RSAPrivateKey;
import Crypto.Impl.RSAPublicKey;
import Crypto.Interfaces.KeyPair;
import Crypto.Interfaces.PrivateKey;
import Crypto.Interfaces.PublicKey;
import Crypto.Interfaces.PublicKeyCryptoSystem;

import java.math.BigInteger;

public class CryptoSystemStub implements PublicKeyCryptoSystem <RSAPublicKey,RSAPrivateKey>{
    @Override
    public BigInteger encrypt(RSAPublicKey key, BigInteger message) {
        return null;
    }

    @Override
    public BigInteger decrypt(RSAPrivateKey key, BigInteger cipher) {
        return null;
    }

    @Override
    public BigInteger sign(RSAPrivateKey key, BigInteger message) {
        return null;
    }

    @Override
    public boolean verify(RSAPublicKey key, BigInteger signature, BigInteger message) {
        return false;
    }


    @Override
    public KeyPair generateNewKeys(BigInteger e) {
        return null;
    }

    @Override
    public int getKeyBitLength() {
        return 0;
    }
}
