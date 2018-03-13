package Impl.Communication;

import Impl.Communication.Events.ReceivedBlockEvent;
import Impl.Communication.Events.RequestEvent;
import Impl.Communication.Events.RequestedEvent;
import Impl.Communication.Events.TransactionHistoryResponseEvent;
import Impl.TransactionHistory;
import Interfaces.Block;
import Interfaces.Communication.Event;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.time.LocalDateTime;
import java.util.List;

public class UDPPublisherNode extends UDPPublisher{

    public UDPPublisherNode(InetAddress localAddress, int localPort, List<UDPConnectionData> connectionsDataList) {
        super(localAddress,localPort,connectionsDataList);
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }


    public void publishBlock(Block block) {
        Event event = new ReceivedBlockEvent(block, getLocalPort(), getLocalAddress());
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(outputStream);
            os.writeObject(event);
            byte[] data = outputStream.toByteArray();
            for (UDPConnectionData d:getConnectionsDataList()){
                DatagramPacket sendPacket = new DatagramPacket(data, data.length, d.getInetAddress(), d.getPort());
                socket.send(sendPacket);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    /**
     * A response for a requested transaction history, can be split in more parts because of receiver buffer size.
     *
     * @param transactionHistory        The transaction history to send
     * @param time                      The timestamp for this response
     * @param index                     The index from where the history was requested.
     * @param part                      The part out of the total number of parts
     * @param parts                     The total number of parts
     * @param ip                        The ip to send the event to
     * @param port                      The port where the requester is listening for a response.
     */

    public void sendTransactionHistoryResponse(TransactionHistory transactionHistory, LocalDateTime time, int index, int part, int parts, InetAddress ip, int port) {
        TransactionHistoryResponseEvent event = new TransactionHistoryResponseEvent(getLocalAddress(),getLocalPort(),transactionHistory,index,part,parts, time);
        send(event,ip,port);
    }

    public void requestBlock(int number, InetAddress ip, int port) {
        Event event = new RequestEvent(number, getLocalPort(), getLocalAddress());
        send(event,ip,port);
    }

    public void requestMaxBlock(InetAddress ip, int port) {
        requestBlock(-1,ip,port);
    }

    public void answerRequest(Block block, InetAddress ip, int port) {
        Event event = new RequestedEvent(block,getLocalPort(), getLocalAddress());
        send(event,ip,port);
    }
}