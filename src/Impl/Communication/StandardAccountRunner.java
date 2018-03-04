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
import java.util.Collection;
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
    }

    @Override
    public Account getAccount() {
        return account;
    }

    @Override
    public Collection<Transaction> getTransactionHistory() {
        return null;
    }

    @Override
    public int getBalance() {
        return 0;
    }

    /**
     * The AccountRunner broadcasts the transaction to all the known nodes
     *
     * @param address The address of the receiver
     * @param value   The value to send
     */
    @Override
    public void makeTransaction(Address address, int value) {
        Pair<BigInteger,Integer> proof = getValueProof(value);
        for (Pair<InetAddress,Integer> p:nodeIpAndPortCollection) {
            eventHandler.handleOutGoingEvent(new TransactionEvent(account.makeTransaction(account.getAddress(), address, value, proof.getKey(), proof.getValue()), p.getValue(),p.getKey()));
        }
    }

    private Pair<BigInteger, Integer> getValueProof(int value) {
        //TODO look in history and find proof
        return new Pair<>(new BigInteger("42"),1);
    }

    @Override
    public EventHandler getEventHandler() {
        return eventHandler;
    }
}
