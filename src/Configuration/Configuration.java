package Configuration;

import Crypto.Impl.RSA;
import Crypto.Interfaces.PublicKeyCryptoSystem;
import Impl.Hashing.SHA256;
import Interfaces.HashingAlgorithm;

import java.math.BigInteger;

public class Configuration {

    private static Configuration configuration = new Configuration();

    //The blockreward for mining a block
    private static int blockReward = 10;
    private static int hardnessParameter = 14;
    private static int keyBitLength = 500;
    private static PublicKeyCryptoSystem cryptoSystem = new RSA(keyBitLength);
    private static int transactionLimit=10;
    private static HashingAlgorithm hasher = new SHA256();
    private static int max_package_size = 8192;

    /**
     * @return The corresponding Biginteger of a hash
     */
    public static BigInteger hash(String data) {
        return hasher.hash(data);
    }


    /**
     * @return  The size of the value that the hash function outputs
     */
    public static int getBitSize() {
        return hasher.getBitSize();
    }

    public static int getBlockReward() {
        return blockReward;
    }

    public static int getHardnessParameter() {
        return hardnessParameter;
    }

    public static int getKeyBitLength() {
        return keyBitLength;
    }

    public static PublicKeyCryptoSystem getCryptoSystem() {
        return cryptoSystem;
    }

    public static void setHardnessParameter(int hardnessParameter) {
        Configuration.hardnessParameter = hardnessParameter;
    }

    public static int getTransactionLimit() {
        return transactionLimit;
    }

    public static int getMax_package_size() {
        return max_package_size;
    }
}
