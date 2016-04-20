/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import businesslogic.Resource;
import businesslogic.Storage;
import database.DatabaseManager;
import database.StorageMapper;
import java.sql.SQLException;
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
public class StorageMapperJUnitTest {
    
    public StorageMapperJUnitTest() {
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
    public void load() throws SQLException {
        DatabaseManager db = new DatabaseManager(
                "SYSDBA", 
                        "masterkey", 
                        "localhost", 
                        "D:\\Users\\Nik\\Documents\\NetBeansProjects\\ConstructionCompany\\test.fdb", 
                        DatabaseManager.CharEncoding.UTF8.name(), 
                        "TYPE4", 
                        DatabaseManager.IsolationLevel.TRANSACTION_SERIALIZABLE.name());
        db.connect();     
        Storage cl =  new StorageMapper().load(1, db);
        
        assertEquals(1,cl.getId());
        assertEquals("storage addr 1",cl.getLocation());
        
        int amounts[]={1000,5000,2000,500,100};
        for(int i = 0; i < cl.getResources().size(); i++){
            assertEquals((i+1),cl.getResource(i).getId());
            assertEquals((i+1),cl.getResource(i).getType());
            assertEquals("resource " + String.valueOf(i+1),cl.getResource(i).getName());
            assertEquals(amounts[i],cl.getResource(i).getAmount());
        }
        db.closeConnection();
        db.close();
    }
    
    @Test
    public void loadAll() throws SQLException{
        DatabaseManager db = new DatabaseManager(
                "SYSDBA", 
                        "masterkey", 
                        "localhost", 
                        "D:\\Users\\Nik\\Documents\\NetBeansProjects\\ConstructionCompany\\test.fdb", 
                        DatabaseManager.CharEncoding.UTF8.name(), 
                        "TYPE4", 
                        DatabaseManager.IsolationLevel.TRANSACTION_SERIALIZABLE.name());
        db.connect();    
        
        int amounts[][]={{1000,5000,2000,500,100},{100,1500,4000,7000,2500},{10000}};
        
        ArrayList<Storage> StoreList = new StorageMapper().loadList(db);        
        for(int i = 0; i < StoreList.size(); i++){
            assertEquals(i+1,StoreList.get(i).getId()); 
            assertEquals("storage addr "+(i+1),StoreList.get(i).getLocation());
            if(i != 2){
                for(int j = 0; j < StoreList.get(i).getResources().size(); j++){                               
                    assertEquals((j+1),StoreList.get(i).getResource(j).getId());
                    assertEquals((j+1),StoreList.get(i).getResource(j).getType());
                    assertEquals("resource " + String.valueOf(j+1),StoreList.get(i).getResource(j).getName());
                    assertEquals(amounts[i][j],StoreList.get(i).getResource(j).getAmount());                
                }
            }else{                
                assertEquals(7,StoreList.get(i).getResource(0).getId());
                assertEquals(7,StoreList.get(i).getResource(0).getType());
                assertEquals("resource 7",StoreList.get(i).getResource(0).getName());
                assertEquals(amounts[i][0],StoreList.get(i).getResource(0).getAmount());    
            }
        }
        db.closeConnection();
        db.close();
    }
    
    @Test
    public void save() throws SQLException{
        DatabaseManager db = new DatabaseManager(
                "SYSDBA", 
                        "masterkey", 
                        "localhost", 
                        "D:\\Users\\Nik\\Documents\\NetBeansProjects\\ConstructionCompany\\test.fdb", 
                        DatabaseManager.CharEncoding.UTF8.name(), 
                        "TYPE4", 
                        DatabaseManager.IsolationLevel.TRANSACTION_SERIALIZABLE.name());
        db.connect(); 

        //Временно сохраняем значения из базы.
        Storage Oldcl = new StorageMapper().load(3, db);
        
        ArrayList<Resource> res = new ArrayList<>();
        res.add(new Resource(7,100,7,10.8, "new resource 7"));
        Storage cl = new Storage(3, "new storage addr 3", res);
        new StorageMapper().save(cl, db);        
        
        cl = new StorageMapper().load(3, db);
        //проверка что всё записалось.
        assertEquals("Id",3,cl.getId());
        assertEquals("new storage addr 3",cl.getLocation());
        assertEquals(100,cl.getResource(0).getAmount());
        assertEquals(7,cl.getResource(0).getType());
        assertEquals(10.8,cl.getResource(0).getCoast(),0);
        assertEquals("new resource 7",cl.getResource(0).getName());      
        
        //возвращаем обратно старые значения.
        new StorageMapper().save(Oldcl, db);
        
        db.closeConnection();
        db.close();
    }
    
    @Test
    public void InsertAndDelete() throws SQLException{
        DatabaseManager db = new DatabaseManager(
                "SYSDBA", 
                        "masterkey", 
                        "localhost", 
                        "D:\\Users\\Nik\\Documents\\NetBeansProjects\\ConstructionCompany\\test.fdb", 
                        DatabaseManager.CharEncoding.UTF8.name(), 
                        "TYPE4", 
                        DatabaseManager.IsolationLevel.TRANSACTION_SERIALIZABLE.name());
        db.connect(); 

        ArrayList<Resource> Resources = new ArrayList<>();
        Resources.add(new Resource(8, 1000, 8, 4.5, "resource 8"));
        Storage cl = new Storage(0,"new location", Resources);
        new StorageMapper().save(cl, db);
        
        cl = new StorageMapper().load(6, db);
        //проверка что всё записалось.
        assertEquals("Id",6,cl.getId());
        assertEquals("new location",cl.getLocation());
        assertEquals(1000,cl.getResource(0).getAmount());
        assertEquals(8,cl.getResource(0).getType());
        assertEquals(4.5,cl.getResource(0).getCoast(),0);
        assertEquals("resource 8",cl.getResource(0).getName());
        
        new StorageMapper().delete(6, db);
        db.startTransaction();
        db.execute("ALTER SEQUENCE STORAGEINFORMATION_ID_GENERATOR RESTART WITH 5");
        db.commitTransaction();
        db.closeConnection();
        db.close();
    }
    
}
