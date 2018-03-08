package Impl.Communication;

import External.Pair;
import Impl.Communication.Events.TransactionEvent;
import Impl.Communication.Events.TransactionHistoryRequestEvent;
import Impl.Communication.Events.TransactionHistoryResponseEvent;
import Impl.ConfirmedTransaction;
import Interfaces.CoinBaseTransaction;
import Interfaces.Communication.Event;
import Interfaces.Communication.EventHandler;

import java.net.InetAddress;
import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;

public class AccountEventHandler implements EventHandler,Runnable{

    private LinkedBlockingQueue<Event> eventQueue;
    private Pair<Collection<ConfirmedTransaction>,Collection<CoinBaseTransaction>> transactionHistory;
    private int port;
    //TODO release resources from receiver
    private UDPReceiver receiver;
    private UDPEventPublisher publisher;
    private Thread t;



    public AccountEventHandler(Pair<Collection<ConfirmedTransaction>, Collection<CoinBaseTransaction>> transactionHistory, LinkedBlockingQueue<Event> eventQueue, int portNumber, Collection<Pair<InetAddress, Integer>> nodeIpAndPortCollection) {
        this.transactionHistory = transactionHistory;
        port = portNumber;
        receiver = new UDPReceiver(eventQueue,port);
        publisher = new UDPEventPublisher(nodeIpAndPortCollection);
        this.eventQueue=eventQueue;
    }

    public void handleEvent(Event event){
        if (event instanceof TransactionHistoryResponseEvent){
            TransactionHistoryResponseEvent the = (TransactionHistoryResponseEvent)event;
            if (transactionHistory.getKey().size()+transactionHistory.getValue().size()==the.getIndex()){
                //TODO UPDATE HISTORY
                System.out.println("GOT AN UPDATE!!!!!!!!!!");
            }else {
                //TODO find out how to merge history
            }
        }else if (event instanceof TransactionEvent || event instanceof TransactionHistoryRequestEvent){
            publisher.broadcastEvent(event);
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
