package FakeClients;

import Impl.FullNode;
import Impl.StandardBlock;
import Impl.StandardBlockChain;
import Impl.StandardTransactionManager;
import Impl.Transactions.ArrayListTransactions;
import Interfaces.Block;
import Interfaces.BlockChain;
import Interfaces.Communication.ConstantHardnessManager;
import Interfaces.Node;
import blockchain.Stubs.AddressStub;
import blockchain.Stubs.CoinBaseTransactionStub;

import java.math.BigInteger;

    public class SuperSimpleBlockChain {

        public static void main(String[] args) {
            //variables
            Block genesisBlock =  new StandardBlock(new BigInteger("42"),20, new BigInteger("42"), 8, new ArrayListTransactions(),1, new CoinBaseTransactionStub());
            BlockChain  myBlockChain = new StandardBlockChain(genesisBlock);
            Node node = new FullNode(myBlockChain, new AddressStub(),new ConstantHardnessManager(), new StandardTransactionManager(myBlockChain));
            BigInteger hash = genesisBlock.hash();

            while(true) {
                Block newBlock = node.mine(hash, new ArrayListTransactions());
                hash = newBlock.hash();
                System.out.println("\nnew block found!");
                System.out.println(newBlock);
            }
        }

}
