import Impl.ArrayListTransactions;
import Impl.FullNode;
import Impl.Hashing.SHA256;
import Impl.StandardBlock;
import Impl.StandardBlockChain;
import Interfaces.Block;
import Interfaces.BlockChain;
import Interfaces.Node;
import com.sun.xml.internal.bind.api.impl.NameConverter;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SuperSimpleBlockChain {
    /**
     * This finds a nonce so that Hash(previous_Hash + nonce) < target, 10 times
     */
    public static void main(String[] args) {
        //init variables
        int iteration = 0;
        Block genesisBlock =  new StandardBlock(new BigInteger("42"),20, new BigInteger("42"), 8, new ArrayListTransactions(),1, new SHA256());
        BlockChain  myBlockChain = new StandardBlockChain(genesisBlock);
        Node node = new FullNode(myBlockChain);
        BigInteger hash = genesisBlock.hash();

        while(iteration<10) {

            // Attempt to find a "correct" nonce

            Block newBlock = node.mine(hash, new ArrayListTransactions());
            hash = newBlock.hash();
            System.out.println("new block found!");
            iteration++;
        }
    }

}
