/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import businesslogic.Client;
import database.ClientMapper;
import database.DatabaseManager;
import java.sql.SQLException;
import java.util.ArrayList;
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
public class ClientMapperJUnitTest {
    
    public ClientMapperJUnitTest() {
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
    public void select() throws SQLException {
        DatabaseManager db = new DatabaseManager(
                "SYSDBA", 
                        "masterkey", 
                        "localhost", 
                        "D:\\Users\\Nik\\Documents\\NetBeansProjects\\ConstructionCompany\\test.fdb", 
                        DatabaseManager.CharEncoding.UTF8.name(), 
                        "TYPE4", 
                        DatabaseManager.IsolationLevel.TRANSACTION_SERIALIZABLE.name());
        db.connect();     
        Client cl =  new ClientMapper().load(4, db);
        assertEquals("Id",4,cl.getID());
        assertEquals("Client4",cl.getName());
        assertEquals("1-2-3-4",cl.getPhoneNumber());
        assertEquals(2,cl.getType());
        assertEquals("addr4",cl.getAddres());
        db.closeConnection();
        db.close();
    }
    
    @Test
    public void selectAll() throws SQLException {
        DatabaseManager db = new DatabaseManager(
                "SYSDBA", 
                        "masterkey", 
                        "localhost", 
                        "D:\\Users\\Nik\\Documents\\NetBeansProjects\\ConstructionCompany\\test.fdb", 
                        DatabaseManager.CharEncoding.UTF8.name(), 
                        "TYPE4", 
                        DatabaseManager.IsolationLevel.TRANSACTION_SERIALIZABLE.name());
        db.connect();     
        ArrayList<Client> cl =  new ClientMapper().loadList(db);
        
        assertEquals("Size",5, cl.size());
        
        assertEquals("Id",3,cl.get(2).getID());
        assertEquals("Client3",cl.get(2).getName());
        assertEquals("1-2-3",cl.get(2).getPhoneNumber());
        assertEquals(1,cl.get(2).getType());
        assertEquals("addr3",cl.get(2).getAddres());
        
        assertEquals("Id",4,cl.get(3).getID());
        assertEquals("Client4",cl.get(3).getName());
        assertEquals("1-2-3-4",cl.get(3).getPhoneNumber());
        assertEquals(2,cl.get(3).getType());
        assertEquals("addr4",cl.get(3).getAddres());
        
        assertEquals("Id",5,cl.get(4).getID());
        assertEquals("Client5",cl.get(4).getName());
        assertEquals("1-2-3-4-5",cl.get(4).getPhoneNumber());
        assertEquals(1,cl.get(4).getType());
        assertEquals("addr5",cl.get(4).getAddres());
        
        db.closeConnection();
        db.close();
    }

    @Test
    public void save() throws SQLException{
        DatabaseManager db = new DatabaseManager(
                "SYSDBA", 
                        "masterkey", 
                        "localhost", 
                        "D:\\Users\\Nik\\Documents\\NetBeansProjects\\ConstructionCompany\\test.fdb", 
                        DatabaseManager.CharEncoding.UTF8.name(), 
                        "TYPE4", 
                        DatabaseManager.IsolationLevel.TRANSACTION_SERIALIZABLE.name());
        db.connect(); 
        Client cl = new Client(2, "new addr", 2, "new name", "1-2-3-MICRO");
        new ClientMapper().save(cl, db);        
        db.closeConnection();
        db.close();
    }
}
