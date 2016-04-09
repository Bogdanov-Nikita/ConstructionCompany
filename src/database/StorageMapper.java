/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import businesslogic.Storage;
//import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Nik
 */
public class StorageMapper extends Mapper<Storage, DatabaseManager>{

    @Override
    public Storage load(int Id, DatabaseManager db) throws SQLException {
        /*db.startTransaction();
        String collumsInfo[] = {
            Database.StorageInformation.id,
            Database.StorageInformation.location
        };
        ResultSet rs = db.executeQuery(
                QueryBilder.select(
                        Database.StorageInformation.Table,
                        collumsInfo,
                        Database.StorageInformation.id + "=?",
                        new String[]{String.valueOf(Id)},
                        null,
                        Database.StorageInformation.id)
        );
        rs.next();
        Storage store = new Storage(rs.getInt(1), rs.getString(2), null);
        String collums[] = {
            Database.StorageInformation.id,
            Database.StorageInformation.location
        };
        ResultSet res = db.executeQuery(
            QueryBilder.select(
                    Database.Storage.Table,
                    collums,
                    Database.Storage.id + "=?",
                    new String[]{String.valueOf(store.getId())},
                    null,
                    Database.Storage.id
            )
        );
        //res.
        db.commitTransaction();
        
        
        return store;*/
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.        
    }

    @Override
    public ArrayList<Storage> loadList(DatabaseManager db) throws SQLException {
        /*db.startTransaction();
        String collumsInfo[] = {
            Database.StorageInformation.id,
            Database.StorageInformation.location
        };
        ResultSet rs = db.executeQuery(
                QueryBilder.select(
                        Database.StorageInformation.Table,
                        collumsInfo,
                        Database.StorageInformation.id + "=?",
                        new String[]{String.valueOf(Id)},
                        null,
                        Database.StorageInformation.id)
        );
        rs.next();
        db.commitTransaction();*/
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
