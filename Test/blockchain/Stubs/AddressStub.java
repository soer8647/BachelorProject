package blockchain.Stubs;

import Crypto.Interfaces.PublicKeyCryptoSystem;
import Interfaces.Address;

public class AddressStub implements Address {
    @Override
    public PublicKeyCryptoSystem getCryptoSystem() {
        return null;
    }
}
