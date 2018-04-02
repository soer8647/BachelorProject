//TODO: Move this
package GUI;

import Configuration.Configuration;
import Crypto.Impl.RSA;
import Crypto.Impl.RSAKeyPair;
import Crypto.Impl.RSAPrivateKey;
import Crypto.Impl.RSAPublicKey;
import Impl.Communication.UDP.UDPConnectionData;
import Impl.StandardAccount;
import Impl.StandardBlock;
import Impl.StandardUDPClient;
import Impl.Transactions.StandardCoinBaseTransaction;
import Interfaces.Account;
import Interfaces.Block;
import Interfaces.CoinBaseTransaction;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class StartupScript {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        Account acc = null;
        System.out.println("Enter n of key:");
        BigInteger n = sc.nextBigInteger();
        if (n.equals(BigInteger.ZERO)) {
            RSA cryptoSystem = new RSA(Configuration.getKeyBitLength());
            RSAKeyPair keyPair = cryptoSystem.generateNewKeys(BigInteger.valueOf(3));
            acc = new StandardAccount(keyPair.getPrivateKey(),keyPair.getPublicKey());
        } else {
            System.out.println("Enter e of key:");
            BigInteger e = sc.nextBigInteger();
             System.out.println("Enter d of key:");
            BigInteger d = sc.nextBigInteger();
            RSAPrivateKey sk = new RSAPrivateKey(n,d);
            RSAPublicKey pk = new RSAPublicKey(e,n);
            acc = new StandardAccount(sk,pk);
        }
        System.out.println("Enter seed port:");
        int port = sc.nextInt();
        System.out.println("Enter seed ip:");
        InetAddress ip = null;
        try {
            ip = InetAddress.getByName(sc.next());
        } catch (UnknownHostException exp) {
            exp.printStackTrace();
        }
        System.out.println("Your Keys:");
        System.out.println("N: " + acc.getPublicKey().getN());
        System.out.println("D: " + acc.getPrivateKey().getD());
        System.out.println("E: " + acc.getPublicKey().getE());

        int myPort = 8000;
        List list = new ArrayList<UDPConnectionData>();
        list.add(new UDPConnectionData(ip,port));
        new StandardUDPClient(acc,myPort,list);
    }
}
