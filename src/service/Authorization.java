/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import database.DatabaseManager;
import service.config.Config;
/**
 *
 * @author Nik
 */

//проверка паралля и логина может быть различного типа, по этому указываем вариат отуда брать.
//может быть несколько реализаций.
public class Authorization {
    boolean ConfigRoleAuth(String Role,String login,String password,Config config){
        return true;
    }
    boolean DatabaseAuth(String Role,String login,String password,DatabaseManager manager){
        //провверка логина и пароля
        return true;//false
    }
}
