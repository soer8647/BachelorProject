package Impl.Communication;

import Impl.Communication.Events.TransactionEvent;
import Interfaces.Account;
import Interfaces.Address;
import Interfaces.Communication.AccountRunner;
import Interfaces.Communication.Event;
import Interfaces.Communication.EventHandler;
import Interfaces.Transaction;
import javafx.util.Pair;

import java.math.BigInteger;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;

public class StandardAccountRunner implements AccountRunner {

    private Account account;
    private LinkedBlockingQueue<Event> transactionQueue;
    private Collection<Transaction> transactionHistory;
    private int balance;
    private EventHandler eventHandler;
    private Collection<Pair<InetAddress,Integer>> nodeIpAndPortCollection;


    public StandardAccountRunner(Account account, LinkedBlockingQueue transactionQueue, Collection<Pair<InetAddress, Integer>> nodeIpAndPortCollection) {
        this.account = account;
        this.transactionQueue = transactionQueue;
        this.nodeIpAndPortCollection = nodeIpAndPortCollection;
        eventHandler = new AccountEventHandler();
        transactionHistory = new ArrayList<>();
        balance = getBalance();
    }

    public StandardAccountRunner(Account account, Collection<Transaction> transactionHistory, Collection<Pair<InetAddress, Integer>> nodeIpAndPortCollection) {
        this.account = account;
        this.transactionHistory = transactionHistory;
        this.eventHandler = new AccountEventHandler();
        this.nodeIpAndPortCollection = nodeIpAndPortCollection;
        balance = getBalance();
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
                eventHandler.handleOutGoingEvent(new TransactionEvent(account.makeTransaction(account.getAddress(), address, value, proof.getKey(), proof.getValue()), p.getValue(),p.getKey()));
            }
        } catch (NotEnoughMoneyException e) {
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
                System.out.println(counter>=bal);
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
}
