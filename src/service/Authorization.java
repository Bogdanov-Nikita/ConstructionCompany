/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import database.Database;
import database.DatabaseManager;
import database.QueryBilder;
import java.sql.ResultSet;
import java.sql.SQLException;
import service.config.Config;
import service.config.ConfigRole;
/**
 *
 * @author Nik
 */

//может быть несколько реализаций.
public class Authorization {
    
    public final static int NOT_FOUND = -1;
    public final static int ROLE_NOT_COMPARE_LOGIN = -2;
    public final static int NOT_CORRECT_PASSWORD = -3;
    
    public static int ConfigRoleAuth(String Role,String login,String password,Config config){
        int index = config.findbyLogin(login);
        if(index >= 0){
            ConfigRole RoleInfo = config.getConfigItem(index).getRole();
            if(RoleInfo.getType().compareTo(Role) == 0){
                if(RoleInfo.getPassword().compareTo(password) == 0){
                    return index;
                }else{
                    return NOT_CORRECT_PASSWORD;
                }
            }else{
                return ROLE_NOT_COMPARE_LOGIN;
            }
        }else{
            return NOT_FOUND;
        }
    }
    
    public static int DatabaseRoleAuth(ConfigRole RoleInfo,DatabaseManager db) throws SQLException{
        int id = -1;
        String columns[] = {
            Database.Roles.login,
            Database.Roles.role_id
        };
        db.startTransaction();
        ResultSet rs = db.executeQuery(
                QueryBilder.select(
                        Database.Roles.Table, 
                        columns, 
                        "\"" + Database.Roles.login + "\"=?", 
                        new String[]{"\'"+RoleInfo.getLogin()+"\'"},
                        null, 
                        null)
        );
        if(rs.next()){
            id = rs.getInt(2);
        }
        db.commitTransaction();        
        return id;
    }
}