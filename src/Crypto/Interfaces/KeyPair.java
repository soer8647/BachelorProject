package Crypto.Interfaces;

import Crypto.Impl.RSAPrivateKey;
import Crypto.Impl.RSAPublicKey;

public interface KeyPair {

    RSAPublicKey getPublicKey();
    RSAPrivateKey getPrivateKey();
}
