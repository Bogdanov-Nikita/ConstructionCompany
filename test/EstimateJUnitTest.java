/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import businesslogic.Estimate;
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
public class EstimateJUnitTest {
    
    public EstimateJUnitTest() {
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
        ArrayList<Resource> Res = new ArrayList<>();
        Res.add(new Resource(100, 1, 0.1, "discr1"));
        Res.add(new Resource(200, 3, 0.5, "discr2"));
        Res.add(new Resource(300, 4, 0.6, "discr3"));
        ArrayList<Work> WL = new ArrayList<>();
        WL.add(new Work(Res, 100, "des"));
        WL.add(new Work(Res, 500, "des2"));
        Estimate e = new Estimate(Estimate.MAIN, WL);
        
        assertFalse(e.isFinish());
        assertFalse(e.isPaid());
        assertEquals(Estimate.MAIN,e.getType());
        assertEquals(1180,e.CoastCalculation(),0);
        assertEquals(e.CoastCalculation(),e.getCoast(),0);
        e.setCoast(100);
        assertEquals(100,e.getCoast(),100);
        assertNotNull(e.getWorkList());
        e.setPaid(true);
        e.setType(Estimate.ADDITIONAL);
        assertTrue(e.isPaid());
        assertEquals(Estimate.ADDITIONAL,e.getType());
       
    }
    
    @Test
    public void test2() {
        ArrayList<Resource> Res = new ArrayList<>();
        Res.add(new Resource(100, 1, 0.1, "discr1"));
        Res.add(new Resource(200, 3, 0.5, "discr2"));
        Res.add(new Resource(300, 4, 0.6, "discr3"));
        Estimate e = new Estimate(Estimate.MAIN, null);
        assertEquals(0,e.CoastCalculation(),0);
        e.setWorkList(new ArrayList<>());
        assertEquals(0,e.CoastCalculation(),0);
        e.add(new Work(Res, 100, "des"));
        e.setWorkList(null);
        e.set(4, null);
        e.add(new Work(Res, 100, "des"));
        e.set(0,new Work(Res, 100, "des"));
        assertEquals(390,e.CoastCalculation(),0);
        e.add(null);
        assertEquals(390,e.CoastCalculation(),0);
    }
    
    @Test
    public void test3() {
        ArrayList<Resource> Res = new ArrayList<>();
        Res.add(new Resource(100, 1, 0.1, "discr1"));
        Res.add(new Resource(200, 3, 0.5, "discr2"));
        Res.add(new Resource(300, 4, 0.6, "discr3"));
        ArrayList<Work> WL = new ArrayList<>();
        WL.add(new Work(Res, 100, "des"));
        WL.add(new Work(Res, 500, "des2"));
        ArrayList<Work> WL2 = new ArrayList<>(WL);
        assertEquals(390,WL.get(0).CoastCalculation(),0);
        assertEquals(790,WL.get(1).CoastCalculation(),0);
        Estimate e = new Estimate(true,Estimate.MAIN,0, WL);
        e.delete(1);
        assertEquals(390,e.CoastCalculation(),0);
        e.delete(0);
        assertEquals(0,e.CoastCalculation(),0);
        e.delete(4);
        e.setWorkList(null);
        e.delete(4);
        WL2.get(0).setFinish(true);
        WL2.get(1).setFinish(true);
        e.setWorkList(WL2);        
        assertTrue(e.isFinish());
    }
}
