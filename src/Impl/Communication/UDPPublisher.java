package Impl.Communication;

import Impl.Communication.Events.ReceivedBlockEvent;
import Impl.Communication.Events.RequestEvent;
import Impl.Communication.Events.RequestedEvent;
import Impl.Communication.Events.TransactionHistoryResponseEvent;
import Impl.TransactionHistory;
import Impl.Transactions.ConfirmedTransaction;
import Interfaces.Block;
import Interfaces.CoinBaseTransaction;
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
    private InetAddress localAdress;
    private int localPort;

    public UDPPublisher(InetAddress localAdress, int localPort, InetAddress[] ip, int[] port) {
        this.localAdress = localAdress;
        this.localPort = localPort;
        if (ip.length != port.length) {
            throw new IllegalArgumentException();
        }
        this.ips = ip;
        this.ports = port;
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void publishBlock(Block block) {
        Event event = new ReceivedBlockEvent(block, localPort, localAdress);
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(outputStream);
            os.writeObject(event);
            byte[] data = outputStream.toByteArray();
            for (int i = 0; i < ips.length; i++) {
                InetAddress ip = this.ips[i];
                int port = this.ports[i];
                DatagramPacket sendPacket = new DatagramPacket(data, data.length, ip, port);
                socket.send(sendPacket);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void sendTransactionHistoryEvent(TransactionHistoryResponseEvent event, InetAddress inetAddress, int port){
        try{
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(outputStream);
        os.writeObject(event);
        byte[] data = outputStream.toByteArray();
        //TODO MAYBE NOT HARDCODE THIS
        if (data.length>8192){
            int splits = data.length / 8192;
            List<ConfirmedTransaction> ctl = event.getTransactions().getConfirmedTransactions();
            List<CoinBaseTransaction> cbl = event.getTransactions().getCoinBaseTransactions();
            int splitSizeClt = ctl.size()/splits;
            int splitSizeCbl = cbl.size()/splits;
            for (int i = 0;i<=splits;i++ ){
                List<ConfirmedTransaction> ctlst = null;
                if (i==splits-1){
                    ctlst = ctl.subList(i*splitSizeClt,ctl.size()-1);
                }else {
                    ctlst = ctl.subList(i *splitSizeClt,(i+1)*splitSizeClt);
                }
                List<CoinBaseTransaction> cblst = null;
                if (i==splits-1){
                    cblst =cbl.subList(i*splitSizeCbl,cbl.size()-1);
                }else {
                    cblst = cbl.subList(i*splitSizeCbl,(i+1)*splitSizeCbl);
                }
                sendEvent(new TransactionHistoryResponseEvent(inetAddress,port,new TransactionHistory(ctlst,cblst),event.getIndex(),i,splits, LocalDateTime.now()),inetAddress,port);
            }

        }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    @Override
    public void sendTransactionHistoryResponse(TransactionHistory transactionHistory, LocalDateTime time, int index, int part, int parts, InetAddress ip, int port) {
        TransactionHistoryResponseEvent event = new TransactionHistoryResponseEvent(localAdress,localPort,transactionHistory,index,part,parts, time);
        sendEvent(event,ip,port);
    }

    @Override
    public void requestBlock(int number, InetAddress ip, int port) {
        Event event = new RequestEvent(number, localPort, localAdress);
        sendEvent(event,ip,port);
    }

    @Override
    public void requestMaxBlock(InetAddress ip, int port) {
        requestBlock(-1,ip,port);
    }

    @Override
    public void answerRequest(Block block, InetAddress ip, int port) {
        Event event = new RequestedEvent(block,localPort,localAdress);
        sendEvent(event,ip,port);
    }
}