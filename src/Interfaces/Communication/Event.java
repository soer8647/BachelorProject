package Interfaces.Communication;

import java.io.Serializable;
import java.net.InetAddress;

public interface Event extends Serializable{

    int getPort();

    InetAddress getIp();
}
