package Impl.Communication;

import Impl.Communication.Events.*;
import Impl.Communication.UDP.UDPConnectionData;
import Impl.Communication.UDP.UDPPublisher;
import Impl.Communication.UDP.UDPReceiver;
import Impl.TransactionHistory;
import Impl.Transactions.ConfirmedTransaction;
import Interfaces.CoinBaseTransaction;
import Interfaces.Communication.Event;
import Interfaces.Communication.EventHandler;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

/*
* The Event handler for an account in the block chain network. Uses the localhost ip.
* */
public class AccountEventHandler implements EventHandler,Runnable{

    private LinkedBlockingQueue<Event> eventQueue;
    private TransactionHistory transactionHistory;
    private UDPReceiver receiver;
    private UDPPublisher publisher;
    private Thread t;
    private Semaphore semaphore;
    private Map<LocalDateTime,List<TransactionHistory>> historyMap;
    private int blockTime = 0;


    public AccountEventHandler(TransactionHistory transactionHistory, LinkedBlockingQueue<Event> eventQueue, int portNumber, List<UDPConnectionData> connectionsData) {
        this.transactionHistory = transactionHistory;
        receiver = new UDPReceiver(eventQueue, portNumber);
        try{
            InetAddress address= InetAddress.getLocalHost();
            publisher = new UDPPublisher(address, portNumber,connectionsData);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        this.eventQueue=eventQueue;
        semaphore = new Semaphore(1);
        historyMap = new HashMap<>();
    }

    /**
     *
     *
     * @param event     The event to handle.
     */
    public void handleEvent(Event event){
        try {
            semaphore.acquire();
            if (event instanceof TransactionHistoryResponseEvent) {
                TransactionHistoryResponseEvent the = (TransactionHistoryResponseEvent) event;
                TransactionHistory th = the.getTransactions();

                if (transactionHistory.size() == the.getIndex() && the.getParts() == 1) {
                    transactionHistory.getConfirmedTransactions().addAll(th.getConfirmedTransactions());
                    transactionHistory.getCoinBaseTransactions().addAll(th.getCoinBaseTransactions());

                } else if(transactionHistory.size() == the.getIndex()){
                    // Get the histories with the same timestamp
                    List<TransactionHistory> histories = historyMap.get(the.getTime());
                    histories.add(transactionHistory);
                    //If we have all the transactions -> merge and update
                    if (the.getParts() == histories.size()) {
                        ArrayList<ConfirmedTransaction> confirmedTransactions = new ArrayList<>();
                        ArrayList<CoinBaseTransaction> coinBaseTransactionArrayList = new ArrayList<>();
                        for (TransactionHistory t : histories) {
                            confirmedTransactions.addAll(t.getConfirmedTransactions());
                            coinBaseTransactionArrayList.addAll(t.getCoinBaseTransactions());
                        }
                        transactionHistory.getConfirmedTransactions().addAll(confirmedTransactions);
                        transactionHistory.getCoinBaseTransactions().addAll(coinBaseTransactionArrayList);
                    }

                }else{
                    System.out.println("EVENT UPDATE TRANSACTION WAS REJECTED");
                }
            } else if (event instanceof TransactionEvent || event instanceof TransactionHistoryRequestEvent) {
                publisher.broadCast(event);
            } else if (event instanceof ReceivedBlockEvent) {
                blockTime = ((ReceivedBlockEvent) event).getBlock().getBlockNumber();
            }
            semaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        JoinEvent event = new JoinEvent(publisher.getLocalPort(),publisher.getLocalAddress());
        publisher.broadCast(event);
        while (!Thread.currentThread().isInterrupted()){
            try{handleEvent(eventQueue.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void start(){
        t = new Thread(this);
        t.start();
    }

    public int getBlockTime() {
        return blockTime;
    }
}
