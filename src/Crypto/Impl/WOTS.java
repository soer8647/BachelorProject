package Crypto.Impl;

import Configuration.Configuration;
import Interfaces.HashingAlgorithm;
import java.security.SecureRandom;
import java.math.BigInteger;
import java.util.Random;

public class WOTS {

    private int bitLength = 180;

    public BigInteger[] sign(WotsPrivateKey key, BigInteger message) {
        //TODO confirm sizes match

        byte[] messageBytes = message.toByteArray();
        byte[] normalized  = normalize(messageBytes);
        BigInteger[] signature = new BigInteger[messageBytes.length];
        for (int i = 0; i < messageBytes.length; i++) {
            signature[i] = hash(key.get(i),128 + normalized[i]);
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

    public byte[] normalize(byte[] bytes) {
        int sum = sumArray(bytes);
        byte[] normalized = bytes.clone();
        if (sum>0) {
            for (int i = 0; i < sum; i++) {
                for (int j = 0; j < bytes.length; j++) {
                    if (normalized[j] > -127) {
                        normalized[j]--;
                        break;
                    }
                }
            }
            return normalized;
        } else if (sum<0) {
            for (int i = 0; i < -sum; i++) {
                for (int j = 0; j < bytes.length; j++) {
                    if (normalized[j] < 127) {
                        normalized[j]++;
                        break;
                    }
                }
            }
            return normalized;
        } else {
            return normalized;
        }
    }

    public int sumArray(byte[] bytes) {
        int sum = 0;
        for (int i = 0; i < bytes.length; i++) {
            sum += bytes[i];
        }
        return sum;
    }

    public boolean verify(WotsPublicKey key, BigInteger[] signature, BigInteger message) {
        byte[] messageBytes = message.toByteArray();
        byte[] normalized  = normalize(messageBytes);
        for (int i = 0; i < messageBytes.length; i++) {
            int times = 255 - 128 - normalized[i];
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
