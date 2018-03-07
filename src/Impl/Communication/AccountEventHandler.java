package Impl.Communication;

import Interfaces.Communication.Event;
import Interfaces.Communication.EventHandler;

public class AccountEventHandler implements EventHandler{
    private int eventCount;


    public AccountEventHandler() {
        eventCount=0;
    }

    @Override
    public void handleIncomingEvent(Event event) {

    }

    @Override
    public void handleOutGoingEvent(Event event) {
        eventCount++;
    }

    @Override
    public int getEventCount() {
        return eventCount;
    }
}
