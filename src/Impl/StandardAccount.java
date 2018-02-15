package Impl;

import Crypto.Impl.RSAPrivateKey;
import Crypto.Impl.RSAPublicKey;
import Crypto.Interfaces.KeyPair;
import Crypto.Interfaces.PublicKeyCryptoSystem;
import Interfaces.Account;
import Interfaces.Address;
import Interfaces.HashingAlgorithm;
import Interfaces.Transaction;

import java.math.BigInteger;

public class StandardAccount implements Account{
    private PublicKeyCryptoSystem cryptoSystem;
    private RSAPrivateKey privateKey;
    private RSAPublicKey publicKey;
    private HashingAlgorithm hashingAlgorithm;

    public StandardAccount(PublicKeyCryptoSystem cryptoSystem, HashingAlgorithm hashingAlgorithm) {
        this.cryptoSystem = cryptoSystem;
        this.hashingAlgorithm = hashingAlgorithm;
        KeyPair keyPair = cryptoSystem.generateNewKeys();

        privateKey = keyPair.getPrivateKey();
        publicKey = keyPair.getPublicKey();

    }

    public StandardAccount(PublicKeyCryptoSystem cryptoSystem, RSAPrivateKey privateKey, RSAPublicKey publicKey, HashingAlgorithm hashingAlgorithm) {
        this.cryptoSystem = cryptoSystem;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.hashingAlgorithm = hashingAlgorithm;
    }

    @Override
    public RSAPublicKey getAddress() {
        return publicKey;
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
     * @param sender        The address of the sender of the transaction
     * @param receiver      The address of the receiver of the transaction.
     * @param value         The amount to be transferred.
     * @param valueProof    The transaction where the sender has a proof of funds
     * @return              The transaction object.
     */
    @Override
    public Transaction makeTransaction(Address sender, Address receiver, int value, BigInteger valueProof,int blockValueProof) {
        String transaction = sender.toString()+receiver.toString()+value+valueProof.toString();
        BigInteger signature = sender.getCryptoSystem().sign(privateKey,hashingAlgorithm.hash(transaction));

        return new StandardTransaction(sender,receiver,value,valueProof,signature, blockValueProof);
    }

    public HashingAlgorithm getHashingAlgorithm() {
        return hashingAlgorithm;
    }
}
