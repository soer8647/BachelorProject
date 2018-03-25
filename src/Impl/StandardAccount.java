package Impl;

import Crypto.Impl.RSAPrivateKey;
import Crypto.Impl.RSAPublicKey;
import Crypto.Interfaces.KeyPair;
import Crypto.Interfaces.PublicKeyCryptoSystem;
import Impl.Transactions.StandardTransaction;
import Interfaces.Account;
import Interfaces.Address;
import Interfaces.HashingAlgorithm;
import Interfaces.Transaction;

import java.math.BigInteger;
/*
* A standard account is a way to access the block chain. An Account should be able to make transactions and view the  transaction history of this account.
*
* */
public class StandardAccount implements Account{
    private PublicKeyCryptoSystem cryptoSystem;
    private RSAPrivateKey privateKey;
    private RSAPublicKey publicKey;
    private PublicKeyAddress address;
    private HashingAlgorithm hashingAlgorithm;

    public StandardAccount(PublicKeyCryptoSystem cryptoSystem, HashingAlgorithm hashingAlgorithm) {
        this.cryptoSystem = cryptoSystem;
        this.hashingAlgorithm = hashingAlgorithm;
        KeyPair keyPair = cryptoSystem.generateNewKeys(BigInteger.valueOf(3));

        privateKey = keyPair.getPrivateKey();
        publicKey = keyPair.getPublicKey();
        address = new PublicKeyAddress(publicKey);
    }

    public StandardAccount(PublicKeyCryptoSystem cryptoSystem, RSAPrivateKey privateKey, RSAPublicKey publicKey, HashingAlgorithm hashingAlgorithm) {
        this.cryptoSystem = cryptoSystem;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.hashingAlgorithm = hashingAlgorithm;
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
    public PublicKeyCryptoSystem getCryptoSystem() {
        return cryptoSystem;
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
    public Transaction makeTransaction(Address receiver, int value, BigInteger valueProof, int blockValueProof) {

        String transaction = address.toString()+receiver.toString()+value+valueProof.toString();
        BigInteger signature = getCryptoSystem().sign(privateKey,hashingAlgorithm.hash(transaction));

        return new StandardTransaction(address,receiver,value,valueProof,signature, blockValueProof);
    }

    public HashingAlgorithm getHashingAlgorithm() {
        return hashingAlgorithm;
    }
}
