package Impl.Hashing;

import Interfaces.HashingAlgorithm;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
public class SHA256 implements HashingAlgorithm {
    private MessageDigest sha256;

    public SHA256() {
        try {
            sha256 = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public BigInteger hash(byte[] data) {
        byte[] hash = sha256.digest(data);
        return new BigInteger(1,hash);
    }

    public BigInteger hash(String data) {
        byte[] hash = sha256.digest(data.getBytes());
        return new BigInteger(1,hash);
    }

    public MessageDigest getHashingAlgorithm() {
        return sha256;
    }

    public int getBitSize() {
        return 256;
    }
}
