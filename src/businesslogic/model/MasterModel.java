/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package businesslogic.model;

import businesslogic.Role;
import businesslogic.Master;
import database.ContentValues;

/**
 *
 * @author Nik
 */
public class MasterModel extends AbstractBehaviorModel{
    Master master;
    @Override
    void Create(Role role) {
        master = (Master)role;
        System.out.println("MasterModel - Create");
    }
    
    @Override
    void onSaveInstanceState(Container savedInstanceState) {
        System.out.println("MasterModel - onSaveInstanceState");
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    void onRestoreInstanceState(Container outState) {
        System.out.println("MasterModel - onRestoreInstanceState");
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    void executeEvent(int Event,ContentValues value) {
        System.out.println("MasterModel - executeEvent "+Event);
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
