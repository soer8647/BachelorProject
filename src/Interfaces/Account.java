package Interfaces;

import Crypto.Impl.PrivateKey;
import Crypto.Impl.PublicKey;
import Crypto.Interfaces.PublicKeyCryptoSystem;

import java.math.BigInteger;

public interface Account {

    /**
     * @return  The address of this account
     */
    PublicKey getAddress();

    /**
     * @param transaction       The transaction that the account makes.
     * @return                  The signature on the transaction.
     */
    BigInteger sign(Transaction transaction);
    PrivateKey getPrivateKey();

    PublicKeyCryptoSystem getCryptoSystem();
}
