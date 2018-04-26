package Impl.Communication;

import External.Pair;
import Impl.Communication.Events.TransactionEvent;
import Impl.Communication.Events.TransactionHistoryRequestEvent;
import Impl.Communication.UDP.UDPConnectionData;
import Impl.TransactionHistory;
import Impl.Transactions.ConfirmedTransaction;
import Impl.Transactions.IllegalTransactionException;
import Impl.Transactions.PendingTransaction;
import Interfaces.*;
import Interfaces.Communication.AccountRunner;
import Interfaces.Communication.Event;
import Interfaces.Communication.EventHandler;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

public class StandardAccountRunner implements AccountRunner {

    private Account account;
    private LinkedBlockingQueue<Event> eventQueue;
    private TransactionHistory transactionHistory;
    private AccountEventHandler eventHandler;
    private Collection<UDPConnectionData> udpConnectionsData;
    private int listeningPort;
    private Map<BigInteger,PendingTransaction> pendingTransactionMap;
    private List<Transaction> discardedTransactions;

    public StandardAccountRunner(Account account, LinkedBlockingQueue<Event> eventQueue, List<UDPConnectionData> connectionsData, int listeningPort) {
        this.listeningPort=listeningPort;
        this.account = account;
        this.eventQueue = eventQueue;
        this.udpConnectionsData = connectionsData;
        discardedTransactions = new ArrayList<>();
        pendingTransactionMap = new HashMap<>();
        transactionHistory = new TransactionHistory(new CopyOnWriteArrayList<>(),new CopyOnWriteArrayList<>());
        eventHandler = new AccountEventHandler(transactionHistory, eventQueue,listeningPort,connectionsData,pendingTransactionMap);
        eventHandler.start();
    }

    public StandardAccountRunner(Account account, TransactionHistory transactionHistory, List<UDPConnectionData> udpConnectionsData, int listeningPort) {
        this.listeningPort=listeningPort;
        this.account = account;
        this.transactionHistory = transactionHistory;
        this.eventQueue = new LinkedBlockingQueue<>();
        this.eventHandler = new AccountEventHandler(transactionHistory, eventQueue,listeningPort, udpConnectionsData,pendingTransactionMap);
        this.udpConnectionsData = udpConnectionsData;
        discardedTransactions = new ArrayList<>();
        pendingTransactionMap = new HashMap<>();
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
        try {
            transactionHistory.getSemaphore().acquire();
            int amount = 0;
            for (Transaction t : getTransactionHistory().getConfirmedTransactions()) {
                if (t.getReceiverAddress().toString().equals(account.getAddress().toString())) amount += t.getValue();
                else if (t.getSenderAddress().toString().equals(account.getAddress().toString()))
                    amount -= t.getValue();
            }
            for (CoinBaseTransaction c : getTransactionHistory().getCoinBaseTransactions()) {
                amount += c.getValue();
            }
            transactionHistory.getSemaphore().release();
            return amount;
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * The AccountRunner broadcasts the transaction to all the known nodes, and puts the transaction in the pending map.
     *
     * @param address The address of the receiver
     * @param value   The value to send
     */
    @Override
    public void makeTransaction(Address address, int value) throws NotEnoughMoneyException {
        try {
            int timestamp = eventHandler.getBlockTime();
            Pair<BigInteger,Integer> proof = getValueProof(value);
            Transaction transaction = account.makeTransaction(address, value, timestamp);
            pendingTransactionMap.put(transaction.transactionHash(),new PendingTransaction(transaction,LocalDateTime.now()));
            for (UDPConnectionData d: udpConnectionsData) {
                eventQueue.put(new TransactionEvent(transaction, d.getPort(),d.getInetAddress()));
            }
        } catch (InterruptedException |IllegalTransactionException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param value The value to find proof of funds for.
     * @return      A pair of the hash of the transaction and the block number, where the proof of funds are.
     * @throws      NotEnoughMoneyException     If there is not enough funds.
     * @throws      IllegalTransactionException If an illegal transaction is encountered in the history.
     */
    public Pair<BigInteger, Integer> getValueProof(int value) throws NotEnoughMoneyException, IllegalTransactionException {
        int bal = getBalance();
        int counter =0;

        ArrayList<VerifiableTransaction> transactions = new ArrayList<>();
        transactions.addAll(getTransactionHistory().getCoinBaseTransactions());
        transactions.addAll(getTransactionHistory().getConfirmedTransactions());

        //Sort the list on block number
        transactions.sort(Comparator.comparing(VerifiableTransaction::getBlockNumber));
        for (VerifiableTransaction vt : transactions){
            System.out.println(vt.getBlockNumber());
            if(vt instanceof CoinBaseTransaction){
                counter+=vt.getValue();
                if (counter>=bal) return new Pair<>(new BigInteger("0"),vt.getBlockNumber());
            }else if (vt instanceof ConfirmedTransaction){
                ConfirmedTransaction confirmed = (ConfirmedTransaction)vt;
                // Account gets money
                if(confirmed.getReceiverAddress().toString().equals(account.getAddress().toString())){
                    counter+=confirmed.getValue();
                    if (counter>=bal) return new Pair<>(confirmed.transactionHash(),vt.getBlockNumber());
                }else if(confirmed.getSenderAddress().toString().equals(account.getAddress().toString())){
                // Account spends money
                    counter-=confirmed.getValue();
                }else{
                    throw new IllegalTransactionException();
                }
            }
        }
        throw new NotEnoughMoneyException("Not enough money to get a value proof");
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
            eventQueue.put(new TransactionHistoryRequestEvent(InetAddress.getLocalHost(),  listeningPort,transactionHistory.getBlocknumber()+1,account.getAddress()));
        } catch (InterruptedException | UnknownHostException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void updatePendingTransactions() {
        List<BigInteger> discarded =pendingTransactionMap.keySet().stream().filter(t->pendingTransactionMap.get(t).getTime().isBefore(LocalDateTime.now().minusMinutes(1))).collect(Collectors.toList());
        // Collect all discarded transactions
        discardedTransactions = discardedTransactions.stream().map(t->pendingTransactionMap.get(t).getTransaction()).collect(Collectors.toList());

        pendingTransactionMap.keySet().forEach(t->{if(discarded.contains(t)){
            pendingTransactionMap.remove(t);
        }});
        
    }


    public Collection<UDPConnectionData> getUdpConnectionsData() {
        return udpConnectionsData;
    }
}
