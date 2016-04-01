/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import businesslogic.Manager;
import businesslogic.Master;
import businesslogic.Role;
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
    }
}
