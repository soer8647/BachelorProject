package Interfaces;

import java.math.BigInteger;

public interface Account {

    /**
     * @return  The address of this account
     */
    BigInteger getAddress();

    /**
     * @param transaction       The transaction that the account makes.
     * @return                  The signature on the transaction.
     */
    BigInteger sign(Transaction transaction);


    BigInteger getPrivateKey();
}
