/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service.config;

/**
 *
 * @author Nik
 */
public class ConfigDatabase {
    
    String host;
    String path;
    String user;
    String password;
    String type;
    String encoding;
    
    public ConfigDatabase(String host, String path, String user, String password, String type, String encoding) {
        this.host = host;
        this.path = path;
        this.user = user;
        this.password = password;
        this.type = type;
        this.encoding = encoding;        
    }
    
    public String getEncoding() {
        return encoding;
    }

    public String getHost() {
        return host;
    }

    public String getPassword() {
        return password;
    }

    public String getPath() {
        return path;
    }

    public String getType() {
        return type;
    }

    public String getUser() {
        return user;
    }
    
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUser(String user) {
        this.user = user;
    }
    
    public boolean isValid(){
        if(     host != null && 
                path != null && 
                user != null && 
                password != null && 
                type != null && 
                encoding != null){            
            return(!host.equals("") && 
                   !path.equals("") && 
                   !user.equals("") && 
                   !password.equals("") && 
                   !type.equals("") && 
                   !encoding.equals(""));            
        }else{ return false;}        
    }

    @Override
    public String toString() {
        String xml = "<Database>\r\n" + 
		"<host>" + host + "</host>\r\n" +
		"<path>" + path + "</path>\r\n" +
		"<user>" + user + "</user>\r\n" +
		"<password>" + password + "</password>\r\n" +
		"<type>" + type + "</type>\r\n" +
		"<encoding>" + encoding + "</encoding>\r\n" +
		"</Database>";
        return xml;
    }
    
}
