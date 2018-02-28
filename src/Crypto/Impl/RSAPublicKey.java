package Crypto.Impl;

import Crypto.Interfaces.PublicKey;
import Crypto.Interfaces.PublicKeyCryptoSystem;

import java.math.BigInteger;

public class RSAPublicKey implements PublicKey {

    private BigInteger e;
    private BigInteger n;

    public RSAPublicKey(BigInteger e, BigInteger n) {
        this.e = e;
        this.n = n;
    }

    public RSAPublicKey(String data){
        System.out.println(data);
        String e_="";
        String n_="";
        char[] chars = data.toCharArray();
        for (int i=0;i<chars.length;i++){
            if(chars[i]=='n' && chars[i+1]==':'){
                i+=2;
                while (chars[i]!=','){
                    n_=n_.concat(String.valueOf(chars[i]));
                    i++;
                }
                if (chars[i+1]=='e' && chars[i+2] == ':'){
                    i+=3;
                    while (chars[i]!=')'){
                        e_=e_.concat(String.valueOf(chars[i]));
                        i++;
                    }
                }
            }
        }
        System.out.println("n: "+n_);

        System.out.println("e: "+e_);
        n = new BigInteger(n_);
        e = new BigInteger(e_);
    }

    public BigInteger getE() {
        return e;
    }

    public BigInteger getN() {
        return n;
    }

    @Override
    public String toString() {
        return "RSAPublicKey:(n:"+n+",e:"+e+")";
    }

    @Override
    public String getCryptoSystemName() {
        return "RSA";
    }
}
