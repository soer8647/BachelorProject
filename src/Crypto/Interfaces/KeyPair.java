package Crypto.Interfaces;

import Crypto.Impl.PrivateKey;
import Crypto.Impl.PublicKey;

import java.math.BigInteger;

public interface KeyPair {

    PublicKey getPublicKey();
    PrivateKey getPrivateKey();
}
