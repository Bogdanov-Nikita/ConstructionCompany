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
    public void load() throws SQLException {
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
    public void loadAll() throws SQLException {
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
        
        assertEquals(3,cl.get(2).getID());
        assertEquals("Client3",cl.get(2).getName());
        assertEquals("1-2-3",cl.get(2).getPhoneNumber());
        assertEquals(1,cl.get(2).getType());
        assertEquals("addr3",cl.get(2).getAddres());
        
        assertEquals(4,cl.get(3).getID());
        assertEquals("Client4",cl.get(3).getName());
        assertEquals("1-2-3-4",cl.get(3).getPhoneNumber());
        assertEquals(2,cl.get(3).getType());
        assertEquals("addr4",cl.get(3).getAddres());
        
        assertEquals(5,cl.get(4).getID());
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

        //Временно сохраняем значения из базы.
        Client Oldcl = new ClientMapper().load(2, db);
        
        Client cl = new Client(2, "new addr", 2, "new name", "1-2-3-MICRO");
        new ClientMapper().save(cl, db);        
        
        cl = new ClientMapper().load(2, db);
        //проверка что всё записалось.
        assertEquals("Id",2,cl.getID());
        assertEquals("new name",cl.getName());
        assertEquals("1-2-3-MICRO",cl.getPhoneNumber());
        assertEquals(2,cl.getType());
        assertEquals("new addr",cl.getAddres());
        //возвращаем обратно старые значения.
        new ClientMapper().save(Oldcl, db);
        
        db.closeConnection();
        db.close();
    }
    
    @Test
    public void saveAll() throws SQLException{
        DatabaseManager db = new DatabaseManager(
                "SYSDBA", 
                        "masterkey", 
                        "localhost", 
                        "D:\\Users\\Nik\\Documents\\NetBeansProjects\\ConstructionCompany\\test.fdb", 
                        DatabaseManager.CharEncoding.UTF8.name(), 
                        "TYPE4", 
                        DatabaseManager.IsolationLevel.TRANSACTION_SERIALIZABLE.name());
        db.connect();
        
        //Временно сохраняем значения из базы.
        ArrayList<Client> oldlist =  new ClientMapper().loadList(db);
        
        ArrayList<Client> list = new ArrayList<>();        
        list.add(new Client(1, "new addr", 1, "new name 1", "phone1"));
        list.add(new Client(2, "new addr2", 2, "new name 2", "phone2"));
        list.add(new Client(2, "new addr3", 3, "new name 3", "phone3"));
        list.add(new Client(1, "new addr4", 4, "new name 4", "phone4"));
        new ClientMapper().saveArray(list, db);
        //проверка что всё записалось.
        list = new ClientMapper().loadList(db);
        assertEquals(2,list.get(1).getID());
        assertEquals("new name 2",list.get(1).getName());
        assertEquals("phone2",list.get(1).getPhoneNumber());
        assertEquals(2,list.get(1).getType());
        assertEquals("new addr2",list.get(1).getAddres());
        
        assertEquals(3,list.get(2).getID());
        assertEquals("new name 3",list.get(2).getName());
        assertEquals("phone3",list.get(2).getPhoneNumber());
        assertEquals(2,list.get(2).getType());
        assertEquals("new addr3",list.get(2).getAddres());
        
        assertEquals(4,list.get(3).getID());
        assertEquals("new name 4",list.get(3).getName());
        assertEquals("phone4",list.get(3).getPhoneNumber());
        assertEquals(1,list.get(3).getType());
        assertEquals("new addr4",list.get(3).getAddres());
        
        //возвращаем обратно старые значения.
        new ClientMapper().saveArray(oldlist, db);
        db.closeConnection();
        db.close();
    }
    
    //Успешно, но надо удалять и востанавливать значение счётчика.(для тестов)
    @Test
    public void insert() throws SQLException{
        /*DatabaseManager db = new DatabaseManager(
                "SYSDBA", 
                        "masterkey", 
                        "localhost", 
                        "D:\\Users\\Nik\\Documents\\NetBeansProjects\\ConstructionCompany\\test.fdb", 
                        DatabaseManager.CharEncoding.UTF8.name(), 
                        "TYPE4", 
                        DatabaseManager.IsolationLevel.TRANSACTION_SERIALIZABLE.name());
        db.connect(); 

        
        Client cl = new Client(2, "new addr7", 0, "new name7", "1-2-3-MICRO-7");
        new ClientMapper().save(cl, db);        
        
        cl = new ClientMapper().load(6, db);
        //проверка что всё записалось.
        assertEquals("Id",6,cl.getID());
        assertEquals("new name7",cl.getName());
        assertEquals("1-2-3-MICRO-7",cl.getPhoneNumber());
        assertEquals(2,cl.getType());
        assertEquals("new addr7",cl.getAddres());
        
        db.closeConnection();
        db.close();*/
    }
}
