package Impl.Communication;

import Configuration.Configuration;
import Interfaces.Communication.HardnessManager;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.Temporal;

public class FlexibleHardnessManager implements HardnessManager {

    private int currentHardness = Configuration.getHardnessParameter();        //TODO: Make this change
    private Duration timeTarget = Duration.ofSeconds(10);
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
            if (timeElapsed.compareTo(timeTarget) < 0) {
                currentHardness++;
                System.out.println("hardness HARDER " + allCounter );
            } else {
                currentHardness--;
                System.out.println("hardness easier " + allCounter );
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
