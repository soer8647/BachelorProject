package Interfaces;

import Crypto.Impl.RSAPrivateKey;
import Crypto.Impl.RSAPublicKey;
import Impl.PublicKeyAddress;

import java.math.BigInteger;

public interface Account {

    /**
     * @return  The address of this account
     */
    PublicKeyAddress getAddress();

    RSAPrivateKey getPrivateKey();

    RSAPublicKey getPublicKey();

    Transaction makeTransaction(Address receiver, int value, BigInteger valueProof, int blockValueProof, int timestamp);
}
