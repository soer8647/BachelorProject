package Interfaces;

import java.io.Serializable;
import java.math.BigInteger;

/*
* This interface is for a single transaction.
*
* A transaction should specify :
* 1. Sender
* 2. Receiver
* 3. Amount
* 4. Proof of funds
* */
public interface Transaction extends Serializable {
    /**
     * @return      The hash of this transaction.
     */
    BigInteger transactionHash();

    /**
     * @return The "address" or identifier of the sender of this transaction.
     */
    Address getSenderAddress();

    /**
     * @return The "address" of identifier of the receiver of this transaction.
     */
    Address getReceiverAddress();

    /**
     * @return The amount of money send in this transaction.
     */
    int getValue();

    BigInteger getSignature();

    int getTimestamp();
}
