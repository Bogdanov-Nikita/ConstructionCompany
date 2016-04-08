/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import businesslogic.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Nik
 */
public class ResourceMapper extends Mapper<Resource, DatabaseManager>{
    
    @Override
    public Resource load(int Id,DatabaseManager db) throws SQLException{
        db.startTransaction();
        String columns[] = {
            Database.Resource.id,
            Database.Resource.type,
            Database.Resource.name,
            Database.Resource.coast
        };        
        ResultSet rs = db.executeQuery(
                QueryBilder.select(
                Database.Resource.Table,
                columns,
                Database.Resource.id+"=?",
                new String[]{String.valueOf(Id)},
                null,
                Database.Resource.id)
        );
        rs.next();
        db.commitTransaction();
        return new Resource(rs.getInt(1), rs.getInt(2), rs.getDouble(3), rs.getString(4));
    }
    
    @Override
    public ArrayList<Resource> loadList(DatabaseManager db) throws SQLException{
        db.startTransaction();
        String columns[] = {
            Database.Resource.id,
            Database.Resource.type,
            Database.Resource.name,
            Database.Resource.coast
        };        
        ResultSet rs = db.executeQuery(
                QueryBilder.select(
                Database.Resource.Table,
                columns,
                null,
                null,
                null,
                Database.Resource.id)
        );
        db.commitTransaction();
        ArrayList<Resource> list = new ArrayList<>();
        while(rs.next()){ 
            list.add(new Resource(rs.getInt(1), rs.getInt(2), rs.getDouble(3), rs.getString(4)));
        }                
        return list;
    }
    
    @Override
    public boolean save(Resource e, DatabaseManager db){
       return false; 
    }
    
    @Override
    public boolean saveArray(ArrayList<Resource> list, DatabaseManager db){
        return false;
    }
    
}
