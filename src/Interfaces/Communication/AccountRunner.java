package Interfaces.Communication;

import External.Pair;
import Impl.Communication.NotEnoughMoneyException;
import Impl.TransactionHistory;
import Impl.Transactions.IllegalTransactionException;
import Impl.Transactions.PendingTransaction;
import Interfaces.Account;
import Interfaces.Address;

import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

public interface AccountRunner {

    /**
     * @return      The account that is linked to this runner.
     */
    Account getAccount();

    /**
     * @return      A collection of all transactions for the linked account.
     */
    TransactionHistory getTransactionHistory();

    Map<BigInteger, PendingTransaction> getPendingTransactionMap();

    /**
     * @return      The balance of the linked account.
     */
    int getBalance();

    /**
     * @param address       The address of teh receiver
     * @param value         The value to send
     */
    void makeTransaction(Address address,int value) throws NotEnoughMoneyException;

    EventHandler getEventHandler();

    /**
     * @param value         The value to find proof of funds for
     * @return              A pair on a transaction hash and a blocknumber.
     * @throws NotEnoughMoneyException  If there is not enough funds, this exception is thrown.
     */
    Pair<BigInteger, Integer> getValueProof(int value) throws NotEnoughMoneyException, IllegalTransactionException;

    LinkedBlockingQueue<Event> getEventQueue();

    /**
     * Send a request to the network to update the history of an account.
     */
    void updateTransactionHistory();

    void updatePendingTransactions();
}
