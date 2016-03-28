/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sql;

import database.ContentValues;
import database.QueryBilder;
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
public class JUnitTestSQLrequest {
    
    public JUnitTestSQLrequest() {
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
    public void update() {
        
        ContentValues v = new ContentValues();
        v.put("collumname1", "value1");
        v.put("collumname2", "value2");
        v.put("collumname3", "value3");
        String actual =
        "UPDATE ALL_KEY_WORDS SET collumname1=value1,"
                + "collumname2=value2,"
                + "collumname3=value3 WHERE coolum1=arg1,collum2=arg2,collum3=arg3;";
        String expected =
        QueryBilder.update("ALL_KEY_WORDS", v, " coolum1=?,collum2=?,collum3=?",
                new String[]{"arg1","arg2","arg3"});
        assertEquals(expected,expected,actual);
    }
    @Test
    public void select() {
        ContentValues v = new ContentValues();
        v.put("collumname1", "value1");
        v.put("collumname2", "value2");
        v.put("collumname3", "value3");
        String actual =
        "SELECT ALL_KEY_T.collum1 as collum1,"
                + " ALL_KEY_T.collum2 as collum2,"
                + " ALL_KEY_T.collum3 as collum3 FROM ALL_KEY_T WHERE collum=arg1;";
        String expected =
        QueryBilder.select("ALL_KEY_T",new String[]{"collum1","collum2","collum3"},"collum=?",
                new String[]{"arg1"}, null, null);
        assertEquals(expected,expected,actual);
    }
    @Test
    public void select2() {
        ContentValues v = new ContentValues();
        v.put("collumname1", "value1");
        v.put("collumname2", "value2");
        v.put("collumname3", "value3");
        String actual =
        "SELECT ALL_KEY_T.collum1 as collum1,"
                + " ALL_KEY_T.collum2 as collum2,"
                + " ALL_KEY_T.collum3 as collum3 FROM ALL_KEY_T WHERE collum=arg1 collum2=arg2;";
        String expected =
        QueryBilder.select("ALL_KEY_T",new String[]{"collum1","collum2","collum3"},"collum=? collum2=?",
                new String[]{"arg1","arg2"}, null, null);
        assertEquals(expected,expected,actual);
    }
}
