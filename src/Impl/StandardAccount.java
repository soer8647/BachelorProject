package Impl;

import Configuration.Configuration;
import Crypto.Impl.RSAPrivateKey;
import Crypto.Impl.RSAPublicKey;
import Crypto.Interfaces.KeyPair;
import Impl.Transactions.StandardTransaction;
import Interfaces.Account;
import Interfaces.Address;
import Interfaces.Transaction;

import java.math.BigInteger;
/*
* A standard account is a way to access the block chain. An Account should be able to make transactions and view the  transaction history of this account.
*
* */
public class StandardAccount implements Account{
    private RSAPrivateKey privateKey;
    private RSAPublicKey publicKey;
    private PublicKeyAddress address;

    public StandardAccount() {
        KeyPair keyPair = Configuration.getCryptoSystem().generateNewKeys(BigInteger.valueOf(3));

        privateKey = keyPair.getPrivateKey();
        publicKey = keyPair.getPublicKey();
        address = new PublicKeyAddress(publicKey);
    }

    public StandardAccount(RSAPrivateKey privateKey, RSAPublicKey publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        address = new PublicKeyAddress(publicKey);
    }

    @Override
    public PublicKeyAddress getAddress() {
        return address;
    }

    @Override
    public RSAPrivateKey getPrivateKey() {
        return privateKey;
    }


    @Override
    public RSAPublicKey getPublicKey() {
        return publicKey;
    }

    /**
     * @param receiver      The address of the receiver of the transaction.
     * @param value         The amount to be transferred.
     * @param valueProof    The transaction where the sender has a proof of funds
     * @return              The transaction object.
     */
    @Override
    public Transaction makeTransaction(Address receiver, int value, BigInteger valueProof, int blockValueProof,int timestamp) {

        String transaction = address.toString()+receiver.toString()+value+valueProof.toString()+timestamp;
        BigInteger signature = Configuration.getCryptoSystem().sign(privateKey,Configuration.hash(transaction));

        return new StandardTransaction(address,receiver,value,valueProof,signature, blockValueProof, timestamp);
    }
}
