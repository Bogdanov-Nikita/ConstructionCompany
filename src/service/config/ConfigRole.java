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
public class ConfigRole {
    
    String type;
    String login;
    String password;

    public ConfigRole(String type, String login,String password) {
        this.type = type;
        this.login = login;
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public boolean isValid(){
        if(     type != null && 
                login != null &&
                password != null ){            
            return(!type.equals("") && 
                   !login.equals("") &&
                   !password.equals(""));            
        }else{ return false;} 
    }
    
}
