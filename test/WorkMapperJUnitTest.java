/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import businesslogic.Resource;
import businesslogic.Work;
import database.DatabaseManager;
import database.WorkMapper;
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
public class WorkMapperJUnitTest {
    
    public WorkMapperJUnitTest() {
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
        Work w = new WorkMapper().load(1, db);
        assertEquals("work1",w.getDescription());
        assertEquals(100,w.getServiceCoast(),0);
        
        assertEquals("resource 1",w.getResources().get(0).getName());
        assertEquals(0.5,w.getResources().get(0).getCoast(),0);
        assertEquals(100,w.getResources().get(0).getAmount());
        assertEquals(1,w.getResources().get(0).getId());
        assertEquals(1,w.getResources().get(0).getType());

        assertEquals("resource 2",w.getResources().get(1).getName());
        assertEquals(1.5,w.getResources().get(1).getCoast(),0);
        assertEquals(200,w.getResources().get(1).getAmount());
        assertEquals(2,w.getResources().get(1).getId());
        assertEquals(2,w.getResources().get(1).getType());
        
        assertEquals("resource 3",w.getResources().get(2).getName());
        assertEquals(0.2,w.getResources().get(2).getCoast(),0);
        assertEquals(1000,w.getResources().get(2).getAmount());
        assertEquals(3,w.getResources().get(2).getId());
        assertEquals(3,w.getResources().get(2).getType());
        
        assertEquals("resource 4",w.getResources().get(3).getName());
        assertEquals(1.5,w.getResources().get(3).getCoast(),0);
        assertEquals(500,w.getResources().get(3).getAmount());
        assertEquals(4,w.getResources().get(3).getId());
        assertEquals(4,w.getResources().get(3).getType());
        
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
        ArrayList<Work> list = new WorkMapper().loadList(db);
        
        
        String Description[]={"work1","work2","work3"};
        double ServiceCoast[]={100,1000,450};

        for(int j = 0; j < list.size(); j++){
            assertEquals(Description[j],list.get(j).getDescription());
            assertEquals(ServiceCoast[j],list.get(j).getServiceCoast(),0);
        }

        assertEquals("resource 1",list.get(0).getResources().get(0).getName());
        assertEquals(0.5,list.get(0).getResources().get(0).getCoast(),0);
        assertEquals(100,list.get(0).getResources().get(0).getAmount());
        assertEquals(1,list.get(0).getResources().get(0).getId());
        assertEquals(1,list.get(0).getResources().get(0).getType());

        assertEquals("resource 2",list.get(0).getResources().get(1).getName());
        assertEquals(1.5,list.get(0).getResources().get(1).getCoast(),0);
        assertEquals(200,list.get(0).getResources().get(1).getAmount());
        assertEquals(2,list.get(0).getResources().get(1).getId());
        assertEquals(2,list.get(0).getResources().get(1).getType());

        assertEquals("resource 3",list.get(0).getResources().get(2).getName());
        assertEquals(0.2,list.get(0).getResources().get(2).getCoast(),0);
        assertEquals(1000,list.get(0).getResources().get(2).getAmount());
        assertEquals(3,list.get(0).getResources().get(2).getId());
        assertEquals(3,list.get(0).getResources().get(2).getType());

        assertEquals("resource 4",list.get(0).getResources().get(3).getName());
        assertEquals(1.5,list.get(0).getResources().get(3).getCoast(),0);
        assertEquals(500,list.get(0).getResources().get(3).getAmount());
        assertEquals(4,list.get(0).getResources().get(3).getId());
        assertEquals(4,list.get(0).getResources().get(3).getType());
        
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
        Work Oldcl = new WorkMapper().load(3, db);
        
        ArrayList<Resource> res = new ArrayList<>();
        res.add(new Resource(5,200,5,10.8, "new resource 5"));
        res.add(new Resource(7,100,7,10.8, "new resource 7"));
        Work cl = new Work(3,res,1000,"new work 3");
        new WorkMapper().save(cl, db);        
        
        cl = new WorkMapper().load(3, db);
        //проверка что всё записалось.
        assertEquals("Id",3,cl.getId());
        assertEquals("new work 3",cl.getDescription());
        assertEquals(1000,cl.getServiceCoast(),0);
        assertEquals(200,cl.getResource(0).getAmount());
        assertEquals(5,cl.getResource(0).getType());
        assertEquals(10.8,cl.getResource(0).getCoast(),0);
        assertEquals("new resource 5",cl.getResource(0).getName());      
        
        //возвращаем обратно старые значения.
        new WorkMapper().save(Oldcl, db);
        
        db.closeConnection();
        db.close();
    }
    
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

        
        ArrayList<Resource> res = new ArrayList<>();
        res.add(new Resource(8,200,8,10.8, "new resource 8"));
        res.add(new Resource(9,100,9,10.8, "new resource 9"));
        Work cl = new Work(0,res,1000,"new work 6");
        new WorkMapper().save(cl, db);
        
        cl = new WorkMapper().load(6, db);
        //проверка что всё записалось.
        assertEquals("Id",6,cl.getId());        
        assertEquals("new work 6",cl.getDescription());
        assertEquals(1000,cl.getServiceCoast(),0);
        assertEquals(200,cl.getResource(0).getAmount());
        assertEquals(100,cl.getResource(1).getAmount());
        assertEquals(8,cl.getResource(0).getType());
        assertEquals(9,cl.getResource(1).getType());
        assertEquals(10.8,cl.getResource(0).getCoast(),0);
        assertEquals("new resource 8",cl.getResource(0).getName());
        assertEquals("new resource 9",cl.getResource(1).getName());
        
        new WorkMapper().delete(6, db);
        db.startTransaction();
        db.execute("ALTER SEQUENCE WORK_ID_GENERATOR RESTART WITH 5");
        db.commitTransaction();
        db.closeConnection();
        db.close();
    }
    
}
