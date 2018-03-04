package Interfaces;

import Crypto.Impl.RSAPrivateKey;
        import Crypto.Impl.RSAPublicKey;
        import Crypto.Interfaces.PublicKeyCryptoSystem;

        import java.math.BigInteger;

public interface Account {

    /**
     * @return  The address of this account
     */
    RSAPublicKey getAddress();


    RSAPrivateKey getPrivateKey();

    PublicKeyCryptoSystem getCryptoSystem();

    RSAPublicKey getPublicKey();

    Transaction makeTransaction(Address sender, Address receiver, int value, BigInteger valueProof, int blockValueProof);

    HashingAlgorithm getHashingAlgorithm();
}
