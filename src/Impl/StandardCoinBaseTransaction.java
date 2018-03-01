package Impl;

import Crypto.Impl.RSAPublicKey;
import Crypto.Interfaces.PublicKey;
import Interfaces.Address;
import Interfaces.CoinBaseTransaction;

public class StandardCoinBaseTransaction implements CoinBaseTransaction {


    private Address minerAddress;
    private int value;

    public StandardCoinBaseTransaction(Address minerAddress, int value) {
        this.minerAddress = minerAddress;
        this.value = value;
    }

    /**
     * @param data      Given a string that would be made from the objects toString method, turn it into an object again.
     */
    public StandardCoinBaseTransaction(String data) {
        try {
            String publickey = data.substring(data.indexOf("["), data.indexOf("]"));
            PublicKey key = new RSAPublicKey(publickey);
            String valueString = data.substring(data.indexOf("value=") + 6, data.length());
            value = Integer.valueOf(valueString);
            minerAddress = new PublicKeyAddress(key);
        }catch (StringIndexOutOfBoundsException e){
            System.out.println("ERROR: StandardCoinBaseTransaction can not be made from String: \n"+data);
        }
    }

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public Address getMinerAddress() {
        return minerAddress;
    }

    @Override
    public String toString() {
        return "StandardCoinBaseTransaction:" +
                "minerAddres=" + minerAddress +
                ", value=" + value;
    }
}
