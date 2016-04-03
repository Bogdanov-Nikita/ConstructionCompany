/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import businesslogic.Resource;
import businesslogic.Storage;
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
public class StorageJUnitTest {
    
    public StorageJUnitTest() {
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
        Res.add(new Resource(100, 1, 0.5, "res1"));
        Res.add(new Resource(200, 2, 0.7, "res2"));
        Res.add(new Resource(300, 3, 1.6, "res3"));
        Res.add(new Resource(400, 4, 1.2, "res4"));
        Storage store = new Storage(1, "location",null);
        assertEquals(1, store.getId());
        assertEquals("location", store.getLocation());
        store.setLocation("notlocation");
        assertEquals("notlocation", store.getLocation());
        assertNull(store.getResources());
        assertTrue(store.isEmpty());
        store.setResources(Res);
        assertNotNull(store.getResources());
    }
    
    @Test
    public void test2() {
        ArrayList<Resource> Res = new ArrayList<>();
        Res.add(new Resource(100, 1, 0.5, "res1"));
        Res.add(new Resource(200, 2, 0.7, "res2"));
        Res.add(new Resource(300, 3, 1.6, "res3"));
        Res.add(new Resource(400, 4, 1.2, "res4"));
        Storage store = new Storage(1, "location",Res);
        assertNotNull(store.getResource(0));
        assertEquals(Storage.SEND_RESORSE_SUCCESS, store.SendResources(1, 1000));
        assertFalse(store.isEmpty());
        assertEquals(Storage.TAKE_RESORSE_SUCCESS, store.TakeResources(0, 1100));
        store.addResource(new Resource(100, 1, 0.5, "res1"));
        assertEquals(Storage.RESORSE_EMPTY, store.TakeResources(0, 1100));
        assertEquals(Storage.INSUFFICIENTLY_RESORSE, store.TakeResources(1, 1100));
        assertEquals(Storage.RESORSE_NOT_FOUND, store.TakeResources(7, 1100));
        assertEquals(Storage.RESORSE_NOT_FOUND, store.SendResources(-10, 1000));
    }
    
    @Test
    public void test3() {
        ArrayList<Resource> Res = new ArrayList<>();
        Res.add(new Resource(100, 1, 0.5, "res1"));
        Res.add(new Resource(200, 2, 0.7, "res2"));
        Res.add(new Resource(300, 3, 1.6, "res3"));
        Res.add(new Resource(400, 4, 1.2, "res4"));
        Storage store = new Storage(1, "location",null);
        assertNull(store.getResource(0));
        assertEquals(Storage.STORAGE_RESOURSE_FAIL, store.SendResources(1, 1000));
        assertTrue(store.isEmpty());
        assertEquals(Storage.STORAGE_EMPTY, store.TakeResources(0, 1100));
        store.setResources(Res);
        assertNull(store.getResource(5));
    }
    
    @Test
    public void test4() {
        ArrayList<Resource> Res = new ArrayList<>();
        Res.add(new Resource(100, 1, 0.5, "res1"));
        Res.add(new Resource(200, 2, 0.7, "res2"));
        Res.add(new Resource(300, 3, 1.6, "res3"));
        Res.add(new Resource(400, 4, 1.2, "res4"));        
        Storage store = new Storage(1, "location",Res);
        assertEquals(Storage.ADD_RESOURSE_FAIL, store.SendResources(1, -1000));
    }
    
}
