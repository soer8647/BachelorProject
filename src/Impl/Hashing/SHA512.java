package Impl.Hashing;

import Interfaces.HashingAlgorithm;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA512 implements HashingAlgorithm {
    private MessageDigest sha512;

    public SHA512() {
        try {
            sha512 = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public BigInteger hash(byte[] data) {
        byte[] hash = sha512.digest(data);
        return new BigInteger(1,hash);
    }

    public BigInteger hash(String data) {
        byte[] hash = sha512.digest(data.getBytes());
        return new BigInteger(1,hash);
    }

    public MessageDigest getHashingAlgorithm() {
        return sha512;
    }

    public int getBitSize() {
        return 512;
    }
}
