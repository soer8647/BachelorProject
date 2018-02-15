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

    /**
     * @param transaction       The transaction that the account makes.
     * @return                  The signature on the transaction.
     */
    BigInteger sign(Transaction transaction);
    RSAPrivateKey getPrivateKey();

    PublicKeyCryptoSystem getCryptoSystem();

    RSAPublicKey getPublicKey();

    Transaction makeTransaction();
}
