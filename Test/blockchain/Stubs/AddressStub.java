package blockchain.Stubs;

import Configuration.Configuration;
import Crypto.Impl.RSA;
import Crypto.Interfaces.PublicKey;
import Crypto.Interfaces.PublicKeyCryptoSystem;
import Interfaces.Address;

import java.math.BigInteger;

public class AddressStub implements Address {

    @Override
    public PublicKey getPublicKey() {
        return null;
    }
}
