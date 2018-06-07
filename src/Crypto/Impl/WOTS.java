package Crypto.Impl;

import Interfaces.HashingAlgorithm;
import java.security.SecureRandom;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;

public class WOTS {

    private int bitLength = 180;
    private HashingAlgorithm hashingAlgorithm;
    private int wotsParam;

    public WOTS(HashingAlgorithm hashingAlgorithm, int wotsParam) {
        this.hashingAlgorithm = hashingAlgorithm;
        this.wotsParam = wotsParam;
    }

    public BigInteger[] sign(WotsPrivateKey key, BigInteger message) {
        //TODO confirm sizes match

        FragmentArray fragments = new FragmentArray(message,wotsParam);
        normalize(fragments);
        BigInteger[] signature = new BigInteger[fragments.getLength()];
        for (int i = 0; i < fragments.getLength(); i++) {
            signature[i] = hash(key.get(i),fragments.getFragmentValue(i));
        }
        return signature;
    }

    public BigInteger hash(BigInteger value, int times) {
        String result = value.toString();
        for (int i = 0; i < times; i++) {
            result = hashingAlgorithm.hash(result).toString();
        }
        return new BigInteger(result);
    }

    public void normalize(FragmentArray fragments) {
        int[] fragmentsArray = fragments.getFragmentsAsArray();
        int sum = sumArray(fragmentsArray);
        int target = (int) ((( Math.pow(2,wotsParam)- 1)/2) * fragmentsArray.length);
        int diff = sum - target;
        if (diff>0) {
            for (int i = 0; i < diff; i++) {
                for (int j = 0; j <  fragments.getLength(); j++) {
                    if (fragments.decrementFragment(j)) {
                        break;
                    }
                }
            }
            return;
        } else if (diff<0) {
            for (int i = 0; i < -diff; i++) {
                for (int j = 0; j <  fragments.getLength(); j++) {
                    if (fragments.incrementFragment(j)) {
                        break;
                    }
                }
            }
            return;
        } else {
            return;
        }
    }

    public int sumArray(int[] array) {
        int sum = 0;
        for (int i = 0; i < array.length; i++) {
            sum += array[i];
        }
        return sum;
    }

    public boolean verify(WotsPublicKey key, BigInteger[] signature, BigInteger message) {
        FragmentArray fragments = new FragmentArray(message, wotsParam);
        normalize(fragments);
        for (int i = 0; i < fragments.getLength(); i++) {
            int times = ((int) Math.pow(2,wotsParam)-1) - fragments.getFragmentValue(i);
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
            publicKeyParts[i] = hash(privateKeyParts[i],(int) Math.pow(2,wotsParam)-1);
        }
        WotsPublicKey publicKey = new WotsPublicKey(publicKeyParts);

        return new WOTSKeyPair(privateKey,publicKey);
    }

    public int getKeyBitLength() {
        return bitLength;
    }
}
