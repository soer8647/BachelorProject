package Configuration;

import Crypto.Impl.RSA;
import Crypto.Interfaces.PublicKeyCryptoSystem;

public class Configuration {
    private static Configuration configuration = new Configuration();

    //The blockreward for mining a block
    public static int blockReward = 10;
    public static int hardnessParameter = 15;
    public static int keyBitLength = 500;
    private static PublicKeyCryptoSystem cryptoSystem = new RSA(keyBitLength);

    public static Configuration getConfiguration() {
        return configuration;
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
}
