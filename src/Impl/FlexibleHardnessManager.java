package Impl;

import Configuration.Configuration;
import Interfaces.HardnessManager;

import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;

public class FlexibleHardnessManager implements HardnessManager {

    private int currentHardness = Configuration.getHardnessParameter();        //TODO: Make this change
    private Duration timeTarget = Configuration.getHardnessTimeTarget();
    private Instant lastTime = Instant.now();
    private int buffer;
    private int adjustInterval = 10;
    private int counter = 0;
    private int allCounter = 0;
    private BigInteger hardValue = BigInteger.valueOf(2).pow(Configuration.getBitSize()).shiftRight(getHardness());

    @Override
    public void notifyOfMining() {
        if (buffer>0) {
            buffer--;
            return;
        }
        allCounter++;
        if (counter==adjustInterval - 1) {
            Instant thisTime = Instant.now();

            Duration timeElapsed = Duration.between(lastTime, thisTime);
            System.out.println( ((double) (timeElapsed.toMillis()) / 1000 ) + " - > " + (timeTarget.toMillis()/1000) + ", HardValue: " + hardValue );
            if (timeElapsed.compareTo(timeTarget) < 0) {
                currentHardness++;
                hardValue = hardValue.divide(new BigInteger("2"));
            } else {
                currentHardness--;
                hardValue = hardValue.multiply(new BigInteger("2"));
            }
            this.lastTime = thisTime;
            counter = 0;
        } else {
            counter++;
        }
    }

    @Override
    public int getHardness() {
        return this.currentHardness;
    }

    @Override
    public void notifyOfRemoved() {
        this.buffer++;
    }

    @Override
    public BigInteger getHardValue() {
        return hardValue;
    }
}
