package Impl;

import Configuration.Configuration;
import Crypto.Impl.RSAPublicKey;
import Impl.Communication.NotEnoughMoneyException;
import Impl.Transactions.*;
import Interfaces.*;
import org.apache.derby.jdbc.EmbeddedDriver;

import java.math.BigInteger;
import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BlockChainDatabase implements BlockChain{

    private static String connectionURL;
    private static Connection conn;
    private final String driver;

    /**
     * @param databaseName      The name of the database that you want to connect to or you want to create.
     * @param genesisblock      The first block that is added to the block chain. Only added if you create a new database or connect to an empty one.
     */
    public BlockChainDatabase(String databaseName, Block genesisblock) {

        //   ## DEFINE VARIABLES SECTION ##
        // define the driver to use
        driver = "org.apache.derby.jdbc.EmbeddedDriver";
        // the database name
        // define the Derby connection URL to use
        //TODO set derby.system.home to another path. This is where the databases are.
        connectionURL = "jdbc:derby:" +"databases/"+databaseName + ";create=true";

        //Store the blockchain in one table and store all transactions in another table
        String blockchain = "CREATE TABLE %s"
                +  "(BLOCKNR INT NOT NULL"
                +  " CONSTRAINT BLOCKNR PRIMARY KEY, "
                +  " NONCE INT NOT NULL,"
                +  " HARDNESS_PARAM INT NOT NULL,"
                +  " PREV_BLOCK_HASH VARCHAR(255) NOT NULL,"
                +  " BLOCK_HASH VARCHAR(255) NOT NULL,"
                +  " COINBASE_TRANS VARCHAR(255) NOT NULL"
                +  " ) ";

        String transactions =  "CREATE TABLE %s"
                +  "(SENDER VARCHAR(255) NOT NULL,"
                +  " RECEIVER VARCHAR(255) NOT NULL,"
                +  " VALUE INT NOT NULL,"
                +  " BLOCKNR INT NOT NULL, "
                +  " TIMESTAMP INT NOT NULL, "
                +  " BLOCKNR_VALUE_PROOF INT NOT NULL,"
                +  " HASH_TRANS_VALUE_PROOF VARCHAR(255) NOT NULL,"
                +  " TRANS_HASH VARCHAR(255) NOT NULL "
                +  " CONSTRAINT TRANS_HASH PRIMARY KEY,"
                +  " SIGNATURE VARCHAR(255) NOT NULL"
                +  " ) " ;

        String pending_transactions =  "CREATE TABLE %s"
                +  "(SENDER VARCHAR(255) NOT NULL,"
                +  " RECEIVER VARCHAR(255) NOT NULL,"
                +  " VALUE INT NOT NULL,"
                +  " BLOCKNR INT NOT NULL, "
                +  " TIMESTAMP INT NOT NULL, "
                +  " BLOCKNR_VALUE_PROOF INT NOT NULL,"
                +  " HASH_TRANS_VALUE_PROOF VARCHAR(255) NOT NULL,"
                +  " PENDING_TRANS_HASH VARCHAR(255) NOT NULL "
                +  " CONSTRAINT PENDING_TRANS_HASH PRIMARY KEY,"
                +  " SIGNATURE VARCHAR(255) NOT NULL"
                +  " ) " ;

        //TODO MAKE FOREIGN KEY CONSTRAINT ON UNSPEND_TRANSACTIONS
        String unspent = "CREATE TABLE %s"
                        +"(UNSPENT_TRANS_HASH VARCHAR(255) NOT NULL CONSTRAINT UNSPENT_TRANS_HASH PRIMARY KEY,"
                        +"VALUE_LEFT INT NOT NULL,"
                        +"IS_COINBASE BOOLEAN NOT NULL,"
                        +"BLOCKNR INT NOT NULL,"
                        +"RECEIVER VARCHAR(255) NOT NULL)";
        //  JDBC code sections
        //  Beginning of Primary DB access section
        //   ## BOOT DATABASE SECTION ##
        try {
            DriverManager.registerDriver(new EmbeddedDriver());
            conn = DriverManager.getConnection(connectionURL);
            // Connect to database
            System.out.println("Connected to database " + databaseName);

            //   ## INITIAL SQL SECTION ##
            createTableIfNotExists("UNSPENT_TRANSACTIONS", unspent);

            createTableIfNotExists("BLOCKCHAIN",blockchain);
            // If the table is empty add genesisblock
            if(getBlockNumber()==-1){
                System.out.println("No block found,Adding block "+genesisblock.getBlockNumber()+" to blockchain");
                addBlock(genesisblock);}


            //Check if table TRANSACTIONS exist
            //   ## INITIAL SQL SECTION ##
            createTableIfNotExists("TRANSACTIONS", transactions);

            createTableIfNotExists("PENDING_TRANSACTIONS", pending_transactions);



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


    /**
     * @param tableName     The name of the table that you want to check if exists.
     * @return              True if the table with the given name exists. False otherwise.
     * @throws SQLException Thrown if anything goes wrong during the queries to the database.
     */
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
     * @param block     The block that your want to append to the block chain.
     */
    public void addBlock(Block block) {
        CoinBaseTransaction coinBaseTransaction = block.getCoinBase();
        // Add coinbase to unspent transactions
        addUnspentTransaction(coinBaseTransaction.transactionHash(),coinBaseTransaction.getValue(),true,block.getBlockNumber(),coinBaseTransaction.getMinerAddress());


        // Update the values of the unspent transactions
        for (Transaction transaction: block.getTransactions().getTransactions()){
            // Get the value proof rest value
            updateUnspentTransactions(transaction.getValueProof(),transaction.getValue(),transaction.getSenderAddress());
            addUnspentTransaction(transaction.transactionHash(),transaction.getValue(),false,block.getBlockNumber(),transaction.getReceiverAddress());
        }
        try{
        String query = "INSERT INTO BLOCKCHAIN " +
                "VALUES ("+block.getBlockNumber()+","
                +block.getNonce()+","
                +block.getHardnessParameter()+",'"
                +block.getPreviousHash().toString()+"','"
                +block.hash().toString()+"','"
                +block.getCoinBase().toString()+"')";

            // Add to block chain
            query(query);
            // Add to transactions
            for (Transaction t:block.getTransactions().getTransactions()){
                addTransaction(t,block.getBlockNumber());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    private void addUnspentTransaction(BigInteger transactionHash, int value, boolean isCoinBase, int blockNumber, Address receiverAddress) {
        String query = "INSERT INTO UNSPENT_TRANSACTIONS "+
                "VALUES ('"+transactionHash.toString()+"',"
                +value+","
                +isCoinBase+","
                +blockNumber+","
                +"'"+receiverAddress.toString()+"')";
        try {
            query(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void removeUnspentTransaction(BigInteger valueProof) {
        try {
            String query = "DELETE FROM UNSPENT_TRANSACTIONS WHERE UNSPENT_TRANS_HASH='"+valueProof+"'";
            query(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getUnspentTransactionValue(BigInteger transactionHash) {
        try {
            Statement s = conn.createStatement();
            String query = "SELECT VALUE_LEFT from UNSPENT_TRANSACTIONS WHERE UNSPENT_TRANS_HASH=" + "'" + transactionHash.toString() + "'";
            ResultSet r = s.executeQuery(query);
            if (r.next()) {
                int result = r.getInt("VALUE_LEFT");
                s.close();
                r.close();
                return result;
            } else {
                s.close();
                r.close();
                throw new NotEnoughMoneyException("Unable to get value proof from database, the transaction has been spent");
            }

        } catch (SQLException | NotEnoughMoneyException e) {
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * @param address The address involved in transactions.
     * @return      A history of all the transactions where this address is involved.
     */
    @Override
    public TransactionHistory getTransactionHistory(Address address) {
        return getTransactionHistory(address,0);
    }

    /**
     * @param address     The address involved in transactions.
     * @param blockNumber The block number from where you want to get the history, inclusive.
     * @return            The transaction history from a given block number.
     */
    @Override
    public TransactionHistory getTransactionHistory(Address address, int blockNumber) {
        List<ConfirmedTransaction> transactions = new ArrayList<>();
        List<CoinBaseTransaction> coinBaseTransactions = new ArrayList<>();

        Statement s;
        try {
            s = conn.createStatement();
            String query = "SELECT * FROM TRANSACTIONS " +
                    "WHERE (SENDER='"+address+"' OR RECEIVER='"+address+"') AND BLOCKNR >="+blockNumber;
            ResultSet r = s.executeQuery(query);
            while (r.next()) {
                transactions.add( getConfirmedTransactionFromResultSet(r,s,false));
            }
            s.close();
            r.close();


        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            //TODO dont fetch all coin base transactions...
            s = conn.createStatement();
            String query = "SELECT COINBASE_TRANS FROM BLOCKCHAIN WHERE BLOCKNR >="+blockNumber;
            ResultSet r = s.executeQuery(query);
            while (r.next()){
                StandardCoinBaseTransaction cbt = new StandardCoinBaseTransaction(r.getString("COINBASE_TRANS"));
                if (cbt.getMinerAddress().toString().equals(address.toString())){
                    coinBaseTransactions.add(cbt);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new TransactionHistory(transactions,coinBaseTransactions);
    }

    private UnspentTransaction getUnspentTransactionFromResultSet(ResultSet set) throws SQLException {
        BigInteger transHash = new BigInteger(set.getString("UNSPENT_TRANS_HASH"));
        int value_left = set.getInt("VALUE_LEFT");
        boolean isCoinBase = set.getBoolean("IS_COINBASE");
        int blocknr = set.getInt("BLOCKNR");
        Address receiver = new PublicKeyAddress(new RSAPublicKey(set.getString("RECEIVER")));
        return new UnspentTransaction(value_left,transHash,isCoinBase,blocknr,receiver);
    }

    private ConfirmedTransaction getConfirmedTransactionFromResultSet(ResultSet set, Statement s, boolean close) throws SQLException {
        // Get the data from the resultset.
        Address sender = new PublicKeyAddress(new RSAPublicKey(set.getString("SENDER")));
        Address receiver = new PublicKeyAddress(new RSAPublicKey(set.getString("RECEIVER")));
        int value = set.getInt("VALUE");
        int blocknr_value_proof = set.getInt("BLOCKNR_VALUE_PROOF");
        BigInteger hash_trans_value_proof = new BigInteger(set.getString("HASH_TRANS_VALUE_PROOF"));
        BigInteger signature = new BigInteger(set.getString("SIGNATURE"));
        int timestamp = set.getInt("TIMESTAMP");
        int blockNr = set.getInt("BLOCKNR");
        if (close){
            s.close();
            set.close();
        }
        return new ConfirmedTransaction(new StandardTransaction(sender,receiver,value,hash_trans_value_proof,signature,blocknr_value_proof, timestamp),blockNr);
    }


    @Override
    public Block removeBlock() {
        //TODO: IMPLEMENT
        return null;
    }

    private Transaction getTransactionFromResultSet(ResultSet set,Statement s,boolean close) throws SQLException {
        // Get the data from the resultset.
        Address sender = new PublicKeyAddress(new RSAPublicKey(set.getString("SENDER")));
        Address receiver = new PublicKeyAddress(new RSAPublicKey(set.getString("RECEIVER")));
        int value = set.getInt("VALUE");
        int blocknr_value_proof = set.getInt("BLOCKNR_VALUE_PROOF");
        BigInteger hash_trans_value_proof = new BigInteger(set.getString("HASH_TRANS_VALUE_PROOF"));
        BigInteger signature = new BigInteger(set.getString("SIGNATURE"));
        int timestamp = set.getInt("TIMESTAMP");
        if (close){
            s.close();
            set.close();
        }
        return new StandardTransaction(sender,receiver,value,hash_trans_value_proof,signature,blocknr_value_proof, timestamp);
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
                + transaction.getTimestamp()+","
                + transaction.getBlockNumberOfValueProof()+",'"
                + transaction.getValueProof().toString()+"','"
                + transaction.transactionHash().toString()+"',"
                +"'"+transaction.getSignature().toString()+"')";

        try {
            query(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param sql       The string equivalence of a SQL statement.
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

            return getTransactionFromResultSet(set,s,true);
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
                int timestamp = setT.getInt("TIMESTAMP");
                transactions.add( new StandardTransaction(sender,receiver,value,hash_trans_value_proof,signature,blocknr_value_proof, timestamp));
            }
            setT.close();
            s.close();
            return   new StandardBlock(new BigInteger(String.valueOf(nonce)),hardness_param,prev_hash,Configuration.getTransactionLimit(),transactions,blockNumber,coinBase);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<UnspentTransaction> getUnspentTransactions(Address address){
        ArrayList<UnspentTransaction> unspentTransactions = new ArrayList<>();
        try {
            String query = "SELECT * FROM UNSPENT_TRANSACTIONS WHERE RECEIVER='"+address+"'";
            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery(query);
            while (rs.next()){
                unspentTransactions.add(getUnspentTransactionFromResultSet(rs));
            }
            rs.close();
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return unspentTransactions;
    }

    @Override
    public int getBlockNumber() {
        return countDataEntries("BLOCKCHAIN")-1;
    }

    public int getTotalNumberOfTransactions(){
        return countDataEntries("TRANSACTIONS");
    }

    public int countDataEntries(String tablename){
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

    /**
     * Creates the table @tableName if it does not exist.
     *
     * @param tableName     Name of the table
     * @param query         The SQL query that will create the table
     */
    private void createTableIfNotExists(String tableName,String query) throws SQLException {
        if (!tableExists(tableName)){
            query = String.format(query, tableName);
            Statement s = conn.createStatement();
            s.execute(query);
            System.out.println ("Creating table: "+ tableName);
            s.close();
        }
    }

    public void dropTable(String tablename){
        String query = "DROP TABLE "+tablename;
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

     public void updateUnspentTransactions(BigInteger beginTransactionHash,int beginValue, Address address){
         try {
             Statement s = conn.createStatement();
             String query = "SELECT * FROM UNSPENT_TRANSACTIONS WHERE RECEIVER='"+address.toString()+"'";

             ResultSet r = s.executeQuery(query);
             ArrayList<UnspentTransaction> unspentTransactions = new ArrayList();
             while(r.next()){
                 BigInteger hash = new BigInteger(r.getString("UNSPENT_TRANS_HASH"));
                 int valueLeft = r.getInt("VALUE_LEFT");
                 int blockNr = r.getInt("BLOCKNR");
                 boolean isCoinBase = r.getBoolean("IS_COINBASE");
                 Address receiver = new PublicKeyAddress(new RSAPublicKey(r.getString("RECEIVER")));

                unspentTransactions.add(new UnspentTransaction(valueLeft,hash,isCoinBase,blockNr,receiver));
             }
             unspentTransactions.sort(Comparator.comparing(UnspentTransaction::getBlockNumber));

             int valueToUpdate=beginValue;
             boolean after=false;
             for (UnspentTransaction u: unspentTransactions){
                 if (u.getUnspentTransactionHash().toString().equals(beginTransactionHash.toString())){
                     after=true;
                 }
                 if (after) {
                     int val = u.getValueLeft() - valueToUpdate;
                     if (val > 0){
                         updateUnspentTransactionValue(u.getUnspentTransactionHash(),val);
                     }else if (val ==0){
                         removeUnspentTransaction(u.getUnspentTransactionHash());
                         break;
                     }else {
                         removeUnspentTransaction(u.getUnspentTransactionHash());
                         valueToUpdate = -val;
                     }
                 }
             }
         } catch (SQLException e) {
             e.printStackTrace();
         }
    }


    public void updateUnspentTransactionValue(BigInteger transactionHash,int value){
        try{
            Statement s = conn.createStatement();
            String query = "UPDATE UNSPENT_TRANSACTIONS SET VALUE_LEFT="+value
                    + " WHERE UNSPENT_TRANS_HASH='"+transactionHash.toString()+"'";
            query(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This methods looks in all unspent transactions and sums up the amount
     *
     * @param address       The address to get balance from
     * @return              The balance
     */
    public int getBalance(Address address) {
        int balance=0;
        try {
            Statement s = conn.createStatement();
            String query = "SELECT VALUE_LEFT FROM UNSPENT_TRANSACTIONS WHERE RECEIVER='"+address+"'";
            ResultSet rs = s.executeQuery(query);
            while (rs.next()){
                balance += rs.getInt("VALUE_LEFT");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return balance;
    }
}
