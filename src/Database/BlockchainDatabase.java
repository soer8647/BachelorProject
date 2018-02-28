package Database;

import java.sql.*;

public class BlockchainDatabase {

    private static String connectionURL;
    private static Connection conn;
    private final String driver;

    public BlockchainDatabase() {

        //   ## DEFINE VARIABLES SECTION ##
        // define the driver to use
        driver = "org.apache.derby.jdbc.EmbeddedDriver";
        // the database name
        String dbName="blockchain";
        // define the Derby connection URL to use
        connectionURL = "jdbc:derby:" + dbName + ";create=true";

        String tableName = "BLOCKCHAIN";
        //Store the blockchain in one table and store all transactions in another table
        String blockchain = "CREATE TABLE BLOCKCHAIN"
                +  "(BLOCKNR INT NOT NULL GENERATED ALWAYS AS IDENTITY "
                +  "   CONSTRAINT BLOCKNR PRIMARY KEY, "
                +  " PREV_BLOCK_HASH BIGINT NOT NULL,"
                +  " BLOCK_HASH BIGINT NOT NULL"
                +  " ) " ;
        //TODO MAYBE RESTRICT VARCHAR
        String transactions =  "CREATE TABLE TRANSACTIONS"
                +  "(SENDER VARCHAR(255) NOT NULL,"
                +  " RECEIVER VARCHAR(255) NOT NULL,"
                +  " VALUE INT NOT NULL,"
                +  " BLOCKNR INT NOT NULL, "
                +  " BLOCKNR_VALUE_PROOF INT NOT NULL,"
                +  " HASH_TRANS_VALUE_PROOF BIGINT NOT NULL,"
                +  " TRANS_HASH BIGINT NOT NULL,"
                +  " SIGNATURE VARCHAR(255) NOT NULL"
                +  " ) " ;
        //  JDBC code sections
        //  Beginning of Primary DB access section
        //   ## BOOT DATABASE SECTION ##
        try {
            // Connect to database
            conn = DriverManager.getConnection(connectionURL);
            System.out.println("Connected to database " + dbName);

            //   ## INITIAL SQL SECTION ##
            if (!tableExists("BLOCKCHAIN")){

                Statement s = conn.createStatement();
                s.execute(blockchain);
                System.out.println ("Creating table: "+ "BLOCKCHAIN");
                s.close();
            }
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

    public void shutDown() {

        try {
            conn.close();
            System.out.println("Closed connection");

            //DATABASE SHUTDOWN SECTION
            /*** In embedded mode, an application should shut down Derby.
             Shutdown throws the XJ015 exception to confirm success. ***/
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
        return tables.next();
    }
}
