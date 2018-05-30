package Impl.Hashing;

import Interfaces.HashingAlgorithm;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA512 implements HashingAlgorithm {

    public SHA512() {
    }

    public BigInteger hash(String data) {
        MessageDigest sha256 = null;
        try {
            sha256 = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] hash = sha256.digest(data.getBytes());
        return new BigInteger(1,hash);
    }

    public MessageDigest getHashingAlgorithm() {
        MessageDigest sha256 = null;
        try {
            sha256 = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return sha256;
    }

    public int getBitSize() {
        return 512;
    }
}
