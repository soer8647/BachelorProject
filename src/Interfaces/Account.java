package Interfaces;

import Crypto.Impl.RSAPrivateKey;
import Crypto.Impl.RSAPublicKey;
import Crypto.Interfaces.PublicKeyCryptoSystem;
import Impl.PublicKeyAddress;

import java.math.BigInteger;

public interface Account {

    /**
     * @return  The address of this account
     */
    PublicKeyAddress getAddress();

    RSAPrivateKey getPrivateKey();

    PublicKeyCryptoSystem getCryptoSystem();

    RSAPublicKey getPublicKey();

    Transaction makeTransaction(Address receiver, int value, BigInteger valueProof, int blockValueProof, int timestamp);

    HashingAlgorithm getHashingAlgorithm();
}
