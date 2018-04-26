package blockchain.Stubs;

import Crypto.Impl.RSA;
import Crypto.Interfaces.KeyPair;
import Crypto.Interfaces.PublicKeyCryptoSystem;
import Impl.PublicKeyAddress;
import Interfaces.Address;
import Interfaces.Transaction;

import java.math.BigInteger;

public class TransactionStub implements Transaction{

    private PublicKeyAddress pk;


    public TransactionStub() {
        PublicKeyCryptoSystem rsa = new RSA(500);
        KeyPair keypair = rsa.generateNewKeys(new BigInteger("3"));
        pk = new PublicKeyAddress(keypair.getPublicKey());
    }

    @Override
    public BigInteger transactionHash() {
        return new BigInteger("404");
    }

    @Override
    public Address getSenderAddress() {
        return pk;
    }

    @Override
    public Address getReceiverAddress() {
        return pk;
    }


    @Override
    public int getValue() {
        return 0;
    }

    @Override
    public BigInteger getSignature() {
        return new BigInteger("8080");
    }

    @Override
    public int getTimestamp() {
        return 0;
    }

}
