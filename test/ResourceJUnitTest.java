/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import businesslogic.Resource;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author Nik
 */
public class ResourceJUnitTest {
    
    public ResourceJUnitTest() {
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
        Resource res = new Resource(-10,-10,-10, -100,null);
        assertEquals("Id",0, res.getId(),0);
        assertEquals("Amount",0,res.getAmount());
        assertEquals("Type",0,res.getType());
        assertEquals("Coast",0, res.getCoast(),0);
    }
    
    @Test
    public void test2() {
        Resource res = new Resource(100,-1,100,10,null);
        assertEquals("Id",100, res.getId(),0);
        assertEquals("Amount",0,res.getAmount());
        assertEquals("Type",100,res.getType());
        assertEquals("Coast",10, res.getCoast(),0);
    }
    
    @Test
    public void test3(){
        Resource res = new Resource(-1,-1,100,10,null);
        assertEquals("Amount",0,res.getAmount());
        assertEquals(false,res.setAmount(-100));
        assertEquals("Amount",0,res.getAmount());
        assertEquals(true,res.setAmount(100));
        assertEquals("Amount",100,res.getAmount());
    }
    
    @Test
    public void test4(){
        Resource res = new Resource(0,-1,100,-100,null);
        assertEquals("Coast",0, res.getCoast(),0);
        assertEquals(false,res.setCoast(-100));
        assertEquals("Coast",0, res.getCoast(),0);
        assertEquals(true,res.setCoast(100));
        assertEquals("Coast",100, res.getCoast(),0);
    }
    
    @Test
    public void test5(){
        Resource res = new Resource(0,1,100,100,null);
        assertEquals("Type",100,res.getType());
        assertEquals(false,res.setType(-100));
        assertEquals("Type",0,res.getType());
        assertEquals(true,res.setType(100));
        assertEquals("Type",100,res.getType());
    }
    
    @Test
    public void test6(){
        Resource res = new Resource(5,1,100,100,"Name");
        assertEquals("Name",res.getName());
        res.setName("Test1");
        assertEquals("Test1",res.getName());
        res.setId(8);
        assertEquals(8,res.getId(),0);
    }
}
