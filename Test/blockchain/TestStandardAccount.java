package blockchain;

import Crypto.Impl.RSA;
import Impl.StandardAccount;
import Interfaces.Account;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TestStandardAccount {

    public Account account;
    @Before
    public void setUp(){
        account = new StandardAccount(new RSA(1000, new BigInteger("3")));
    }

    @Test
    public void shouldHaveAnAddress(){
        assertNotEquals(account.getAddress(),null);
    }

    @Test
    public void shouldHavePrivateKey(){
        assertNotEquals(account.getPrivateKey(),null);
    }

    @Test
    public void shouldUseRsa() {
        assertEquals(account.getCryptoSystem().getClass(), RSA.class);
    }
}
