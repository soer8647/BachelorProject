package Crypto;

import java.math.BigInteger;
import java.security.*;


public class ExternalRSA {
    public static int getKeySize() {
        return keySize;
    }

    public static void setKeySize(int keySize) {
        ExternalRSA.keySize = keySize;
    }

    private static int keySize = 3298;

    public static byte[] sign(BigInteger plainText, PrivateKey privateKey) throws Exception {
        Signature privateSignature = Signature.getInstance("SHA256withRSA");
        privateSignature.initSign(privateKey);
        privateSignature.update(plainText.toByteArray());

        byte[] signature = privateSignature.sign();

        return signature;
    }

    public static boolean verify(BigInteger plainText, byte[] signature, PublicKey publicKey) throws Exception {
        Signature publicSignature = Signature.getInstance("SHA256withRSA");
        publicSignature.initVerify(publicKey);
        publicSignature.update(plainText.toByteArray());

        return publicSignature.verify(signature);
    }

    public static KeyPair buildKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(keySize, new SecureRandom());
        return keyPairGenerator.genKeyPair();
    }
}