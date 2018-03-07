package Impl.Communication;

import Impl.Communication.Events.TransactionHistoryResponseEvent;
import Interfaces.Communication.Event;
import Interfaces.Communication.EventHandler;
import Interfaces.Transaction;
import javafx.util.Pair;

import java.net.InetAddress;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class AccountEventHandler implements EventHandler{

    private final LinkedBlockingQueue<Event> outGoingEventQueue;
    private final LinkedBlockingQueue<Event> incomingEventQueue;
    private CopyOnWriteArrayList<Transaction> transactionHistory;
    private int port;
    //TODO release resources from receiver
    private UDPReceiver receiver;
    private UDPEventPublisher publisher;
    private boolean running;
    private Thread incoming;
    private Thread outgoing;


    public AccountEventHandler(CopyOnWriteArrayList<Transaction> transactionHistory, LinkedBlockingQueue<Event> outGoingEventQueue, int portNumber, Collection<Pair<InetAddress, Integer>> nodeIpAndPortCollection) {
        this.transactionHistory = transactionHistory;
        port = portNumber;
        incomingEventQueue = new LinkedBlockingQueue<>();
        receiver = new UDPReceiver(incomingEventQueue,port);
        publisher = new UDPEventPublisher(nodeIpAndPortCollection);
        this.outGoingEventQueue = outGoingEventQueue;
        running = true;
        start();
    }

    @Override
    public void handleIncomingEvent(Event event) {
        //TODO RUNNER TAKES EVENT FROM QUEUE
        if (event instanceof TransactionHistoryResponseEvent){
            TransactionHistoryResponseEvent the = (TransactionHistoryResponseEvent)event;
            if (transactionHistory.size()+1==the.getIndex()){
                transactionHistory.addAll(the.getTransactions());
            }else {
                //TODO find out how to merge history
            }
        }else{
            System.out.println("NOT IMPLEMENTED");
        }
    }

    @Override
    public void handleOutGoingEvent(Event event) {
        //TODO RUNNER TAKES FROM QUEUE AND CALLS THIS
        publisher.broadcastEvent(event);
    }

    private void start(){
        incoming = new Thread(()->{
            while (running){
                try {
                    handleIncomingEvent(incomingEventQueue.take());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        incoming.start();
        outgoing = new Thread(()->{
          while (running){
              try {
                  handleOutGoingEvent(outGoingEventQueue.take());
              } catch (InterruptedException e) {
                  e.printStackTrace();
              }
          }
        });
        outgoing.start();
    }

    public void stop(){
        running = false;
        incoming.interrupt();
        outgoing.interrupt();
    }


}
