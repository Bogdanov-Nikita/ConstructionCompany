/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import businesslogic.Storage;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Nik
 */
public class StorageMapper extends Mapper<Storage, DatabaseManager>{

    @Override
    public Storage load(int Id, DatabaseManager db) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ArrayList<Storage> loadList(DatabaseManager db) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean save(Storage e, DatabaseManager db) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean saveArray(ArrayList<Storage> list, DatabaseManager db) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
