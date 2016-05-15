/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import Resources.R;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import service.config.Config;
import service.config.ConfigXmlParser;

/**
 *
 * @author Nik
 */
public class ConfigJUnitTest {
    
    public ConfigJUnitTest() {
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
    public void configInit() {
        Config config;
        ConfigXmlParser configInfo = new ConfigXmlParser();
        if(configInfo.OpenConfig(R.FileName.Config) == ConfigXmlParser.CONFIG_SUCCESS){  
            config = configInfo.getConfig();
            assertEquals("localhost",config.getConfigItem(1).getDatabase().getHost());
            assertEquals("D:\\Users\\Nik\\Documents\\NetBeansProjects\\ConstructionCompany\\TEST.FDB",config.getConfigItem(1).getDatabase().getPath());            
            assertEquals("SYSDBA",config.getConfigItem(1).getDatabase().getUser());
            assertEquals("masterkey",config.getConfigItem(1).getDatabase().getPassword());
            assertEquals("UTF8",config.getConfigItem(1).getDatabase().getEncoding());
            assertEquals("TYPE4",config.getConfigItem(1).getDatabase().getType());
            assertEquals("tester2",config.getConfigItem(1).getRole().getLogin());
            assertEquals("2",config.getConfigItem(1).getRole().getPassword());
            assertEquals("Master",config.getConfigItem(1).getRole().getType());
            assertTrue(config.getConfigItem(1).getSettings().isAskDialog());
            assertFalse(config.getConfigItem(1).getSettings().isExitOperation());
        }else{
            config = null;
        }
        assertNotNull(config);
    }
    @Test
    public void configSetAndValid() {
        Config config;
        ConfigXmlParser configInfo = new ConfigXmlParser();
        if(configInfo.OpenConfig(R.FileName.Config) == ConfigXmlParser.CONFIG_SUCCESS){  
            config = configInfo.getConfig();
            assertTrue(config.getConfigItem(1).getDatabase().isValid());
            assertTrue(config.getConfigItem(1).getRole().isValid());
            assertTrue(config.getConfigItem(1).isValid());
            config.getConfigItem(1).getDatabase().setHost("localhost");
            config.getConfigItem(1).getDatabase().setPath("D:\\Users\\Nik\\Documents\\NetBeansProjects\\ConstructionCompany\\TEST.FDB");
            config.getConfigItem(1).getDatabase().setUser("SYSDBA");  
            config.getConfigItem(1).getDatabase().setPassword("masterkey");
            config.getConfigItem(1).getDatabase().setEncoding("UTF8");
            config.getConfigItem(1).getDatabase().setType("TYPE4");
            config.getConfigItem(1).getRole().setLogin("tester2");
            config.getConfigItem(1).getRole().setPassword("2");
            config.getConfigItem(1).getRole().setType("Master");
            assertEquals("localhost",config.getConfigItem(1).getDatabase().getHost());
            config.getConfigItem(1).getSettings().setAskDialog(true);
            config.getConfigItem(1).getSettings().setExitOperation(false);
            
            assertEquals("D:\\Users\\Nik\\Documents\\NetBeansProjects\\ConstructionCompany\\TEST.FDB",config.getConfigItem(1).getDatabase().getPath());            
            assertEquals("SYSDBA",config.getConfigItem(1).getDatabase().getUser());
            assertEquals("masterkey",config.getConfigItem(1).getDatabase().getPassword());
            assertEquals("UTF8",config.getConfigItem(1).getDatabase().getEncoding());
            assertEquals("TYPE4",config.getConfigItem(1).getDatabase().getType());
            assertEquals("tester2",config.getConfigItem(1).getRole().getLogin());
            assertEquals("2",config.getConfigItem(1).getRole().getPassword());
            assertEquals("Master",config.getConfigItem(1).getRole().getType());
            assertTrue(config.getConfigItem(1).getSettings().isAskDialog());
            assertFalse(config.getConfigItem(1).getSettings().isExitOperation());
            String ex = config.getConfigItem(1).toString();
            assertEquals(ex,config.getConfigItem(1).toString());

            ex = config.toString();
            assertEquals(ex,config.toString());
            config.writetoFile(R.FileName.Config);
            assertEquals(1,config.findbyLogin("tester2")) ;
        }else{
            config = null;
        }
        assertNotNull(config);
    }
}
