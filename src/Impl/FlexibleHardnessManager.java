package Impl;

import Configuration.Configuration;
import Interfaces.HardnessManager;

import java.time.Duration;
import java.time.Instant;

public class FlexibleHardnessManager implements HardnessManager {

    private int currentHardness = Configuration.getHardnessParameter();        //TODO: Make this change
    private Duration timeTarget = Duration.ofSeconds(4);
    private Instant lastTime = Instant.now();
    private int buffer;
    private int adjustInterval = 10;
    private int counter = 0;
    private int allCounter = 0;

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
            System.out.println( (double) (timeElapsed.toMillis()) / 1000);
            if (timeElapsed.compareTo(timeTarget) < 0) {
                currentHardness++;
            } else {
                currentHardness--;
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
}
