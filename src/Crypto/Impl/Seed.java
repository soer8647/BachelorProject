package Crypto.Impl;

import java.security.SecureRandom;
import java.util.Random;

public class Seed {
    Random random = new SecureRandom();

    public long currentIndex(int index) {
        return 0;
    }

    public long getSubseed(int index) {
        return random.nextLong();
    }
}
