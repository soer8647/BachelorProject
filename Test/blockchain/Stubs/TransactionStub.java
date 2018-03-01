package blockchain.Stubs;

import Crypto.Impl.RSA;
import Crypto.Impl.RSAKeyPair;
import Crypto.Interfaces.KeyPair;
import Crypto.Interfaces.PublicKey;
import Crypto.Interfaces.PublicKeyCryptoSystem;
import Impl.PublicKeyAddress;
import Interfaces.Address;
import Interfaces.Transaction;

import java.math.BigInteger;

public class TransactionStub implements Transaction{

    private final KeyPair keypair;
    private PublicKeyAddress pk;


    public TransactionStub() {
        PublicKeyCryptoSystem rsa = new RSA(500);
        keypair = rsa.generateNewKeys(new BigInteger("3"));
        pk = new PublicKeyAddress(keypair.getPublicKey());
    }

    @Override
    public BigInteger transActionHash() {
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
    public BigInteger getValueProof() {
        return new BigInteger("8008");
    }

    @Override
    public int getBlockNumberOfValueProof() {
        return 0;
    }

    @Override
    public BigInteger getSignature() {
        return new BigInteger("8080");
    }
}
