package blockchain.Stubs;

import Impl.Communication.UDP.UDPPublisherNode;
import Impl.TransactionHistory;
import Interfaces.Block;

import java.net.InetAddress;
import java.time.LocalDateTime;

public class FauxPublisher extends UDPPublisherNode {
    private FauxReceiver receiver;

    public FauxPublisher(FauxReceiver receiver) {
        super(null,0,null);
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
    public void send(Object object, InetAddress ip, int port) {

    }

    @Override
    public void sendTransactionHistoryResponse(TransactionHistory transactionHistory, LocalDateTime time, int index, int part, int parts, InetAddress ip, int port) {

    }
}
