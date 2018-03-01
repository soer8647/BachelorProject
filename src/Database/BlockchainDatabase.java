package Database;
import Configuration.Configuration;
import Impl.*;
import Interfaces.*;
import Crypto.Impl.RSAPublicKey;
import org.apache.derby.jdbc.EmbeddedDriver;

import java.math.BigInteger;
import java.sql.*;

public class BlockchainDatabase implements BlockChain{

    private static String connectionURL;
    private static Connection conn;
    private final String driver;

    public BlockchainDatabase(String databaseName, Block genesisblock) {

        //   ## DEFINE VARIABLES SECTION ##
        // define the driver to use
        driver = "org.apache.derby.jdbc.EmbeddedDriver";
        // the database name
        // define the Derby connection URL to use
        connectionURL = "jdbc:derby:" + databaseName + ";create=true";

        //Store the blockchain in one table and store all transactions in another table
        String blockchain = "CREATE TABLE BLOCKCHAIN"
                +  "(BLOCKNR INT NOT NULL"
                +  " CONSTRAINT BLOCKNR PRIMARY KEY, "
                +  " NONCE INT NOT NULL,"
                +  " HARDNESS_PARAM INT NOT NULL,"
                +  " PREV_BLOCK_HASH VARCHAR(255) NOT NULL,"
                +  " BLOCK_HASH VARCHAR(255) NOT NULL,"
                +  " COINBASE_TRANS VARCHAR(255) NOT NULL"
                +  " ) ";

        String transactions =  "CREATE TABLE TRANSACTIONS"
                +  "(SENDER VARCHAR(255) NOT NULL,"
                +  " RECEIVER VARCHAR(255) NOT NULL,"
                +  " VALUE INT NOT NULL,"
                +  " BLOCKNR INT NOT NULL, "
                +  " BLOCKNR_VALUE_PROOF INT NOT NULL,"
                +  " HASH_TRANS_VALUE_PROOF VARCHAR(255) NOT NULL,"
                +  " TRANS_HASH VARCHAR(255) NOT NULL,"
                +  " SIGNATURE VARCHAR(255) NOT NULL"
                +  " ) " ;
        //  JDBC code sections
        //  Beginning of Primary DB access section
        //   ## BOOT DATABASE SECTION ##
        try {
            DriverManager.registerDriver(new EmbeddedDriver());
            conn = DriverManager.getConnection(connectionURL);
            // Connect to database
            System.out.println("Connected to database " + databaseName);

            //   ## INITIAL SQL SECTION ##
            if (!tableExists("BLOCKCHAIN")){

                Statement s = conn.createStatement();
                s.execute(blockchain);
                System.out.println ("Creating table: "+ "BLOCKCHAIN");
                s.close();
            }
            // If the table is empty add genesisblock
            if(getBlockNumber()==-1){
                System.out.println("No block found,Adding block "+genesisblock.getBlockNumber()+" to blockchain");
                addBlock(genesisblock);}
            //Check if table TRANSACTIONS exist
            //   ## INITIAL SQL SECTION ##
            if (!tableExists("TRANSACTIONS")){

                Statement s = conn.createStatement();
                s.execute(transactions);
                System.out.println ("Creating table: "+ "TRANSACTIONS");
                s.close();
            }

            //  Beginning of the primary catch block: prints stack trace
        }  catch (Throwable e)  {
            /*       Catch all exceptions and pass them to
             *       the Throwable.printStackTrace method  */
            System.out.println(" . . . exception thrown:");
            e.printStackTrace(System.out);
        }
    }

    /**
     *  Closes the connection and shuts down the database.
     */
    public void shutDown() {

        try {
            conn.close();
            System.out.println("Closed connection");

            //DATABASE SHUTDOWN SECTION
            //Shutdown throws the XJ015 exception to confirm success.
            if (driver.equals("org.apache.derby.jdbc.EmbeddedDriver")) {
                boolean gotSQLExc = false;
                try {
                    DriverManager.getConnection("jdbc:derby:;shutdown=true");
                } catch (SQLException se)  {
                    if ( se.getSQLState().equals("XJ015") ) {
                        gotSQLExc = true;
                    }
                }
                if (!gotSQLExc) {
                    System.out.println("Database did not shut down normally");
                }  else  {
                    System.out.println("Database shut down normally");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    private static boolean tableExists(String tableName) throws SQLException {
        //Check if table TRANSACTIONS exist
        conn = DriverManager.getConnection(connectionURL);
        DatabaseMetaData dbm = conn.getMetaData();
        ResultSet tables = dbm.getTables(null, null, tableName, null);
        boolean exist=  tables.next();
        tables.close();
        return  exist;
    }

    /**
     * @param block     The block that your want to append to the blockchain.
     */
    public void addBlock(Block block) {
        String query = "INSERT INTO BLOCKCHAIN " +
                "VALUES ("+block.getBlockNumber()+","
                +block.getNonce()+","
                +block.getHardnessParameter()+",'"
                +block.getPreviousHash().toString()+"','"
                +block.hash().toString()+"','"
                +block.getCoinBase().toString()+"')";
        try {
            query(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Block getGenesisBlock() {
        return getBlock(0);
    }

    /**
     * Calls: DELETE TABLE @name on the database. To remove all entries.
     *
     * @param name  The name of the table.
     */
    public void clearTable(String name){
        try {
            if (conn.isClosed()) conn = DriverManager.getConnection(connectionURL);
            Statement s = conn.createStatement();
            String sql = "DELETE FROM " +name;
            s.execute(sql);
            s.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param transaction       The transaction to put on the blockchain.
     * @param blocknumber       The blocknumber of the mined block where this transaction was in.
     */
    public void addTransaction(Transaction transaction,int blocknumber) {
        String query = "INSERT INTO TRANSACTIONS "
                + "VALUES ("
                + "'"+transaction.getSenderAddress()+"',"
                + "'"+transaction.getReceiverAddress()+"',"
                + transaction.getValue()+","
                + blocknumber+","
                + transaction.getBlockNumberOfValueProof()+",'"
                + transaction.getValueProof().toString()+"','"
                + transaction.transActionHash().toString()+"',"
                +"'"+transaction.getSignature().toString()+"')";

        try {
            query(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param sql       The string equivalence of a SQL statement.
     * @return          True if the query was successful. False otherwise.
     */
    private void query(String sql) throws SQLException {
        Statement s = conn.createStatement();
        s.execute(sql);
        s.close();
    }

    /**
     * @param transActionHash       The hash of the transaction that you want to retrieve.
     * @param blockNumber           The blocknumber of the block where the transaction was put on the blockchain.
     * @return                      The transaction object.
     */
    public Transaction getTransaction(BigInteger transActionHash, int blockNumber) {
        try {
            Statement s = conn.createStatement();
            // Query to database
            String query = "SELECT * FROM TRANSACTIONS WHERE BLOCKNR="+blockNumber +" AND TRANS_HASH="+"'"+transActionHash.toString()+"'";
            ResultSet set = s.executeQuery(query);

            set.next();
            // Get the data from the resultset.
            Address sender = new PublicKeyAddress(new RSAPublicKey(set.getString("SENDER")));
            Address receiver = new PublicKeyAddress(new RSAPublicKey(set.getString("RECEIVER")));
            int value = set.getInt("VALUE");
            int blocknr_value_proof = set.getInt("BLOCKNR_VALUE_PROOF");
            BigInteger hash_trans_value_proof = new BigInteger(set.getString("HASH_TRANS_VALUE_PROOF"));
            BigInteger signature = new BigInteger(set.getString("SIGNATURE"));
            // Clean up resources
            s.close();
            set.close();
            return new StandardTransaction(sender,receiver,value,hash_trans_value_proof,signature,blocknr_value_proof);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param blockNumber       The blocknumber of the block you want to retrieve.
     * @return                  The block with the given blocknumber.
     */
    public Block getBlock(int blockNumber) {
        try {
            Statement s = conn.createStatement();
            // Get data from database
            String query = "SELECT * FROM BLOCKCHAIN WHERE BLOCKNR="+blockNumber;
            ResultSet set = s.executeQuery(query);
            set.next(); //There should be only one block since blocknr is primary key
            BigInteger prev_hash=new BigInteger(set.getString("PREV_BLOCK_HASH"));
            int nonce = set.getInt("NONCE");
            int hardness_param= set.getInt("HARDNESS_PARAM");
            CoinBaseTransaction coinBase = new StandardCoinBaseTransaction(set.getString("COINBASE_TRANS"));

            set.close();


            String queryT = "SELECT * FROM TRANSACTIONS WHERE BLOCKNR="+blockNumber;

            Transactions transactions = new ArrayListTransactions();
            ResultSet setT = s.executeQuery(queryT);
            while(setT.next()){
                Address sender = new PublicKeyAddress(new RSAPublicKey(setT.getString("SENDER")));
                Address receiver = new PublicKeyAddress(new RSAPublicKey(setT.getString("RECEIVER")));
                int value = setT.getInt("VALUE");
                int blocknr_value_proof = setT.getInt("BLOCKNR_VALUE_PROOF");
                BigInteger hash_trans_value_proof = new BigInteger(setT.getString("HASH_TRANS_VALUE_PROOF"));
                BigInteger signature = new BigInteger(setT.getString("SIGNATURE"));
                transactions.add( new StandardTransaction(sender,receiver,value,hash_trans_value_proof,signature,blocknr_value_proof));
            }
            setT.close();
            s.close();
            return   new StandardBlock(new BigInteger(String.valueOf(nonce)),hardness_param,prev_hash,Configuration.transactionLimit,transactions,blockNumber,coinBase);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getBlockNumber() {
        return countDataEntries("BLOCKCHAIN")-1;
    }

    public int getTotalNumberOfTransactions(){
        return countDataEntries("TRANSACTIONS");
    }

    private int countDataEntries(String tablename){
        try {
            String query = "SELECT COUNT(*) FROM "+tablename;
            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery(query);
            rs.next();
            int rt =rs.getInt(1);
            rs.close();
            s.close();
            return  rt;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -2;
    }
}
