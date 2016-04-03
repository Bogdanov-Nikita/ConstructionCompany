/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import businesslogic.ErrorMsg;
import businesslogic.Estimate;
import businesslogic.Manager;
import businesslogic.Order;
import businesslogic.Resource;
import businesslogic.Storage;
import businesslogic.Work;
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
public class ManagerJUnitTest {
    
    public ManagerJUnitTest() {
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
        Manager m = new Manager("Company", 1234, "name", "12345");
        assertEquals("Company",m.getCompanyAddress());
        m.setCompanyAddress("co1");
        assertEquals("co1",m.getCompanyAddress());
        assertNotNull(m.CreateOrder(new Date(), 1234, null,125));
        ArrayList<Work> WorkList = new ArrayList<>();
        ArrayList<Resource> Res = new ArrayList<>();
        Res.add(new Resource(100, 1, 0.1, "discr1"));
        Res.add(new Resource(200, 3, 0.5, "discr2"));
        Res.add(new Resource(300, 4, 0.6, "discr3"));
        WorkList.add(new Work(Res, 100, "des"));
        WorkList.add(new Work(Res, 500, "des2"));
        Order ord = m.CreateOrder(new Date(), 1234, WorkList,125);
        assertEquals(Manager.ESTIMATE_ERROR_CAN_NOT_ADD,m.CreateEstimate(null, Estimate.ADDITIONAL, null));
        assertEquals(Manager.ESTIMATE_SUCCESS,m.CreateEstimate(ord, Estimate.ADDITIONAL, WorkList));
        assertNotNull(ord);
        assertEquals(1234,ord.getManagerID());
        assertEquals(125,ord.getClientID());                
        assertFalse(m.CloseOrder(true, ord, new Date()));
        ord.setStatus(Order.WAITING_ACKNOWLEDGMENT_PAY);
        assertFalse(m.CloseOrder(false, ord, new Date()));
        ord.setCurrentCoast(10);
        assertFalse(m.CloseOrder(true, ord, new Date()));
        ord.setCurrentCoast(0);
        ord.setStatus(Order.WAITING_ACKNOWLEDGMENT_PAY);
        assertTrue(m.CloseOrder(true, ord, new Date()));
    }
    
    @Test
    public void test2() {
        Manager m = new Manager("Company", 1234, "name", "12345");
        assertEquals("Company",m.getCompanyAddress());
        m.setCompanyAddress("co1");
        assertEquals("co1",m.getCompanyAddress());
        assertNotNull(m.CreateOrder(new Date(), 1234, null,125));
        ArrayList<Work> WorkList = new ArrayList<>();
        ArrayList<Resource> Res = new ArrayList<>();
        Res.add(new Resource(100, 1, 0.1, "discr1"));
        Res.add(new Resource(200, 3, 0.5, "discr2"));
        Res.add(new Resource(300, 4, 0.6, "discr3"));
        WorkList.add(new Work(Res, 100, "des"));
        WorkList.add(new Work(Res, 500, "des2"));
        Estimate e = new Estimate(Estimate.MAIN, WorkList);
        Estimate e2 = new Estimate(Estimate.ADDITIONAL, WorkList);
        Order ord = new Order(new Date(), 12345);
        assertEquals(Manager.ESTIMATE_SUCCESS,m.CreateEstimate(ord, e));
        ord.setCurrentCoast(2000);
        assertEquals(Manager.ESTIMATE_CLIENT_NEED_PAY,m.CreateEstimate(ord, e2));      
    }
    @Test
    public void test3() {
        Manager m = new Manager("Company", 1234, "name", "12345");
        Order ord = mock(Order.class);
        
        Estimate e = mock(Estimate.class);
        when(e.getType()).thenReturn(Estimate.MAIN);           
        when(ord.addEstimate(e)).thenReturn(Boolean.FALSE);
        when(ord.setCurrentCoast(0)).thenReturn(Boolean.TRUE);
        assertEquals(Manager.ESTIMATE_ERROR_CAN_NOT_ADD,m.CreateEstimate(ord, e));
        
        Estimate e2 = mock(Estimate.class);
        when(e2.getType()).thenReturn(Estimate.MAIN);
        when(ord.addEstimate(e2)).thenReturn(Boolean.TRUE);
        when(ord.CoastCalculation()).thenReturn(0.0);
        when(ord.setCurrentCoast(0.0)).thenReturn(Boolean.FALSE);
        assertEquals(Manager.ESTIMATE_ERROR_CAN_NOT_SET_COAST,m.CreateEstimate(ord, e2));
        
        Estimate e3 = mock(Estimate.class);
        when(e3.getType()).thenReturn(Estimate.MAIN);
        when(ord.addEstimate(e3)).thenReturn(Boolean.FALSE);
        when(ord.CoastCalculation()).thenReturn(0.0);
        when(ord.setCurrentCoast(0.0)).thenReturn(Boolean.FALSE);
        assertEquals(Manager.ESTIMATE_ERROR_CAN_NOT_ADD,m.CreateEstimate(ord, e3));
        
        Estimate e4 = mock(Estimate.class);
        when(e4.getType()).thenReturn(Estimate.ADDITIONAL);
        when(ord.addEstimate(e4)).thenReturn(Boolean.FALSE);
        when(ord.CoastCalculation()).thenReturn(0.0);
        when(ord.setCurrentCoast(0.0)).thenReturn(Boolean.FALSE);
        assertEquals(Manager.ESTIMATE_ERROR_CAN_NOT_ADD,m.CreateEstimate(ord, e4));
        
        when(e4.getType()).thenReturn(0);
        assertEquals(0,m.CreateEstimate(ord, e4));
    }
    @Test
    public void test4(){      
        Storage store = new Storage(5, "location", new ArrayList<>());
        store.addResource(new Resource(100, 1, 0.1, "discr1"));
        store.addResource(new Resource(200, 3, 0.5, "discr2"));
        store.addResource(new Resource(300, 4, 0.6, "discr3"));        
        Manager m = new Manager("Company", 1234, "name", "12345");
        ArrayList<Work> WorkList = new ArrayList<>();
        ArrayList<Resource> Res = new ArrayList<>();
        Res.add(new Resource(200, 1, 0.1, "discr1"));
        Res.add(new Resource(200, 3, 0.5, "discr2"));
        Res.add(new Resource(300, 4, 0.6, "discr3"));
        WorkList.add(new Work(Res, 100, "des"));
        WorkList.add(new Work(Res, 500, "des2"));
        Order ord = m.CreateOrder(new Date(), 123, WorkList, 1234);
        ArrayList<ErrorMsg> er;
        er = m.TakeResourseFromStorage(null,ord.getFullWorkList(),null);
        assertEquals(er.get(0).code,Manager.STORAGE_NULL);
        ArrayList<ErrorMsg> er2;
        ArrayList<Resource> prolist = null;
        er2 = m.TakeResourseFromStorage(store,ord.getFullWorkList(),prolist);
        assertTrue(er2.isEmpty());
    }
    
    @Test
    public void test5(){        
        Storage store = new Storage(5, "location", new ArrayList<>());
        store.addResource(new Resource(100, 1, 0.1, "discr1"));
        store.addResource(new Resource(200, 3, 0.5, "discr2"));
        store.addResource(new Resource(300, 4, 0.6, "discr3"));        
        Manager m = new Manager("Company", 1234, "name", "12345");
        ArrayList<Work> WorkList = new ArrayList<>();
        ArrayList<Resource> Res = new ArrayList<>();
        Res.add(new Resource(200, 1, 0.1, "discr1"));
        Res.add(new Resource(200, 3, 0.5, "discr2"));
        Res.add(new Resource(300, 4, 0.6, "discr3"));
        Res.add(new Resource(400, 5, 0.6, "discr3"));
        WorkList.add(new Work(Res, 100, "des"));
        WorkList.add(new Work(Res, 500, "des2"));
        Order ord = m.CreateOrder(new Date(), 123, WorkList, 1234);
        ArrayList<ErrorMsg> er;
        ArrayList<Resource> prolist = new ArrayList<>();
        er = m.TakeResourseFromStorage(store,ord.getFullWorkList(),prolist);        
        assertFalse(er.isEmpty());
        assertEquals(er.get(0).code,Storage.RESORSE_NOT_FOUND);
        assertEquals(Storage.SEND_RESORSE_SUCCESS,m.SendResourseToStorage(store,prolist));
    }
    @Test
    public void test6(){        
        Storage store = new Storage(5, "location", new ArrayList<>());    
        Manager m = new Manager("Company", 1234, "name", "12345");
        Order ord = m.CreateOrder(new Date(), 123, null, 1234);
        ArrayList<ErrorMsg> er;
        ArrayList<Resource> prolist = new ArrayList<>();
        er = m.TakeResourseFromStorage(store,ord.getFullWorkList(),prolist);
        assertFalse(er.isEmpty());
        assertEquals(er.get(0).code,Manager.WORKLIST_NULL);
        assertEquals(Manager.STORAGE_NULL,m.SendResourseToStorage(null, prolist));
    }
    
    @Test
    public void test7(){        
        Storage store = mock(Storage.class);
        when(store.findResoursePositionByType(1)).thenReturn(1);
        when(store.findResoursePositionByType(2)).thenReturn(2);
        when(store.findResoursePositionByType(5)).thenReturn(3);
        when(store.TakeResources(0,200)).thenReturn(Storage.RESORSE_NOT_FOUND);
        when(store.TakeResources(1,200)).thenReturn(Storage.STORAGE_EMPTY);
        when(store.TakeResources(2,300)).thenReturn(Storage.RESORSE_NOT_FOUND);
        when(store.TakeResources(3,500)).thenReturn(Storage.STORAGE_EMPTY);
        Manager m = new Manager("Company", 1234, "name", "12345");
        ArrayList<Work> WorkList = new ArrayList<>();
        ArrayList<Resource> Res = new ArrayList<>();
        Res.add(new Resource(200, 1, 0.1, "discr1"));
        Res.add(new Resource(200, 3, 0.5, "discr2"));
        Res.add(new Resource(300, 4, 0.6, "discr3"));
        Res.add(new Resource(400, 5, 0.6, "discr3"));
        WorkList.add(new Work(Res, 100, "des"));
        WorkList.add(new Work(Res, 500, "des2"));        
        Order ord = m.CreateOrder(new Date(), 123, WorkList, 1234);
        ArrayList<ErrorMsg> er;
        ArrayList<Resource> prolist = new ArrayList<>();
        er = m.TakeResourseFromStorage(store,ord.getFullWorkList(),prolist);
        assertFalse(er.isEmpty());
        assertEquals(er.get(0).code,Storage.STORAGE_EMPTY);
        assertEquals(er.get(0).other,-1);
        assertEquals(er.get(1).code,Storage.RESORSE_NOT_FOUND);
        assertEquals(er.get(1).other,3);
        assertEquals(Manager.STORAGE_NULL,m.SendResourseToStorage(null, prolist));
        prolist.add(new Resource(200,1,0.5,""));
        when(store.SendResources(1,200)).thenReturn(Storage.STORAGE_RESOURSE_FAIL);
        assertEquals(Storage.STORAGE_RESOURSE_FAIL,m.SendResourseToStorage(store, prolist));
        assertFalse(m.TakeResourseFromStorage(store,null,prolist).isEmpty());
    }
}
