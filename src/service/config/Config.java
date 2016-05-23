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
import service.ExitMsg;

/**
 *
 * @author Nik
 */
public class Config {
    
    public ArrayList<ConfigItem> ItemList;

    public Config() {
        ItemList = new ArrayList<>();
    }
    
    public ConfigItem getConfigItem(int index){
        return ItemList.get(index);
    }
    
    public int findbyLogin(String login){
        for (int i = 0; i < ItemList.size(); i++) {
            if (ItemList.get(i).getRole().getLogin().compareTo(login) == 0) {
                return i;
            }
        }
        return -1;
    }
    
    public ExitMsg writetoFile(String FileName){
        try (BufferedOutputStream File = new BufferedOutputStream(new FileOutputStream(FileName))) {
            File.write(toString().getBytes());
            return new ExitMsg(ExitMsg.SUCCESS, null);
        }catch (IOException ex) {
            Logger.getLogger(Config.class.getName()).log(Level.SEVERE, "I/O  Exeption ", ex);
            return new ExitMsg(ExitMsg.FILE_WRITE_ERROR ,ex.toString());
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
