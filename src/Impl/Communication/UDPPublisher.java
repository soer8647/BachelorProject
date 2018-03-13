package Impl.Communication;

import Impl.Communication.Events.ReceivedBlockEvent;
import Impl.Communication.Events.RequestEvent;
import Impl.Communication.Events.RequestedEvent;
import Impl.Communication.Events.TransactionHistoryResponseEvent;
import Impl.TransactionHistory;
import Interfaces.Block;
import Interfaces.Communication.Event;
import Interfaces.Communication.Publisher;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.time.LocalDateTime;
import java.util.List;

public class UDPPublisher implements Publisher{
    private InetAddress[] ips;
    private DatagramSocket socket;
    private int[] ports;
    private InetAddress localAddress;
    private int localPort;
    private List<UDPConnectionData> connectionsDataList;

    public UDPPublisher(InetAddress localAddress, int localPort, List<UDPConnectionData> connectionsDataList) {
        this.localAddress = localAddress;
        this.localPort = localPort;
        this.connectionsDataList = connectionsDataList;
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void publishBlock(Block block) {
        Event event = new ReceivedBlockEvent(block, localPort, localAddress);
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(outputStream);
            os.writeObject(event);
            byte[] data = outputStream.toByteArray();
            for (UDPConnectionData d:connectionsDataList){
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
     * Send an event to a ip and port using the UDP protocol.
     *
     * @param event     The event to send
     * @param ip        The ip to send the event to
     * @param port      The port were the receiver is listening
     */
    public void sendEvent(Event event, InetAddress ip, int port) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(outputStream);
            os.writeObject(event);
            byte[] data = outputStream.toByteArray();
            DatagramPacket sendPacket = new DatagramPacket(data, data.length, ip, port);
            socket.send(sendPacket);
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
    @Override
    public void sendTransactionHistoryResponse(TransactionHistory transactionHistory, LocalDateTime time, int index, int part, int parts, InetAddress ip, int port) {
        TransactionHistoryResponseEvent event = new TransactionHistoryResponseEvent(localAddress,localPort,transactionHistory,index,part,parts, time);
        sendEvent(event,ip,port);
    }

    @Override
    public void requestBlock(int number, InetAddress ip, int port) {
        Event event = new RequestEvent(number, localPort, localAddress);
        sendEvent(event,ip,port);
    }

    @Override
    public void requestMaxBlock(InetAddress ip, int port) {
        requestBlock(-1,ip,port);
    }

    @Override
    public void answerRequest(Block block, InetAddress ip, int port) {
        Event event = new RequestedEvent(block,localPort, localAddress);
        sendEvent(event,ip,port);
    }
}