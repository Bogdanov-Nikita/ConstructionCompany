/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import businesslogic.Estimate;
import businesslogic.Order;
import java.util.ArrayList;
import java.util.Date;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
/**
 *
 * @author Nik
 */
public class OrderJUnitTest {
    
    public OrderJUnitTest() {
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
    public void test1() {
        Order ord = new Order(new Date(System.currentTimeMillis()), 12345);
        assertNotNull(ord.getCreate());
        assertNotNull(ord.getLastUpdate());
        assertNull(ord.getEnd());
        assertEquals(Order.OPEN,ord.getStatus());
        Estimate me = mock(Estimate.class);
        when(me.CoastCalculation()).thenReturn(2000.0);
        when(me.isFinish()).thenReturn(Boolean.TRUE);
        ord.addEstimate(me);
        assertEquals(2000,ord.CoastCalculation(),0);
        ord.addEstimate(me);
        assertEquals(4000,ord.CoastCalculation(),0);
        assertTrue(ord.isFinish());
        assertNotNull(ord.getFullWorkList());
        Date d;
        ord.setLastUpdate(d= new Date(System.currentTimeMillis()));
        assertEquals(ord.getLastUpdate(),d);
        assertTrue(ord.CloseOrder(d));        
        assertEquals(ord.getEnd(),d);
        assertFalse(ord.CloseOrder(null));
        ord.setLastUpdate(null);
        assertNotNull(ord.getLastUpdate());
        ord.setStatus(Order.INPROGRESS);
        assertEquals(Order.INPROGRESS,ord.getStatus());
        assertEquals(0,ord.getCurrentCoast(),0);
        assertTrue(ord.setCurrentCoast(1000));
        assertEquals(1000,ord.getCurrentCoast(),0);   
        ord.clear();
        assertEquals(0,ord.CoastCalculation(),0);
    }
    
    @Test
    public void test2() {
        Order ord = new Order(new Date(System.currentTimeMillis()), 12345);
        Estimate me = mock(Estimate.class);
        when(me.CoastCalculation()).thenReturn(2000.0);
        when(me.isFinish()).thenReturn(Boolean.TRUE);
        Estimate me2 = mock(Estimate.class);
        when(me2.CoastCalculation()).thenReturn(1000.0);
        when(me2.isFinish()).thenReturn(Boolean.FALSE); 
        ord.addEstimate(me);
        assertTrue(ord.setCurrentCoast(ord.CoastCalculation()));
        assertTrue(ord.ClientPay(1000));
        assertFalse(ord.ClientPay(-2000));
        assertFalse(ord.ClientPay(3000));
        assertTrue(ord.ClientPay(1000));
        ord.clear();
        assertTrue(ord.getEstimateList().isEmpty());
        ord.setEstimateList(null);
        ord.setEstimate(0, null);
        ord.deleteEstimate(5);
        assertFalse(ord.addEstimate(null));
        assertNull(ord.getEstimateList());
        assertTrue(ord.addEstimate(me));
        ord.deleteEstimate(0);
        ord.setEstimateList(new ArrayList<>());
        assertTrue(ord.addEstimate(me2));
        assertEquals(me2,ord.getEstimate(0));      
        ord.setEstimate(0, me);
        assertEquals(2000,ord.CoastCalculation(),0);
        ord.addEstimate(me);
        assertEquals(4000,ord.CoastCalculation(),0);
        ord.setEstimateList(null);
        ord.addEstimate(me2);
        assertFalse(ord.isFinish());
    }
    
    @Test
    public void test3() {
        Order ord = new Order(12345,Order.OPEN, 41, 45, 0, 0, null, null, null, null);
        assertFalse(ord.setCurrentCoast(-100));
        assertEquals(0,ord.getCurrentCoast(),0);
        assertEquals(0,ord.CoastCalculation(),0);
        ord.setEstimateList(new ArrayList<>());
        ord.deleteEstimate(5);
    }
}
