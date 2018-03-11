package Impl.Communication;

import External.Pair;
import Impl.Communication.Events.TransactionEvent;
import Impl.Communication.Events.TransactionHistoryRequestEvent;
import Impl.Communication.Events.TransactionHistoryResponseEvent;
import Impl.TransactionHistory;
import Impl.Transactions.ConfirmedTransaction;
import Interfaces.CoinBaseTransaction;
import Interfaces.Communication.Event;
import Interfaces.Communication.EventHandler;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

public class AccountEventHandler implements EventHandler,Runnable{

    private LinkedBlockingQueue<Event> eventQueue;
    private TransactionHistory transactionHistory;
    private int port;
    //TODO release resources from receiver
    private UDPReceiver receiver;
    private UDPEventPublisher publisher;
    private Thread t;
    private Semaphore semaphore;
    private Map<LocalDateTime,List<TransactionHistory>> historyMap;


    public AccountEventHandler(TransactionHistory transactionHistory, LinkedBlockingQueue<Event> eventQueue, int portNumber, Collection<Pair<InetAddress, Integer>> nodeIpAndPortCollection) {
        this.transactionHistory = transactionHistory;
        port = portNumber;
        receiver = new UDPReceiver(eventQueue,port);
        publisher = new UDPEventPublisher(nodeIpAndPortCollection);
        this.eventQueue=eventQueue;
        semaphore = new Semaphore(1);
        historyMap = new HashMap<>();
    }

    public void handleEvent(Event event){
        try {
            semaphore.acquire();
            if (event instanceof TransactionHistoryResponseEvent) {
                TransactionHistoryResponseEvent the = (TransactionHistoryResponseEvent) event;
                TransactionHistory th = the.getTransactions();


                System.out.println("SIZE "+ (transactionHistory.size()) +" and requested: "+the.getIndex());

                if (transactionHistory.size() == the.getIndex() && the.getParts() == 1) {
                    transactionHistory.getConfirmedTransactions().addAll(th.getConfirmedTransactions());
                    transactionHistory.getCoinBaseTransactions().addAll(th.getCoinBaseTransactions());
                    System.out.println("ADDING WITHOUT MERGING");

                } else if(transactionHistory.size() == the.getIndex()){
                    // Get the histories with the same timestamp
                    //TODO have a map<time,List<TH>>
                    List<TransactionHistory> histories = historyMap.get(the.getTime());
                    histories.add(transactionHistory);
                    //If we have all the transactions
                    if (the.getParts() == histories.size()) {
                        System.out.println("MERGING");
                        ArrayList<ConfirmedTransaction> confirmedTransactions = new ArrayList<>();
                        ArrayList<CoinBaseTransaction> coinBaseTransactionArrayList = new ArrayList<>();
                        for (TransactionHistory t : histories) {
                            confirmedTransactions.addAll(t.getConfirmedTransactions());
                            coinBaseTransactionArrayList.addAll(t.getCoinBaseTransactions());
                        }
                        transactionHistory.getConfirmedTransactions().addAll(confirmedTransactions);
                        transactionHistory.getCoinBaseTransactions().addAll(coinBaseTransactionArrayList);
                    }

                    //TODO find out how to merge history. Idea : save history and merge when the full history is seen.
                }else{
                    System.out.println("EVENT UPDATETRANSACTION WAS REJECTED");
                }
            } else if (event instanceof TransactionEvent || event instanceof TransactionHistoryRequestEvent) {
                publisher.broadcastEvent(event);
            }
            semaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
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
}
