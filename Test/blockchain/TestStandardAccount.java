package blockchain;

import Impl.StandardAccount;
import Interfaces.Account;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.Assert.assertNotEquals;

public class TestStandardAccount {

    public Account account;
    @Before
    public void setUp(){
        account = new StandardAccount();
    }

    @Test
    public void shouldHaveAnAddress(){
        assertNotEquals(account.getAddress(),null);
    }

    @Test
    public void shouldHavePrivateKey(){
        assertNotEquals(account.getPrivateKey(),null);
    }
}
