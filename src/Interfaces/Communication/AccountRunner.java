package Interfaces.Communication;

import Impl.Communication.NotEnoughMoneyException;
import Interfaces.Account;
import Interfaces.Address;
import Interfaces.Transaction;
import javafx.util.Pair;

import java.math.BigInteger;
import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;

public interface AccountRunner {

    /**
     * @return      The account that is linked to this runner.
     */
    Account getAccount();

    /**
     * @return      A collection of all transactions for the linked account.
     */
    Collection<Transaction> getTransactionHistory();

    /**
     * @return      The balance of the linked account.
     */
    int getBalance();

    /**
     * @param address       The address of teh receiver
     * @param value         The value to send
     */
    void makeTransaction(Address address,int value);

    EventHandler getEventHandler();

    Pair<BigInteger, Integer> getValueProof(int value) throws NotEnoughMoneyException;

    LinkedBlockingQueue<Event> getOutGoingEventQueue();
}
