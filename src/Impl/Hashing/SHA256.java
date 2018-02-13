package Impl.Hashing;

import Interfaces.HashingAlgorithm;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256 implements HashingAlgorithm {
    private MessageDigest sha256;

    public SHA256() {
        try {
            this.sha256 = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {

            e.printStackTrace();
        }
    }

    @Override
    public BigInteger hash(String data) {
        byte[] hash = sha256.digest(data.getBytes());
        return new BigInteger(1,hash);
    }

    @Override
    public MessageDigest getHashingAlgorithm() {
        return sha256;
    }

    @Override
    public int getBitSize() {
        return 256;
    }
}
