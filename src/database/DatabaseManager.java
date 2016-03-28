/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package database;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.firebirdsql.management.FBManager;
import org.firebirdsql.pool.FBWrappingDataSource;

/**
 *
 * @author Nik
 */
public class DatabaseManager {
    FBWrappingDataSource dataSource;
    Connection Connection;
    Statement Statement;
    String User;
    String Password;
    String Host;
    String Path;
    String Encoding;
    String Type;
    
    public enum IsolationLevel{
        TRANSACTION_READ_COMMITTED,
        /**
         * Transactions with this isolation level can see only
         * committed records. However, it does not prevent
         * so-called non-repeatable reads and phantom
         * reads.
         */ 
        TRANSACTION_REPEATABLE_READ,
        /**
         * This isolation level prevents the non-repeatable
         * reads, a situation when a row is read in one
         * transaction, then modified in another transaction,
         * and later re-read in the first transaction. In this
         * case different values had been read within the
         * same transaction.
         */
        TRANSACTION_SERIALIZABLE
        /**
         * Transactions with this isolation level prohibit the
         * phantom reads, the situation when one
         * transaction reads all rows satisfying the WHERE
         * condition, another transaction inserts a row
         * satisfying that condition, and first transaction reexecutes
         * the statement.
         */
    }
    
    public enum DriverType{
        TYPE4,      //PURE_JAVA for type 4 JDBC driver
        TYPE2      //NATIVE for type 2 JDBC driver 
        //EMBEDDED    //for using embedded version of the Firebird. not support in this program.
    }
    
    public enum CharEncoding{
        ASCII,          //ASCII	-
        BIG_5,          //Big5	Traditional Chinese
        DOS437,         //Cp437	MS-DOS: United States, Australia, New Zeland, South Africa
        DOS737,         //Cp737	MS-DOS: Greek
        DOS775,         //Cp775	MS-DOS: Baltic
        DOS850,         //Cp850	MS-DOS: Latin-1
        DOS852,         //Cp852	MS-DOS: Latin-2
        DOS857,         //Cp857	IBM: Turkish
        DOS858,         //Cp858	IBM: Latin-1 + Euro
        DOS860,         //Cp860	MS-DOS: Portuguese
        DOS861,         //Cp861	MS-DOS: Icelandic
        DOS862,         //Cp862	IBM: Hebrew
        DOS863,         //Cp863	MS-DOS: Canadian French
        DOS864,         //Cp864	IBM: Arabic
        DOS865,         //Cp865	MS-DOS: Nordic
        DOS866,         //Cp866	IBM: Cyrillic
        DOS869,         //Cp869	IBM: Modern Greek
        EUCJ_0208,      //EUC_JP	JIS X 0201, 0208, 0212, EUC encoding, Japanese
        GB_2312,	//EUC_CN	GB2312, EUC encoding, Simplified Chinese
        ISO8859_1,	//ISO-8859-1	ISO 8859-1, Latin alphabet No. 1
        ISO8859_2,	//ISO-8859-2	ISO 8859-2
        ISO8859_3,	//ISO-8859-3	ISO 8859-3
        ISO8859_4,	//ISO-8859-4	ISO 8859-4
        ISO8859_5,	//ISO-8859-5	ISO 8859-5
        ISO8859_6,	//ISO-8859-6	ISO 8859-6
        ISO8859_7,	//ISO-8859-7	ISO 8859-7
        ISO8859_8,	//ISO-8859-8	ISO 8859-8
        ISO8859_9,	//ISO-8859-9	ISO 8859-9
        ISO8859_13,	//ISO-8859-13	ISO 8859-13
        KSC_5601,	//MS949	Windows Korean
        UNICODE_FSS,	//UTF-8	8-bit Unicode Transformation Format (deprecated since FB 2.0)
        UTF8,           //UTF-8	8-bit Unicode Transformation Format (FB 2.0+)
        WIN1250,        //Cp1250	Windows Eastern European
        WIN1251,	//Cp1251	Windows Cyrillic
        WIN1252,	//Cp1252	Windows Latin-1
        WIN1253,	//Cp1253	Windows Greek
        WIN1254,	//Cp1254	Windows Turkish
        WIN1255,	//Cp1255	-
        WIN1256,	//Cp1256	-
        WIN1257         //Cp1257	-   
    }
    
    
    public static void CreateDatabase(String fileName) throws Exception{
        CreateDatabase(fileName, "SYSDBA", "masterkey",DriverType.TYPE4.name());
    }
    
    public static void CreateDatabase(String User,String Password,String fileName,String Type) throws Exception{
        FBManager manager = new FBManager();
        manager.start();
        manager.setType(Type);
        manager.createDatabase(fileName,User,Password);
        manager.stop();
    }
    //after call conect();
    /*public static DatabaseManager OpenDatabase(String User,String Password,String fileName) {            
        return new DatabaseManager(User, Password, fileName);
    }*/
    
    // used only for open
    private DatabaseManager(String User, String Password, String fileName) {
        this.User = User;
        this.Password = Password;
        this.Host = "localhost";
        this.Path = fileName;
        this.Encoding = CharEncoding.UTF8.toString();
        this.Type = null;
        dataSource = new org.firebirdsql.pool.FBWrappingDataSource();
        dataSource.setDatabase("//"+Host+"/"+Path);
        dataSource.setEncoding(Encoding);
        dataSource.setLoginTimeout(10);
        dataSource.setDefaultIsolation(IsolationLevel.TRANSACTION_SERIALIZABLE.toString());
    }
    
    
    /** 
     * defaul: encodinc - utf8 , type - type4
     * @param User to database
     * @param Password to database
     * @param Host host to databse
     * @param Path path to database
     * @param Isolation TRANSACTION_READ_COMMITTED/TRANSACTION_REPEATABLE_READ/TRANSACTION_SERIALIZABLE */
    
    public DatabaseManager(String User, String Password, String Host, String Path,String Isolation) {
        this(User, Password, Host, Path,
                CharEncoding.UTF8.toString(),
                DriverType.TYPE4.toString(),
                Isolation);
    }
    
    //defaul encodinc - utf8 , type - type4 , Isolation - TRANSACTION_SERIALIZABLE
    public DatabaseManager(String User, String Password, String Host, String Path) {
        this(User, Password, Host, Path,
                CharEncoding.UTF8.toString(),
                DriverType.TYPE4.toString(),
                IsolationLevel.TRANSACTION_SERIALIZABLE.toString());
    }    
    /** 
     * @param User to database
     * @param Password to database
     * @param Host host to databse
     * @param Path path to database
     * @param Encoding encoding (UTF8,Cp1251,....)
     * @param Type type
     * @param Isolation 
     * TRANSACTION_READ_COMMITTED or
     * TRANSACTION_REPEATABLE_READ or
     * TRANSACTION_SERIALIZABLE 
     */
    public DatabaseManager(String User, String Password, String Host, String Path, String Encoding, String Type,String Isolation ) {
        this.User = User;
        this.Password = Password;
        this.Host = Host;
        this.Path = Path;
        this.Encoding = Encoding;
        this.Type = Type;
        
        dataSource = new org.firebirdsql.pool.FBWrappingDataSource();
        dataSource.setDatabase("//"+Host+"/"+Path);
        dataSource.setType(Type);
        dataSource.setEncoding(Encoding);
        dataSource.setLoginTimeout(10);
        dataSource.setDefaultIsolation(Isolation);
    }
    
    public void connect() throws SQLException{
        
        try{
            Connection = dataSource.getConnection(User, Password);
            if(Type == null){ Type=dataSource.getType();}
        }
        catch(SQLException e){
            Connection.close();
            throw new SQLException(e.getMessage());
        }
    }    
    
    public String[] getTableNames() throws SQLException{
        
        String TableNames[];
        ResultSet rs = Connection.getMetaData().getTables(null, null, "%",new String[]{"TABLE"});
        if(rs.last()){
            TableNames = new String[rs.getRow()];
            rs.beforeFirst();
            for(int i=0;rs.next();i++) {
                TableNames[i] = rs.getString("TABLE_NAME");
            }
            return TableNames;
        }else{
            return null;
        }                
    }
    /**
     * and you get valid rowcoun
     * @param TableName - name of table
     * @return - Colum's Name in table
     * @throws java.sql.SQLException - if Transaction fail*/
    
    int length;//use only after call getColumName
    
    public String[] getColumName (String TableName)throws SQLException{
        ResultSet rs = executeQuery(QueryBilder.select(TableName, null, null, null, null, null));
        int size = rs.getMetaData().getColumnCount();
        String ColumName[] = new String[size];
        for(int i=1;i<=size;i++){
            ColumName[i-1]=rs.getMetaData().getColumnName(i);
        }
        //этот тупой 2 строчный велосипед из за отсутствия поддержки команд Afterlast, beforeFerst
        length = 0;//1
        while(rs.next()){length++;}//2  
        return ColumName;
    }
    
    public int getCurrentTableLength(){
        return length;
    }
    
    public String[][] getTableData(String TableName,String ColumName[])throws SQLException{
        ResultSet rs = executeQuery(QueryBilder.select(TableName, ColumName, null, null, null, null));
        String TableData[][] = new String[length][ColumName.length];
        int k = 0;
        while(rs.next()){                    
            for(int i=1;i<=ColumName.length;i++){
                TableData[k][i-1] = rs.getString(i);
            }
            k++;                    
        }
        return TableData;
    }
    
    public DatabaseMetaData getMetaData() throws SQLException{
        return Connection.getMetaData();
    }
    
    public void startTransaction() throws SQLException{        
        try{                    
            Connection.setAutoCommit(false);
            Statement = Connection.createStatement();            
        }
        catch(SQLException e){
            Statement.close();
            throw new SQLException(e.getMessage());
        }    
    }
    
    public void startTransaction(int Isolation) throws SQLException{        
        try{
            Connection.setTransactionIsolation(Isolation);
            Connection.setAutoCommit(false);
            Statement = Connection.createStatement();            
        }
        catch(SQLException e){
            Statement.close();
            throw new SQLException(e.getMessage());
        }    
    }
    
    public void rollbackTransaction() throws SQLException{
        Connection.rollback();        
    }
    
    public void commitTransaction() throws SQLException{
        Connection.commit();
    }    
    
    public ResultSet executeQuery(String executeQuery) throws SQLException{
        ResultSet resultset = null;
        try{
            resultset = Statement.executeQuery(executeQuery);                        
        }
        catch(SQLException e){
            Connection.rollback();
            throw new SQLException(e.getMessage());
        } 
        return resultset;
    }
    
    public boolean execute(String executeQuery) throws SQLException{
        boolean resultset = false;
        try{
            resultset = Statement.execute(executeQuery);                        
        }
        catch(SQLException e){
            Connection.rollback();
            throw new SQLException(e.getMessage());
        } 
        return resultset;
    }
    
    public int update(String executeQuery) throws SQLException{
        int res = -1;
        try{
            res = Statement.executeUpdate(executeQuery);                        
        }
        catch(SQLException e){
            Connection.rollback();
            throw new SQLException(e.getMessage());
        } 
        return res;
    } 
    
    
    public void closeConnection() throws SQLException{
        Connection.close();
    }
    //Only one before next use, you mast create new Database Maneger .
    public void close(){
        dataSource.shutdown();
    }
    
    
}
