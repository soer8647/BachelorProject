package Interfaces.Communication;

import Interfaces.Account;
import Interfaces.Address;
import Interfaces.Transaction;

import java.util.Collection;

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
}
