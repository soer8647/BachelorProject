package Impl;

import Impl.Hashing.SHA256;
import Interfaces.HashingAlgorithm;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;

public class Global {
    private static HashingAlgorithm hasher = new SHA256();
    /**
     * @return The corresponding Biginteger of a hash
     */
    public static BigInteger hash(String data) {
        return hasher.hash(data);
    }

    /**
     * @return The MessageDigest object corresponding to a hashing algorithm.
     */
    public static MessageDigest getHashingAlgorithm() {
        return hasher.getHashingAlgorithm();
    }

    /**
     * @return  The size of the value that the hash function outputs
     */
    public static int getBitSize() {
        return hasher.getBitSize();
    }
}
