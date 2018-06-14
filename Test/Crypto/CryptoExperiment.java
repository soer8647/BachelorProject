package Crypto;

import Crypto.Impl.*;
import External.Pair;
import Impl.Hashing.SHA256;

import java.math.BigInteger;
import java.util.stream.LongStream;

public class CryptoExperiment {


    private static int packingFactor = 2;
    private static int rsaBitLength = 3078;
    private static int iterations = 1000;
    private static final boolean doSigning = true;
    private static final boolean doVerification = true;
    private static final boolean doKeyGeneration = true;


    private static BigInteger e = new BigInteger("65537");
    private static RSA rsa = new RSA(rsaBitLength);
    private static WOTS wots = new WOTS(new SHA256(),packingFactor);

    public static void main(String[] args) {
        try {
            System.out.println("Packing Factor: " + packingFactor);
            System.out.println("iterations: " + iterations);

            if (doKeyGeneration ) {
                System.out.println("\n KeyGen");
                runKeyGenTest(iterations);
            }
            if (doSigning) {
                System.out.println("\n Signing");
                runSignTest(iterations);
            }
            if (doVerification ) {
                System.out.println("\n Verifying");
                runVerifyTest(iterations);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        printResult("rsa",rsaArray);
        printResult("wots",wotsArray);
    }

    public static void runSignTest(int iterations) {
        long[] rsaArray = new long[iterations];
        long[] wotsArray = new long[iterations];

        // init keys
        RSAPrivateKey rsaKey = rsa.generateNewKeys(e).getPrivateKey();
        WotsPrivateKey wotsKey = wots.generateNewKeys(new Seed(), 0, 1 + 256/packingFactor).getPrivateKey();

        BigInteger message = new SHA256().hash("TestMessage");

        for (int i = 0; i < iterations; i++) {
            Pair<Long, Long> times = testSignTime(rsaKey, wotsKey, message);
            rsaArray[i] = times.getKey();
            wotsArray[i] = times.getValue();
        }
        printResult("rsa",rsaArray);
        printResult("wots",wotsArray);

    }


    public static void runVerifyTest(int iterations) throws Exception {
        long[] rsaArray = new long[iterations];
        long[] wotsArray = new long[iterations];

        // init keys
        RSAKeyPair rSAKeys = rsa.generateNewKeys(e);

        WOTSKeyPair wOTSKeys = wots.generateNewKeys(new Seed(), 0, 1 + 256/packingFactor);

        BigInteger message = new SHA256().hash("TestMessage");

        BigInteger rsaSignature = rsa.sign(rSAKeys.getPrivateKey(),message);
        BigInteger[] wotsSignature = wots.sign(wOTSKeys.getPrivateKey(), message);

        for (int i = 0; i < iterations; i++) {
            Pair<Long, Long> times = testVerifyTime(rSAKeys.getPublicKey(), wOTSKeys.getPublicKey(), message, rsaSignature, wotsSignature);
            rsaArray[i] = times.getKey();
            wotsArray[i] = times.getValue();
        }
        printResult("rsa",rsaArray);
        printResult("wots",wotsArray);

    }

    public static Pair<Long, Long> testKeyGeneration() {
        long start = System.nanoTime();
        rsa.generateNewKeys(e);
        long end = System.nanoTime();
        long RSATime = end - start;

        long start2 = System.nanoTime();
        wots.generateNewKeys(new Seed(), 0, 256/packingFactor);
        long end2 = System.nanoTime();
        long WOTSTime = end2 - start2;

        return new Pair<>(RSATime, WOTSTime);
    }

    public static Pair<Long, Long> testSignTime(RSAPrivateKey rsaKey, WotsPrivateKey wotsKey, BigInteger message) {
        long start = System.nanoTime();
        rsa.sign(rsaKey,message);
        long end = System.nanoTime();
        long RSATime = end - start;

        long start2 = System.nanoTime();
        wots.sign(wotsKey, message);
        long end2 = System.nanoTime();
        long WOTSTime = end2 - start2;

        return new Pair<>(RSATime, WOTSTime);
    }

    public static Pair<Long, Long> testVerifyTime(RSAPublicKey rsaKey, WotsPublicKey wotsKey, BigInteger message, BigInteger RSASignature, BigInteger[] WOTSSignature) {
        long start = System.nanoTime();
        rsa.verify(rsaKey,RSASignature,message);
        long end = System.nanoTime();
        long RSATime = end - start;

        long start2 = System.nanoTime();
        wots.verify(wotsKey, WOTSSignature, message);
        long end2 = System.nanoTime();
        long WOTSTime = end2 - start2;

        return new Pair<>(RSATime, WOTSTime);
    }

    private static void printResult(String name,long[] array) {
        System.out.println(name + " max = " + LongStream.of(array).max().getAsLong() / Math.pow(10, 9));
        System.out.println(name + " min = " + LongStream.of(array).min().getAsLong() / Math.pow(10, 9));
        System.out.println(name + " average = " + (LongStream.of(array).average().getAsDouble() / Math.pow(10, 9)));
    }


}
