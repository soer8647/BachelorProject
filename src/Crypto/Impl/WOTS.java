package Crypto.Impl;

import Configuration.Configuration;
import Interfaces.HashingAlgorithm;
import java.security.SecureRandom;
import java.math.BigInteger;
import java.util.Random;

public class WOTS {

    private int bitLength = 2000;

    public BigInteger[] sign(WotsPrivateKey key, BigInteger message) {
        //TODO confirm sizes match

        byte[] messageBytes = message.toByteArray();
        BigInteger[] signature = new BigInteger[messageBytes.length];
        for (int i = 0; i < messageBytes.length; i++) {
            signature[i] = hash(key.get(i),128 + messageBytes[i]);
        }
        return signature;
    }

    public BigInteger hash(BigInteger value, int times) {
        String result = value.toString();
        for (int i = 0; i < times; i++) {
            result = Configuration.hash(result).toString();
        }
        return new BigInteger(result);
    }

    public boolean verify(WotsPublicKey key, BigInteger[] signature, BigInteger message) {
        byte[] messageBytes = message.toByteArray();
        for (int i = 0; i < messageBytes.length; i++) {
            int times = 255 - 128 - messageBytes[i];
            System.out.println(times);
            if (!key.get(i).equals(hash(signature[i], times))) {
                return false;
            }
        }
        return true;
    }

    public WOTSKeyPair generateNewKeys(Seed seed, int index, int length) {
        BigInteger[] privateKeyParts = new BigInteger[length];
        Random random = new SecureRandom();
        //Generate private key
        random.setSeed(seed.getSubseed(index));
        for (int i = 0; i < length; i++) {
            privateKeyParts[i] = new BigInteger(bitLength, random);
        }

        WotsPrivateKey privateKey = new WotsPrivateKey(privateKeyParts);
        //Compute public key
        BigInteger[] publicKeyParts = new BigInteger[length];
        for (int i = 0; i < length; i++) {
            publicKeyParts[i] = hash(privateKeyParts[i],255);
        }
        WotsPublicKey publicKey = new WotsPublicKey(publicKeyParts);

        return new WOTSKeyPair(privateKey,publicKey);
    }

    public int getKeyBitLength() {
        return bitLength;
    }
}
