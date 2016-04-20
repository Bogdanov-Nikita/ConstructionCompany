/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import businesslogic.Resource;
import businesslogic.Work;
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
public class WorkJUnitTest {
    
    public WorkJUnitTest() {
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
        Work w = new Work(0,null, -10, null);
        assertNull(w.getResources());
        w.add(new Resource(1,1, 1, 1, "discr"));
        assertNotNull(w.getResources());
    }
    @Test
    public void test2() {
        Work w = new Work(0,null, -10, null);
        assertNull(w.getDescription());
        assertFalse(w.isFinish());
        w.setDescription("descr");
        w.setFinish(true);
        assertNotNull(w.getDescription());
        assertTrue(w.isFinish());
        assertTrue(0 == w.getServiceCoast());
        w.setServiceCoast(100);
        assertTrue(100 == w.getServiceCoast());
    }
    @Test
    public void test3() {
        ArrayList<Resource> Res = new ArrayList<>();
        Res.add(new Resource(1,100, 1, 0.1, "discr1"));
        Res.add(new Resource(2,200, 3, 0.5, "discr2"));
        Res.add(new Resource(3,300, 4, 0.6, "discr3"));
        Work w = new Work(0,Res, 100, "des");
        w.add(null);
        w.set(1, null);
        w.set(1, new Resource(4,400, 8, 1.7, "discr4"));
        assertTrue(970 == w.CoastCalculation());
        w.delete(0);
        w.delete(0);
        assertTrue(280 == w.CoastCalculation());
        w.setServiceCoast(-10);
        assertTrue(180 == w.CoastCalculation());
        w.setServiceCoast(20);
        assertTrue(200 == w.CoastCalculation());
    }
    @Test
    public void test4() {
        ArrayList<Resource> Res = new ArrayList<>();
        Res.add(new Resource(1,100, 1, 0.1, "discr1"));
        Res.add(new Resource(2,200, 3, 0.5, "discr2"));
        Res.add(new Resource(3,300, 4, 0.6, "discr3"));
        Work w = new Work(0,null, 100, "des");
        assertTrue(100 == w.CoastCalculation());
        assertTrue(0 == w.amountResources(1));
        w.add(new Resource(5,100, 1, 0.1, "discr1"));
        assertTrue(110 == w.CoastCalculation());
        w.setResources(Res);
        assertTrue(200 == w.amountResources(3));
        assertTrue(0 == w.amountResources(10));
        w.add(new Resource(1,100, 2, 0.1, "discr1"));
    }
    
    @Test
    public void test5() {
        Work w = new Work(0,null, 100, "des");
        w.add(new Resource(1,100, 1, 0.1, "discr1"));
        w.delete(5);
        w.setResources(null);
        w.set(0, null);
        w.delete(5);
        
    }
    
}
