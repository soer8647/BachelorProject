package Impl.Communication;

import External.Pair;
import Impl.Communication.Events.TransactionEvent;
import Interfaces.Account;
import Interfaces.Address;
import Interfaces.Communication.AccountRunner;
import Interfaces.Communication.Event;
import Interfaces.Communication.EventHandler;
import Interfaces.Transaction;

import java.math.BigInteger;
import java.net.InetAddress;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class StandardAccountRunner implements AccountRunner {

    private Account account;
    private LinkedBlockingQueue<Event> outGoingEventQueue;
    private CopyOnWriteArrayList<Transaction> transactionHistory;
    private EventHandler eventHandler;
    private Collection<Pair<InetAddress,Integer>> nodeIpAndPortCollection;


    public StandardAccountRunner(Account account, LinkedBlockingQueue<Event> outGoingEventQueue, Collection<Pair<InetAddress, Integer>> nodeIpAndPortCollection,int listeningPort) {
        this.account = account;
        this.outGoingEventQueue = outGoingEventQueue;
        this.nodeIpAndPortCollection = nodeIpAndPortCollection;
        transactionHistory = new CopyOnWriteArrayList<>();
        eventHandler = new AccountEventHandler(transactionHistory,outGoingEventQueue,listeningPort,nodeIpAndPortCollection);
    }

    public StandardAccountRunner(Account account, CopyOnWriteArrayList<Transaction> transactionHistory, Collection<Pair<InetAddress, Integer>> nodeIpAndPortCollection,int listeningPort) {
        this.account = account;
        this.transactionHistory = transactionHistory;
        this.outGoingEventQueue = new LinkedBlockingQueue<>();
        this.eventHandler = new AccountEventHandler(transactionHistory,outGoingEventQueue,listeningPort,nodeIpAndPortCollection);
        this.nodeIpAndPortCollection = nodeIpAndPortCollection;
    }

    @Override
    public Account getAccount() {
        return account;
    }

    @Override
    public Collection<Transaction> getTransactionHistory() {
        return transactionHistory;
    }

    @Override
    public int getBalance() {
        int amount = 0;
        for (Transaction t:getTransactionHistory()){
            if (t.getReceiverAddress().toString().equals(account.getAddress().toString())) amount+=t.getValue();
            else if(t.getSenderAddress().toString().equals(account.getAddress().toString())) amount-=t.getValue();

        }
        return amount;
    }

    /**
     * The AccountRunner broadcasts the transaction to all the known nodes
     *
     * @param address The address of the receiver
     * @param value   The value to send
     */
    @Override
    public void makeTransaction(Address address, int value) {
        try {
        Pair<BigInteger,Integer> proof = getValueProof(value);
            for (Pair<InetAddress,Integer> p:nodeIpAndPortCollection) {
                outGoingEventQueue.put(new TransactionEvent(account.makeTransaction(account.getAddress(), address, value, proof.getKey(), proof.getValue()), p.getValue(),p.getKey()));
            }
        } catch (NotEnoughMoneyException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            //TODO Make transaction again?
            e.printStackTrace();
        }
    }

    public Pair<BigInteger, Integer> getValueProof(int value) throws NotEnoughMoneyException {
        int bal = getBalance();
        Object[] th = getTransactionHistory().toArray();
        int counter =0;
        for (int i =th.length-1;i>=0;i--){
            Transaction t = (Transaction)th[i];
            if (t.getReceiverAddress().toString().equals(account.getAddress().toString())){
                //gets money
                counter+=t.getValue();
                //spends money
                if (counter>=bal) return new Pair<>(t.getValueProof(),t.getBlockNumberOfValueProof());
            }
            else if(t.getSenderAddress().toString().equals(account.getAddress().toString())){
                counter-=t.getValue();
            }
        }
        throw new NotEnoughMoneyException();
    }

    @Override
    public EventHandler getEventHandler() {
        return eventHandler;
    }

    public LinkedBlockingQueue<Event> getOutGoingEventQueue() {
        return outGoingEventQueue;
    }

    public Collection<Pair<InetAddress, Integer>> getNodeIpAndPortCollection() {
        return nodeIpAndPortCollection;
    }
}
