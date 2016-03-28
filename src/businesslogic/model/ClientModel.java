/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package businesslogic.model;

import businesslogic.Client;
import businesslogic.Role;
import database.ContentValues;

/**
 *
 * @author Nik
 */
public class ClientModel extends AbstractBehaviorModel{
    Client client;
    @Override
    void Create(Role role) {
        client = (Client)role;
        System.out.println("ClientModel - Create");
    }

    @Override
    void onSaveInstanceState(Container savedInstanceState) {
        System.out.println("ClientModel - onSaveInstanceState");
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    void onRestoreInstanceState(Container outState) {
        System.out.println("ClientModel - onRestoreInstanceState");
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    void executeEvent(int Event,ContentValues value) {
        System.out.println("ClientModel - executeEvent "+Event);
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
