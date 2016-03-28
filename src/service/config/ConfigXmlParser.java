/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package service.config;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;



/**
 *
 * @author Nik
 */

public class ConfigXmlParser {
    
    public final static int CONFIG_SUCCESS = 0;
    public final static int CONFIG_ERROR = -1;

    Config Config;
    
    /**
     * для работы с конфигурационным файлом
     * @param FileName nane or path to configuratin file 
     * @return  CONFIG_SUCCESS or CONFIG_ERROR 
     */
    public int OpenConfig(String FileName) {                               
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser;
        try {
            saxParser = factory.newSAXParser();
            ConfigHandler handler = new ConfigHandler();
            saxParser.parse(FileName, handler);
            Config =  handler.getConfig();
        }
        catch(SAXException |ParserConfigurationException |IOException e){
            Logger.getLogger(ConfigHandler.class.getName()).log(Level.WARNING, "Can't find or open config file or file is illegal",e);
            return CONFIG_ERROR;
        }
        return CONFIG_SUCCESS;
    }
    /**
     * @return ConfigItem -  Full Configuration Information 
     */
    public Config getConfig() {
        return Config;
    }
    
}