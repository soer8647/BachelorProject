package Impl.Communication;

import External.Pair;
import Impl.Communication.Events.TransactionEvent;
import Impl.Communication.Events.TransactionHistoryRequestEvent;
import Impl.ConfirmedTransaction;
import Interfaces.Account;
import Interfaces.Address;
import Interfaces.CoinBaseTransaction;
import Interfaces.Communication.AccountRunner;
import Interfaces.Communication.Event;
import Interfaces.Communication.EventHandler;
import Interfaces.Transaction;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class StandardAccountRunner implements AccountRunner {

    private Account account;
    private LinkedBlockingQueue<Event> eventQueue;
    private Pair<Collection<ConfirmedTransaction>,Collection<CoinBaseTransaction>> transactionHistory;
    private AccountEventHandler eventHandler;
    private Collection<Pair<InetAddress,Integer>> nodeIpAndPortCollection;
    private int listeningPort;

    public StandardAccountRunner(Account account, LinkedBlockingQueue<Event> eventQueue, Collection<Pair<InetAddress, Integer>> nodeIpAndPortCollection, int listeningPort) {
        this.listeningPort=listeningPort;
        this.account = account;
        this.eventQueue = eventQueue;
        this.nodeIpAndPortCollection = nodeIpAndPortCollection;
        transactionHistory = new Pair<>(new CopyOnWriteArrayList<ConfirmedTransaction>(),new CopyOnWriteArrayList<CoinBaseTransaction>());
        eventHandler = new AccountEventHandler(transactionHistory, eventQueue,listeningPort,nodeIpAndPortCollection);
        eventHandler.start();
    }

    public StandardAccountRunner(Account account,Pair<Collection<ConfirmedTransaction>,Collection<CoinBaseTransaction>> transactionHistory, Collection<Pair<InetAddress, Integer>> nodeIpAndPortCollection,int listeningPort) {
        this.listeningPort=listeningPort;
        this.account = account;
        this.transactionHistory = transactionHistory;
        this.eventQueue = new LinkedBlockingQueue<>();
        this.eventHandler = new AccountEventHandler(transactionHistory, eventQueue,listeningPort,nodeIpAndPortCollection);
        this.nodeIpAndPortCollection = nodeIpAndPortCollection;
        eventHandler.start();
    }

    @Override
    public Account getAccount() {
        return account;
    }

    @Override
    public Pair<Collection<ConfirmedTransaction>, Collection<CoinBaseTransaction>> getTransactionHistory() {
        return transactionHistory;
    }

    @Override
    public int getBalance() {
        int amount = 0;
        for (Transaction t:getTransactionHistory().getKey()){
            if (t.getReceiverAddress().toString().equals(account.getAddress().toString())) amount+=t.getValue();
            else if(t.getSenderAddress().toString().equals(account.getAddress().toString())) amount-=t.getValue();
        }
        for (CoinBaseTransaction c:getTransactionHistory().getValue()){
            amount+=c.getValue();
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
                eventQueue.put(new TransactionEvent(account.makeTransaction(account.getAddress(), address, value, proof.getKey(), proof.getValue()), p.getValue(),p.getKey()));
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
        Object[] th = getTransactionHistory().getKey().toArray();
        Object[] cb = getTransactionHistory().getValue().toArray();

        int coinBaseCounter=cb.length-1;
        int counter =0;
        for (int i =th.length-1;i>=0;i--){

            ConfirmedTransaction t = (ConfirmedTransaction)th[i];
            if(coinBaseCounter>=0){
                CoinBaseTransaction cbt = (CoinBaseTransaction)cb[coinBaseCounter];
                if (t.getBlockNumber()<=cbt.getBlockNumber()){
                    counter+=cbt.getValue();
                    if (counter>=bal) return new Pair<>(BigInteger.ZERO,cbt.getBlockNumber());
                }
            }
            //TODO we can have more coinbasetransactions in between transactions

            if (t.getReceiverAddress().toString().equals(account.getAddress().toString())){
                //gets money
                counter+=t.getValue();
                //spends money
                if (counter>=bal) return new Pair<>(t.transActionHash(),t.getBlockNumber());
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

    public LinkedBlockingQueue<Event> getEventQueue() {
        return eventQueue;
    }

    @Override
    public void updateTransactionHistory() {
        try {
            eventQueue.put(new TransactionHistoryRequestEvent(InetAddress.getLocalHost(),  listeningPort,transactionHistory.getKey().size()+transactionHistory.getValue().size(),account.getAddress()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } ;
    }

    public Collection<Pair<InetAddress, Integer>> getNodeIpAndPortCollection() {
        return nodeIpAndPortCollection;
    }
}
