/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import businesslogic.Master;
import database.DatabaseManager;
import database.MasterMapper;
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
public class MasterMapperJUnitTest {
    
    public MasterMapperJUnitTest() {
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
    public void load() throws SQLException{
        DatabaseManager db = new DatabaseManager(
                "SYSDBA", 
                        "masterkey", 
                        "localhost", 
                        "D:\\Users\\Nik\\Documents\\NetBeansProjects\\ConstructionCompany\\test.fdb", 
                        DatabaseManager.CharEncoding.UTF8.name(), 
                        "TYPE4", 
                        DatabaseManager.IsolationLevel.TRANSACTION_SERIALIZABLE.name());
        db.connect();     
        Master cl =  new MasterMapper().load(4, db);
        assertEquals("Id",4,cl.getID());
        assertEquals("Master4",cl.getName());
        assertEquals("1-2-3-4",cl.getPhoneNumber());
        db.closeConnection();
        db.close();
    }
    
    @Test
    public void loadAll() throws SQLException{
        DatabaseManager db = new DatabaseManager(
                "SYSDBA", 
                        "masterkey", 
                        "localhost", 
                        "D:\\Users\\Nik\\Documents\\NetBeansProjects\\ConstructionCompany\\test.fdb", 
                        DatabaseManager.CharEncoding.UTF8.name(), 
                        "TYPE4", 
                        DatabaseManager.IsolationLevel.TRANSACTION_SERIALIZABLE.name());
        db.connect();     
        ArrayList<Master> cl =  new MasterMapper().loadList(db);
        
        assertEquals("Size",5, cl.size());
        
        assertEquals(3,cl.get(2).getID());
        assertEquals("Master3",cl.get(2).getName());
        assertEquals("1-2-3",cl.get(2).getPhoneNumber());
        
        assertEquals(4,cl.get(3).getID());
        assertEquals("Master4",cl.get(3).getName());
        assertEquals("1-2-3-4",cl.get(3).getPhoneNumber());
        
        assertEquals(5,cl.get(4).getID());
        assertEquals("Master5",cl.get(4).getName());
        assertEquals("1-2-3-4-5",cl.get(4).getPhoneNumber());
        
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
        Master Oldcl = new MasterMapper().load(2, db);
        
        Master cl = new Master(2, "new name", "1-2-3-MICRO");
        new MasterMapper().save(cl, db);        
        
        cl = new MasterMapper().load(2, db);
        //проверка что всё записалось.
        assertEquals("Id",2,cl.getID());
        assertEquals("new name",cl.getName());
        assertEquals("1-2-3-MICRO",cl.getPhoneNumber());
        //возвращаем обратно старые значения.
        new MasterMapper().save(Oldcl, db);
        
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
        ArrayList<Master> oldlist =  new MasterMapper().loadList(db);
        
        ArrayList<Master> list = new ArrayList<>();        
        list.add(new Master(1, "new name 1", "phone1"));
        list.add(new Master(2, "new name 2", "phone2"));
        list.add(new Master(3, "new name 3", "phone3"));
        list.add(new Master(4, "new name 4", "phone4"));
        new MasterMapper().saveArray(list, db);
        //проверка что всё записалось.
        list = new MasterMapper().loadList(db);
        assertEquals(2,list.get(1).getID());
        assertEquals("new name 2",list.get(1).getName());
        assertEquals("phone2",list.get(1).getPhoneNumber());
        
        assertEquals(3,list.get(2).getID());
        assertEquals("new name 3",list.get(2).getName());
        assertEquals("phone3",list.get(2).getPhoneNumber());
        
        assertEquals(4,list.get(3).getID());
        assertEquals("new name 4",list.get(3).getName());
        assertEquals("phone4",list.get(3).getPhoneNumber());
        
        //возвращаем обратно старые значения.
        new MasterMapper().saveArray(oldlist, db);
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

        
        Master cl = new Master(0, "new name7", "1-2-3-MICRO-7");
        new MasterMapper().save(cl, db);        
        
        cl = new MasterMapper().load(6, db);
        //проверка что всё записалось.
        assertEquals("Id",6,cl.getID());
        assertEquals("new name7",cl.getName());
        assertEquals("1-2-3-MICRO-7",cl.getPhoneNumber());
        
        new MasterMapper().delete(6, db);
        db.startTransaction();
        db.execute("ALTER SEQUENCE MASTER_ID_GENERATOR RESTART WITH 5");
        db.commitTransaction();
        db.closeConnection();
        db.close();
    }
    
    
}
