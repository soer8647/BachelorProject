package Interfaces.Communication;

import External.Pair;
import Impl.Communication.NotEnoughMoneyException;
import Impl.ConfirmedTransaction;
import Interfaces.Account;
import Interfaces.Address;
import Interfaces.CoinBaseTransaction;

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
    Pair<Collection<ConfirmedTransaction>, Collection<CoinBaseTransaction>> getTransactionHistory();

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

    /**
     * @param value         The value to find proof of funds for
     * @return              A pair on a transaction hash and a blocknumber.
     * @throws NotEnoughMoneyException
     */
    Pair<BigInteger, Integer> getValueProof(int value) throws NotEnoughMoneyException;

    LinkedBlockingQueue<Event> getEventQueue();

    /**
     * Send a request to the network to update the history of an account.
     */
    void updateTransactionHistory();
}
