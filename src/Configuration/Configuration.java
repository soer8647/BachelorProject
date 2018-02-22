package Configuration;

public class Configuration {
    private static Configuration configuration = new Configuration();

    //The blockreward for mining a block
    public static int blockReward = 10;
    public static int hardnessParameter = 10;

    public static Configuration getConfiguration() {
        return configuration;
    }

    public static int getBlockReward() {
        return blockReward;
    }


}
