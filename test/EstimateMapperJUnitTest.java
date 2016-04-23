/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import businesslogic.Estimate;
import businesslogic.Work;
import database.DatabaseManager;
import database.EstimateMapper;
import database.WorkMapper;
import java.sql.SQLException;
import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author Nik
 */
public class EstimateMapperJUnitTest {
    
    public EstimateMapperJUnitTest() {
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
        Estimate e = new EstimateMapper().load(1, db);
        assertEquals(1,e.getId());
        assertEquals(1,e.getType());
        assertEquals(1000.0,e.getCoast(),0);
        assertFalse(e.isPaid());
        assertFalse(e.isFinish());
        
        assertEquals(1,e.getWork(0).getId());
        assertEquals(2,e.getWork(1).getId());       
        assertEquals("work1",e.getWork(0).getDescription());
        assertEquals("work2",e.getWork(1).getDescription());       
        assertEquals(100.0,e.getWork(0).getServiceCoast(),0);
        assertEquals(1000.0,e.getWork(1).getServiceCoast(),0);
        assertEquals(1,e.getWork(0).getMasterId());
        assertEquals(1,e.getWork(1).getMasterId());

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
        ArrayList<Estimate> list = new EstimateMapper().loadList(db);
        
        int Ids[]={1,1,2,2,3};
        int OrdIds[]={1,1,1,1,2};
        int Types[]={1,1,2,2,1};
        double Coasts[]={1000.0,1000.0,2000.0,2000.0,10000.0};
        for(int i = 0; i < list.size(); i++){
            assertEquals(Ids[i],list.get(i).getId());
            assertEquals(OrdIds[i],list.get(i).getOrderId());
            assertEquals(Types[i],list.get(i).getType());
            assertFalse(list.get(i).isPaid());
            assertEquals(Coasts[i],list.get(i).getCoast(),0);
            assertFalse(list.get(i).isFinish());
        }
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
        Estimate Oldcl = new EstimateMapper().load(3, db);
        
        ArrayList<Work> work = new ArrayList<>();
        work.add(new WorkMapper().load(1, db));
        work.get(0).setMasterId(3);
        Estimate cl = new Estimate(3, 1, false, Estimate.MAIN, 700.0, work);
        new EstimateMapper().save(cl, db);
        
        cl = new EstimateMapper().load(3, db);
        //проверка что всё записалось.
        assertEquals(3, cl.getId());
        assertEquals(1,cl.getOrderId());
        assertFalse(cl.isPaid());
        assertEquals(Estimate.MAIN,cl.getType());
        assertEquals(700.0,cl.getCoast(),0);
        assertFalse(cl.isFinish());
        assertEquals(3,cl.getWork(0).getMasterId());

        //возвращаем обратно старые значения.
        new EstimateMapper().save(Oldcl, db);
        db.closeConnection();
        db.close();
    }
}