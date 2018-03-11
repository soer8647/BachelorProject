package Impl.Transactions;

import Crypto.Impl.RSAPublicKey;
import Crypto.Interfaces.PublicKey;
import Impl.PublicKeyAddress;
import Interfaces.Address;
import Interfaces.CoinBaseTransaction;

public class StandardCoinBaseTransaction implements CoinBaseTransaction {


    private Address minerAddress;
    private int value;
    private int blockNumber;

    public StandardCoinBaseTransaction(Address minerAddress, int value, int blockNumber) {
        this.minerAddress = minerAddress;
        this.value = value;
        this.blockNumber = blockNumber;
    }

    /**
     * @param data      Given a string that would be made from the objects toString method, turn it into an object again.
     */
    public StandardCoinBaseTransaction(String data) {
        try {
            String publickey = data.substring(data.indexOf("["), data.indexOf("]"));
            PublicKey key = new RSAPublicKey(publickey);
            String valueString = data.substring(data.indexOf("{(")+2, data.indexOf(")}"));
            value = Integer.valueOf(valueString);
            minerAddress = new PublicKeyAddress(key);
            String blocknumber = data.substring(data.indexOf("blockNumber=")+12,data.length());
            blockNumber=Integer.valueOf(blocknumber);
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
    public int getBlockNumber() {
        return blockNumber;
    }

    @Override
    public String toString() {
        return "SCBTransaction:" +
                "address=" + minerAddress +
                ",value={(" + value+")}"+
                ",blockNumber="+blockNumber;
    }
}
