/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import businesslogic.Client;
import businesslogic.Manager;
import businesslogic.Master;
import businesslogic.Order;
import businesslogic.Role;
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
public class RoleJUnitTest {
    
    public RoleJUnitTest() {
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
        Role r = new Manager("CompanyAddress", 0,"FName","123");
        r.setID(10);
        assertEquals(r.getID(),10);
        r.setName("Name");
        assertEquals(r.getName(),"Name");
        r.setPhoneNumber("12345");
        assertEquals(r.getPhoneNumber(),"12345");
    }
    
    @Test
    public void test2() {
        Role r = new Master(0,"FName","123");
        r.setID(10);
        assertEquals(r.getID(),10);
        r.setName("Name");
        assertEquals(r.getName(),"Name");
        r.setPhoneNumber("12345");
        assertEquals(r.getPhoneNumber(),"12345");
        ((Master)r).MakeWork(new Order(new Date(), 123));
    }
    
    @Test
    public void test3() {
        Client r = new Client(Client.LEGAL,"addr",0,"FName","123");
        r.setID(10);
        assertEquals(r.getID(),10);
        r.setName("Name");
        assertEquals(r.getName(),"Name");
        r.setPhoneNumber("12345");
        assertEquals(r.getPhoneNumber(),"12345");
        assertEquals(Client.LEGAL,r.getType());
        r.setType(Client.PHYSICAL);
        assertEquals(Client.PHYSICAL,r.getType());
        assertEquals("addr", r.getAddres());
        r.setAddres(null);
        assertNull(r.getAddres());
        Order ord = mock(Order.class);
        when(ord.isFinish()).thenReturn(Boolean.TRUE);
        assertTrue(r.TakeWork(ord));
        when(ord.ClientPay(1000)).thenReturn(Boolean.TRUE);
        assertTrue(r.PayEstimatePart(ord,1000));
        r.PayEstimateFull(ord);
    }
}
