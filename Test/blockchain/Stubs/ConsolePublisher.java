package blockchain.Stubs;

import Impl.TransactionHistory;
import Interfaces.Block;
import Interfaces.Communication.Event;
import Interfaces.Communication.Publisher;

import java.net.InetAddress;
import java.time.LocalDateTime;

/**
 *  A publisher that just writes the block to the console
 */
public class ConsolePublisher implements Publisher {
    @Override
    public void publishBlock(Block block) {
        System.out.println(block);
    }

    @Override
    public void requestBlock(int number, InetAddress ip, int port) {

    }

    @Override
    public void requestMaxBlock(InetAddress ip, int port) {

    }

    @Override
    public void answerRequest(Block block, InetAddress ip, int port) {

    }

    @Override
    public void sendEvent(Event event, InetAddress ip, int port) {

    }

    @Override
    public void sendTransactionHistoryResponse(TransactionHistory transactionHistory, LocalDateTime time, int index, int part, int parts, InetAddress ip, int port) {

    }
}
