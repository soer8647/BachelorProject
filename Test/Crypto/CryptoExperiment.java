package Crypto;

import Configuration.Configuration;
import Crypto.Impl.*;
import External.Pair;
import Impl.Hashing.SHA256;
import Impl.Hashing.SHA512;

import javax.swing.text.EditorKit;
import java.math.BigInteger;
import java.security.*;
import java.util.stream.LongStream;

public class CryptoExperiment {

    public static void main(String[] args) {
        try {
            int iterations = 10;
            System.out.println("iterations: " + iterations);
            if (false) {
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

    public static void runKeyGenTest2(int iterations) {
        long[] rsaArray = new long[iterations];
        long[] wotsArray = new long[iterations];
        for (int i = 0; i < iterations; i++) {
            Pair<Long, Long> times = testKeyGeneration2();
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
        PrivateKey rsaKey = null;
        try {
            rsaKey = ExternalRSA.buildKeyPair().getPrivate();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        WotsPrivateKey wotsKey = new WOTS(new SHA256(),8).generateNewKeys(new Seed(), 0, 32).getPrivateKey();

        BigInteger message = new SHA256().hash("Fuck Jacob");

        for (int i = 0; i < iterations; i++) {
            Pair<Long, Long> times = testSignTime(rsaKey, wotsKey, message);
            rsaArray[i] = times.getKey();
            wotsArray[i] = times.getValue();
        }
        printResult("rsa",rsaArray);
        printResult("wots",wotsArray);

    }

    public static void runSignTest2(int iterations) {
        long[] rsaArray = new long[iterations];
        long[] wotsArray = new long[iterations];

        // init keys
        PrivateKey rsaKey = null;
        try {
            rsaKey = ExternalRSA.buildKeyPair().getPrivate();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        WotsPrivateKey wotsKey = new WOTS(new SHA512(),8).generateNewKeys(new Seed(), 0, 65).getPrivateKey();

        BigInteger message = new SHA512().hash("Fuck Jacob");

        for (int i = 0; i < iterations; i++) {
            Pair<Long, Long> times = testSignTime2(rsaKey, wotsKey, message);
            rsaArray[i] = times.getKey();
            wotsArray[i] = times.getValue();
        }
        printResult("rsa",rsaArray);
        printResult("wots",wotsArray);

    }

    public static void runVerifyTest(int iterations) throws Exception {
        long[] rsaArray = new long[iterations];
        long[] wotsArray = new long[iterations];

        RSA rsa = new RSA(3298);
        WOTS wots = new WOTS(new SHA256(),8);

        // init keys
        KeyPair rSAKeys = ExternalRSA.buildKeyPair();

        WOTSKeyPair wOTSKeys = wots.generateNewKeys(new Seed(), 0, 32);

        BigInteger message = new SHA256().hash("Fuck Jacob");

        byte[] rsaSignature = ExternalRSA.sign(message, rSAKeys.getPrivate());
        BigInteger[] wotsSignature = wots.sign(wOTSKeys.getPrivateKey(), message);

        for (int i = 0; i < iterations; i++) {
            Pair<Long, Long> times = testVerifyTime(rSAKeys.getPublic(), wOTSKeys.getPublicKey(), message, rsaSignature, wotsSignature);
            rsaArray[i] = times.getKey();
            wotsArray[i] = times.getValue();
        }
        printResult("rsa",rsaArray);
        printResult("wots",wotsArray);

    }

    public static void runVerifyTest2(int iterations) throws Exception {
        long[] rsaArray = new long[iterations];
        long[] wotsArray = new long[iterations];

        RSA rsa = new RSA(15424);
        WOTS wots = new WOTS(new SHA512(),8);

        // init keys
        KeyPair rSAKeys = ExternalRSA.buildKeyPair();

        WOTSKeyPair wOTSKeys = wots.generateNewKeys(new Seed(), 0, 65);

        BigInteger message = new SHA512().hash("Fuck Jacob");

        byte[] rsaSignature = ExternalRSA.sign(message, rSAKeys.getPrivate());
        BigInteger[] wotsSignature = wots.sign(wOTSKeys.getPrivateKey(), message);

        for (int i = 0; i < iterations; i++) {
            Pair<Long, Long> times = testVerifyTime2(rSAKeys.getPublic(), wOTSKeys.getPublicKey(), message, rsaSignature, wotsSignature);
            rsaArray[i] = times.getKey();
            wotsArray[i] = times.getValue();
        }
        printResult("rsa",rsaArray);
        printResult("wots",wotsArray);

    }

    public static Pair<Long, Long> testKeyGeneration() {
        ExternalRSA.setKeySize(3298);
        long start = System.nanoTime();
        try {
            ExternalRSA.buildKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        long end = System.nanoTime();
        long RSATime = end - start;

        WOTS wots = new WOTS(new SHA256(),8);
        long start2 = System.nanoTime();
        wots.generateNewKeys(new Seed(), 0, 32);
        long end2 = System.nanoTime();
        long WOTSTime = end2 - start2;

        return new Pair<>(RSATime, WOTSTime);
    }

    public static Pair<Long, Long> testKeyGeneration2() {
        //Long keys
        ExternalRSA.setKeySize(15424);
        long start = System.nanoTime();
        try {
            ExternalRSA.buildKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        long end = System.nanoTime();
        long RSATime = end - start;

        WOTS wots = new WOTS(new SHA512(),8);
        long start2 = System.nanoTime();
        wots.generateNewKeys(new Seed(), 0, 64);
        long end2 = System.nanoTime();
        long WOTSTime = end2 - start2;

        return new Pair<>(RSATime, WOTSTime);
    }

    public static Pair<Long, Long> testSignTime(PrivateKey rsaKey, WotsPrivateKey wotsKey, BigInteger message) {
        long start = System.nanoTime();
        try {
            ExternalRSA.sign(message, rsaKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        long end = System.nanoTime();
        long RSATime = end - start;

        WOTS wots = new WOTS(new SHA256(),8);
        long start2 = System.nanoTime();
        wots.sign(wotsKey, message);
        long end2 = System.nanoTime();
        long WOTSTime = end2 - start2;

        return new Pair<>(RSATime, WOTSTime);
    }

    public static Pair<Long, Long> testSignTime2(PrivateKey rsaKey, WotsPrivateKey wotsKey, BigInteger message) {
        //Long keys
        long start = System.nanoTime();
        try {
            ExternalRSA.sign(message, rsaKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        long end = System.nanoTime();
        long RSATime = end - start;

        WOTS wots = new WOTS(new SHA512(),8);
        long start2 = System.nanoTime();
        wots.sign(wotsKey, message);
        long end2 = System.nanoTime();
        long WOTSTime = end2 - start2;

        return new Pair<>(RSATime, WOTSTime);
    }

    public static Pair<Long, Long> testVerifyTime(PublicKey rsaKey, WotsPublicKey wotsKey, BigInteger message, byte[] RSASignature, BigInteger[] WOTSSignature) {
        long start = System.nanoTime();
        try {
            ExternalRSA.verify(message, RSASignature, rsaKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        long end = System.nanoTime();
        long RSATime = end - start;

        WOTS wots = new WOTS(new SHA256(),8);
        long start2 = System.nanoTime();
        wots.verify(wotsKey, WOTSSignature, message);
        long end2 = System.nanoTime();
        long WOTSTime = end2 - start2;

        return new Pair<>(RSATime, WOTSTime);
    }

    public static Pair<Long, Long> testVerifyTime2(PublicKey rsaKey, WotsPublicKey wotsKey, BigInteger message, byte[] RSASignature, BigInteger[] WOTSSignature) {
        long start = System.nanoTime();
        try {
            ExternalRSA.verify(message, RSASignature, rsaKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        long end = System.nanoTime();
        long RSATime = end - start;

        WOTS wots = new WOTS(new SHA512(),8);
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
