/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import businesslogic.Resource;
import database.DatabaseManager;
import database.ResourceMapper;
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
public class ResourceMapperJUnitTest {
    
    public ResourceMapperJUnitTest() {
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
        Resource cl =  new ResourceMapper().load(4, db);
        assertEquals("Id",4,cl.getId());
        assertEquals(4,cl.getType());
        assertEquals("resource 4",cl.getName());        
        assertEquals(1.5,cl.getCoast(),0);
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
        ArrayList<Resource> cl =  new ResourceMapper().loadList(db);
        
        assertEquals("Size",10, cl.size());
        
        assertEquals("Id",3,cl.get(2).getId());
        assertEquals(3,cl.get(2).getType());
        assertEquals("resource 3",cl.get(2).getName());        
        assertEquals(0.2,cl.get(2).getCoast(),0);
        
        assertEquals("Id",4,cl.get(3).getId());
        assertEquals(4,cl.get(3).getType());
        assertEquals("resource 4",cl.get(3).getName());        
        assertEquals(1.5,cl.get(3).getCoast(),0);
        
        assertEquals("Id",5,cl.get(4).getId());
        assertEquals(5,cl.get(4).getType());
        assertEquals("resource 5",cl.get(4).getName());        
        assertEquals(5.1,cl.get(4).getCoast(),0);
        
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
        Resource Oldcl = new ResourceMapper().load(2, db);
        
        Resource cl = new Resource(2,0,2,100.5,"new resource 2");
        new ResourceMapper().save(cl, db);        
        
        cl = new ResourceMapper().load(2, db);
        //проверка что всё записалось.
        assertEquals("Id",2,cl.getId());
        assertEquals(2,cl.getType());
        assertEquals("new resource 2",cl.getName());        
        assertEquals(100.5,cl.getCoast(),0);
        
        //возвращаем обратно старые значения.
        new ResourceMapper().save(Oldcl, db);
        
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
        ArrayList<Resource> oldlist =  new ResourceMapper().loadList(db);
        
        ArrayList<Resource> list = new ArrayList<>();        
        list.add(new Resource(1,0,1,100.5,"new resource 1"));
        list.add(new Resource(2,0,2,10.2,"new resource 2"));
        list.add(new Resource(3,0,3,40.1,"new resource 3"));
        list.add(new Resource(4,0,4,5.3,"new resource 4"));
        new ResourceMapper().saveArray(list, db);
        //проверка что всё записалось.
        list = new ResourceMapper().loadList(db);
        
        assertEquals("Id",2,list.get(1).getId());
        assertEquals(2,list.get(1).getType());
        assertEquals("new resource 2",list.get(1).getName());        
        assertEquals(10.2,list.get(1).getCoast(),0);
        
        assertEquals("Id",3,list.get(2).getId());
        assertEquals(3,list.get(2).getType());
        assertEquals("new resource 3",list.get(2).getName());        
        assertEquals(40.1,list.get(2).getCoast(),0);
        
        assertEquals("Id",4,list.get(3).getId());
        assertEquals(4,list.get(3).getType());
        assertEquals("new resource 4",list.get(3).getName());        
        assertEquals(5.3,list.get(3).getCoast(),0);
        
        
        //возвращаем обратно старые значения.
        new ResourceMapper().saveArray(oldlist, db);
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

        
        Resource cl = new Resource(0,0,11,70.1,"new insert res");
        new ResourceMapper().save(cl, db);        
        
        cl = new ResourceMapper().load(11, db);
        //проверка что всё записалось.
        assertEquals("Id",11,cl.getId());
        assertEquals(11,cl.getType());
        assertEquals("new insert res",cl.getName());        
        assertEquals(70.1,cl.getCoast(),0);
        
        new ResourceMapper().delete(11, db);
        db.startTransaction();
        db.execute("ALTER SEQUENCE RESOUSE_ID_GENERATOR RESTART WITH 10");
        db.commitTransaction();
        db.closeConnection();
        db.close();
    }
    
}
