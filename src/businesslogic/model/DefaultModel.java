/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package businesslogic.model;

import businesslogic.Role;
import database.ContentValues;

/**
 *
 * @author Nik
 */
public class DefaultModel extends AbstractBehaviorModel{

    @Override
    void Create(Role role) {
        this.role = role;
        System.out.println("DefaultView - Create");
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    void onSaveInstanceState(Container savedInstanceState) {
        System.out.println("DefaultModel - onSaveInstanceState");
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    void onRestoreInstanceState(Container outState) {
        System.out.println("DefaultModel - onRestoreInstanceState");
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    void executeEvent(int Event,ContentValues value) {
        System.out.println("DefaultModel - executeEvent "+Event);
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
