package Interfaces.Communication;

import java.net.InetAddress;

public interface Publisher {
    void send(Object object, InetAddress ip, int port);

    void broadCast(Object object);


}
