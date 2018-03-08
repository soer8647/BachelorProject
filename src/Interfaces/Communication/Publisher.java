package Interfaces.Communication;

import Interfaces.Block;

import java.net.InetAddress;

public interface Publisher {
    void publishBlock(Block block);

    void requestBlock(int number, InetAddress ip, int port);

    void requestMaxBlock(InetAddress ip, int port);

    void answerRequest(Block block, InetAddress ip, int port);

    void sendEvent(Event event, InetAddress ip, int port);
}
