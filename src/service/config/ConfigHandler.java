/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package service.config;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Nik
 */
class ConfigHandler extends DefaultHandler {
    
        Config config;
        int Count = 0;
        boolean Database = false;        
        boolean Settins = false;
        //database part
        boolean HostFlag = false;
	boolean PathFlag = false;
	boolean UserFlag = false;
        boolean PasswordFlag =false;
        boolean TypeFlag = false;
        boolean EncodingFlag = false;
        
        String Host;
	String Path;
	String User;
        String Password;
        String Type;
        String Encoding;
        
    public ConfigHandler() {
        config = new Config();
        setNull();
    }
    private void setNull(){
        Host = null;Path = null;User = null;
        Password = null;Type = null;Encoding = null;
    }
    @Override
    public void startElement(String uri, String localName,String qName,
        Attributes attributes)  {
        
        if(qName.equalsIgnoreCase("Role")){   
            config.ItemList.add(new ConfigItem(null, null, null));
            
            String login = null;
            String type = null;
            String pass = null;
            
            for (int att = 0; att < attributes.getLength(); att++) {
                String attName = attributes.getQName(att);
                if(attName.equals("type")){
                    type = attributes.getValue(att);
                }
                if(attName.equals("login")){
                    login = attributes.getValue(att);
                }
                if(attName.equals("password")){
                    pass = attributes.getValue(att);
                }
            }
            ConfigRole role = new ConfigRole(type, login,pass);
            config.ItemList.get(Count).setRole(role);
        }
        if (qName.equalsIgnoreCase("Settings")) {
            
            boolean AskExitDialog = false;
            boolean ExitOperation = false;
            
            for (int att = 0; att < attributes.getLength(); att++) {
                String attName = attributes.getQName(att);
                if(attName.equals("AskExitDialog")){
                    if(attributes.getValue(att).equalsIgnoreCase("yes")||attributes.getValue(att).equalsIgnoreCase("true")){
                        AskExitDialog = true;
                    }
                }
                if(attName.equals("ExitOperation")){
                    if(attributes.getValue(att).equalsIgnoreCase("yes")||attributes.getValue(att).equalsIgnoreCase("true")){
                        ExitOperation = true;
                    }
                }
            }
            ConfigSettings settings = new ConfigSettings(AskExitDialog, ExitOperation);
            config.ItemList.get(Count).setSettings(settings);
        }
        if (qName.equalsIgnoreCase("Database")) {
                Database = true;
        }
        if (qName.equalsIgnoreCase("Host")) {
                HostFlag = true;
        }
        if (qName.equalsIgnoreCase("Path")) {
                PathFlag = true;
        }
        if (qName.equalsIgnoreCase("User")) {
                UserFlag = true;
        }
        if (qName.equalsIgnoreCase("Password")) {
                PasswordFlag = true;
        }
        if (qName.equalsIgnoreCase("Type")) {
                TypeFlag = true;
        }
        if (qName.equalsIgnoreCase("Encoding")) {
                EncodingFlag = true;
        }
        
    }

    @Override
    public void fatalError(SAXParseException e) throws SAXException {                    
        Logger.getLogger(ConfigHandler.class.getName()).log(Level.WARNING, "Fatal Config Handler Error",e);
    }

    @Override
    public void endElement(String uri, String localName,
        String qName) throws SAXException {

        if (qName.equalsIgnoreCase("Role")) {
            Count++;
        }
        if (qName.equalsIgnoreCase("Database")) {
                Database = false;
                ConfigDatabase database = new ConfigDatabase(Host, Path, User, Password, Type, Encoding);
                config.ItemList.get(Count).setDatabase(database);
                setNull();
        }
        if (qName.equalsIgnoreCase("Host")) {
                this.HostFlag = false;
        }
        if (qName.equalsIgnoreCase("Path")) {
                PathFlag = false;
        }
        if (qName.equalsIgnoreCase("User")) {
                UserFlag = false;
        }
        if (qName.equalsIgnoreCase("Password")) {
                PasswordFlag = false;
        }
        if (qName.equalsIgnoreCase("Type")) {
                TypeFlag = false;
        }
        if (qName.equalsIgnoreCase("Encoding")) {
                EncodingFlag = false;
        }
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        
        if(Database){
            if (HostFlag) {
                Host = String.copyValueOf(ch, start, length);
            }
            if (PathFlag) {
                Path = String.copyValueOf(ch, start, length);
            }
            if (UserFlag) {
                User = String.copyValueOf(ch, start, length);
            }
            if (PasswordFlag) {
                Password = String.copyValueOf(ch, start, length);
            }
            if (TypeFlag) {
                Type = String.copyValueOf(ch, start, length);
            }
            if (EncodingFlag) {
                Encoding = String.copyValueOf(ch, start, length);
            }
        }
    }

    public Config getConfig() {
        return config;
    }

}
