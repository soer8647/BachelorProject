package blockchain.Stubs;

import Impl.TransactionHistory;
import Interfaces.Block;
import Interfaces.Communication.Event;
import Interfaces.Communication.Publisher;

import java.net.InetAddress;
import java.time.LocalDateTime;

public class FauxPublisher implements Publisher {
    private FauxReceiver receiver;

    public FauxPublisher(FauxReceiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void publishBlock(Block block) {
        receiver.receiveBlock(block);
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
