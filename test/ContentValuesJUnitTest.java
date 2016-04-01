/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import database.ContentValues;
import database.Value;
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
public class ContentValuesJUnitTest {
    
    public ContentValuesJUnitTest() {
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
        ContentValues v = new ContentValues();
        v.put("name", "value");
        assertEquals(v.containsKey("name"),true);
        v.clear();
        assertEquals(v.containsKey("name"),false);
    }
    @Test
    public void test2() {
        ContentValues v = new ContentValues();
        v.put("name", "value");
        v.put("name2", "value2");
        v.set(1,new Value("name3","value3"));
        assertEquals(v.get(0).name,"name");
        assertEquals(v.get(1).name,"name3");
        assertEquals(v.get(0).value,"value");
        assertEquals(v.get(1).value,"value3");
        v.remove(0);
        assertEquals(v.get(0).name,"name3");
        assertEquals(v.get(0).value,"value3");
        assertEquals(v.containsKey("null"),false);
    }
}
