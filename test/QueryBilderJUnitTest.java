/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


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
public class QueryBilderJUnitTest {
    
    public QueryBilderJUnitTest() {
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
        QueryBilder d = new QueryBilder();
        ContentValues v = new ContentValues();
        v.put("collumname1", "value1");
        v.put("collumname2", "value2");
        v.put("collumname3", "value3");
        
        String actual =
                "UPDATE ALL_KEY_WORDS SET collumname1=\'value1\',"
                + "collumname2=\'value2\',"
                + "collumname3=\'value3\' WHERE coolum1=\'arg1\',collum2=\'arg2\',collum3=\'arg3\';";
        String expected =
        QueryBilder.update("ALL_KEY_WORDS", v, "coolum1=?,collum2=?,collum3=?",
                new String[]{"arg1","arg2","arg3"});
        assertEquals(expected,expected,actual);
    }
    
    @Test
    public void update2() {
        
        ContentValues v = new ContentValues();
        String actual =
                "UPDATE ALL_KEY_WORDS SET ";
        String expected =
        QueryBilder.update("ALL_KEY_WORDS", v,"coolum1=?,collum2=?,collum3=?",null);
        assertEquals(expected,expected,actual);
    }
    @Test
    public void update3() {
        
        ContentValues v = new ContentValues();
        v.put("collumname1", "value1");
        v.put("collumname2", "value2");
        v.put("collumname3", "value3");
        String actual =
                "UPDATE ALL_KEY_WORDS SET "
                + "collumname1='value1',"
                + "collumname2='value2',"
                + "collumname3='value3' WHERE coolum1=?,collum2=?,collum3=?;";
        String expected =
        QueryBilder.update("ALL_KEY_WORDS", v,"coolum1=?,collum2=?,collum3=?",null);
        assertEquals(expected,expected,actual);
    }
    @Test
    public void update4() {
        
        ContentValues v = new ContentValues();
        v.put("collumname1", "value1");
        v.put("collumname2", "value2");
        v.put("collumname3", "value3");
        String actual =
                "UPDATE ALL_KEY_WORDS SET collumname1='value1',collumname2='value2',collumname3='value3' ;";
        String expected =
        QueryBilder.update("ALL_KEY_WORDS", v,null,null);
        assertEquals(expected,expected,actual);
    }
    @Test
    public void select() {
        
        String actual =
        "SELECT ALL_KEY_T.collum1 as collum1,"
                + " ALL_KEY_T.collum2 as collum2,"
                + " ALL_KEY_T.collum3 as collum3 FROM ALL_KEY_T WHERE collum=\'arg1\';";
        String expected =
        QueryBilder.select("ALL_KEY_T",new String[]{"collum1","collum2","collum3"},"collum=?",
                new String[]{"arg1"}, null, null);
        assertEquals(expected,expected,actual);
    }
    
    @Test
    public void select2() {
                
        String actual =
        "SELECT ALL_KEY_T.collum1 as collum1,"
                + " ALL_KEY_T.collum2 as collum2,"
                + " ALL_KEY_T.collum3 as collum3 FROM ALL_KEY_T WHERE collum=\'arg1\' collum2=\'arg2\' GROUP BY collum1 ORDER BY collum2;";
        String expected =
        QueryBilder.select("ALL_KEY_T",new String[]{"collum1","collum2","collum3"},"collum=? collum2=?",
                new String[]{"arg1","arg2"}, "collum1", "collum2");
        assertEquals(expected,expected,actual);
    }
    
    @Test
    public void select3() {
        
        String actual =
        "SELECT * FROM ALL_KEY_T ;";
        String expected =
        QueryBilder.select("ALL_KEY_T",null,null,null,null,null);
        assertEquals(expected,expected,actual);
    }
    
    @Test
    public void select4() {
                
        String actual =
        "SELECT ALL_KEY_T.collum1 as collum1, "
                + "ALL_KEY_T.collum2 as collum2, "
                + "ALL_KEY_T.collum3 as collum3 FROM ALL_KEY_T WHERE collum=? collum2=? GROUP BY collum1 ORDER BY collum2;";
        String expected =
        QueryBilder.select("ALL_KEY_T",new String[]{"collum1","collum2","collum3"},"collum=? collum2=?",
                null, "collum1", "collum2");
        assertEquals(expected,expected,actual);
    }
    
    @Test
    public void insert(){
        ContentValues v = new ContentValues();
        v.put("collumname1", "value1");
        v.put("collumname2", "value2");
        v.put("collumname3", "value3");        
        
        String actual =
                "INSERT INTO ALL_KEY_T (collumname1,collumname2,collumname3) VALUES (\'value1\',\'value2\',\'value3\');";
        String expected = QueryBilder.insert("ALL_KEY_T", v);
        assertEquals(expected,expected,actual);
    }
    
    @Test
    public void insert2(){
        ContentValues v = new ContentValues();
        String actual =
                "INSERT INTO ALL_KEY_T ";
        String expected = QueryBilder.insert("ALL_KEY_T", v);
        assertEquals(expected,expected,actual);
    }
    
    @Test
    public void delete(){
        ContentValues v = new ContentValues();
        v.put("collumname1", "value1");
        v.put("collumname2", "value2");
        v.put("collumname3", "value3");        
        
        String actual =
                "DELETE FROM ALL_KEY_T WHERE  collumname1=\'value1\' AND collumname2=\'value2\' AND collumname3=\'value3\';";
        String expected = QueryBilder.delete("ALL_KEY_T", v);
        assertEquals(expected,expected,actual);
    }
    
    @Test
    public void delete2(){     
        
        String actual =
                "DELETE FROM ALL_KEY_T WHERE collumname1=\'value1\' AND collumname2=\'value2\' AND collumname3=\'value3\';";
        String part = "collumname1=\'value1\' AND collumname2=\'value2\' AND collumname3=\'value3\'";
        String expected = QueryBilder.delete("ALL_KEY_T", part);
        assertEquals(expected,expected,actual);
    }
    
    @Test
    public void delete3(){     
        ContentValues v = new ContentValues();
        String actual =
                "DELETE FROM ALL_KEY_T WHERE  ;";
        String expected = QueryBilder.delete("ALL_KEY_T", v);
        assertEquals(expected,expected,actual);
    }
}
