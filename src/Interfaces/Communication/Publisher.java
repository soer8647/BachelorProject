package Interfaces.Communication;

import java.net.InetAddress;

public interface Publisher {
    void sendEvent(Event event, InetAddress ip, int port);

    void broadCastEvent(Event event);


}
