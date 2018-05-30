package Crypto;

import Configuration.Configuration;
import Crypto.Impl.*;
import External.Pair;
import Impl.Hashing.SHA256;
import Impl.Hashing.SHA512;

import java.math.BigInteger;
import java.util.stream.LongStream;

public class CryptoExperiment {

    public static void main(String[] args) {
        int iterations = 100;
        if (true) {
            System.out.println("\n KeyGen");
            System.out.println("\n short");
            runKeyGenTest(iterations);
            System.out.println("\n long");
            runKeyGenTest2(iterations);
        }
        if (true) {
            System.out.println("\n Signing");
            System.out.println("\n short");
            runSignTest(iterations);
            System.out.println("\n long");
            runSignTest2(iterations);
        }
        if (true) {
            System.out.println("\n Verifying");
            System.out.println("\n short");
            runVerifyTest(iterations);
            System.out.println("\n long");
            runVerifyTest2(iterations);
        }
    }

    public static void runKeyGenTest(int iterations) {
        long[] rsaArray = new long[iterations];
        long[] wotsArray = new long[iterations];
        for (int i = 0; i < iterations; i++) {
            Pair<Long, Long> times = testKeyGeneration();
            rsaArray[i] = times.getKey();
            wotsArray[i] = times.getValue();
        }
        System.out.println("rsa max = " + LongStream.of(rsaArray).max().getAsLong()/ Math.pow(10,9));
        System.out.println("rsa min = " + LongStream.of(rsaArray).min().getAsLong()/ Math.pow(10,9));
        System.out.println("rsa average = " + (LongStream.of(rsaArray).average().getAsDouble() / Math.pow(10,9)));
        System.out.println("wots max = " + LongStream.of(wotsArray).max().getAsLong()/ Math.pow(10,9));
        System.out.println("wots min = " + LongStream.of(wotsArray).min().getAsLong()/ Math.pow(10,9));
        System.out.println("wots average = " + (LongStream.of(wotsArray).average().getAsDouble() / Math.pow(10,9)));
    }

    public static void runKeyGenTest2(int iterations) {
        long[] rsaArray = new long[iterations];
        long[] wotsArray = new long[iterations];
        for (int i = 0; i < iterations; i++) {
            Pair<Long, Long> times = testKeyGeneration2();
            rsaArray[i] = times.getKey();
            wotsArray[i] = times.getValue();
        }
        System.out.println("rsa max = " + LongStream.of(rsaArray).max().getAsLong()/ Math.pow(10,9));
        System.out.println("rsa min = " + LongStream.of(rsaArray).min().getAsLong()/ Math.pow(10,9));
        System.out.println("rsa average = " + (LongStream.of(rsaArray).average().getAsDouble() / Math.pow(10,9)));
        System.out.println("wots max = " + LongStream.of(wotsArray).max().getAsLong()/ Math.pow(10,9));
        System.out.println("wots min = " + LongStream.of(wotsArray).min().getAsLong()/ Math.pow(10,9));
        System.out.println("wots average = " + (LongStream.of(wotsArray).average().getAsDouble() / Math.pow(10,9)));
    }

    public static void runSignTest(int iterations) {
        long[] rsaArray = new long[iterations];
        long[] wotsArray = new long[iterations];

        // init keys
        RSAPrivateKey rsaKey = new RSA(3298).generateNewKeys(new BigInteger("3")).getPrivateKey();

        WotsPrivateKey wotsKey = new WOTS(new SHA256()).generateNewKeys(new Seed(),0,32).getPrivateKey();

        BigInteger message = new SHA256().hash("Fuck Jacob");

        for (int i = 0; i < iterations; i++) {
            Pair<Long, Long> times = testSignTime(rsaKey,wotsKey,message);
            rsaArray[i] = times.getKey();
            wotsArray[i] = times.getValue();
        }
        System.out.println("rsa max = " + LongStream.of(rsaArray).max().getAsLong()/ Math.pow(10,9));
        System.out.println("rsa min = " + LongStream.of(rsaArray).min().getAsLong()/ Math.pow(10,9));
        System.out.println("rsa average = " + (LongStream.of(rsaArray).average().getAsDouble() / Math.pow(10,9)));
        System.out.println("wots max = " + LongStream.of(wotsArray).max().getAsLong()/ Math.pow(10,9));
        System.out.println("wots min = " + LongStream.of(wotsArray).min().getAsLong()/ Math.pow(10,9));
        System.out.println("wots average = " + (LongStream.of(wotsArray).average().getAsDouble() / Math.pow(10,9)));

    }

    public static void runSignTest2(int iterations) {

    }

    public static Pair<Long,Long> testKeyGeneration() {
        RSA rsa = new RSA(3298);
        long start = System.nanoTime();
        rsa.generateNewKeys(new BigInteger("3"));
        long end = System.nanoTime();
        long RSATime = end - start;

        WOTS wots = new WOTS(new SHA256());
        long start2 = System.nanoTime();
        wots.generateNewKeys(new Seed(),0,32);
        long end2 = System.nanoTime();
        long WOTSTime = end2 - start2;

        return new Pair<>(RSATime,WOTSTime);
    }

    public static void runVerifyTest(int iterations) {
        long[] rsaArray = new long[iterations];
        long[] wotsArray = new long[iterations];

        RSA rsa = new RSA(3298);
        WOTS wots = new WOTS(new SHA256());

        // init keys
        RSAKeyPair rSAKeys = rsa.generateNewKeys(new BigInteger("3"));

        WOTSKeyPair wOTSKeys = wots.generateNewKeys(new Seed(),0,32);

        BigInteger message = new SHA256().hash("Fuck Jacob");

        BigInteger rsaSignature = rsa.sign(rSAKeys.getPrivateKey(), message);
        BigInteger[] wotsSignature = wots.sign(wOTSKeys.getPrivateKey(), message);

        for (int i = 0; i < iterations; i++) {
            Pair<Long, Long> times = testVerifyTime(rSAKeys.getPublicKey(), wOTSKeys.getPublicKey(),message,rsaSignature,wotsSignature);
            rsaArray[i] = times.getKey();
            wotsArray[i] = times.getValue();
        }
        System.out.println("rsa max = " + LongStream.of(rsaArray).max().getAsLong()/ Math.pow(10,9));
        System.out.println("rsa min = " + LongStream.of(rsaArray).min().getAsLong()/ Math.pow(10,9));
        System.out.println("rsa average = " + (LongStream.of(rsaArray).average().getAsDouble() / Math.pow(10,9)));
        System.out.println("wots max = " + LongStream.of(wotsArray).max().getAsLong()/ Math.pow(10,9));
        System.out.println("wots min = " + LongStream.of(wotsArray).min().getAsLong()/ Math.pow(10,9));
        System.out.println("wots average = " + (LongStream.of(wotsArray).average().getAsDouble() / Math.pow(10,9)));

    }

    public static void runVerifyTest2(int iterations) {
        long[] rsaArray = new long[iterations];
        long[] wotsArray = new long[iterations];

        RSA rsa = new RSA(15424);
        WOTS wots = new WOTS(new SHA512());

        // init keys
        RSAKeyPair rSAKeys = rsa.generateNewKeys(new BigInteger("3"));

        WOTSKeyPair wOTSKeys = wots.generateNewKeys(new Seed(),0,64);

        BigInteger message = new SHA512().hash("Fuck Jacob");

        BigInteger rsaSignature = rsa.sign(rSAKeys.getPrivateKey(), message);
        BigInteger[] wotsSignature = wots.sign(wOTSKeys.getPrivateKey(), message);

        for (int i = 0; i < iterations; i++) {
            Pair<Long, Long> times = testVerifyTime(rSAKeys.getPublicKey(), wOTSKeys.getPublicKey(),message,rsaSignature,wotsSignature);
            rsaArray[i] = times.getKey();
            wotsArray[i] = times.getValue();
        }
        System.out.println("rsa max = " + LongStream.of(rsaArray).max().getAsLong()/ Math.pow(10,9));
        System.out.println("rsa min = " + LongStream.of(rsaArray).min().getAsLong()/ Math.pow(10,9));
        System.out.println("rsa average = " + (LongStream.of(rsaArray).average().getAsDouble() / Math.pow(10,9)));
        System.out.println("wots max = " + LongStream.of(wotsArray).max().getAsLong()/ Math.pow(10,9));
        System.out.println("wots min = " + LongStream.of(wotsArray).min().getAsLong()/ Math.pow(10,9));
        System.out.println("wots average = " + (LongStream.of(wotsArray).average().getAsDouble() / Math.pow(10,9)));

    }


    public static Pair<Long,Long> testKeyGeneration2() {
        //Long keys
        RSA rsa = new RSA(15424);
        long start = System.nanoTime();
        rsa.generateNewKeys(new BigInteger("3"));
        long end = System.nanoTime();
        long RSATime = end - start;

        WOTS wots = new WOTS(new SHA512());
        long start2 = System.nanoTime();
        wots.generateNewKeys(new Seed(),0,64);
        long end2 = System.nanoTime();
        long WOTSTime = end2 - start2;

        return new Pair<>(RSATime,WOTSTime);
    }

    public static Pair<Long,Long> testSignTime(RSAPrivateKey rsaKey, WotsPrivateKey wotsKey, BigInteger message) {
        RSA rsa = new RSA(3298);
        long start = System.nanoTime();
        rsa.sign(rsaKey,message);
        long end = System.nanoTime();
        long RSATime = end - start;

        WOTS wots = new WOTS(new SHA256());
        long start2 = System.nanoTime();
        wots.sign(wotsKey,message);
        long end2 = System.nanoTime();
        long WOTSTime = end2 - start2;

        return new Pair<>(RSATime,WOTSTime);
    }

    public static Pair<Long,Long> testSignTime2(RSAPrivateKey rsaKey, WotsPrivateKey wotsKey, BigInteger message) {
        //Long keys
        RSA rsa = new RSA(15424);
        long start = System.nanoTime();
        rsa.sign(rsaKey,message);
        long end = System.nanoTime();
        long RSATime = end - start;

        WOTS wots = new WOTS(new SHA512());
        long start2 = System.nanoTime();
        wots.sign(wotsKey,message);
        long end2 = System.nanoTime();
        long WOTSTime = end2 - start2;

        return new Pair<>(RSATime,WOTSTime);
    }

    public static Pair<Long,Long> testVerifyTime(RSAPublicKey rsaKey, WotsPublicKey wotsKey, BigInteger message, BigInteger RSASignature, BigInteger[] WOTSSignature) {
        RSA rsa = new RSA(3298);
        long start = System.nanoTime();
        rsa.verify(rsaKey,message,RSASignature);
        long end = System.nanoTime();
        long RSATime = end - start;

        WOTS wots = new WOTS(new SHA256());
        long start2 = System.nanoTime();
        wots.verify(wotsKey,WOTSSignature,message);
        long end2 = System.nanoTime();
        long WOTSTime = end2 - start2;

        return new Pair<>(RSATime,WOTSTime);
    }

    public static Pair<Long,Long> testVerifyTime2(RSAPublicKey rsaKey, WotsPublicKey wotsKey, BigInteger message, BigInteger RSASignature, BigInteger[] WOTSSignature) {
        RSA rsa = new RSA(15424);
        long start = System.nanoTime();
        rsa.verify(rsaKey,message,RSASignature);
        long end = System.nanoTime();
        long RSATime = end - start;

        WOTS wots = new WOTS(new SHA512());
        long start2 = System.nanoTime();
        wots.verify(wotsKey,WOTSSignature,message);
        long end2 = System.nanoTime();
        long WOTSTime = end2 - start2;

        return new Pair<>(RSATime,WOTSTime);
    }


}
