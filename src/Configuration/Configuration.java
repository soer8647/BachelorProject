package Configuration;

import Crypto.Impl.RSA;
import Crypto.Impl.RSAPrivateKey;
import Crypto.Impl.RSAPublicKey;
import Crypto.Interfaces.PublicKey;
import Crypto.Interfaces.PublicKeyCryptoSystem;
import Impl.Hashing.SHA256;
import Impl.PublicKeyAddress;
import Impl.StandardBlock;
import Impl.Transactions.StandardCoinBaseTransaction;
import Interfaces.Block;
import Interfaces.HashingAlgorithm;

import java.math.BigInteger;
import java.time.Duration;
import java.util.ArrayList;

public class Configuration {

    private static Configuration configuration = new Configuration();

    //The blockreward for mining a block
    private static int blockReward = 10;
    private static int hardnessParameter = 14;
    private static int keyBitLength = 500;
    private static PublicKeyCryptoSystem<RSAPublicKey,RSAPrivateKey> cryptoSystem = new RSA(keyBitLength);
    private static int transactionLimit=10;
    private static HashingAlgorithm hasher = new SHA256();
    private static int max_package_size = 8192;
    private static Duration hardnessTimeTarget = Duration.ofSeconds(4);
    /**
     * The number of blocks a transaction needs to be buried under to be valid.
     */
    private static int confirmedTransactionDepth = 6;

    private static PublicKey secretmanKey = new RSAPublicKey(BigInteger.ONE,BigInteger.valueOf(11));
    public static Block genesisblock = new StandardBlock(BigInteger.ZERO,0,BigInteger.ZERO, new ArrayList<>(),0, new StandardCoinBaseTransaction(new PublicKeyAddress(secretmanKey) , getBlockReward(), 0));


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

    public static Duration getHardnessTimeTarget() {
        return hardnessTimeTarget;
    }


    public static void setHardnessTimeTarget(int seconds) {
        hardnessTimeTarget = Duration.ofSeconds(seconds);
    }

    public static int getConfirmedTransactionDepth() {
        return confirmedTransactionDepth;
    }

    public static HashingAlgorithm getHashingAlgorithm() {
        return hasher;
    }
}
