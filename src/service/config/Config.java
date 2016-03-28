/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service.config;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nik
 */
public class Config {
    
    public ArrayList<ConfigItem> ItemList;

    public Config() {
        ItemList = new ArrayList<>();
    }
    
    ConfigItem findbyLogin(String login){
        for (ConfigItem ItemList1 : ItemList) {
            if (ItemList1.getRole().getLogin().compareTo(login) == 0) {
                return ItemList1;
            }
        }
        return null;
    }
    
    public void writetoFile(String FileName){
        try (BufferedOutputStream File = new BufferedOutputStream(new FileOutputStream(FileName))) {
            File.write(toString().getBytes());
        }catch (IOException ex) {
            Logger.getLogger(Config.class.getName()).log(Level.SEVERE, "I/O  Exeption ", ex);
        }
    }

    @Override
    public String toString() {
        String xml = "<RoleList>\r\n";
        for (ConfigItem ItemList1 : ItemList) {
            xml = xml + ItemList1.toString() + "\r\n";
        }
        xml = xml + "</RoleList>";
        return xml;
    }
}
