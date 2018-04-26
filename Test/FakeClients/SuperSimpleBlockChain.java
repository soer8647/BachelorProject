package FakeClients;

import Impl.*;
import Interfaces.Block;
import Interfaces.BlockChain;
import Interfaces.Node;
import blockchain.Stubs.AddressStub;
import blockchain.Stubs.CoinBaseTransactionStub;

import java.math.BigInteger;
import java.util.ArrayList;

public class SuperSimpleBlockChain {

        public static void main(String[] args) {
            //variables
            Block genesisBlock =  new StandardBlock(new BigInteger("42"),20, new BigInteger("42"), new ArrayList<>(),1, new CoinBaseTransactionStub());
            BlockChain  myBlockChain = new StandardBlockChain(genesisBlock);
            Node node = new FullNode(myBlockChain, new AddressStub(),new ConstantHardnessManager(), new StandardTransactionManager(myBlockChain));
            BigInteger hash = genesisBlock.hash();

            while(true) {
                Block newBlock = node.mine(hash, new ArrayList<>());
                hash = newBlock.hash();
                System.out.println("\nnew block found!");
                System.out.println(newBlock);
            }
        }

}
