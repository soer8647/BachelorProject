package Interfaces;

import Crypto.Interfaces.PublicKey;
import Crypto.Interfaces.PublicKeyCryptoSystem;

import java.io.Serializable;
import java.math.BigInteger;

public interface Address extends Serializable {
        PublicKey getPublicKey();
}
