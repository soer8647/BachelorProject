package Interfaces;

import Crypto.Interfaces.PublicKey;
import Crypto.Interfaces.PublicKeyCryptoSystem;

import java.math.BigInteger;

public interface Address {
        PublicKeyCryptoSystem getCryptoSystem();

        PublicKey getPublicKey();
}
