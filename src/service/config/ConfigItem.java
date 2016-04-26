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
public class ConfigItem {
    
    ConfigRole Role;
    ConfigDatabase Database;
    ConfigSettings Settings;

    public ConfigItem(ConfigRole Role, ConfigDatabase Database, ConfigSettings Settings) {
        this.Role = Role;
        this.Database = Database;
        this.Settings = Settings;
    }    
    
    public ConfigRole getRole() {
        return Role;
    }

    public ConfigDatabase getDatabase() {
        return Database;
    }

    public ConfigSettings getSettings() {
        return Settings;
    }

    public void setRole(ConfigRole Role) {
        this.Role = Role;
    }

    public void setDatabase(ConfigDatabase Database) {
        this.Database = Database;
    }

    public void setSettings(ConfigSettings Settings) {
        this.Settings = Settings;
    }
    
    @Override
    public String toString() {
        String xml = "<Role type = \"" + Role.getType() + "\" "
                + "login = \"" + Role.getLogin() + "\" "
                + "password = \"" + Role.getPassword() +"\">\r\n" +
            Settings.toString() + "\r\n" +
            Database.toString() + "\r\n" +
            "</Role>";
        return xml;
    }
    public boolean isValid(){
        if(     Role != null && 
                Database != null && 
                Settings != null){            
            return(Role.isValid() && 
                   Database.isValid());           
        }else{ return false;}
    }
}
