package Crypto;

import Configuration.Configuration;
import Crypto.Impl.FragmentArray;
import Crypto.Impl.Seed;
import Crypto.Impl.WOTS;
import Crypto.Impl.WOTSKeyPair;
import Crypto.Interfaces.KeyPair;

import java.math.BigInteger;

public class WotsParameterExperiment {


    public static void main(String[] args) {
        long start;
        long end;
        long time;
        WOTS wots;
        int topParam = 8;
        int iterations = 100;

        BigInteger messageSeed = new BigInteger("102103201123128");
        BigInteger message = Configuration.hash(messageSeed.toString());

        long[][][] times = new long[3][topParam][iterations];

        for (int j = 0; j < iterations; j++) {
            for (int i = 1; i <= topParam; i++) {
                //System.out.println("n = " + i + ", and Chain length: " + Math.pow(2, i));
                wots = new WOTS(Configuration.getHashingAlgorithm(), i);
                Seed seed = new Seed();

                FragmentArray fragmentArray = new FragmentArray(message, i);

                start = System.nanoTime();
                WOTSKeyPair keys = wots.generateNewKeys(seed, 0, fragmentArray.getLength());
                end = System.nanoTime();
                time = end - start;
                times[0][i - 1][j] = time;


                start = System.nanoTime();
                BigInteger[] signature = wots.sign(keys.getPrivateKey(), message);
                end = System.nanoTime();
                time = end - start;
                times[1][i - 1][j] = time;

                start = System.nanoTime();
                wots.verify(keys.getPublicKey(), signature, message);
                end = System.nanoTime();
                time = end - start;
                times[2][i - 1][j] = time;
            }
        }

        for (int j = 0; j < topParam; j++) {
            for (int i = 0; i < 3; i++) {
                long sum = 0;
                for (int k = 0; k < iterations; k++) {
                    sum += times[i][j][k];
                }
                System.out.print(sum/iterations);
                if (i<2) {
                    System.out.print(",");
                }

            }
            System.out.println();
        }
    }
}
