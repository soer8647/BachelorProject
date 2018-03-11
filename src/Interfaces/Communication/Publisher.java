package Interfaces.Communication;

import Impl.TransactionHistory;
import Interfaces.Block;

import java.net.InetAddress;
import java.time.LocalDateTime;

public interface Publisher {
    void publishBlock(Block block);

    void requestBlock(int number, InetAddress ip, int port);

    void requestMaxBlock(InetAddress ip, int port);

    void answerRequest(Block block, InetAddress ip, int port);

    void sendEvent(Event event, InetAddress ip, int port);

    void sendTransactionHistoryResponse(TransactionHistory transactionHistory, LocalDateTime time, int index, int part, int parts, InetAddress ip, int port);

}
