/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import database.Database;
import database.DatabaseManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Nik
 */
public class DatabaseManagerJUnitTest {
    
    public DatabaseManagerJUnitTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }


    @Test
    public void test() throws SQLException {
        
        DatabaseManager db = new DatabaseManager(
                "SYSDBA", 
                        "masterkey", 
                        "localhost", 
                        "D:\\Users\\Nik\\Documents\\NetBeansProjects\\ConstructionCompany\\test.fdb", 
                        DatabaseManager.CharEncoding.UTF8.name(), 
                        "TYPE4", 
                        DatabaseManager.IsolationLevel.TRANSACTION_SERIALIZABLE.name());
        db.connect();
        db.startTransaction();
        String TableName[] = db.getTableNames();
        db.commitTransaction();
        
        String Controll[] = {
            Database.Client.Table,
            Database.Estimate.Table,
            Database.EstimateWorks.Table,
            Database.Manager.Table,
            Database.Master.Table,
            Database.Order.Table,
            Database.Resource.Table,
            Database.Storage.Table,
            Database.StorageInformation.Table,
            Database.Work.Table,            
            Database.WorksAndResource.Table
        };
        
        
        assertArrayEquals(Controll,TableName);
        
        db.startTransaction();
        
        String TableCollum[][] = {            
            {
                Database.Client.id,
                Database.Client.name,
                Database.Client.phone_number,
                Database.Client.type,
                Database.Client.addres
            },{        
                Database.Estimate.id,
                Database.Estimate.order_id,
                Database.Estimate.type,
                Database.Estimate.coast,
                Database.Estimate.paid
            },{
                Database.EstimateWorks.estimate_id,
                Database.EstimateWorks.master_id,
                Database.EstimateWorks.work_id,
                Database.EstimateWorks.finish
            },{
                Database.Manager.id,
                Database.Manager.name,
                Database.Manager.phone_number,
                Database.Manager.office_address
            },{
                Database.Master.id,
                Database.Master.name,
                Database.Master.phone_number
            },{
                Database.Order.id,
                Database.Order.number,
                Database.Order.client_id,
                Database.Order.manager_id,
                Database.Order.status,
                Database.Order.current_coast,
                Database.Order.create_date,
                Database.Order.update_date,
                Database.Order.end_date
            },{
                Database.Resource.id,
                Database.Resource.type,
                Database.Resource.name,
                Database.Resource.coast
            },{
                Database.Storage.id,
                Database.Storage.resource_id,
                Database.Storage.amount
            },{
                Database.StorageInformation.id,
                Database.StorageInformation.location
            },{
                Database.Work.id,
                Database.Work.description,
                Database.Work.service_coast
            },{
                Database.WorksAndResource.work_id,
                Database.WorksAndResource.resource_id,
                Database.WorksAndResource.amount
            }
        };
        int i = 0;
        for (String Controll1 : Controll) {
            assertArrayEquals(TableCollum[i],db.getColumName(Controll1));
            i++;
        }
        db.commitTransaction();
        db.closeConnection();
        db.close();
        
    }
    
    @Test
    public void test2() throws SQLException{
        DatabaseManager db = new DatabaseManager(
                "SYSDBA", 
                        "masterkey", 
                        "localhost", 
                        "D:\\Users\\Nik\\Documents\\NetBeansProjects\\ConstructionCompany\\test.fdb", 
                        DatabaseManager.CharEncoding.UTF8.name(), 
                        "TYPE4", 
                        DatabaseManager.IsolationLevel.TRANSACTION_SERIALIZABLE.name());
        db.connect();
        db.startTransaction();
        String SQL = "SELECT "
                +"\"" + Database.Client.Table + "\".\"" + Database.Client.id + "\" as \"id\", "
                +"\"" + Database.Client.Table + "\".\"" + Database.Client.type + "\" as \"type\", "
                +"\"" + Database.Client.Table + "\".\"" + Database.Client.name + "\" as \"name\", "
                +"\"" + Database.Client.Table + "\".\"" + Database.Client.phone_number + "\" as \"phone_number\", "                
                +"\"" + Database.Client.Table + "\".\"" + Database.Client.addres + "\" as \"addres\" "
                +"FROM "
                +"\"" + Database.Client.Table + "\" WHERE  "
                +"\"" + Database.Client.Table + "\".\"" + Database.Client.id + "\" = 4 ;";
        ResultSet rs = db.executeQuery(SQL);        
        rs.next();
        db.commitTransaction();
        assertEquals("id",4,rs.getInt(1));
        assertEquals("type",2,rs.getInt(2));
        assertEquals("name","Client4",rs.getString(3).trim());
        assertEquals("phone_number","1-2-3-4",rs.getString(4).trim());
        assertEquals("addres4","addr4",rs.getString(5).trim());        
        db.closeConnection();
        db.close();
    }
}
