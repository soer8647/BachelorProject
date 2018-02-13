package Interfaces;

import java.math.BigInteger;
import java.security.MessageDigest;

/*
* This interface is for a single transaction.
*
* A transaction should specify :
* 1. Sender
* 2. Reciever
* 3. Amount
* */
public interface Transaction {
    /**
     * @return      The hash of this transaction.
     */
    BigInteger transActionHash();

    /**
     * @return The "address" or identifier of the sender of this transaction.
     */
    long getSender();

    /**
     * @return The "address" of identifier of the receiver of this transaction.
     */
    long getReceiver();

    /**
     * @return The amount of money send in this transaction.
     */
    int getValue();

    /**
     * @return The transaction where the sender has a proof of funds to make this transaction.
     */
    Transaction getValueProof();


}
