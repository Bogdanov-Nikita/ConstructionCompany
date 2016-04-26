/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import businesslogic.Estimate;
import businesslogic.Order;
import database.DatabaseManager;
import database.EstimateMapper;
import database.OrderMapper;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
public class OrderMapperJUnitTest {
    
    public OrderMapperJUnitTest() {
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
        Order e = new OrderMapper().load(1, db);
        assertEquals(1,e.getId());
        assertEquals(1,e.getClientID());
        assertEquals(1,e.getManagerID());
        assertEquals(Order.OPEN,e.getStatus());
        assertEquals(1000.0,e.getCurrentCoast(),0);
        SimpleDateFormat ft = new SimpleDateFormat ("dd.MM.yyyy HH:mm:ss");
        assertEquals("25.04.2016 21:01:02",ft.format(e.getCreate()));
        assertEquals("25.04.2016 21:01:06",ft.format(e.getLastUpdate()));
        assertNull(e.getEnd());
        assertEquals(2, e.getEstimateList().size());
        assertEquals(1,e.getEstimate(0).getId());
        assertEquals(2,e.getEstimate(1).getId());
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
        ArrayList<Order> list = new OrderMapper().loadList(db);
        assertEquals(3,list.size());
        
        assertEquals(1,list.get(0).getId());
        assertEquals(1,list.get(0).getClientID());
        assertEquals(1,list.get(0).getManagerID());
        
        assertEquals(2,list.get(1).getId());
        assertEquals(2,list.get(1).getClientID());
        assertEquals(1,list.get(1).getManagerID());
        
        assertEquals(3,list.get(2).getId());
        assertEquals(3,list.get(2).getClientID());
        assertEquals(1,list.get(2).getManagerID());
        
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
        Order Oldcl = new OrderMapper().load(2, db);
        
        ArrayList<Estimate> estimatelist = new ArrayList<>();
        estimatelist.add(new EstimateMapper().load(3, db));
        
        Order cl = new Order(2, 4,Order.INPROGRESS,3,3,1700.0,0.0,new Date(),new Date(),null,estimatelist);
        new OrderMapper().save(cl, db);
        
        cl = new OrderMapper().load(2, db);
        //проверка что всё записалось.
               
        assertEquals(2, cl.getId());
        assertEquals(3,cl.getClientID());
        assertEquals(3,cl.getManagerID());
        assertEquals(Order.INPROGRESS,cl.getStatus());       
        assertEquals(1700.0,cl.getCurrentCoast(),0);
        assertFalse(cl.isFinish());
        
        //возвращаем обратно старые значения.
        new OrderMapper().save(Oldcl, db);
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
        
        ArrayList<Estimate> estimatelist = new ArrayList<>();
        estimatelist.add(new EstimateMapper().load(2, db));        
        estimatelist.add(new EstimateMapper().load(3, db));
        estimatelist.get(0).setId(0);
        estimatelist.get(1).setId(0);
        Order cl = new Order(0, 5,Order.INPROGRESS,3,3,1700.0,0.0,new Date(),new Date(),null,estimatelist);
        new OrderMapper().save(cl, db);
        
        new OrderMapper().delete(4, db);
        db.startTransaction();
        db.execute("ALTER SEQUENCE ORDER_ID_GENERATOR RESTART WITH 3");
        db.execute("ALTER SEQUENCE ESTIMATE_ID_GENERATOR RESTART WITH 5");
        db.commitTransaction();
        db.closeConnection();
        db.close();
    }
}
