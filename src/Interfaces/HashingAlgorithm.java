package Interfaces;

import Impl.Hashing.SHA256;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;

public interface HashingAlgorithm  {
    /**
     * @return The corresponding Biginteger of a hash
     */
    BigInteger hash(String data);
    /**
     * @return The MessageDigest object corresponding to a hashing algorithm.
     */
    MessageDigest getHashingAlgorithm();

    /**
     * @return  The size of the value that the hash function outputs
     */
    int getBitSize();
}
