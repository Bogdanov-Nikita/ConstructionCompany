/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package businesslogic.model;

import businesslogic.Role;
import businesslogic.Manager;
import database.ContentValues;

/**
 *
 * @author Nik
 */
public class ManagerModel extends AbstractBehaviorModel{
    Manager meneger; 
    @Override
    void Create(Role role) {
        //проверка и инициализация. ответственность инициализации роли лежит на разработке описателя модели смотри класс BehaviorModel
        meneger = (Manager)role;
        System.out.println("ManagerModel - Create");
    }
    
    @Override
    void onSaveInstanceState(Container savedInstanceState) {
        System.out.println("ManagerModel - onSaveInstanceState");
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    void onRestoreInstanceState(Container outState) {
        System.out.println("ManagerModel - onRestoreInstanceState");
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    void executeEvent(int Event,ContentValues value) {
        System.out.println("ManagerModel - executeEvent "+Event);
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
