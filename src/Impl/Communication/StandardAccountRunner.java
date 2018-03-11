package Impl.Communication;

import External.Pair;
import Impl.Communication.Events.TransactionEvent;
import Impl.Communication.Events.TransactionHistoryRequestEvent;
import Impl.TransactionHistory;
import Impl.Transactions.ConfirmedTransaction;
import Impl.Transactions.IllegalTransactionException;
import Interfaces.*;
import Interfaces.Communication.AccountRunner;
import Interfaces.Communication.Event;
import Interfaces.Communication.EventHandler;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class StandardAccountRunner implements AccountRunner {

    private Account account;
    private LinkedBlockingQueue<Event> eventQueue;
    private TransactionHistory transactionHistory;
    private AccountEventHandler eventHandler;
    private Collection<Pair<InetAddress,Integer>> nodeIpAndPortCollection;
    private int listeningPort;

    public StandardAccountRunner(Account account, LinkedBlockingQueue<Event> eventQueue, Collection<Pair<InetAddress, Integer>> nodeIpAndPortCollection, int listeningPort) {
        this.listeningPort=listeningPort;
        this.account = account;
        this.eventQueue = eventQueue;
        this.nodeIpAndPortCollection = nodeIpAndPortCollection;
        transactionHistory = new TransactionHistory(new CopyOnWriteArrayList<>(),new CopyOnWriteArrayList<>());
        eventHandler = new AccountEventHandler(transactionHistory, eventQueue,listeningPort,nodeIpAndPortCollection);
        eventHandler.start();
    }

    public StandardAccountRunner(Account account,TransactionHistory transactionHistory, Collection<Pair<InetAddress, Integer>> nodeIpAndPortCollection,int listeningPort) {
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
    public TransactionHistory getTransactionHistory() {
        return transactionHistory;
    }

    @Override
    public int getBalance() {
        int amount = 0;
        for (Transaction t:getTransactionHistory().getConfirmedTransactions()){
            if (t.getReceiverAddress().toString().equals(account.getAddress().toString())) amount+=t.getValue();
            else if(t.getSenderAddress().toString().equals(account.getAddress().toString())) amount-=t.getValue();
        }
        for (CoinBaseTransaction c:getTransactionHistory().getCoinBaseTransactions()){
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
        } catch (NotEnoughMoneyException | InterruptedException |IllegalTransactionException e) {
            e.printStackTrace();
        }
    }

    public Pair<BigInteger, Integer> getValueProof(int value) throws NotEnoughMoneyException, IllegalTransactionException {
        int bal = getBalance();
        Object[] th = getTransactionHistory().getConfirmedTransactions().toArray();
        Object[] cb = getTransactionHistory().getCoinBaseTransactions().toArray();
        int counter =0;

        ArrayList<VerifiableTransaction> transactions = new ArrayList<>();
        transactions.addAll(getTransactionHistory().getCoinBaseTransactions());
        transactions.addAll(getTransactionHistory().getConfirmedTransactions());

        //Sort the list on block number
        transactions.sort(Comparator.comparing(VerifiableTransaction::getBlockNumber));
        for (VerifiableTransaction vt : transactions){
            if(vt instanceof CoinBaseTransaction){
                counter+=vt.getValue();
                if (counter>=bal) return new Pair<>(new BigInteger("0"),vt.getBlockNumber());
            }else if (vt instanceof ConfirmedTransaction){
                ConfirmedTransaction confirmed = (ConfirmedTransaction)vt;
                // Account gets money
                if(confirmed.getReceiverAddress().toString().equals(account.getAddress().toString())){
                    counter+=confirmed.getValue();
                    if (counter>=bal) return new Pair<>(confirmed.transActionHash(),vt.getBlockNumber());
                }else if(confirmed.getSenderAddress().toString().equals(account.getAddress().toString())){
                // Account spends money
                    counter-=confirmed.getValue();
                }else{
                    throw new IllegalTransactionException();
                }
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
            eventQueue.put(new TransactionHistoryRequestEvent(InetAddress.getLocalHost(),  listeningPort,transactionHistory.getConfirmedTransactions().size()+transactionHistory.getCoinBaseTransactions().size(),account.getAddress()));
        } catch (InterruptedException | UnknownHostException e) {
            e.printStackTrace();
        }

    }

    public Collection<Pair<InetAddress, Integer>> getNodeIpAndPortCollection() {
        return nodeIpAndPortCollection;
    }
}
