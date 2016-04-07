/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import businesslogic.Master;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Nik
 */
public class MasterMapper extends Mapper<Master, DatabaseManager>{

    @Override
    public Master load(int Id, DatabaseManager db) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ArrayList<Master> loadList(DatabaseManager db) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean save(Master e, DatabaseManager db) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean saveArray(ArrayList<Master> list, DatabaseManager db) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
