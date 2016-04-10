/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import businesslogic.Manager;
import database.DatabaseManager;
import database.ManagerMapper;
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
public class ManagerMapperJUnitTest {
    
    public ManagerMapperJUnitTest() {
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
        Manager cl =  new ManagerMapper().load(4, db);
        assertEquals("Id",4,cl.getID());
        assertEquals("Manager4",cl.getName());
        assertEquals("1-2-3-4",cl.getPhoneNumber());
        assertEquals("addr4",cl.getOfficeAddress());
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
        ArrayList<Manager> cl =  new ManagerMapper().loadList(db);
        
        assertEquals("Size",5, cl.size());
        
        assertEquals(3,cl.get(2).getID());
        assertEquals("Manager3",cl.get(2).getName());
        assertEquals("1-2-3",cl.get(2).getPhoneNumber());
        assertEquals("addr3",cl.get(2).getOfficeAddress());
        
        assertEquals(4,cl.get(3).getID());
        assertEquals("Manager4",cl.get(3).getName());
        assertEquals("1-2-3-4",cl.get(3).getPhoneNumber());
        assertEquals("addr4",cl.get(3).getOfficeAddress());
        
        assertEquals(5,cl.get(4).getID());
        assertEquals("Manager5",cl.get(4).getName());
        assertEquals("1-2-3-4-5",cl.get(4).getPhoneNumber());
        assertEquals("addr5",cl.get(4).getOfficeAddress());
        
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
        Manager Oldcl = new ManagerMapper().load(2, db);
        
        Manager cl = new Manager("new addr", 2, "new name", "1-2-3-MICRO");
        new ManagerMapper().save(cl, db);        
        
        cl = new ManagerMapper().load(2, db);
        //проверка что всё записалось.
        assertEquals("Id",2,cl.getID());
        assertEquals("new name",cl.getName());
        assertEquals("1-2-3-MICRO",cl.getPhoneNumber());
        assertEquals("new addr",cl.getOfficeAddress());
        //возвращаем обратно старые значения.
        new ManagerMapper().save(Oldcl, db);
        
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
        ArrayList<Manager> oldlist =  new ManagerMapper().loadList(db);
        
        ArrayList<Manager> list = new ArrayList<>();        
        list.add(new Manager("new addr", 1, "new name 1", "phone1"));
        list.add(new Manager("new addr2", 2, "new name 2", "phone2"));
        list.add(new Manager("new addr3", 3, "new name 3", "phone3"));
        list.add(new Manager("new addr4", 4, "new name 4", "phone4"));
        new ManagerMapper().saveArray(list, db);
        //проверка что всё записалось.
        list = new ManagerMapper().loadList(db);
        assertEquals(2,list.get(1).getID());
        assertEquals("new name 2",list.get(1).getName());
        assertEquals("phone2",list.get(1).getPhoneNumber());
        assertEquals("new addr2",list.get(1).getOfficeAddress());
        
        assertEquals(3,list.get(2).getID());
        assertEquals("new name 3",list.get(2).getName());
        assertEquals("phone3",list.get(2).getPhoneNumber());
        assertEquals("new addr3",list.get(2).getOfficeAddress());
        
        assertEquals(4,list.get(3).getID());
        assertEquals("new name 4",list.get(3).getName());
        assertEquals("phone4",list.get(3).getPhoneNumber());
        assertEquals("new addr4",list.get(3).getOfficeAddress());
        
        //возвращаем обратно старые значения.
        new ManagerMapper().saveArray(oldlist, db);
        db.closeConnection();
        db.close();
    }
    
    //Осторожно этот тест можно применять только с последним элементом базы 
    //иначе в последующих тестах могут появится плавающие ошибки!
    @Test
    public void InsertAndDelete() throws SQLException{
        DatabaseManager db = new DatabaseManager(
                "SYSDBA", 
                        "masterkey", 
                        "localhost", 
                        "D:\\Users\\Nik\\Documents\\NetBeansProjects\\ConstructionCompany\\test.fdb", 
                        DatabaseManager.CharEncoding.UTF8.name(), 
                        "TYPE4", 
                        DatabaseManager.IsolationLevel.TRANSACTION_SERIALIZABLE.name());
        db.connect(); 

        
        Manager cl = new Manager("new addr7", 0, "new name7", "1-2-3-MICRO-7");
        new ManagerMapper().save(cl, db);        
        
        cl = new ManagerMapper().load(6, db);
        //проверка что всё записалось.
        assertEquals("Id",6,cl.getID());
        assertEquals("new name7",cl.getName());
        assertEquals("1-2-3-MICRO-7",cl.getPhoneNumber());
        assertEquals("new addr7",cl.getOfficeAddress());
        
        new ManagerMapper().delete(6, db);
        db.startTransaction();
        db.execute("ALTER SEQUENCE MANAGER_ID_GENERATOR RESTART WITH 5");
        db.commitTransaction();
        db.closeConnection();
        db.close();
    }
}
