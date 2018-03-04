package Interfaces.Communication;

public interface EventHandler {
    void handleIncomingEvent(Event event);
    void handleOutGoingEvent(Event event);
    int getEventCount();
}
