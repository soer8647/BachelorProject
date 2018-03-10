package Impl.Communication;

import External.Pair;
import Impl.Communication.Events.TransactionEvent;
import Impl.Communication.Events.TransactionHistoryRequestEvent;
import Impl.Communication.Events.TransactionHistoryResponseEvent;
import Impl.TransactionHistory;
import Interfaces.Communication.Event;
import Interfaces.Communication.EventHandler;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.net.InetAddress;
import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;

public class AccountEventHandler implements EventHandler,Runnable{

    private LinkedBlockingQueue<Event> eventQueue;
    private TransactionHistory transactionHistory;
    private int port;
    //TODO release resources from receiver
    private UDPReceiver receiver;
    private UDPEventPublisher publisher;
    private Thread t;



    public AccountEventHandler(TransactionHistory transactionHistory, LinkedBlockingQueue<Event> eventQueue, int portNumber, Collection<Pair<InetAddress, Integer>> nodeIpAndPortCollection) {
        this.transactionHistory = transactionHistory;
        port = portNumber;
        receiver = new UDPReceiver(eventQueue,port);
        publisher = new UDPEventPublisher(nodeIpAndPortCollection);
        this.eventQueue=eventQueue;
    }

    public void handleEvent(Event event){
        if (event instanceof TransactionHistoryResponseEvent){
            TransactionHistoryResponseEvent the = (TransactionHistoryResponseEvent)event;
            TransactionHistory th = the.getTransactions();
            if (transactionHistory.getConfirmedTransactions().size()+transactionHistory.getCoinBaseTransactions().size()==the.getIndex()){
                transactionHistory.getConfirmedTransactions().addAll(th.getConfirmedTransactions());
                transactionHistory.getCoinBaseTransactions().addAll(th.getCoinBaseTransactions());
            }else {
                throw new NotImplementedException();
                //TODO find out how to merge history. Idea : save history and merge when the full history is seen.
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
